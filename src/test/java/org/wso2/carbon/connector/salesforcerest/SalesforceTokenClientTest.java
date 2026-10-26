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

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Arrays;
import java.util.Base64;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import static org.wso2.carbon.connector.salesforcerest.JwtException.ERROR_CONFIG;
import static org.wso2.carbon.connector.salesforcerest.JwtException.ERROR_TOKEN;

public class SalesforceTokenClientTest {

    private static final String ASSERTION = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ4In0.c2ln";
    private HttpsTestServer server;
    private URL tokenUrl;
    private SSLSocketFactory jvmDefaultSocketFactory;

    @BeforeClass
    public void setUp() throws Exception {
        server = new HttpsTestServer();
        tokenUrl = server.url("/services/oauth2/token");
        // The connector uses the JVM default trust (in MI: client-truststore.jks). For these tests the default also
        // trusts the test server's self-signed certificate; host name verification stays on.
        jvmDefaultSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
        HttpsURLConnection.setDefaultSSLSocketFactory(server.clientSocketFactory());
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        HttpsURLConnection.setDefaultSSLSocketFactory(jvmDefaultSocketFactory);
        server.close();
    }

    @BeforeMethod
    public void reset() {
        server.requests.set(0);
    }

    private SalesforceTokenClient.Token exchange(URL url, int timeout) throws Exception {
        return SalesforceTokenClient.exchange(url, ASSERTION, timeout);
    }

    @Test
    public void buildsVerifiableRs256Assertion() throws Exception {
        KeyPair keyPair = JwtTestUtils.sharedKeyPair();
        // A value with quotes must stay a value: it cannot add or change claims.
        String subject = "user@example.com\",\"sub\":\"admin@example.com";
        String jwt = SalesforceTokenClient.buildAssertion("consumer-key", subject,
                "https://login.salesforce.com", 1_700_000_180L, keyPair.getPrivate());

        String[] parts = jwt.split("\\.");
        Assert.assertEquals(parts.length, 3);
        Assert.assertEquals(new JSONObject(decode(parts[0])).getString("alg"), "RS256");
        JSONObject claims = new JSONObject(decode(parts[1]));
        Assert.assertEquals(claims.getString("iss"), "consumer-key");
        Assert.assertEquals(claims.getString("sub"), subject);
        Assert.assertEquals(claims.getString("aud"), "https://login.salesforce.com");
        Assert.assertEquals(claims.getLong("exp"), 1_700_000_180L);
        Assert.assertEquals(claims.length(), 4);

        Signature verifier = Signature.getInstance("SHA256withRSA");
        verifier.initVerify(keyPair.getPublic());
        verifier.update((parts[0] + "." + parts[1]).getBytes(StandardCharsets.US_ASCII));
        Assert.assertTrue(verifier.verify(Base64.getUrlDecoder().decode(parts[2])), "signature does not verify");
    }

    @Test
    public void derivesAudienceFromTokenEndpoint() {
        Assert.assertEquals(SalesforceTokenClient.audience("login.salesforce.com"),
                SalesforceTokenClient.PRODUCTION_AUDIENCE);
        Assert.assertEquals(SalesforceTokenClient.audience("acme.my.salesforce.com"),
                SalesforceTokenClient.PRODUCTION_AUDIENCE);
        Assert.assertEquals(SalesforceTokenClient.audience("TEST.salesforce.com"),
                SalesforceTokenClient.SANDBOX_AUDIENCE);
        Assert.assertEquals(SalesforceTokenClient.audience("acme--uat.sandbox.my.salesforce.com"),
                SalesforceTokenClient.SANDBOX_AUDIENCE);
        Assert.assertEquals(SalesforceTokenClient.audience("power-ruby-1234-dev-ed.scratch.my.salesforce.com"),
                SalesforceTokenClient.SANDBOX_AUDIENCE);
    }

