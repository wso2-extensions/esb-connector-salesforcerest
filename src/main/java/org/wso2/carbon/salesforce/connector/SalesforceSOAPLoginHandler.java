package org.wso2.carbon.salesforce.connector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class SalesforceSOAPLoginHandler extends AbstractConnector {

    private static final Log log = LogFactory.getLog(SalesforceSOAPLoginHandler.class);

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {

        try {
            // -------- parameters ----------
            String connectionName = (String) ConnectorUtils.lookupTemplateParamater(messageContext, "connectionName");
            String username       = (String) getParameter(messageContext, "username");
            String password       = (String) getParameter(messageContext, "password");
            String loginUrl       = (String) getParameter(messageContext, "loginUrl");
            String forceLoginProp = (String) getParameter(messageContext, "forceLogin");
            boolean forceLogin    = "true".equalsIgnoreCase(forceLoginProp);
            String doneFlag       = (String) messageContext.getProperty("salesforce.login.done");

            if (!forceLogin && "true".equals(doneFlag)) {
                log.debug("Login already completed for connection '" + connectionName + "'. Skipping.");
                return;
            }

            // -------- SOAP request body ----------
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

            // Masked log so credentials aren’t written in full
            if (log.isDebugEnabled()) {
                log.debug("SOAP-Login request built for connection '" + connectionName + "' (to " + loginUrl + ')');
            }

            HttpPost post = new HttpPost(loginUrl);
            post.setHeader("Content-Type", "text/xml; charset=UTF-8");
            post.setHeader("SOAPAction", "urn:partner.soap.sforce.com/Soap/loginRequest");
            post.setEntity(new StringEntity(soapBody, StandardCharsets.UTF_8));

            // -------- HTTP call ----------
            try (CloseableHttpClient client = HttpClients.createDefault();
                 CloseableHttpResponse response = client.execute(post)) {

                int statusCode = response.getStatusLine().getStatusCode();
                log.debug("SOAP-Login HTTP status = " + statusCode);

                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new ConnectException("Empty SOAP login response");
                }

                String responseXml = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                if (log.isTraceEnabled()) {
                    log.trace("SOAP-Login response payload:\n" + responseXml);
                }

                Document doc = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder()
                        .parse(new ByteArrayInputStream(responseXml.getBytes(StandardCharsets.UTF_8)));

                XPath xp = XPathFactory.newInstance().newXPath();
                Node faultNode = (Node) xp.evaluate(
                        "/*[local-name()='Envelope']/*[local-name()='Body']/*[local-name()='Fault']"
                                + "/*[local-name()='faultstring']",
                        doc, XPathConstants.NODE);

                if (faultNode != null) {
                    throw new ConnectException("Salesforce login failed: " + faultNode.getTextContent());
                }

                String sessionId = xp.evaluate("string(//*[local-name()='sessionId'])", doc);
                String serverUrl = xp.evaluate("string(//*[local-name()='serverUrl'])", doc);

                if (sessionId.isEmpty() || serverUrl.isEmpty()) {
                    throw new ConnectException("sessionId or serverUrl not found in login response");
                }

                // -------- persist & log ----------
                messageContext.setProperty("salesforce.sessionId", sessionId);
                messageContext.setProperty("salesforce.serviceUrl", serverUrl);
                messageContext.setProperty("salesforce.login.done", "true");

                log.info(String.format(
                        "Salesforce SOAP login successful [connection=%s, sessionId=%s, serviceUrl=%s]",
                        connectionName, sessionId, serverUrl));

            }

        } catch (ConnectException ce) {
            throw ce;
        } catch (Exception e) {
            log.error("Error during Salesforce SOAP login", e);
            throw new ConnectException(e, "Error during Salesforce SOAP login");
        }
    }
}
