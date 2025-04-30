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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RestURLBuilder extends AbstractConnector {

    private static final String ENCODING = "UTF-8";
    private static final String URL_PATH = "uri.var.urlPath";
    private static final String URL_QUERY = "uri.var.urlQuery";

    private String apiVersion = "v60.0"; // default version, make configurable if needed
    private String basePath = "/services/data/";
    private String resourcePath = "";
    private String pathParameters = "";
    private String queryParameters = "";

    private static final Set<String> SPECIAL_PARAMETERS = new HashSet<>(Arrays.asList(
            "fields", "filter", "orderBy", "limit", "offset"));

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
            String urlPath = basePath + apiVersion + resourcePath;

            if (StringUtils.isNotEmpty(this.pathParameters)) {
                String[] pathParameterList = getPathParameters().split(",");
                for (String pathParam : pathParameterList) {
                    String paramValue = (String) getParameter(messageContext, pathParam);
                    if (StringUtils.isNotEmpty(paramValue)) {
                        String encodedValue = URLEncoder.encode(paramValue, ENCODING);
                        urlPath = urlPath.replace("{" + pathParam + "}", encodedValue);
                    } else {
                        String errorMsg = "Required path parameter '" + pathParam + "' not found in message context.";
                        handleException(errorMsg, messageContext);
                    }
                }
            }

            StringBuilder queryBuilder = new StringBuilder();
            if (StringUtils.isNotEmpty(this.queryParameters)) {
                String[] queryParamList = getQueryParameters().split(",");
                for (String queryParam : queryParamList) {
                    String paramValue = (String) getParameter(messageContext, queryParam);
                    if (StringUtils.isNotEmpty(paramValue)) {
                        String encodedValue = URLEncoder.encode(paramValue, ENCODING);
                        if (SPECIAL_PARAMETERS.contains(queryParam)) {
                            queryBuilder.append(queryParam).append("=").append(encodedValue).append("&");
                        } else {
                            queryBuilder.append(queryParam).append("=").append(encodedValue).append("&");
                        }
                    }
                }
            }

            String urlQuery = "";
            if (queryBuilder.length() > 0) {
                urlQuery = "?" + queryBuilder.substring(0, queryBuilder.length() - 1);
            }

            messageContext.setProperty(URL_PATH, urlPath);
            messageContext.setProperty(URL_QUERY, urlQuery);

        } catch (UnsupportedEncodingException e) {
            String error = "Error encoding query parameters for Salesforce URL.";
            handleException(error, e, messageContext);
        }
    }
}
