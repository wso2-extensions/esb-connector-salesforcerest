/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.salesforce.connector;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants; // Added
import org.apache.axis2.builder.Builder;
import org.apache.axis2.builder.BuilderUtil;
import org.apache.axis2.transport.TransportUtils;
import org.apache.commons.lang.StringEscapeUtils; // Added
import org.apache.commons.lang.StringUtils; // Added
import org.apache.commons.logging.Log; // Added
import org.apache.commons.logging.LogFactory; // Added
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil; // Added
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.util.ConnectorUtils;

import au.com.bytecode.opencsv.CSVReader; // Added
import com.google.gson.Gson; // Added
import com.google.gson.GsonBuilder; // Added
import com.google.gson.JsonObject; // Added
import com.google.gson.JsonPrimitive; // Added

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader; // Added
import java.util.ArrayList; // Added for csvToJson (was List, making concrete for initialization)
import java.util.List; // Added
import java.util.stream.Collectors; // Added
import java.util.stream.Stream; // Added


public class ProcessResults extends AbstractConnector {

    private static final Log log = LogFactory.getLog(ProcessResults.class); // Added Log initialization

    // Inlined SalesforceConstants
    private static final String OUTPUT_TYPE_PARAM = "outputType";
    private static final String INCLUDE_RESULT_TO_PARAM = "includeResultTo";
    private static final String FILE_PATH_PARAM = "filePath";
    private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
    private static final String TEXT_CSV_CONTENT_TYPE = "text/csv";

    // Inlined ResponseConstants (used by inlined SalesforceUtils methods)
    private static final String PROPERTY_ERROR_CODE = "ERROR_CODE";
    private static final String PROPERTY_ERROR_MESSAGE = "ERROR_MESSAGE";
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_INTERNAL_SERVER_ERROR = 500;

    // Inlined private constants from SalesforceUtils (used by inlined generateErrorOutput)
    private static final String NO_ENTITY_BODY_PROPERTY = "NO_ENTITY_BODY";
    private static final String HTTP_SC_PROPERTY = "HTTP_SC";

    private final String JSON = "JSON"; // Existing constant
    private final String FILE = "FILE"; // Existing constant

    @Override
    public void connect(MessageContext messageContext) {

        try {
            String output = messageContext.getEnvelope().getBody().getFirstElement().getText();
            // Using inlined constants
            String outputType = (String) ConnectorUtils.lookupTemplateParamater(messageContext, OUTPUT_TYPE_PARAM);
            String includeResultTo = (String) ConnectorUtils.lookupTemplateParamater(messageContext, INCLUDE_RESULT_TO_PARAM);
            String filePath = (String) ConnectorUtils.lookupTemplateParamater(messageContext, FILE_PATH_PARAM);
            if (JSON.equals(outputType)) { // Use .equals for string comparison
                output = toJson(output);
            }
            if (FILE.equals(includeResultTo)) { // Use .equals for string comparison
                storeInFile(filePath, output);
                String response = "{\"result\":\"success\"}";
                // Using inlined constant
                storeInPayload(messageContext, response, APPLICATION_JSON_CONTENT_TYPE);
            } else {
                String contentType = getContentType(outputType);
                storeInPayload(messageContext, output, contentType);
            }
        } catch (IOException e) {
            // Using inlined/local methods
            setErrorsInMessage(messageContext, 1, e.getMessage());
            generateErrorOutput(messageContext, e);
            log.error("Error occurred while processing the result", e);
        }
    }

    private String toJson(String csv) throws IOException {
        String jsonOutput = "{}";
        if (!csv.isEmpty()) {
            // Using inlined/local method
            jsonOutput = csvToJson(csv);
        }
        return jsonOutput;
    }