    @Test
    public void neverShowsCredentialsFromTheEndpointUrl() throws Exception {
        String message = JwtTestUtils.expectError(ERROR_CONFIG, () -> exchange(new URL("https://admin:s3cret@"
                + tokenUrl.getHost() + ":" + tokenUrl.getPort() + tokenUrl.getPath()), 5000));
        Assert.assertFalse(message.contains("s3cret") || message.contains("admin"), message);
        Assert.assertEquals(server.requests.get(), 0);
        // Transport errors name the endpoint by host and port only.
        message = JwtTestUtils.expectError(ERROR_TOKEN,
                () -> exchange(new URL("https://localhost:1/services/oauth2/token"), 2000));
        Assert.assertTrue(message.contains("https://localhost:1:"), message);
    }

    @Test
    public void postsFormEncodedGrantAndReturnsToken() throws Exception {
        server.handler = (exchange, body) -> HttpsTestServer.respond(exchange, 200,
                "{\"access_token\":\"00DMOCK!AQ.token\",\"instance_url\":\"https://acme.my.salesforce.com\"}");
        SalesforceTokenClient.Token token = exchange(tokenUrl, 5000);
        Assert.assertEquals(token.accessToken, "00DMOCK!AQ.token");
        Assert.assertEquals(token.instanceUrl, "https://acme.my.salesforce.com");
        Assert.assertEquals(server.lastMethod, "POST");
        Assert.assertTrue(server.lastContentType.startsWith("application/x-www-form-urlencoded"));
        Assert.assertEquals(URLDecoder.decode(server.lastBody, "UTF-8"),
                "grant_type=" + SalesforceTokenClient.GRANT_TYPE + "&assertion=" + ASSERTION);
    }

    @Test
    public void usesTheConfiguredAudienceOrDerivesIt() throws Exception {
        server.handler = (exchange, body) -> HttpsTestServer.respond(exchange, 200,
                "{\"access_token\":\"token\",\"instance_url\":\"https://acme.my.salesforce.com\"}");
        PrivateKey key = JwtTestUtils.sharedKeyPair().getPrivate();
        SalesforceTokenClient.requestToken(tokenUrl, "client", "user", "https://audience.example.com", key, 5000);
        Assert.assertEquals(sentClaims().getString("aud"), "https://audience.example.com");
        // Without a configured audience it is derived from the token endpoint host (localhost: production).
        SalesforceTokenClient.requestToken(tokenUrl, "client", "user", null, key, 5000);
        Assert.assertEquals(sentClaims().getString("aud"), SalesforceTokenClient.PRODUCTION_AUDIENCE);
    }

    private JSONObject sentClaims() throws Exception {
        String assertion = URLDecoder.decode(server.lastBody, "UTF-8").split("&assertion=")[1];
        return new JSONObject(decode(assertion.split("\\.")[1]));
    }

    @Test
    public void surfacesSanitizedSalesforceErrors() {
        server.handler = (exchange, body) -> HttpsTestServer.respond(exchange, 400,
                "{\"error\":\"invalid_grant\",\"error_description\":\"user hasn't approved this consumer"
                        + "\\r\\nFAKE-LOG-LINE <script>\"}");
        String message = JwtTestUtils.expectError(ERROR_TOKEN, () -> exchange(tokenUrl, 5000));
        Assert.assertTrue(message.contains("HTTP 400"), message);
        Assert.assertTrue(message.contains("invalid_grant - user hasn't approved this consumer"), message);
        Assert.assertFalse(message.contains("\n") || message.contains("<") || message.contains(ASSERTION), message);
        // Salesforce text is also truncated.
        char[] longText = new char[1000];
        Arrays.fill(longText, 'a');
        Assert.assertEquals(SalesforceTokenClient.sanitize(new String(longText)).length(), 256);
    }

