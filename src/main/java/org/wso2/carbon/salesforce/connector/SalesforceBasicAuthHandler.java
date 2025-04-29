package org.wso2.carbon.salesforce.connector;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.util.ConnectorUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SalesforceBasicAuthHandler extends AbstractConnector {

    private static final Log log = LogFactory.getLog(SalesforceBasicAuthHandler.class);

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {

        String username = (String) ConnectorUtils.lookupTemplateParamater(messageContext, "username");
        String password = (String) ConnectorUtils.lookupTemplateParamater(messageContext, "password");
        String securityToken = (String) ConnectorUtils.lookupTemplateParamater(messageContext, "securityToken");

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            handleException("Username and password must be provided for Basic Authentication.", messageContext);
        }

        // Append security token to password if provided
        String completePassword = password;
        if (StringUtils.isNotBlank(securityToken)) {
            completePassword += securityToken;
        }

        String credentials = username + ":" + completePassword;
        String encodedAuth = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        messageContext.setProperty("_OH_INTERNAL_BASIC_AUTH_HEADER_", encodedAuth);

        if (log.isDebugEnabled()) {
            log.debug("Basic Auth header successfully set.");
        }
    }
}
