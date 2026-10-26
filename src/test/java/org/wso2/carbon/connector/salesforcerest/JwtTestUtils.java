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

import org.testng.Assert;
import org.testng.SkipException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Shared test utilities: throw-away key material generated with the JDK keytool and JCE (so no private key is
 * committed and no extra crypto library is needed), and an assertion for {@link JwtException} error codes.
 */
final class JwtTestUtils {

    static final String PASSWORD = "test-pass";
    static final String ALIAS = "sfjwt";
    private static Path shared;

    private JwtTestUtils() {
    }

    /**
     * PKCS#12 keystore (password {@link #PASSWORD}, alias {@link #ALIAS}) with a 2048-bit RSA key and a self-signed
     * certificate for localhost. Generated once per test run.
     */
    static synchronized Path sharedKeyStore() throws Exception {
        if (shared == null) {
            Path dir = Files.createTempDirectory("sf-jwt-test");
            dir.toFile().deleteOnExit();
            shared = generateKeyStore(dir.resolve("shared.p12"), "PKCS12", ALIAS, 2048, PASSWORD, PASSWORD);
            shared.toFile().deleteOnExit();
        }
        return shared;
    }

    static KeyPair sharedKeyPair() throws Exception {
        return keyPair(sharedKeyStore(), "PKCS12", ALIAS, PASSWORD, PASSWORD);
    }

    static KeyPair rsa(int bits) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(bits);
        return generator.generateKeyPair();
    }

    static Path generateKeyStore(Path file, String type, String alias, int bits, String storePassword,
                                 String keyPassword) throws Exception {
        Files.deleteIfExists(file);
        String keytool = Paths.get(System.getProperty("java.home"), "bin",
                File.separatorChar == '\\' ? "keytool.exe" : "keytool").toString();
        List<String> command = Arrays.asList(keytool, "-genkeypair", "-noprompt", "-alias", alias,
                "-keyalg", "RSA", "-keysize", String.valueOf(bits), "-sigalg", "SHA256withRSA",
                "-dname", "CN=localhost", "-ext", "SAN=dns:localhost", "-validity", "2",
                "-storetype", type, "-keystore", file.toString(),
                "-storepass", storePassword, "-keypass", keyPassword);
        Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        byte[] output = readAll(process.getInputStream());
        Assert.assertTrue(process.waitFor(60, TimeUnit.SECONDS), "keytool timed out");
        Assert.assertEquals(process.exitValue(), 0, "keytool failed: " + new String(output, StandardCharsets.UTF_8));
        return file;
    }

    static KeyStore loadKeyStore(Path file, String type, String storePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(type);
        try (InputStream in = Files.newInputStream(file)) {
            keyStore.load(in, storePassword.toCharArray());
        }
        return keyStore;
    }

    static KeyPair keyPair(Path file, String type, String alias, String storePassword, String keyPassword)
            throws Exception {
        KeyStore keyStore = loadKeyStore(file, type, storePassword);
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, keyPassword.toCharArray());
        return new KeyPair(keyStore.getCertificate(alias).getPublicKey(), privateKey);
    }

    static String pem(String label, byte[] der) {
        return "-----BEGIN " + label + "-----\n"
                + Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(der)
                + "\n-----END " + label + "-----\n";
    }

    /**
     * Encrypts a key with openssl, the way customers produce encrypted PEM keys. Skips the calling test when openssl
     * is not installed.
     *
     * @param scheme "-v2 aes-256-cbc" (PBES2, the openssl 3 default) or "-v1 PBE-SHA1-3DES" (legacy PBES1)
     */
    static Path opensslEncryptedPem(Path dir, PrivateKey key, String password, String scheme) throws Exception {
        Path plain = write(dir.resolve("plain-for-openssl.pem"), pem("PRIVATE KEY", key.getEncoded()));
        Path out = dir.resolve("encrypted-" + scheme.replaceAll("[^A-Za-z0-9]", "") + ".pem");
        List<String> command = new ArrayList<>(Arrays.asList("openssl", "pkcs8", "-topk8", "-in", plain.toString(),
                "-out", out.toString(), "-passout", "pass:" + password));
        command.addAll(Arrays.asList(scheme.split(" ")));
        Process process;
        try {
            process = new ProcessBuilder(command).redirectErrorStream(true).start();
        } catch (IOException e) {
            throw new SkipException("openssl is not installed");
        }
        byte[] output = readAll(process.getInputStream());
        Assert.assertTrue(process.waitFor(60, TimeUnit.SECONDS), "openssl timed out");
        Files.delete(plain);
        Assert.assertEquals(process.exitValue(), 0, "openssl failed: " + new String(output, StandardCharsets.UTF_8));
        return out;
    }

    static Path write(Path file, String content) throws Exception {
        Files.write(file, content.getBytes(StandardCharsets.US_ASCII));
        return file;
    }

    static String expectError(String expectedCode, ThrowingRunnable action) {
        try {
            action.run();
        } catch (JwtException e) {
            Assert.assertEquals(e.getCode(), expectedCode, "unexpected error code, message: " + e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            throw new AssertionError("expected JwtException " + expectedCode + " but got " + e, e);
        }
        throw new AssertionError("expected JwtException " + expectedCode);
    }

    private static byte[] readAll(InputStream in) throws Exception {
        try (InputStream stream = in) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = stream.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return out.toByteArray();
        }
    }

    interface ThrowingRunnable {
        void run() throws Exception;
    }
}
