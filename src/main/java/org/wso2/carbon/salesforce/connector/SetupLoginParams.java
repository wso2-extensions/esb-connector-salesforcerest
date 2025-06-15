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