    @Test
    public void rejectsUnusableResponses() {
        server.handler = (exchange, body) -> HttpsTestServer.respond(exchange, 503, "<html>maintenance</html>");
        JwtTestUtils.expectError(ERROR_TOKEN, () -> exchange(tokenUrl, 5000));
        server.handler = (exchange, body) -> HttpsTestServer.respond(exchange, 200, "{}");
        JwtTestUtils.expectError(ERROR_TOKEN, () -> exchange(tokenUrl, 5000));
        // A token with CR/LF would allow header injection in the Authorization header.
        server.handler = (exchange, body) -> HttpsTestServer.respond(exchange, 200,
                "{\"access_token\":\"bad\\r\\nX-Injected: 1\"}");
        JwtTestUtils.expectError(ERROR_TOKEN, () -> exchange(tokenUrl, 5000));
        // The instance URL becomes the API URL, so it must be an https URL with only a host.
        for (String instanceUrl : new String[]{null, "http://acme.my.salesforce.com",
                "https://acme.my.salesforce.com/services?x=1"}) {
            String response = "{\"access_token\":\"token\""
                    + (instanceUrl == null ? "" : ",\"instance_url\":\"" + instanceUrl + "\"") + "}";
            server.handler = (exchange, body) -> HttpsTestServer.respond(exchange, 200, response);
            JwtTestUtils.expectError(ERROR_TOKEN, () -> exchange(tokenUrl, 5000));
        }
    }

    @Test
    public void acceptsOnlyHttpsUrlsWithAHost() {
        Assert.assertEquals(SalesforceTokenClient.origin("https://LOGIN.salesforce.com/"),
                "https://login.salesforce.com");
        Assert.assertEquals(SalesforceTokenClient.origin("https://localhost:8443"), "https://localhost:8443");
        for (String url : new String[]{null, "", "login.salesforce.com", "http://login.salesforce.com",
                "https://login.salesforce.com/services/oauth2/token", "https://user:pass@login.salesforce.com",
                "https://login.salesforce.com?x=1", "https://login.salesforce.com/#x",
                "https://login .salesforce.com"}) {
            Assert.assertNull(SalesforceTokenClient.origin(url), url);
        }
    }

    @Test
    public void doesNotFollowRedirects() {
        server.handler = (exchange, body) -> {
            exchange.getResponseHeaders().set("Location", "https://attacker.example/collect");
            exchange.sendResponseHeaders(302, -1);
        };
        JwtTestUtils.expectError(ERROR_TOKEN, () -> exchange(tokenUrl, 5000));
        Assert.assertEquals(server.requests.get(), 1);
    }

    @Test
    public void capsResponseSize() {
        char[] large = new char[SalesforceTokenClient.MAX_RESPONSE_BYTES + 10];
        Arrays.fill(large, 'a');
        server.handler = (exchange, body) -> HttpsTestServer.respond(exchange, 200,
                "{\"access_token\":\"" + new String(large) + "\"}");
        JwtTestUtils.expectError(ERROR_TOKEN, () -> exchange(tokenUrl, 5000));
    }

    @Test
    public void timesOut() {
        server.handler = (exchange, body) -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        JwtTestUtils.expectError(ERROR_TOKEN, () -> exchange(tokenUrl, 300));
    }

    @Test
    public void enforcesTls() throws Exception {
        // Plain http is refused before any connection is made.
        JwtTestUtils.expectError(ERROR_CONFIG,
                () -> exchange(new URL("http://localhost:1/services/oauth2/token"), 5000));
        // Untrusted certificate (JVM default trust store) and certificate for another host name.
        HttpsURLConnection.setDefaultSSLSocketFactory(jvmDefaultSocketFactory);
        try {
            JwtTestUtils.expectError(ERROR_TOKEN, () -> exchange(tokenUrl, 5000));
        } finally {
            HttpsURLConnection.setDefaultSSLSocketFactory(server.clientSocketFactory());
        }
        JwtTestUtils.expectError(ERROR_TOKEN,
                () -> exchange(new URL(tokenUrl.toString().replace("localhost", "127.0.0.1")), 5000));
        Assert.assertEquals(server.requests.get(), 0);
    }

    private static String decode(String part) {
        return new String(Base64.getUrlDecoder().decode(part), StandardCharsets.UTF_8);
    }
}
