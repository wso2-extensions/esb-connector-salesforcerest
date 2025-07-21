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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.wso2.integration.connector.core.AbstractConnector;
import org.wso2.integration.connector.core.ConnectException;
import org.wso2.integration.connector.core.util.ConnectorUtils;

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
            String connectionName = (String) ConnectorUtils.lookupTemplateParamater(messageContext, "connectionName");
            String username = (String) getParameter(messageContext, "username");
            String password = (String) getParameter(messageContext, "password");
            String loginUrl = (String) getParameter(messageContext, "loginUrl");
            String forceLoginProp = (String) getParameter(messageContext, "forceLogin");
            boolean forceLogin = "true".equalsIgnoreCase(forceLoginProp);
            String doneFlag = (String) messageContext.getProperty("salesforce.login.done");

            if (!forceLogin && "true".equals(doneFlag)) {
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
                String responseXml = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = db.parse(
                        new ByteArrayInputStream(responseXml.getBytes(StandardCharsets.UTF_8)));

                XPath xp = XPathFactory.newInstance().newXPath();

                Node faultNode = (Node) xp.evaluate(
                        "/*[local-name()='Envelope']/*[local-name()='Body']/*[local-name()='Fault']"
                                + "/*[local-name()='faultstring']",
                        doc, XPathConstants.NODE);

                if (faultNode != null) {
                    throw new ConnectException("Salesforce login failed: "
                            + faultNode.getTextContent());
                }

                String sessionId = xp.evaluate("string(//*[local-name()='sessionId'])", doc);
                String serverUrl = xp.evaluate("string(//*[local-name()='serverUrl'])", doc);

                if (sessionId.isEmpty() || serverUrl.isEmpty()) {
                    throw new ConnectException("sessionId or serverUrl not found in login response");
                }

                messageContext.setProperty("salesforce.sessionId", sessionId);
                messageContext.setProperty("salesforce.serviceUrl", serverUrl);
                messageContext.setProperty("salesforce.login.done", "true");
            }

        } catch (ConnectException ce) {
            throw ce;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConnectException(e, "Error during Salesforce SOAP login");
        }
    }
}
