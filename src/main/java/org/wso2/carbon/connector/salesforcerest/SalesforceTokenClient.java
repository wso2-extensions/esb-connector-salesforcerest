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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;
import java.util.Locale;
import javax.net.ssl.HttpsURLConnection;

import static org.wso2.carbon.connector.salesforcerest.JwtException.ERROR_CONFIG;
import static org.wso2.carbon.connector.salesforcerest.JwtException.ERROR_TOKEN;

/**
 * Obtains an access token from the Salesforce token endpoint with the OAuth 2.0 JWT bearer grant (RFC 7523): builds
 * the signed assertion and POSTs it over HTTPS, using the JVM default trust (in MI: client-truststore.jks).
 */
final class SalesforceTokenClient {

    static final String GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer";
    static final String PRODUCTION_AUDIENCE = "https://login.salesforce.com";
    static final String SANDBOX_AUDIENCE = "https://test.salesforce.com";
    // Salesforce requires the assertion to expire within 3 minutes.
    static final int ASSERTION_LIFETIME_SECONDS = 180;
    static final int MAX_RESPONSE_BYTES = 64 * 1024;

    private SalesforceTokenClient() {
    }

    /**
     * Signs an assertion for the user and exchanges it for an access token.
     */
    static Token requestToken(URL tokenUrl, String clientId, String username, PrivateKey key, int timeout)
            throws JwtException {
        String assertion = buildAssertion(clientId, username, audience(tokenUrl.getHost()),
                System.currentTimeMillis() / 1000 + ASSERTION_LIFETIME_SECONDS, key);
        return exchange(tokenUrl, assertion, timeout);
    }

