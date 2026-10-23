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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.SynapseException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.salesforcerest.SalesforceTokenClient.Token;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;

import static org.wso2.carbon.connector.salesforcerest.JwtException.ERROR_CONFIG;

/**
 * OAuth 2.0 JWT bearer flow (RFC 7523) for the Salesforce REST connector (authType=JWT). Class mediator used by the
 * connector templates:
 * <ul>
 * <li>From salesforcerest.init: validates the connection, loads the signing key ({@link SigningKeyLoader}) and sets
 * uri.var.accessToken to the connection's current token ({@link TokenCache}), or obtains a new one
 * ({@link SalesforceTokenClient}). Sets uri.var.apiUrl to the configured instanceUrl, or else to the instance URL
 * returned with the token; apiUrl is not used.</li>
 * <li>From salesforcerest.callWithRetry with property renew=true: obtains a new token after a 401.</li>
 * </ul>
 * Tokens are kept in memory only. Passwords and keys are never put into message context properties, and no secret
 * is logged.
 */
public class JwtBearerTokenGenerator extends AbstractConnector {

    private static final Log log = LogFactory.getLog(JwtBearerTokenGenerator.class);
    // The connection settings resolved by init, used again when the token is renewed for the same message.
    private static final String CONNECTION_PROPERTY = "_SALESFORCE_JWT_CONNECTION_";
    private static final String ACCESS_TOKEN_PROPERTY = "uri.var.accessToken";
    private static final String API_URL_PROPERTY = "uri.var.apiUrl";
    private static final String TOKEN_PATH = "/services/oauth2/token";
    private static final int DEFAULT_TIMEOUT_MILLIS = 3000;

    // Shared by all connections and threads in this MI instance.
    private static final SigningKeyLoader KEYS = new SigningKeyLoader();
    private static final TokenCache TOKENS = new TokenCache();

    private boolean renew;

    /**
     * Set to true (class mediator property) when called after Salesforce rejected the current token.
     */
    public void setRenew(String renew) {
        this.renew = Boolean.parseBoolean(renew);
    }

    @Override
    public void connect(MessageContext messageContext) {
        try {
            if (renew) {
                renewToken(messageContext);
            } else {
                initialize(messageContext);
            }
        } catch (JwtException e) {
            // Failed token requests are logged once by TokenCache; configuration errors are logged here.
            if (ERROR_CONFIG.equals(e.getCode())) {
                log.error(e.getCode() + ": " + e.getMessage());
            }
            messageContext.setProperty(SynapseConstants.ERROR_CODE, e.getCode());
            messageContext.setProperty(SynapseConstants.ERROR_MESSAGE, e.getMessage());
            throw new SynapseException(e.getMessage());
        }
    }

    private void initialize(MessageContext messageContext) throws JwtException {
        for (String other : new String[]{"refreshToken", "password", "accessToken"}) {
            if (parameter(messageContext, other) != null) {
                throw new JwtException(ERROR_CONFIG, other + " must not be set when authType is JWT");
            }
        }
        String name = required(messageContext, "name");
        String clientId = required(messageContext, "clientId");
        String username = required(messageContext, "username");
        // Set by init, with https://login.salesforce.com as the default.
        String tokenEndpoint = SalesforceTokenClient.origin(
                text(messageContext.getProperty("uri.var.tokenEndpointHostname")));
        if (tokenEndpoint == null) {
            throw new JwtException(ERROR_CONFIG, "tokenEndpointHostname must be an https URL with only a host, for "
                    + "example https://login.salesforce.com");
        }
        Path path = SigningKeyLoader.resolvePath(required(messageContext, "keyStorePath"));
        String keyStoreType = SigningKeyLoader.resolveType(parameter(messageContext, "keyStoreType"), path);
        String keyStorePassword = parameter(messageContext, "keyStorePassword");
        String keyPassword = parameter(messageContext, "keyPassword");
        String keyAlias = parameter(messageContext, "keyAlias");

        long lastModified;
        try {
            lastModified = Files.getLastModifiedTime(path).toMillis();
        } catch (IOException e) {
            throw new JwtException(ERROR_CONFIG, "The key file '" + path.getFileName() + "' does not exist");
        }
        String keyFileId = fingerprint(path.toString(), keyStoreType, keyAlias, keyStorePassword, keyPassword);
        String keyHandle = KEYS.keyHandle(keyFileId, lastModified,
                () -> SigningKeyLoader.load(path, keyStoreType, keyStorePassword, keyAlias, keyPassword));

        Connection connection = new Connection(name, clientId, username, tokenEndpoint,
                parameter(messageContext, "instanceUrl"), keyHandle,
                identity(clientId, username, tokenEndpoint, KEYS.key(keyHandle)));
        messageContext.setProperty(CONNECTION_PROPERTY, connection);
        Token token = TOKENS.knownToken(name, connection.identity);
        if (token == null) {
            token = TOKENS.obtainToken(name, connection.identity, null,
                    () -> requestToken(connection, timeout(messageContext)));
        }
        setToken(messageContext, connection, token);
    }

