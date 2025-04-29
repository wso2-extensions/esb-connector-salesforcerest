package org.wso2.carbon.salesforce.connector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTokenStore implements TokenStore {
    private final Map<String, Token> TOKEN_MAP = new ConcurrentHashMap<>(2);

    @Override
    public Token get(String tokenKey) {
        return TOKEN_MAP.get(tokenKey);
    }

    @Override
    public void add(String tokenKey, Token token) {
        TOKEN_MAP.put(tokenKey, token);
    }

    @Override
    public Token remove(String tokenKey) {
        return TOKEN_MAP.remove(tokenKey);
    }

    @Override
    public void clean() {
        TOKEN_MAP.clear();
    }
}