    /**
     * POSTs the assertion to the token endpoint and returns the access token and the instance URL of the org.
     */
    static Token exchange(URL tokenUrl, String assertion, int timeout) throws JwtException {
        if (!"https".equalsIgnoreCase(tokenUrl.getProtocol())) {
            throw new JwtException(ERROR_CONFIG, "tokenEndpointHostname must be an https:// URL");
        }
        if (tokenUrl.getUserInfo() != null) {
            throw new JwtException(ERROR_CONFIG, "tokenEndpointHostname must not contain credentials");
        }
        // Host and port only: error messages never include the user info part of the URL.
        String endpoint = "https://" + tokenUrl.getHost() + (tokenUrl.getPort() == -1 ? "" : ":" + tokenUrl.getPort());
        int status;
        String body;
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) tokenUrl.openConnection();
            // The JDK verifies the host name itself and only consults this verifier when that check fails.
            connection.setHostnameVerifier((host, session) -> false);
            connection.setInstanceFollowRedirects(false);
            connection.setUseCaches(false);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept", "application/json");
            byte[] form = ("grant_type=" + urlEncode(GRANT_TYPE) + "&assertion=" + urlEncode(assertion))
                    .getBytes(StandardCharsets.US_ASCII);
            connection.setFixedLengthStreamingMode(form.length);
            try (OutputStream out = connection.getOutputStream()) {
                out.write(form);
            }
            status = connection.getResponseCode();
            body = read(status >= 200 && status < 300 ? connection.getInputStream() : connection.getErrorStream());
        } catch (IOException e) {
            throw new JwtException(ERROR_TOKEN, "Unable to call the Salesforce token endpoint " + endpoint + ": "
                    + e.getClass().getSimpleName() + " " + sanitize(e.getMessage()));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        JSONObject json = parse(body);
        if (status != 200) {
            String reason = json == null ? "" : (": " + sanitize(json.optString("error"))
                    + " - " + sanitize(json.optString("error_description")));
            throw new JwtException(ERROR_TOKEN,
                    "The Salesforce token endpoint rejected the JWT bearer request (HTTP " + status + ")" + reason);
        }
        String token = json == null ? "" : json.optString("access_token");
        if (!isVisibleAscii(token)) {
            throw new JwtException(ERROR_TOKEN, "The Salesforce token response does not contain a valid access_token");
        }
        String instanceUrl = origin(json.optString("instance_url"));
        if (instanceUrl == null) {
            throw new JwtException(ERROR_TOKEN, "The Salesforce token response does not contain a valid instance_url");
        }
        return new Token(token, instanceUrl);
    }

    /**
     * @return https://host[:port] if the URL is an https URL with nothing but a host (and port), otherwise null
     */
    static String origin(String url) {
        if (!isVisibleAscii(url)) {
            return null;
        }
        try {
            URL parsed = new URL(url);
            String path = parsed.getPath();
            if (!"https".equalsIgnoreCase(parsed.getProtocol()) || parsed.getHost().isEmpty()
                    || parsed.getUserInfo() != null || parsed.getQuery() != null || parsed.getRef() != null
                    || !(path.isEmpty() || "/".equals(path))) {
                return null;
            }
            return "https://" + parsed.getHost().toLowerCase(Locale.ROOT)
                    + (parsed.getPort() == -1 ? "" : ":" + parsed.getPort());
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Builds the signed JWT bearer assertion (RS256).
     */
    static String buildAssertion(String issuer, String subject, String audience, long expiresAt, PrivateKey key)
            throws JwtException {
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String header = encoder.encodeToString("{\"alg\":\"RS256\"}".getBytes(StandardCharsets.UTF_8));
        // JSONObject.quote escapes the values, so configuration values cannot change the structure of the claims.
        String claims = "{\"iss\":" + JSONObject.quote(issuer) + ",\"sub\":" + JSONObject.quote(subject)
                + ",\"aud\":" + JSONObject.quote(audience) + ",\"exp\":" + expiresAt + "}";
        String signingInput = header + "." + encoder.encodeToString(claims.getBytes(StandardCharsets.UTF_8));
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(key);
            signature.update(signingInput.getBytes(StandardCharsets.US_ASCII));
            return signingInput + "." + encoder.encodeToString(signature.sign());
        } catch (GeneralSecurityException e) {
            throw new JwtException(ERROR_CONFIG, "Unable to sign the JWT assertion with the configured key");
        }
    }

    /**
     * Returns the aud claim for the token endpoint host.
     */
    static String audience(String tokenHost) {
        String host = tokenHost.toLowerCase(Locale.ROOT);
        // Sandboxes and scratch orgs log in through test.salesforce.com.
        return "test.salesforce.com".equals(host) || host.endsWith(".sandbox.my.salesforce.com")
                || host.endsWith(".scratch.my.salesforce.com") ? SANDBOX_AUDIENCE : PRODUCTION_AUDIENCE;
    }

    /**
     * Keeps external text (Salesforce error messages) safe for logs and fault messages: printable characters
     * from a conservative set only (no CR/LF, markup or quotes), truncated.
     */
    static String sanitize(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (sb.length() >= 256) {
                break;
            }
            if ((c < 0x80 && Character.isLetterOrDigit(c)) || " .,:;_-'()/=@!?+".indexOf(c) >= 0) {
                sb.append(c);
            }
        }
        return sb.toString().trim();
    }

    private static JSONObject parse(String body) {
        try {
            return body == null || body.isEmpty() ? null : new JSONObject(body);
        } catch (Exception e) {
            // Not JSON (org.json's JSONException is checked in some versions and unchecked in others).
            return null;
        }
    }

    /**
     * Reads the response body, failing if it is larger than MAX_RESPONSE_BYTES.
     */
    private static String read(InputStream in) throws IOException {
        if (in == null) {
            return "";
        }
        try (InputStream stream = in) {
            ByteArrayOutputStream body = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int read;
            while ((read = stream.read(buffer)) != -1) {
                body.write(buffer, 0, read);
                if (body.size() > MAX_RESPONSE_BYTES) {
                    throw new IOException("Token endpoint response exceeds " + MAX_RESPONSE_BYTES + " bytes");
                }
            }
            return new String(body.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    private static boolean isVisibleAscii(String value) {
        if (value == null || value.isEmpty() || value.length() > 8192) {
            return false;
        }
        for (char c : value.toCharArray()) {
            if (c < 0x21 || c > 0x7e) {
                return false;
            }
        }
        return true;
    }

    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * An access token and the instance URL of the org it belongs to.
     */
    static final class Token {

        final String accessToken;
        final String instanceUrl;

        Token(String accessToken, String instanceUrl) {
            this.accessToken = accessToken;
            this.instanceUrl = instanceUrl;
        }

        @Override
        public String toString() {
            // Never prints the access token.
            return "Token[" + instanceUrl + "]";
        }
    }
}
