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

import org.apache.commons.lang3.StringUtils;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class RestURLBuilder extends AbstractConnector {

    private static final String ENCODING = "UTF-8";
    private static final String URL_PATH = "uri.var.urlPath";
    private static final String URL_QUERY = "uri.var.urlQuery";
    private static final String AUTH_HOST = "uri.var.auth";

    private String apiVersion = "v60.0";
    private final String basePath = "/services/data/";
    private String resourcePath = "";
    private String pathParameters = "";
    private String queryParameters = "";
    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getPathParameters() {
        return pathParameters;
    }

    public void setPathParameters(String pathParameters) {
        this.pathParameters = pathParameters;
    }

    public String getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(String queryParameters) {
        this.queryParameters = queryParameters;
    }

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        try {
            String ctxVersion = (String) messageContext.getProperty("_OH_INTERNAL_API_VERSION_");
            if (StringUtils.isNotBlank(ctxVersion)) {
                apiVersion = ctxVersion;
            }

            String instanceUrl = (String) messageContext.getProperty("_OH_INTERNAL_INSTANCE_URL_");
            messageContext.setProperty(AUTH_HOST, StringUtils.defaultIfBlank(instanceUrl, ""));

            String resolvedResourcePath = resourcePath.replace("{$version}", apiVersion);
            String urlPath = resolvedResourcePath.startsWith("/services/data/")
                    ? resolvedResourcePath
                    : basePath + apiVersion + (resolvedResourcePath.startsWith("/") ? resolvedResourcePath : "/" + resolvedResourcePath);

            if (StringUtils.isNotEmpty(this.pathParameters)) {
                for (String pathParam : getPathParameters().split(",")) {
                    String paramValue = (String) getParameter(messageContext, pathParam);
                    if (StringUtils.isNotEmpty(paramValue)) {
                        String encodedValue = URLEncoder.encode(paramValue, ENCODING);
                        urlPath = urlPath.replace("{" + pathParam + "}", encodedValue);
                    } else {
                        handleException("Required path parameter '" + pathParam + "' not found in message context.", messageContext);
                    }
                }
            }

            StringBuilder queryBuilder = new StringBuilder();
            if (StringUtils.isNotEmpty(this.queryParameters)) {
                for (String queryParam : getQueryParameters().split(",")) {
                    String paramValue = (String) getParameter(messageContext, queryParam);
                    if (StringUtils.isNotEmpty(paramValue)) {
                        String encodedValue = URLEncoder.encode(paramValue, ENCODING).replace("+", "%20");
                        queryBuilder.append(queryParam).append('=').append(encodedValue).append('&');
                    }
                }
            }

            String urlQuery = "";
            if (queryBuilder.length() > 0) {
                urlQuery = "?" + queryBuilder.substring(0, queryBuilder.length() - 1); // keep leading “?”
            }

            messageContext.setProperty(URL_PATH, urlPath);
            messageContext.setProperty(URL_QUERY, urlQuery);

        } catch (UnsupportedEncodingException e) {
            handleException("Error encoding query parameters for Salesforce URL.", e, messageContext);
        }
    }
}
