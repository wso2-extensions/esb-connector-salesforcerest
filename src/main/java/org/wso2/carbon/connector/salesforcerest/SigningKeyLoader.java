/*
 * Copyright (c) 2026, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.connector.salesforcerest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Loads the RSA private key (at least 2048 bits) that signs the JWT bearer assertion, and keeps loaded keys:
 * <ul>
 * <li>JKS / PKCS12: a keystore protected by keyStorePassword; the key password defaults to the store password.</li>
 * <li>PEM: a file with a PKCS#8 private key ("BEGIN PRIVATE KEY", or "BEGIN ENCRYPTED PRIVATE KEY" decrypted with
 * keyPassword). Other blocks in the file, such as the certificate chain, are ignored.</li>
 * </ul>
 * Each loaded key is found by a random handle, which is safe to put in the message context.
 */
final class SigningKeyLoader {

    static final String TYPE_JKS = "JKS";
    static final String TYPE_PKCS12 = "PKCS12";
    static final String TYPE_PEM = "PEM";
    static final int MIN_RSA_KEY_BITS = 2048;

    private static final long MAX_KEY_FILE_BYTES = 1024 * 1024;
    private static final String PEM_KEY = "PRIVATE KEY";
    private static final String PEM_ENCRYPTED_KEY = "ENCRYPTED PRIVATE KEY";
    private static final String PBES2_NAME = "PBES2";
    private static final String PBES2_OID = "1.2.840.113549.1.5.13";
    // A URL scheme ("file:", "http://", ...). Single letters are left alone so Windows drive paths still work.
    private static final Pattern URL_SCHEME = Pattern.compile("^[a-zA-Z][a-zA-Z0-9+.-]+:.*", Pattern.DOTALL);

    // Loaded signing keys, by handle (a random id). The handle is kept in the connection settings in the message
    // context so that a renewal in callWithRetry, which cannot read the init parameters, can find the key.
    private final ConcurrentHashMap<String, SigningKey> keys = new ConcurrentHashMap<>();
    // Handle of each key file. The key file id includes a hash of the passwords, so it is kept here and never put in
    // the message context.
    private final ConcurrentHashMap<String, String> keyHandles = new ConcurrentHashMap<>();
    // Last failed load of each key file. Reported again, without reading and decrypting the file, until the file
    // changes or TokenCache.FAILURE_HOLD_MILLIS has passed.
    private final ConcurrentHashMap<String, LoadFailure> failures = new ConcurrentHashMap<>();

    /**
     * @return the random handle of the key file identified by keyFileId. The key is loaded once and loaded again
     * (under the same handle) when the file's modification time changes.
     */
    String keyHandle(String keyFileId, long lastModified, KeyLoader loader) throws JwtException {
        LoadFailure failure = failures.get(keyFileId);
        if (failure != null && failure.lastModified == lastModified && System.nanoTime() - failure.atNanos
                < TimeUnit.MILLISECONDS.toNanos(TokenCache.FAILURE_HOLD_MILLIS)) {
            throw config(failure.message);
        }
        String handle = keyHandles.get(keyFileId);
        if (handle == null) {
            String candidate = UUID.randomUUID().toString();
            keys.put(candidate, new SigningKey(load(keyFileId, lastModified, loader), lastModified));
            handle = keyHandles.putIfAbsent(keyFileId, candidate);
            if (handle == null) {
                return candidate;
            }
            // Another request loaded the same key file at the same time; use its handle.
            keys.remove(candidate);
        }
        SigningKey current = keys.get(handle);
        if (current == null || current.lastModified != lastModified) {
            keys.put(handle, new SigningKey(load(keyFileId, lastModified, loader), lastModified));
        }
        return handle;
    }

    private PrivateKey load(String keyFileId, long lastModified, KeyLoader loader) throws JwtException {
        try {
            PrivateKey key = loader.load();
            failures.remove(keyFileId);
            return key;
        } catch (JwtException e) {
            failures.put(keyFileId, new LoadFailure(lastModified, e.getMessage(), System.nanoTime()));
            throw e;
        }
    }

    /**
     * @return the key for the handle, or null if there is none
     */
    PrivateKey key(String handle) {
        SigningKey signingKey = handle == null ? null : keys.get(handle);
        return signingKey == null ? null : signingKey.key;
    }

    /**
     * Loads a signing key.
     */
    interface KeyLoader {
        PrivateKey load() throws JwtException;
    }

    /**
     * A loaded signing key and the modification time of its file.
     */
    private static final class SigningKey {

        private final PrivateKey key;
        private final long lastModified;

        SigningKey(PrivateKey key, long lastModified) {
            this.key = key;
            this.lastModified = lastModified;
        }
    }

