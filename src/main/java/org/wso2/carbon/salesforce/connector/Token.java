package org.wso2.carbon.salesforce.connector;

public class Token {
    private final String accessToken;
    private final Long createTimestamp;
    private final Long expireIn;

    public Token(String accessToken, Long createTimestamp, Long expireIn) {
        this.accessToken = accessToken;
        this.createTimestamp = createTimestamp;
        this.expireIn = expireIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Long getExpireIn() {
        return expireIn;
    }

    public Long getCreateTimestamp() {
        return createTimestamp;
    }

    public boolean isActive() {
        long curTimeInMillis = System.currentTimeMillis();
        return (curTimeInMillis - createTimestamp) < expireIn;
    }
}
