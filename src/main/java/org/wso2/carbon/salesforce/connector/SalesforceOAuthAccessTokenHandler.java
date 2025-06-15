/*
 *  Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.salesforce.connector;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.util.ConnectorUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SalesforceOAuthAccessTokenHandler extends AbstractConnector {
    private static final JsonParser parser = new JsonParser();

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {

        String connectionName = (String) ConnectorUtils.lookupTemplateParamater(messageContext, "connectionName");
        String clientId = (String) getParameter(messageContext, "clientId");
        String clientSecret = (String) getParameter(messageContext, "clientSecret");
        String tokenUrl = (String) getParameter(messageContext, "tokenUrl");
        String scope = (String) getParameter(messageContext, "scope");
        String instanceURL = (String) getParameter(messageContext, "instanceUrl");

        if (StringUtils.isBlank(clientId) || StringUtils.isBlank(clientSecret) || StringUtils.isBlank(tokenUrl)) {
            handleException("Mandatory OAuth parameters missing.", messageContext);
        }

        Map<String, String> payloadParameters = new HashMap<>();
        payloadParameters.put("client_id", clientId);
        payloadParameters.put("client_secret", clientSecret);
        payloadParameters.put("grant_type", "client_credentials");
        if (StringUtils.isNotBlank(scope)) {
            payloadParameters.put("scope", scope);
        }
        String tokenKey = getTokenKey(connectionName, payloadParameters);
        Token token = TokenManager.getToken(tokenKey);
        if (token == null || !token.isActive()) {
            if (token != null && !token.isActive()) {
                TokenManager.removeToken(tokenKey);
            }
            token = fetchAndStoreNewToken(tokenKey, messageContext, payloadParameters, tokenUrl);
        }

        messageContext.setProperty("_OH_INTERNAL_ACCESS_TOKEN_", token.getAccessToken());
        messageContext.setProperty("_OH_INTERNAL_INSTANCE_URL_", instanceURL);
    }

    private synchronized Token fetchAndStoreNewToken(String tokenKey, MessageContext messageContext,
                                                     Map<String, String> payloadParameters, String tokenUrl) {
        Token token = fetchAccessToken(messageContext, payloadParameters, tokenUrl);
        TokenManager.addToken(tokenKey, token);
        return token;
    }

    private Token fetchAccessToken(MessageContext messageContext, Map<String, String> payloadParameters,
                                   String tokenUrl) {

        long currentTime = System.currentTimeMillis();
        HttpPost postRequest = new HttpPost(tokenUrl);

        ArrayList<NameValuePair> parameters = new ArrayList<>();
        for (Map.Entry<String, String> entry : payloadParameters.entrySet()) {
            parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        try {
            postRequest.setEntity(new UrlEncodedFormEntity(parameters));
        } catch (UnsupportedEncodingException e) {
            handleException("Error creating access token payload.", messageContext);
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(postRequest)) {

            HttpEntity entity = response.getEntity();
            if (entity == null) {
                handleException("Empty response received from token endpoint.", messageContext);
            }

            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(entity);

            if (statusCode == HttpURLConnection.HTTP_OK) {
                JsonObject json = parser.parse(responseBody).getAsJsonObject();
                String accessToken = json.get("access_token").getAsString();
                long expiresIn = json.has("expires_in") ? json.get("expires_in").getAsLong() * 1000 : 3600000; // default 1h
                return new Token(accessToken, currentTime, expiresIn);
            } else {
                handleException("Failed to retrieve access token. Status: " + statusCode + ", Response: " + responseBody, messageContext);
            }

        } catch (IOException e) {
            handleException("I/O error while retrieving access token.", messageContext);
        }
        return null;
    }

    private String getTokenKey(String connectionName, Map<String, String> params) {
        return connectionName + "_" + Objects.hash(params);
    }
}