    /**
     * A failed load of a key file: the file's modification time, the error message and when it failed.
     */
    private static final class LoadFailure {

        private final long lastModified;
        private final String message;
        private final long atNanos;

        LoadFailure(long lastModified, String message, long atNanos) {
            this.lastModified = lastModified;
            this.message = message;
            this.atNanos = atNanos;
        }
    }

    /**
     * @param type JKS, PKCS12 or PEM; null to infer it from the file extension (.jks, .pem, otherwise PKCS12)
     */
    static String resolveType(String type, Path path) throws JwtException {
        if (type == null) {
            String name = String.valueOf(path.getFileName()).toLowerCase(Locale.ROOT);
            if (name.endsWith(".jks")) {
                return TYPE_JKS;
            }
            return name.endsWith(".pem") || name.endsWith(".key") ? TYPE_PEM : TYPE_PKCS12;
        }
        String upper = type.toUpperCase(Locale.ROOT);
        if (TYPE_JKS.equals(upper) || TYPE_PKCS12.equals(upper) || TYPE_PEM.equals(upper)) {
            return upper;
        }
        throw new JwtException(JwtException.ERROR_CONFIG, "keyStoreType must be JKS, PKCS12 or PEM");
    }

    static Path resolvePath(String keyStorePath) throws JwtException {
        if (keyStorePath.indexOf('\u0000') >= 0 || URL_SCHEME.matcher(keyStorePath).matches()) {
            throw config("keyStorePath must be a path to a local file, not a URL");
        }
        try {
            Path path = Paths.get(keyStorePath);
            if (!path.isAbsolute()) {
                String home = System.getProperty("carbon.home", System.getProperty("user.dir"));
                path = Paths.get(home).resolve(path);
            }
            return path.normalize();
        } catch (InvalidPathException e) {
            throw config("keyStorePath is not a valid file path");
        }
    }

    /**
     * @param storePassword keystore password (JKS / PKCS12); not used for PEM
     * @param alias         key alias (JKS / PKCS12); optional when the keystore holds a single key
     * @param keyPassword   key password; defaults to the store password. Required (either one) for an encrypted
     *                      PEM key
     */
    static PrivateKey load(Path path, String type, String storePassword, String alias, String keyPassword)
            throws JwtException {
        String fileName = String.valueOf(path.getFileName());
        byte[] content;
        try {
            if (!Files.isRegularFile(path) || Files.size(path) > MAX_KEY_FILE_BYTES) {
                throw config("The key file '" + fileName + "' does not exist or is too large");
            }
            content = Files.readAllBytes(path);
        } catch (IOException e) {
            throw config("Unable to read the key file '" + fileName + "'");
        }
        String effectiveKeyPassword = keyPassword != null ? keyPassword : storePassword;
        try {
            Key key = TYPE_PEM.equals(type)
                    ? fromPem(new String(content, StandardCharsets.US_ASCII), effectiveKeyPassword, fileName)
                    : fromKeyStore(content, type, storePassword, alias, effectiveKeyPassword, fileName);
            return checkRsa(key);
        } finally {
            Arrays.fill(content, (byte) 0);
        }
    }

