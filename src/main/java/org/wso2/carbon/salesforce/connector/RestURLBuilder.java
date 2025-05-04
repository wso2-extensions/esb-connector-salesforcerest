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
    private static final String AUTH_HOST = "uri.var.auth";

    private String apiVersion = "v60.0";
    private final String basePath = "/services/data/";
    private String resourcePath = "";
    private String pathParameters = "";
    private String queryParameters = "";

    // ───── getters / setters ────────────────────────────────────────────────────
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

    // ───── main logic ──────────────────────────────────────────────────────────
    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        try {
            // Allow template to override version
            String ctxVersion = (String) messageContext.getProperty("_OH_INTERNAL_API_VERSION_");
            if (StringUtils.isNotBlank(ctxVersion)) {
                apiVersion = ctxVersion;
            }

            // Base host (comes from OAuth token call)
            String instanceUrl = (String) messageContext.getProperty("_OH_INTERNAL_INSTANCE_URL_");
            messageContext.setProperty(AUTH_HOST, StringUtils.defaultIfBlank(instanceUrl, ""));

            // Build /services/data/vXX.X/… path
            String resolvedResourcePath = resourcePath.replace("{$version}", apiVersion);
            String urlPath = resolvedResourcePath.startsWith("/services/data/")
                    ? resolvedResourcePath
                    : basePath + apiVersion + (resolvedResourcePath.startsWith("/") ? resolvedResourcePath : "/" + resolvedResourcePath);

            // Insert path parameters (if any)
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

            // Build query string
            StringBuilder queryBuilder = new StringBuilder();
            if (StringUtils.isNotEmpty(this.queryParameters)) {
                for (String queryParam : getQueryParameters().split(",")) {
                    String paramValue = (String) getParameter(messageContext, queryParam);
                    if (StringUtils.isNotEmpty(paramValue)) {
                        // ↓↓↓ FIX: “+” ➜ “%20” so spaces remain RFC-3986 compliant
                        String encodedValue = URLEncoder.encode(paramValue, ENCODING).replace("+", "%20");
                        queryBuilder.append(queryParam).append('=').append(encodedValue).append('&');
                    }
                }
            }

            String urlQuery = "";
            if (queryBuilder.length() > 0) {
                urlQuery = "?" + queryBuilder.substring(0, queryBuilder.length() - 1); // keep leading “?”
            }

            // Store parts in the message context for the URI-template mediator
            messageContext.setProperty(URL_PATH, urlPath);
            messageContext.setProperty(URL_QUERY, urlQuery);

        } catch (UnsupportedEncodingException e) {
            handleException("Error encoding query parameters for Salesforce URL.", e, messageContext);
        }
    }
}
