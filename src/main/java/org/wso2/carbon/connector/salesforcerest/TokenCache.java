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
import org.wso2.carbon.connector.salesforcerest.SalesforceTokenClient.Token;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.wso2.carbon.connector.salesforcerest.JwtException.ERROR_TOKEN;

/**
 * Access tokens of the JWT bearer connections, in memory only.
 * <p>
 * Each connection (by name) has one slot holding its current token and the identity the token was obtained for; the
 * token is reused by that identity, and a token for a different identity replaces it. Tokens are requested by one
 * thread at a time per connection, and a failed request is reported to further requests of the same identity for
 * {@link #FAILURE_HOLD_MILLIS}.
 */
final class TokenCache {

    private static final Log log = LogFactory.getLog(TokenCache.class);

    // How long a failed token request (and a failed key load, see SigningKeyLoader) is reported again without retrying.
    static final long FAILURE_HOLD_MILLIS = 5000;

    // Token state of each connection (by name). Each state object is also the lock for obtaining that connection's
    // token, so different connections never wait for each other.
    private final ConcurrentHashMap<String, ConnectionState> connections = new ConcurrentHashMap<>();

    /**
     * @return the connection's current token if it belongs to the given identity, otherwise null
     */
    Token knownToken(String connectionKey, String identity) {
        ConnectionState state = connections.get(connectionKey);
        ConnectionToken current = state == null ? null : state.token;
        return current != null && current.identity.equals(identity) ? current.token : null;
    }

    /**
     * Returns the connection's token for this identity, or requests a new one if there is none, the current one
     * belongs to another identity, or it was rejected. A new token replaces the connection's current token.
     * <ul>
     * <li>A failed request is reported to the requests of the same identity waiting for it and to new ones for
     * {@link #FAILURE_HOLD_MILLIS}; after that, one request tries again.</li>
     * <li>Salesforce returns the same token while its session is valid. If the new token is the rejected one, the
     * 401 was not caused by an expired session, so it is reported as a failed request.</li>
     * </ul>
     *
     * @param rejected the access token Salesforce rejected (HTTP 401), or null
     */
    Token obtainToken(String connectionKey, String identity, String rejected, TokenFetcher fetcher)
            throws JwtException {
        ConnectionState state = connections.computeIfAbsent(connectionKey, k -> new ConnectionState());
        rethrowRecentFailure(state, identity);
        synchronized (state) {
            ConnectionToken latest = state.token;
            if (latest != null && latest.identity.equals(identity) && !latest.token.accessToken.equals(rejected)) {
                return latest.token;
            }
            rethrowRecentFailure(state, identity);
            Token token;
            try {
                token = fetcher.fetch();
                if (token.accessToken.equals(rejected)) {
                    throw new JwtException(ERROR_TOKEN, "Salesforce rejected an access token whose session is still "
                            + "valid (HTTP 401). Check that apiUrl, if set, is the instance URL of the user's "
                            + "org, and that the org does not lock sessions to another IP address");
                }
            } catch (JwtException e) {
                // Logged once per failed request; requests rejected during the hold time are not logged again.
                log.error("Unable to obtain a Salesforce access token for connection '" + connectionKey + "': "
                        + e.getCode() + " " + e.getMessage());
                state.failure = new Failure(identity, e, System.nanoTime());
                throw e;
            }
            state.failure = null;
            state.token = new ConnectionToken(identity, token);
            return token;
        }
    }

    private static void rethrowRecentFailure(ConnectionState state, String identity) throws JwtException {
        Failure failure = state.failure;
        // nanoTime, unlike the wall clock, never jumps backwards, so the hold cannot be stretched by a clock change.
        if (failure != null && failure.identity.equals(identity)
                && System.nanoTime() - failure.atNanos < TimeUnit.MILLISECONDS.toNanos(FAILURE_HOLD_MILLIS)) {
            throw new JwtException(failure.code, failure.message);
        }
    }

    /**
     * Obtains a new token from Salesforce.
     */
    interface TokenFetcher {
        Token fetch() throws JwtException;
    }

    /**
     * Token state of one connection. Fields are written while holding the object's lock and are volatile, so they can
     * also be read without it.
     */
    private static final class ConnectionState {

        private volatile ConnectionToken token;
        private volatile Failure failure;
    }

    /**
     * A token and the identity it was obtained for.
     */
    private static final class ConnectionToken {

        private final String identity;
        private final Token token;

        ConnectionToken(String identity, Token token) {
            this.identity = identity;
            this.token = token;
        }
    }

    private static final class Failure {

        private final String identity;
        private final String code;
        private final String message;
        private final long atNanos;

        Failure(String identity, JwtException e, long atNanos) {
            this.identity = identity;
            this.code = e.getCode();
            this.message = e.getMessage();
            this.atNanos = atNanos;
        }
    }
}
