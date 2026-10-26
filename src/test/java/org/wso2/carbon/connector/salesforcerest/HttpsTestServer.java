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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * In-JVM HTTPS server on localhost with a generated certificate, plus a client socket factory that trusts it
 * (with normal hostname verification).
 */
final class HttpsTestServer implements AutoCloseable {

    interface Handler {
        void handle(HttpExchange exchange, String requestBody) throws IOException;
    }

    private final HttpsServer server;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final SSLSocketFactory clientSocketFactory;
    final AtomicInteger requests = new AtomicInteger();
    volatile Handler handler = (exchange, body) -> respond(exchange, 404, "{}");
    volatile String lastMethod;
    volatile String lastContentType;
    volatile String lastBody;

    HttpsTestServer() throws Exception {
        KeyStore serverStore = JwtTestUtils.loadKeyStore(JwtTestUtils.sharedKeyStore(), "PKCS12",
                JwtTestUtils.PASSWORD);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(serverStore, JwtTestUtils.PASSWORD.toCharArray());
        SSLContext serverContext = SSLContext.getInstance("TLS");
        serverContext.init(kmf.getKeyManagers(), null, null);

        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        trustStore.load(null, null);
        trustStore.setCertificateEntry("server", serverStore.getCertificate(JwtTestUtils.ALIAS));
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        SSLContext clientContext = SSLContext.getInstance("TLS");
        clientContext.init(null, tmf.getTrustManagers(), null);
        clientSocketFactory = clientContext.getSocketFactory();

        server = HttpsServer.create(new InetSocketAddress("localhost", 0), 0);
        server.setHttpsConfigurator(new HttpsConfigurator(serverContext));
        server.createContext("/", exchange -> {
            requests.incrementAndGet();
            lastMethod = exchange.getRequestMethod();
            lastContentType = exchange.getRequestHeaders().getFirst("Content-Type");
            lastBody = read(exchange.getRequestBody());
            try {
                handler.handle(exchange, lastBody);
            } finally {
                exchange.close();
            }
        });
        server.setExecutor(executor);
        server.start();
    }

    String origin() {
        return "https://localhost:" + server.getAddress().getPort();
    }

    URL url(String path) throws Exception {
        return new URL(origin() + path);
    }

    SSLSocketFactory clientSocketFactory() {
        return clientSocketFactory;
    }

    static void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static String read(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    @Override
    public void close() {
        server.stop(0);
        executor.shutdownNow();
    }
}
