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

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.util.ConnectorUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class SalesforceSOAPLoginHandler extends AbstractConnector {

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        try {
            /* ------------------------------------------------------------------
               1. Read parameters from Synapse message context
               ------------------------------------------------------------------ */
            String connectionName = (String) ConnectorUtils.lookupTemplateParamater(messageContext, "connectionName");
            String username = (String) getParameter(messageContext, "username");
            String password = (String) getParameter(messageContext, "password");
            String loginUrl = (String) getParameter(messageContext, "loginUrl");
            String forceLoginProp = (String) getParameter(messageContext, "forceLogin");
            boolean forceLogin = "true".equalsIgnoreCase(forceLoginProp);
            String doneFlag = (String) messageContext.getProperty("salesforce.login.done");

            if (!forceLogin && "true".equals(doneFlag)) {
                System.out.println("Skipping login: already done and forceLogin=false");
                return;
            }

            String soapBody =
                    "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                            "xmlns:urn=\"urn:partner.soap.sforce.com\">" +
                            "<soapenv:Body>" +
                            "<urn:login>" +
                            "<urn:username>" + username + "</urn:username>" +
                            "<urn:password>" + password + "</urn:password>" +
                            "</urn:login>" +
                            "</soapenv:Body>" +
                            "</soapenv:Envelope>";

            /* ------------------------------------------------------------------
               3. Send HTTP POST to the Salesforce login endpoint
               ------------------------------------------------------------------ */
            HttpPost post = new HttpPost(loginUrl);
            post.setHeader("Content-Type", "text/xml; charset=UTF-8");
            post.setHeader("SOAPAction", "urn:partner.soap.sforce.com/Soap/loginRequest");
            post.setEntity(new StringEntity(soapBody, StandardCharsets.UTF_8));

            try (CloseableHttpClient client = HttpClients.createDefault();
                 CloseableHttpResponse response = client.execute(post)) {

                int statusCode = response.getStatusLine().getStatusCode();

                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new ConnectException("Empty SOAP login response");
                }

                /* ------------------------------------------------------------------
                   4. Read response as string (for logging + parsing)
                   ------------------------------------------------------------------ */
                String responseXml = EntityUtils.toString(entity, StandardCharsets.UTF_8);

                /* ------------------------------------------------------------------
                   5. Parse XML & check for faults
                   ------------------------------------------------------------------ */
                DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = db.parse(
                        new ByteArrayInputStream(responseXml.getBytes(StandardCharsets.UTF_8)));

                XPath xp = XPathFactory.newInstance().newXPath();

                // Detect SOAP Fault
                Node faultNode = (Node) xp.evaluate(
                        "/*[local-name()='Envelope']/*[local-name()='Body']/*[local-name()='Fault']"
                                + "/*[local-name()='faultstring']",
                        doc, XPathConstants.NODE);

                if (faultNode != null) {
                    throw new ConnectException("Salesforce login failed: "
                            + faultNode.getTextContent());
                }

                // Extract values regardless of namespace prefixes
                String sessionId = xp.evaluate("string(//*[local-name()='sessionId'])", doc);
                String serverUrl = xp.evaluate("string(//*[local-name()='serverUrl'])", doc);

                if (sessionId.isEmpty() || serverUrl.isEmpty()) {
                    throw new ConnectException("sessionId or serverUrl not found in login response");
                }

                System.out.println("Parsed values → sessionId=" + sessionId
                        + ", serverUrl=" + serverUrl);

                messageContext.setProperty("salesforce.sessionId", sessionId);
                messageContext.setProperty("salesforce.serviceUrl", serverUrl);
                messageContext.setProperty("salesforce.login.done", "true");

                System.out.println("Saved properties on MessageContext");
            }

        } catch (ConnectException ce) {
            System.err.println("ConnectException: " + ce.getMessage());
            throw ce; // propagate to the fault sequence
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            throw new ConnectException(e, "Error during Salesforce SOAP login");
        } finally {
            System.out.println("<<< Exiting SalesforceSOAPLoginHandler.connect()");
        }
    }
}
