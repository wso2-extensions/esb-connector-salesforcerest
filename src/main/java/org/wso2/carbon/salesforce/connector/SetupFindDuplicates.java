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
import org.apache.synapse.SynapseLog;
import org.wso2.integration.connector.core.AbstractConnector;

/**
 * This class adds data to message context for find duplicates operation
 */
public class SetupFindDuplicates extends AbstractConnector {

    public void connect(MessageContext synCtx) {

        SynapseLog synLog = getLog(synCtx);

        if (synLog.isTraceOrDebugEnabled()) {
            synLog.traceOrDebug("Start : Salesforce Find Duplicates mediator");

            if (synLog.isTraceTraceEnabled()) {
                synLog.traceTrace("Message : " + synCtx.getEnvelope());
            }
        }

        SalesforceUtil.addSobjects("findDuplicates", SalesforceUtil.SALESFORCE_SOBJECTS, synCtx, synLog,
                null);

        if (synLog.isTraceOrDebugEnabled()) {
            synLog.traceOrDebug("End : Salesforce Find Duplicates mediator");

            if (synLog.isTraceTraceEnabled()) {
                synLog.traceTrace("Message : " + synCtx.getEnvelope());
            }
        }
    }
}