package org.wso2.carbon.salesforce.connector;

import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.util.ConnectorUtils;

public class SetupLoginParams extends AbstractConnector {
    public static final String SALESFORCE_LOGIN_DONE = "salesforce.login.done";
    public static final String SALESFORCE_LOGIN_FORCE = "forceLogin";

    public void connect(MessageContext messageContext) {
        // Set the force login
        String strValue =
                (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                        SALESFORCE_LOGIN_FORCE);
        if (strValue != null || "true".equals(strValue)) {
            // Setting Transport Headers
            Axis2MessageContext axis2smc = (Axis2MessageContext) messageContext;
            axis2smc.getAxis2MessageContext().getOperationContext()
                    .setProperty(SALESFORCE_LOGIN_DONE, "false");
        }
    }

}
