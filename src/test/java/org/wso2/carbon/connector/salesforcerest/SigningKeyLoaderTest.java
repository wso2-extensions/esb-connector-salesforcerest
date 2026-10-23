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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.wso2.carbon.connector.salesforcerest.JwtException.ERROR_CONFIG;

public class SigningKeyLoaderTest {

    private Path dir;
    private KeyPair keyPair;

    @BeforeClass
    public void setUp() throws Exception {
        dir = Files.createTempDirectory("sf-jwt-keys");
        JwtTestUtils.generateKeyStore(dir.resolve("jwt.p12"), "PKCS12", "sfjwt", 2048, "store-pass", "store-pass");
        JwtTestUtils.generateKeyStore(dir.resolve("jwt.jks"), "JKS", "sfjwt", 2048, "store-pass", "key-pass");
        keyPair = JwtTestUtils.keyPair(dir.resolve("jwt.p12"), "PKCS12", "sfjwt", "store-pass", "store-pass");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        try (Stream<Path> files = Files.walk(dir)) {
            files.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        }
    }

    private PrivateKey load(String file, String type, String storePassword, String alias, String keyPassword)
            throws Exception {
        Path path = dir.resolve(file);
        return SigningKeyLoader.load(path, SigningKeyLoader.resolveType(type, path), storePassword, alias,
                keyPassword);
    }

    private static java.math.BigInteger modulus(PrivateKey key) {
        return ((RSAPrivateKey) key).getModulus();
    }

    @Test
    public void infersTypeFromExtensionOrUsesConfiguredType() throws Exception {
        Assert.assertEquals(SigningKeyLoader.resolveType(null, dir.resolve("a.jks")), "JKS");
        Assert.assertEquals(SigningKeyLoader.resolveType(null, dir.resolve("a.pem")), "PEM");
        Assert.assertEquals(SigningKeyLoader.resolveType(null, dir.resolve("a.key")), "PEM");
        Assert.assertEquals(SigningKeyLoader.resolveType(null, dir.resolve("a.p12")), "PKCS12");
        Assert.assertEquals(SigningKeyLoader.resolveType(null, dir.resolve("a.pfx")), "PKCS12");
        Assert.assertEquals(SigningKeyLoader.resolveType("pem", dir.resolve("a.p12")), "PEM");
        JwtTestUtils.expectError(ERROR_CONFIG, () -> SigningKeyLoader.resolveType("PFX", dir.resolve("a.p12")));
    }

    @Test
    public void loadsPkcs12WithAndWithoutAlias() throws Exception {
        Assert.assertEquals(modulus(load("jwt.p12", null, "store-pass", "sfjwt", null)), modulus(keyPair.getPrivate()));
        Assert.assertEquals(modulus(load("jwt.p12", "PKCS12", "store-pass", null, null)),
                modulus(keyPair.getPrivate()));
    }

    @Test
    public void loadsJksWithSeparateKeyPassword() throws Exception {
        Assert.assertNotNull(load("jwt.jks", null, "store-pass", "sfjwt", "key-pass"));
        // Without keyPassword the store password is used for the key, which is wrong for this keystore.
        String message = JwtTestUtils.expectError(ERROR_CONFIG,
                () -> load("jwt.jks", null, "store-pass", "sfjwt", null));
        Assert.assertTrue(message.contains("keyPassword"), message);
    }

    @Test
    public void reportsWrongKeystoreSettingsWithoutLeakingSecrets() {
        String message = JwtTestUtils.expectError(ERROR_CONFIG,
                () -> load("jwt.p12", null, "wrong-pass", "sfjwt", null));
        Assert.assertFalse(message.contains("wrong-pass"), message);
        JwtTestUtils.expectError(ERROR_CONFIG, () -> load("jwt.p12", null, null, "sfjwt", null));
        JwtTestUtils.expectError(ERROR_CONFIG, () -> load("jwt.p12", null, "store-pass", "other", null));
        JwtTestUtils.expectError(ERROR_CONFIG, () -> load("missing.p12", null, "store-pass", "sfjwt", null));
    }

    @Test
    public void rejectsUrlsAsKeyLocation() {
        JwtTestUtils.expectError(ERROR_CONFIG, () -> SigningKeyLoader.resolvePath("file:///etc/passwd"));
        JwtTestUtils.expectError(ERROR_CONFIG, () -> SigningKeyLoader.resolvePath("https://attacker.example/key.p12"));
    }

    @Test
    public void resolvesRelativePathsAgainstCarbonHome() throws Exception {
        String previous = System.getProperty("carbon.home");
        System.setProperty("carbon.home", dir.toString());
        try {
            Assert.assertEquals(SigningKeyLoader.resolvePath("keys/../jwt.p12"), dir.resolve("jwt.p12"));
        } finally {
            if (previous == null) {
                System.clearProperty("carbon.home");
            } else {
                System.setProperty("carbon.home", previous);
            }
        }
    }

    @Test
    public void loadsUnencryptedPemWithCertificateBlock() throws Exception {
        String pem = JwtTestUtils.pem("CERTIFICATE", new byte[]{1, 2, 3})
                + JwtTestUtils.pem("PRIVATE KEY", keyPair.getPrivate().getEncoded());
        JwtTestUtils.write(dir.resolve("plain.pem"), pem);
        Assert.assertEquals(modulus(load("plain.pem", null, null, null, null)), modulus(keyPair.getPrivate()));
    }

