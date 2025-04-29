package org.wso2.carbon.salesforce.connector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TokenManager {
    private static final Log log = LogFactory.getLog(TokenManager.class);
    private static final TokenStore TOKEN_STORE = new InMemoryTokenStore();

    private TokenManager() {
    }

    public static void addToken(String resourceKey, Token token) {
        TOKEN_STORE.add(resourceKey, token);
    }

    public static Token getToken(String resourceKey) {
        return TOKEN_STORE.get(resourceKey);
    }

    public static void removeToken(String resourceKey) {
        TOKEN_STORE.remove(resourceKey);
    }

    public static void clean() {
        TOKEN_STORE.clean();
        if (log.isDebugEnabled()) {
            log.debug("Token map cleaned.");
        }
    }
}
