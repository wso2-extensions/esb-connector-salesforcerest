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
                return;
            }
            
            if (username == null || password == null || loginUrl == null) {
                throw new ConnectException("Missing required SOAP login parameters");
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

            HttpPost post = new HttpPost(loginUrl);
            post.setHeader("Content-Type", "text/xml; charset=UTF-8");
            post.setHeader("SOAPAction", "urn:partner.soap.sforce.com/Soap/loginRequest");
            post.setEntity(new StringEntity(soapBody, StandardCharsets.UTF_8));

            // -------- HTTP call ----------
            try (CloseableHttpClient client = HttpClients.createDefault();
                 CloseableHttpResponse response = client.execute(post)) {

                int statusCode = response.getStatusLine().getStatusCode();

                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new ConnectException("Empty SOAP login response");
                }

                String responseXml = EntityUtils.toString(entity, StandardCharsets.UTF_8);

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

                // -------- persist ----------
                messageContext.setProperty("salesforce.sessionId", sessionId);
                messageContext.setProperty("salesforce.serviceUrl", serverUrl);
                messageContext.setProperty("salesforce.login.done", "true");

            }

        } catch (ConnectException ce) {
            throw ce;
        } catch (Exception e) {
            throw new ConnectException(e, "Error during Salesforce SOAP login: " + e.getMessage());
        }
    }
}
