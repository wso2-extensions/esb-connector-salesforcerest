package org.wso2.carbon.salesforce.connector;

public interface TokenStore {
    Token get(String tokenKey);

    void add(String tokenKey, Token token);

    Token remove(String tokenKey);

    void clean();
}