    private void storeInFile(String filePath, String content) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        FileOutputStream outputStream = new FileOutputStream(filePath);
        byte[] buffer = new byte[4096];
        int bytesRead; // Removed initialization to -1 as it's set in loop
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.close(); // Added to close the stream
        inputStream.close(); // Added to close the stream
    }

    private void storeInPayload(MessageContext messageContext, String content, String contentType) throws AxisFault {
        org.apache.axis2.context.MessageContext axis2MsgCtx = ((org.apache.synapse.core.axis2.
                Axis2MessageContext) messageContext).getAxis2MessageContext();
        Builder builder = BuilderUtil.getBuilderFromSelector(contentType, axis2MsgCtx);

        InputStream jsonStream = new ByteArrayInputStream(content.getBytes());
        OMElement documentElement = builder.processDocument(jsonStream, contentType, axis2MsgCtx);
        // documentElement.toString(); // Removed as it might not be necessary
        messageContext.setEnvelope(TransportUtils.createSOAPEnvelope(documentElement));
    }

    private String getContentType(String outputType) {
        if (JSON.equals(outputType)) { // Use .equals for string comparison
            // Using inlined constants
            return APPLICATION_JSON_CONTENT_TYPE;
        } else {
            return TEXT_CSV_CONTENT_TYPE;
        }
    }

    // Inlined methods from SalesforceUtils:

    /**
     * Sets error code and error message in the MessageContext.
     *
     * @param messageContext MessageContext to set properties.
     * @param statusCode     Status code of the error.
     * @param message        Error message.
     */
    private static void setErrorsInMessage(MessageContext messageContext, int statusCode, String message) {
        messageContext.setProperty(PROPERTY_ERROR_CODE, statusCode);
        messageContext.setProperty(PROPERTY_ERROR_MESSAGE, message);
    }

    /**
     * Generates error output for the message context.
     *
     * @param messageContext Message context.
     * @param e              Exception.
     */
    private static void generateErrorOutput(MessageContext messageContext, Exception e) {
        org.apache.axis2.context.MessageContext axisCtx =
                ((org.apache.synapse.core.axis2.Axis2MessageContext) messageContext).getAxis2MessageContext();
        axisCtx.setProperty(Constants.Configuration.MESSAGE_TYPE, APPLICATION_JSON_CONTENT_TYPE);
        axisCtx.setProperty(Constants.Configuration.CONTENT_TYPE, APPLICATION_JSON_CONTENT_TYPE);
        axisCtx.removeProperty(NO_ENTITY_BODY_PROPERTY);
        // Inlined from SalesforceUtils, ensuring quotes are escaped for valid JSON
        String jsonString = "{\"error\":\"" + StringEscapeUtils.escapeJava(e.getMessage()) + "\"}";
        try {
            JsonUtil.getNewJsonPayload(axisCtx, jsonString, true, true);
            axisCtx.setProperty(HTTP_SC_PROPERTY, HTTP_INTERNAL_SERVER_ERROR);
        } catch (AxisFault ex) {
            log.error("Error while generating error output", ex);
            axisCtx.setProperty(HTTP_SC_PROPERTY, HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Converts CSV string to JSON string.
     *
     * @param csvString CSV string.
     * @return JSON string.
     * @throws IOException If an error occurs while reading the CSV string.
     */
    private static String csvToJson(String csvString) throws IOException {
        String unescapedCsv = StringEscapeUtils.unescapeJava(csvString);
        CSVReader reader = new CSVReader(new StringReader(unescapedCsv));
        List<String[]> allRows = reader.readAll();
        reader.close(); // Close reader
        if (allRows.isEmpty()) {
            return "[]"; // Return empty JSON array if CSV is empty
        }
        String[] headers = allRows.get(0);
        List<JsonObject> jsonObjects = new ArrayList<>();
        for (int i = 1; i < allRows.size(); i++) {
            String[] row = allRows.get(i);
            JsonObject jsonObject = new JsonObject();
            for (int j = 0; j < headers.length; j++) {
                String header = removeQuotes(headers[j]);
                String value = getCellValue(row, j);
                jsonObject.add(header, new JsonPrimitive(value));
            }
            jsonObjects.add(jsonObject);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObjects);
    }

    /**
     * Removes quotes from a string.
     *
     * @param field String to remove quotes from.
     * @return String without quotes.
     */
    private static String removeQuotes(String field) {
        if (field.startsWith("\"") && field.endsWith("\"")) {
            return field.substring(1, field.length() - 1);
        }
        return field;
    }

    /**
     * Gets cell value from a row.
     *
     * @param row   String array representing a row.
     * @param index Index of the cell.
     * @return Cell value.
     */
    private static String getCellValue(String[] row, int index) {
        if (index < row.length && StringUtils.isNotBlank(row[index])) {
            return row[index];
        }
        return "";
    }
}