    private void renewToken(MessageContext messageContext) throws JwtException {
        Object value = messageContext.getProperty(CONNECTION_PROPERTY);
        if (!(value instanceof Connection)) {
            throw new JwtException(ERROR_CONFIG, "The JWT bearer connection is not initialized; call "
                    + "salesforcerest.init with authType JWT first");
        }
        Connection connection = (Connection) value;
        String rejected = text(messageContext.getProperty(ACCESS_TOKEN_PROPERTY));
        setToken(messageContext, connection, TOKENS.obtainToken(connection.name, connection.identity, rejected,
                () -> requestToken(connection, timeout(messageContext))));
    }

    private static void setToken(MessageContext messageContext, Connection connection, Token token) {
        messageContext.setProperty(ACCESS_TOKEN_PROPERTY, token.accessToken);
        messageContext.setProperty(API_URL_PROPERTY,
                connection.instanceUrl != null ? connection.instanceUrl : token.instanceUrl);
    }

    /**
     * Fingerprint of the identity a token belongs to: consumer key, username, token endpoint and signing key. A
     * connection's token is only reused when it was obtained for the same identity.
     */
    static String identity(String clientId, String username, String tokenEndpoint, PrivateKey key) {
        // The RSA modulus identifies the key itself, independent of the file it is stored in.
        String keyIdentity = key instanceof RSAPrivateKey
                ? ((RSAPrivateKey) key).getModulus().toString(16) : String.valueOf(key.getAlgorithm());
        return fingerprint(clientId, username, tokenEndpoint, keyIdentity);
    }

    private static Token requestToken(Connection connection, int timeout) throws JwtException {
        URL tokenUrl;
        try {
            tokenUrl = new URL(connection.tokenEndpoint + TOKEN_PATH);
        } catch (MalformedURLException e) {
            throw new JwtException(ERROR_CONFIG, "tokenEndpointHostname is not a valid URL");
        }
        Token token = SalesforceTokenClient.requestToken(tokenUrl, connection.clientId, connection.username,
                KEYS.key(connection.keyHandle), timeout);
        if (connection.instanceUrl != null
                && !token.instanceUrl.equals(SalesforceTokenClient.origin(connection.instanceUrl))) {
            log.warn("The instanceUrl of connection '" + connection.name + "' differs from the instance URL "
                    + "returned by Salesforce (" + token.instanceUrl + "); API calls are sent to instanceUrl");
        }
        if (log.isDebugEnabled()) {
            log.debug("Obtained a Salesforce access token with the JWT bearer flow for connection '"
                    + connection.name + "'");
        }
        return token;
    }

    private String required(MessageContext messageContext, String name) throws JwtException {
        String value = parameter(messageContext, name);
        if (value == null) {
            throw new JwtException(ERROR_CONFIG, name + " is required when authType is JWT");
        }
        return value;
    }

    private String parameter(MessageContext messageContext, String name) {
        return text(getParameter(messageContext, name));
    }

    private static int timeout(MessageContext messageContext) {
        try {
            int timeout = Integer.parseInt(text(messageContext.getProperty("timeout")));
            return timeout > 0 ? timeout : DEFAULT_TIMEOUT_MILLIS;
        } catch (NumberFormatException e) {
            return DEFAULT_TIMEOUT_MILLIS;
        }
    }

    private static String text(Object value) {
        String text = value == null ? "" : value.toString().trim();
        return text.isEmpty() ? null : text;
    }

    /**
     * SHA-256 of the values; each value is hashed separately, so different values can never be joined into the same
     * input.
     */
    private static String fingerprint(String... values) {
        StringBuilder hashes = new StringBuilder();
        for (String value : values) {
            hashes.append(sha256Hex(String.valueOf(value)));
        }
        return sha256Hex(hashes.toString());
    }

    private static String sha256Hex(String value) {
        try {
            StringBuilder sb = new StringBuilder();
            for (byte b : MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * The settings of a JWT bearer connection, resolved once by init. No passwords or keys: the key is referenced by
     * its random handle in {@link SigningKeyLoader}.
     */
    private static final class Connection {

        private final String name;
        private final String clientId;
        private final String username;
        private final String tokenEndpoint;
        private final String instanceUrl;
        private final String keyHandle;
        private final String identity;

        Connection(String name, String clientId, String username, String tokenEndpoint, String instanceUrl,
                   String keyHandle, String identity) {
            this.name = name;
            this.clientId = clientId;
            this.username = username;
            this.tokenEndpoint = tokenEndpoint;
            this.instanceUrl = instanceUrl;
            this.keyHandle = keyHandle;
            this.identity = identity;
        }

        @Override
        public String toString() {
            return "SalesforceJwtConnection[" + name + "]";
        }
    }
}