    @Test
    public void loadsEncryptedPemWithKeyPassword() throws Exception {
        // PBES2 / AES-256 is what openssl 3 produces by default; PBES1 / 3DES is the older openssl format.
        for (String scheme : new String[]{"-v2 aes-256-cbc", "-v1 PBE-SHA1-3DES"}) {
            String file = JwtTestUtils.opensslEncryptedPem(dir, keyPair.getPrivate(), "pem-pass", scheme)
                    .getFileName().toString();
            Assert.assertEquals(modulus(load(file, null, null, null, "pem-pass")), modulus(keyPair.getPrivate()),
                    scheme);
            // keyStorePassword is accepted as the key password when keyPassword is not set.
            Assert.assertNotNull(load(file, null, "pem-pass", null, null), scheme);
            JwtTestUtils.expectError(ERROR_CONFIG, () -> load(file, null, null, null, null));
            String message = JwtTestUtils.expectError(ERROR_CONFIG, () -> load(file, null, null, null, "wrong"));
            Assert.assertTrue(message.contains("keyPassword"), message);
        }
    }

    @Test
    public void rejectsUnsupportedPemContent() throws Exception {
        JwtTestUtils.write(dir.resolve("pkcs1.pem"),
                "-----BEGIN RSA PRIVATE KEY-----\nAAAA\n-----END RSA PRIVATE KEY-----\n");
        String message = JwtTestUtils.expectError(ERROR_CONFIG, () -> load("pkcs1.pem", null, null, null, null));
        Assert.assertTrue(message.contains("openssl pkcs8"), message);

        JwtTestUtils.write(dir.resolve("cert-only.pem"), JwtTestUtils.pem("CERTIFICATE", new byte[]{1, 2, 3}));
        JwtTestUtils.expectError(ERROR_CONFIG, () -> load("cert-only.pem", null, null, null, null));

        JwtTestUtils.write(dir.resolve("garbage.pem"),
                JwtTestUtils.pem("PRIVATE KEY", "TUlJU0VDUkVU".getBytes("US-ASCII")));
        message = JwtTestUtils.expectError(ERROR_CONFIG, () -> load("garbage.pem", null, null, null, null));
        Assert.assertFalse(message.contains("TUlJU0VDUkVU"), "key material leaked: " + message);
    }

    @Test
    public void rejectsShortRsaKeys() throws Exception {
        JwtTestUtils.generateKeyStore(dir.resolve("weak.p12"), "PKCS12", "weak", 1024, "store-pass", "store-pass");
        JwtTestUtils.expectError(ERROR_CONFIG, () -> load("weak.p12", null, "store-pass", "weak", null));
        JwtTestUtils.write(dir.resolve("weak.pem"),
                JwtTestUtils.pem("PRIVATE KEY", JwtTestUtils.rsa(1024).getPrivate().getEncoded()));
        JwtTestUtils.expectError(ERROR_CONFIG, () -> load("weak.pem", null, null, null, null));
    }

    @Test
    public void keyHandleIsRandomAndKeptWhenTheKeyFileChanges() throws Exception {
        SigningKeyLoader keys = new SigningKeyLoader();
        PrivateKey first = JwtTestUtils.sharedKeyPair().getPrivate();
        PrivateKey rotated = JwtTestUtils.rsa(2048).getPrivate();
        AtomicInteger loads = new AtomicInteger();
        // The internal id contains a hash of the passwords; it must never be what goes into the message context.
        String keyFileId = "internal-id-with-password-hash";
        String handle = keys.keyHandle(keyFileId, 1000L, () -> {
            loads.incrementAndGet();
            return first;
        });
        Assert.assertTrue(handle.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"), handle);
        Assert.assertFalse(handle.contains(keyFileId));
        // Same file version: no reload.
        Assert.assertEquals(keys.keyHandle(keyFileId, 1000L, () -> {
            loads.incrementAndGet();
            return first;
        }), handle);
        Assert.assertEquals(loads.get(), 1);
        // File replaced: same handle (one entry per key file), key reloaded.
        Assert.assertEquals(keys.keyHandle(keyFileId, 2000L, () -> {
            loads.incrementAndGet();
            return rotated;
        }), handle);
        Assert.assertEquals(loads.get(), 2);
        Assert.assertNotEquals(keys.keyHandle("another-key-file", 1000L, () -> first), handle);
    }

    @Test
    public void failedLoadIsNotRepeatedUntilTheKeyFileChanges() throws Exception {
        SigningKeyLoader keys = new SigningKeyLoader();
        AtomicInteger loads = new AtomicInteger();
        SigningKeyLoader.KeyLoader wrongPassword = () -> {
            loads.incrementAndGet();
            throw new JwtException(ERROR_CONFIG, "Wrong keystore password");
        };
        JwtTestUtils.expectError(ERROR_CONFIG, () -> keys.keyHandle("key-file", 1000L, wrongPassword));
        // Same file: the failure is reported without reading and decrypting the file again.
        String message = JwtTestUtils.expectError(ERROR_CONFIG,
                () -> keys.keyHandle("key-file", 1000L, wrongPassword));
        Assert.assertEquals(message, "Wrong keystore password");
        Assert.assertEquals(loads.get(), 1);
        // Replaced file: loaded again.
        PrivateKey key = JwtTestUtils.sharedKeyPair().getPrivate();
        Assert.assertNotNull(keys.keyHandle("key-file", 2000L, () -> key));
    }
}
