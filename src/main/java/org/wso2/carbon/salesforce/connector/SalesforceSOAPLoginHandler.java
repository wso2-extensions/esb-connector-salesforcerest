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
        System.out.println(">>> Entering SalesforceSOAPLoginHandler.connect()");
        try {
            /* ------------------------------------------------------------------
               1. Read parameters from Synapse message context
               ------------------------------------------------------------------ */
            String connectionName = (String) ConnectorUtils.lookupTemplateParamater(messageContext, "connectionName");
            String username       = (String) getParameter(messageContext, "username");
            String password       = (String) getParameter(messageContext, "password");
            String securityToken  = (String) getParameter(messageContext, "securityToken");
            String loginUrl       = (String) getParameter(messageContext, "loginUrl");
            String forceLoginProp = (String) getParameter(messageContext, "forceLogin");
            boolean forceLogin    = "true".equalsIgnoreCase(forceLoginProp);
            String doneFlag       = (String) messageContext.getProperty("salesforce.login.done");

            System.out.println("Params → connectionName=" + connectionName
                    + ", username=" + username
                    + ", loginUrl=" + loginUrl
                    + ", forceLogin=" + forceLogin
                    + ", alreadyDone=" + doneFlag);

            if (!forceLogin && "true".equals(doneFlag)) {
                System.out.println("Skipping login: already done and forceLogin=false");
                return;
            }

            /* ------------------------------------------------------------------
               2. Build SOAP envelope
               ------------------------------------------------------------------ */
            String fullPassword = password + (securityToken != null ? securityToken : "");
            String soapBody =
                    "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                            "xmlns:urn=\"urn:partner.soap.sforce.com\">" +
                            "<soapenv:Body>" +
                            "<urn:login>" +
                            "<urn:username>" + username + "</urn:username>" +
                            "<urn:password>" + fullPassword + "</urn:password>" +
                            "</urn:login>" +
                            "</soapenv:Body>" +
                            "</soapenv:Envelope>";

            System.out.println("Built SOAP body (truncated): "
                    + soapBody.substring(0, Math.min(soapBody.length(), 100)) + "...");

            /* ------------------------------------------------------------------
               3. Send HTTP POST to the Salesforce login endpoint
               ------------------------------------------------------------------ */
            HttpPost post = new HttpPost(loginUrl);
            post.setHeader("Content-Type", "text/xml; charset=UTF-8");
            post.setHeader("SOAPAction", "urn:partner.soap.sforce.com/Soap/loginRequest");
            post.setEntity(new StringEntity(soapBody, StandardCharsets.UTF_8));

            System.out.println("Executing POST to " + loginUrl);
            try (CloseableHttpClient client = HttpClients.createDefault();
                 CloseableHttpResponse response = client.execute(post)) {

                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("HTTP status: " + statusCode);

                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new ConnectException("Empty SOAP login response");
                }

                /* ------------------------------------------------------------------
                   4. Read response as string (for logging + parsing)
                   ------------------------------------------------------------------ */
                String responseXml = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                System.out.println("SOAP response:\n" + responseXml);

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

                /* ------------------------------------------------------------------
                   6. Store values back on the Synapse MessageContext
                   ------------------------------------------------------------------ */
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
