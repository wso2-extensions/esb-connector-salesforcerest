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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.salesforcerest.SalesforceTokenClient.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.wso2.carbon.connector.salesforcerest.JwtException.ERROR_TOKEN;

public class TokenCacheTest {

    private TokenCache cache;

    @BeforeMethod
    public void newCache() {
        cache = new TokenCache();
    }

    private static Token token(String accessToken) {
        return new Token(accessToken, "https://example.my.salesforce.com");
    }

    private String obtain(String connection, String identity, String rejected, TokenCache.TokenFetcher fetcher)
            throws JwtException {
        return cache.obtainToken(connection, identity, rejected, fetcher).accessToken;
    }

    @Test
    public void tokenIsReusedOnlyByItsIdentity() throws Exception {
        AtomicInteger fetches = new AtomicInteger();
        TokenCache.TokenFetcher fetcher = () -> token("token-" + fetches.incrementAndGet());
        String connection = "test-reuse";
        Assert.assertNull(cache.knownToken(connection, "identity-a"));
        Assert.assertEquals(obtain(connection, "identity-a", null, fetcher), "token-1");
        // The same identity reuses the token without asking Salesforce again.
        Assert.assertEquals(cache.knownToken(connection, "identity-a").accessToken, "token-1");
        Assert.assertEquals(obtain(connection, "identity-a", null, fetcher), "token-1");
        Assert.assertEquals(fetches.get(), 1);
        // Another identity on the same connection never gets it; its own token replaces it.
        Assert.assertNull(cache.knownToken(connection, "identity-b"));
        Assert.assertEquals(obtain(connection, "identity-b", null, fetcher), "token-2");
        Assert.assertNull(cache.knownToken(connection, "identity-a"));
    }

    @Test
    public void rejectedTokenIsRenewedOnce() throws Exception {
        String connection = "test-renew";
        AtomicInteger fetches = new AtomicInteger();
        TokenCache.TokenFetcher fetcher = () -> token("token-" + fetches.incrementAndGet());
        String rejected = obtain(connection, "id", null, fetcher);
        // The first request that reports the rejected token gets a new one ...
        String renewed = obtain(connection, "id", rejected, fetcher);
        Assert.assertNotEquals(renewed, rejected);
        // ... and later requests that were rejected with the same old token reuse it.
        Assert.assertEquals(obtain(connection, "id", rejected, fetcher), renewed);
        Assert.assertEquals(fetches.get(), 2);
    }

    @Test
    public void rejectionOfAValidSessionIsReportedWithoutRepeatedLogins() throws Exception {
        String connection = "test-valid-session";
        AtomicInteger fetches = new AtomicInteger();
        // Salesforce returns the same token while the session is valid.
        TokenCache.TokenFetcher sameSession = () -> {
            fetches.incrementAndGet();
            return token("token-1");
        };
        String rejected = obtain(connection, "id", null, sameSession);
        String message = JwtTestUtils.expectError(ERROR_TOKEN,
                () -> obtain(connection, "id", rejected, sameSession));
        Assert.assertTrue(message.contains("apiUrl"), message);
        // Further 401s within the hold time are reported without logging in again.
        JwtTestUtils.expectError(ERROR_TOKEN, () -> obtain(connection, "id", rejected, sameSession));
        Assert.assertEquals(fetches.get(), 2);
    }

    @Test
    public void waitingRequestsShareOneFailedTokenRequest() throws Exception {
        String connection = "test-outage";
        AtomicInteger attempts = new AtomicInteger();
        TokenCache.TokenFetcher unreachable = () -> {
            attempts.incrementAndGet();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            throw new JwtException(ERROR_TOKEN, "Unable to call the Salesforce token endpoint: timeout");
        };
        ExecutorService pool = Executors.newFixedThreadPool(10);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<String>> results = new ArrayList<>();
        long begin = System.currentTimeMillis();
        try {
            for (int i = 0; i < 10; i++) {
                results.add(pool.submit(() -> {
                    start.await();
                    return JwtTestUtils.expectError(ERROR_TOKEN,
                            () -> cache.obtainToken(connection, "id", null, unreachable));
                }));
            }
            start.countDown();
            for (Future<String> result : results) {
                Assert.assertTrue(result.get(10, TimeUnit.SECONDS).contains("timeout"));
            }
        } finally {
            pool.shutdownNow();
        }
        // One attempt for all ten requests, and nobody waited for ten timeouts in a row.
        Assert.assertEquals(attempts.get(), 1);
        Assert.assertTrue(System.currentTimeMillis() - begin < 2000, "requests were serialized");
    }

    @Test
    public void failureOfOneIdentityDoesNotBlockAnother() throws Exception {
        String connection = "test-failure-per-identity";
        JwtTestUtils.expectError(ERROR_TOKEN, () -> cache.obtainToken(connection, "identity-a", null, () -> {
            throw new JwtException(ERROR_TOKEN, "invalid_grant");
        }));
        Assert.assertEquals(obtain(connection, "identity-b", null, () -> token("token-b")), "token-b");
    }

    @Test
    public void failureIsRetriedAfterTheHoldTime() throws Exception {
        String connection = "test-retry-after-hold";
        AtomicInteger attempts = new AtomicInteger();
        TokenCache.TokenFetcher failing = () -> {
            attempts.incrementAndGet();
            throw new JwtException(ERROR_TOKEN, "invalid_grant");
        };
        JwtTestUtils.expectError(ERROR_TOKEN, () -> cache.obtainToken(connection, "id", null, failing));
        // Within the hold time the failure is reported without a new attempt.
        JwtTestUtils.expectError(ERROR_TOKEN, () -> cache.obtainToken(connection, "id", null, failing));
        Assert.assertEquals(attempts.get(), 1);
        Thread.sleep(TokenCache.FAILURE_HOLD_MILLIS + 200);
        Assert.assertEquals(obtain(connection, "id", null, () -> token("recovered")), "recovered");
    }
}