    private static Key fromKeyStore(byte[] content, String type, String storePassword, String alias,
                                    String keyPassword, String fileName) throws JwtException {
        if (storePassword == null) {
            throw config("keyStorePassword is required for a " + type + " keystore");
        }
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance(type);
            keyStore.load(new ByteArrayInputStream(content), storePassword.toCharArray());
        } catch (IOException | GeneralSecurityException e) {
            throw config("Unable to open the keystore '" + fileName + "': check keyStorePassword and keyStoreType");
        }
        try {
            String keyAlias = alias != null ? alias : singleKeyAlias(keyStore, fileName);
            if (!keyStore.isKeyEntry(keyAlias)) {
                throw config("The keystore '" + fileName + "' has no private key with the configured keyAlias");
            }
            return keyStore.getKey(keyAlias, keyPassword.toCharArray());
        } catch (GeneralSecurityException e) {
            throw config("Unable to read the private key from '" + fileName + "': check keyPassword");
        }
    }

    private static String singleKeyAlias(KeyStore keyStore, String fileName) throws GeneralSecurityException,
            JwtException {
        List<String> aliases = new ArrayList<>();
        for (String candidate : Collections.list(keyStore.aliases())) {
            if (keyStore.isKeyEntry(candidate)) {
                aliases.add(candidate);
            }
        }
        if (aliases.size() != 1) {
            throw config("keyAlias is required: the keystore '" + fileName + "' contains " + aliases.size()
                    + " keys");
        }
        return aliases.get(0);
    }

    private static Key fromPem(String pem, String keyPassword, String fileName) throws JwtException {
        if (pem.contains("BEGIN RSA PRIVATE KEY")) {
            throw config("The key in '" + fileName + "' is in PKCS#1 format. Convert it to PKCS#8 with: "
                    + "openssl pkcs8 -topk8 -in key.pem -out key-pkcs8.pem");
        }
        byte[] encrypted = pemBlock(pem, PEM_ENCRYPTED_KEY);
        byte[] der = null;
        try {
            if (encrypted != null) {
                if (keyPassword == null) {
                    throw config("keyPassword is required for the encrypted key in '" + fileName + "'");
                }
                der = decrypt(encrypted, keyPassword, fileName);
            } else {
                der = pemBlock(pem, PEM_KEY);
                if (der == null) {
                    throw config("'" + fileName + "' does not contain a PEM private key (BEGIN PRIVATE KEY)");
                }
            }
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
        } catch (GeneralSecurityException e) {
            // The cause is not kept: some providers include parts of the encoded key in their messages.
            throw config("Unable to read the private key from '" + fileName + "'"
                    + (encrypted != null ? ": check keyPassword" : ""));
        } finally {
            if (der != null) {
                Arrays.fill(der, (byte) 0);
            }
            if (encrypted != null) {
                Arrays.fill(encrypted, (byte) 0);
            }
        }
    }

    /**
     * Decrypts an encrypted PKCS#8 key.
     */
    private static byte[] decrypt(byte[] encrypted, String password, String fileName)
            throws GeneralSecurityException, JwtException {
        EncryptedPrivateKeyInfo info;
        try {
            info = new EncryptedPrivateKeyInfo(encrypted);
        } catch (IOException e) {
            // The JDK also fails here for encryption schemes it does not know (for example Camellia).
            throw config("The encrypted key in '" + fileName + "' is not valid or uses an encryption algorithm this "
                    + "Java runtime does not support. Re-encrypt it, for example with "
                    + "openssl pkcs8 -topk8 -v2 aes-256-cbc");
        }
        String algorithm = info.getAlgName();
        if (PBES2_NAME.equalsIgnoreCase(algorithm) || PBES2_OID.equals(algorithm)) {
            // On JDK 8 to 17, PBES2 keys (the openssl 3 default) report only the scheme; the parameters name the
            // concrete algorithm, e.g. PBEWithHmacSHA256AndAES_256.
            algorithm = info.getAlgParameters().toString();
        }
        SecretKey pbeKey;
        Cipher cipher;
        try {
            pbeKey = SecretKeyFactory.getInstance(algorithm).generateSecret(new PBEKeySpec(password.toCharArray()));
            cipher = Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw config("The key in '" + fileName + "' is encrypted with an algorithm this Java runtime does not "
                    + "support (" + SalesforceTokenClient.sanitize(algorithm) + "). Re-encrypt it, for example with "
                    + "openssl pkcs8 -topk8 -v2 aes-256-cbc");
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, pbeKey, info.getAlgParameters());
        } catch (InvalidKeyException e) {
            throw config("The Java runtime rejected the encryption of the key in '" + fileName + "' ("
                    + SalesforceTokenClient.sanitize(algorithm) + "). On older Java 8 updates, enable the "
                    + "unlimited strength crypto policy, or re-encrypt the key");
        }
        // A wrong password surfaces here (bad padding or an invalid key spec): reported as "check keyPassword".
        return info.getKeySpec(cipher).getEncoded();
    }

    /**
     * @return the decoded content of the first "-----BEGIN label-----" block, or null if there is none
     */
    private static byte[] pemBlock(String pem, String label) throws JwtException {
        String begin = "-----BEGIN " + label + "-----";
        String end = "-----END " + label + "-----";
        int start = pem.indexOf(begin);
        int stop = start < 0 ? -1 : pem.indexOf(end, start);
        if (start < 0 || stop < 0) {
            return null;
        }
        try {
            return Base64.getDecoder().decode(pem.substring(start + begin.length(), stop).replaceAll("\\s", ""));
        } catch (IllegalArgumentException e) {
            throw config("The PEM private key is not valid base64");
        }
    }

    private static PrivateKey checkRsa(Key key) throws JwtException {
        if (!(key instanceof RSAPrivateKey)) {
            throw config("The signing key must be an RSA private key (Salesforce requires RS256)");
        }
        if (((RSAPrivateKey) key).getModulus().bitLength() < MIN_RSA_KEY_BITS) {
            throw config("The RSA signing key must be at least " + MIN_RSA_KEY_BITS + " bits");
        }
        return (PrivateKey) key;
    }

    private static JwtException config(String message) {
        return new JwtException(JwtException.ERROR_CONFIG, message);
    }
}
