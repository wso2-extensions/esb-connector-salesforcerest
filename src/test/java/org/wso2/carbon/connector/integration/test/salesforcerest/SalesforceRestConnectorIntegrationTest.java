/*
 *
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.carbon.connector.integration.test.salesforcerest;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;


import java.io.IOException;
import java.lang.String;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class SalesforceRestConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        addCertificatesToEIKeyStore("client-truststore.jks", "wso2carbon");

        String connectorName = System.getProperty("connector_name") + "-connector-" +
                System.getProperty("connector_version") + ".zip";
        init(connectorName);
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");

        apiRequestHeadersMap.put("Accept-Charset", "UTF-8");
        apiRequestHeadersMap.put("Content-Type", "application/json");

        getApiConfigProperties();
        getAccessToken();

        String accessToken = connectorProperties.getProperty("accessToken");
        apiRequestHeadersMap.put("Authorization", "Bearer " + accessToken);

        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().minusDays(2);
        connectorProperties.put("startTime",startDate + connectorProperties.getProperty("startTime"));
        connectorProperties.put("endTime",startDate + connectorProperties.getProperty("endTime"));
        connectorProperties.put("apiStartTime",startDate + connectorProperties.getProperty("apiStartTime"));
        connectorProperties.put("apiEndTime",startDate + connectorProperties.getProperty("apiEndTime"));
    }

    private void getAccessToken() throws IOException, JSONException {

        Map<String, String> apiTokenRequestHeadersMap = new HashMap<String, String>();
        Map<String, String> apiParametersMap = new HashMap<String, String>();
        apiTokenRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
        RestResponse<JSONObject> apiTokenRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("hostName") +
                                "/services/oauth2/token?grant_type=refresh_token&client_id=" +
                                connectorProperties.getProperty("clientId")+"&client_secret=" +
                                connectorProperties.getProperty("clientSecret")+"&refresh_token=" +
                                connectorProperties.getProperty("refreshToken"), "POST",
                        apiTokenRequestHeadersMap, "", apiParametersMap);

        String accessToken = apiTokenRestResponse.getBody().get("access_token").toString();
        connectorProperties.put("accessToken",accessToken);
    }

    /**
     * Test case for describeSGlobal method.
     */
    @Test(enabled = true, description = "salesforcerest {describeGlobal} integration test.")
    public void describeGlobal() throws IOException, JSONException {

        String methodName = "describeGlobal";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "describeGlobal.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("sobjects").length(), apiRestResponse.getBody().getJSONArray("sobjects").length());
    }

    /**
     * Test case for describeSObject method.
     */
    @Test(enabled = true, description = "salesforcerest {describeSObject} integration test.")
    public void describeSObject() throws IOException, JSONException {

        String methodName = "describeSObject";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "describeSObject.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/" + connectorProperties.getProperty("sObjectName") + "/describe";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("childRelationships").getJSONObject(1).getString("childSObject"), apiRestResponse.getBody().getJSONArray("childRelationships").getJSONObject(1).getString("childSObject"));
    }

    /**
     * Test case for sObjectBasicInfo method.
     */
    @Test(enabled = true, description = "salesforcerest {sObjectBasicInfo} integration test.")
    public void sObjectBasicInfo() throws IOException, JSONException {

        String methodName = "sObjectBasicInfo";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "sObjectBasicInfo.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/" + connectorProperties.getProperty("sObjectName");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("objectDescribe").toString(), apiRestResponse.getBody().getJSONObject("objectDescribe").toString());
    }

    /**
     * Test case for getDeleted method.
     */
    @Test(enabled = true, description = "salesforcerest {getDeleted} integration test.")
    public void getDeleted() throws IOException, JSONException {

        String methodName = "getDeleted";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "getDeleted.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/" + connectorProperties.getProperty("sObjectName") + "/deleted/?start=" + connectorProperties.getProperty("apiStartTime") + "&end=" + connectorProperties.getProperty("apiEndTime");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("earliestDateAvailable"), apiRestResponse.getBody().getString("earliestDateAvailable"));
    }

    /**
     * Test case for getUpdated method.
     */
    @Test(enabled = true, description = "salesforcerest {getUpdated} integration test.")
    public void getUpdated() throws IOException, JSONException {

        String methodName = "getUpdated";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "getUpdated.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/" + connectorProperties.getProperty("sObjectName") + "/updated/?start=" + connectorProperties.getProperty("apiStartTime") + "&end=" + connectorProperties.getProperty("apiEndTime");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Test case for listResourcesByApiVersion method.
     */
    @Test(enabled = true, description = "salesforcerest {listResourcesByApiVersion} integration test.")
    public void listResourcesByApiVersion() throws IOException, JSONException {

        String methodName = "listResourcesByApiVersion";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "listResourcesByApiVersion.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("sobjects"), apiRestResponse.getBody().getString("sobjects"));
    }

    /**
     * Test case for query method.
     */
    @Test(enabled = true, description = "salesforcerest {query} integration test.")
    public void query() throws IOException, JSONException {

        String methodName = "query";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "query.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/query/?q=" + connectorProperties.getProperty("apiQueryString");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("records").toString(), apiRestResponse.getBody().getJSONArray("records").toString());
    }

    /**
     * Test case for queryPerformanceFeedback method.
     */
    @Test(enabled = true, description = "salesforcerest {queryPerformanceFeedback} integration test.")
    public void queryPerformanceFeedback() throws IOException, JSONException {

        String methodName = "queryPerformanceFeedback";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "queryPerformanceFeedback.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/query/?explain=" + connectorProperties.getProperty("apiQueryString");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("plans").length(), apiRestResponse.getBody().getJSONArray("plans").length());
    }

    /**
     * Test case for queryAll method.
     */
    @Test(enabled = true, description = "salesforcerest {queryAll} integration test.")
    public void queryAll() throws IOException, JSONException {

        String methodName = "queryAll";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "queryAll.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/queryAll/?q=" + connectorProperties.getProperty("apiQueryString");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("records").toString(), apiRestResponse.getBody().getJSONArray("records").toString());
    }

    /**
     * Test case for queryAllMore method.
     */
    @Test(enabled = false, description = "salesforcerest {queryAllMore} integration test.")
    public void queryAllMore() throws IOException, JSONException {

        String methodName = "queryAllMore";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "queryAllMore.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/"+ connectorProperties.getProperty("apiVersion")+"/queryAll/"+connectorProperties.getProperty("nextRecordsUrl");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Test case for queryMore method.
     */
    @Test(enabled = false, description = "salesforcerest {queryMore} integration test.")
    public void queryMore() throws IOException, JSONException {

        String methodName = "queryMore";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "queryMore.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/"+ connectorProperties.getProperty("apiVersion")+"/query/"+connectorProperties.getProperty("nextRecordsUrl");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Test case for getSpecificAction method.
     */
    @Test(enabled = true, description = "salesforcerest {getSpecificAction} integration test.")
    public void getSpecificAction() throws IOException, JSONException {

        String methodName = "getSpecificAction";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "getSpecificAction.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/quickActions/" + connectorProperties.getProperty("actionName");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Test case for quickActions method.
     */
    @Test(enabled = true, description = "salesforcerest {quickActions} integration test.")
    public void quickActions() throws IOException, JSONException {

        String methodName = "quickActions";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "quickActions.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/quickActions";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("output"), apiRestResponse.getBody().getString("output"));
    }

    /**
     * Test case for sObjectAction method.
     */
    @Test(enabled = true, description = "salesforcerest {sObjectAction} integration test.")
    public void sObjectAction() throws IOException, JSONException {

        String methodName = "sObjectAction";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "sObjectAction.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/" + connectorProperties.getProperty("sObjectName") + "/quickActions";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("output"), apiRestResponse.getBody().getString("output"));
    }

    /**
     * Test case for recentlyViewedItem method.
     */
    @Test(enabled = true, description = "salesforcerest {recentlyViewedItem} integration test.")
    public void recentlyViewedItem() throws IOException, JSONException {

        String methodName = "recentlyViewedItem";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "recentlyViewedItem.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/recent/?limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Test case for search method.
     */
    @Test(enabled = true, description = "salesforcerest {search} integration test.")
    public void search() throws IOException, JSONException {

        String methodName = "search";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "search.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/search/?q=" + connectorProperties.getProperty("apiSearchString");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Test case for searchResultLayout method.
     */
    @Test(enabled = true, description = "salesforcerest {searchResultLayout} integration test.")
    public void searchResultLayout() throws IOException, JSONException {

        String methodName = "searchResultLayout";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "searchResultLayout.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/search/layout/?q=" + connectorProperties.getProperty("sObjectNameList");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }

    /**
     * Test case for searchScopeAndOrder method.
     */
    @Test(enabled = true, description = "salesforcerest {searchScopeAndOrder} integration test.")
    public void searchScopeAndOrder() throws IOException, JSONException {

        String methodName = "searchScopeAndOrder";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "searchScopeAndOrder.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/search/scopeOrder";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("output"), apiRestResponse.getBody().getString("output"));
    }

    /**
     * Test case for getUserInformation method.
     */
    @Test(enabled = true, description = "salesforcerest {getUserInformation} integration test.")
    public void getUserInformation() throws IOException, JSONException {

        String apiUserEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/query/?q=" + connectorProperties.getProperty("apiUserQueryString");
        RestResponse<JSONObject> apiUserRestResponse = sendJsonRestRequest(apiUserEndPoint, "GET", apiRequestHeadersMap);
        String userId = apiUserRestResponse.getBody().getJSONArray("records").getJSONObject(0). getString("Id");
        connectorProperties.put("userId", userId);

        String methodName = "getUserInformation";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "getUserInformation.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/User/" + connectorProperties.getProperty("userId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Test case for resetPassword method.
     */
    @Test(enabled = false, dependsOnMethods = {"getUserInformation"}, description = "salesforcerest {resetPassword} integration test.")
    public void resetPassword() throws IOException, JSONException {

        String methodName = "resetPassword";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "resetPassword.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/"+ connectorProperties.getProperty("apiVersion")+"/sobjects/User/"+connectorProperties.getProperty("userId")+"/password";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Test case for setPassword method.
     */
    @Test(enabled = false, dependsOnMethods = {"getUserInformation"}, description = "salesforcerest {setPassword} integration test.")
    public void setPassword() throws IOException, JSONException {
        connectorProperties.put("NewPassword", "sdgfkuahsdgfq3A"); // Please edit the value to a new alphanumeric value before enabling this test case

        String methodName = "setPassword";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "setPassword.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
    }

    /**
     * Test case for listViews method.
     */
    @Test(enabled = true, description = "salesforcerest {listViews} integration test.")
    public void listViews() throws IOException, JSONException {

        String methodName = "listViews";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "listViews.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/" + connectorProperties.getProperty("sObjectName") + "/listviews";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        String listViewId = esbRestResponse.getBody().getJSONArray("listviews").getJSONObject(0).getString("id");
        connectorProperties.put("listViewID",listViewId);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("listviews").length(), apiRestResponse.getBody().getJSONArray("listviews").length());
    }

    /**
     * Test case for listViewById method.
     */
    @Test(enabled = true, dependsOnMethods = {"listViews"}, description = "salesforcerest {listViewById} integration test.")
    public void listViewById() throws IOException, JSONException {

        String methodName = "listViewById";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "listViewById.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/" + connectorProperties.getProperty("sObjectName") + "/listviews/" + connectorProperties.getProperty("listViewID");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("developerName"), apiRestResponse.getBody().getString("developerName"));
    }

    /**
     * Positive test case for describeListViewById method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"listViewById"}, description = "salesforcerest {describeListViewById} integration test with mandatory parameters.")
    public void describeListViewByIdWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "describeListViewById";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "describeListViewById.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/" + connectorProperties.getProperty("sObjectName") + "/listviews/" + connectorProperties.getProperty("listViewID") + "/describe";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("query"), apiRestResponse.getBody().getString("query"));
    }

    /**
     * Test case for listviewQueryPerformanceFeedback method.
     */
    @Test(enabled = true, dependsOnMethods = {"describeListViewByIdWithMandatoryParameters"}, description = "salesforcerest {listviewQueryPerformanceFeedback} integration test.")
    public void listviewQueryPerformanceFeedback() throws IOException, JSONException {

        String methodName = "listviewQueryPerformanceFeedback";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "listviewQueryPerformanceFeedback.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/query/?explain=" + connectorProperties.getProperty("listViewID");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("plans").toString(), apiRestResponse.getBody().getJSONArray("plans").toString());
    }

    /**
     * Positive test case for listViewResults method with mandatory parameters.
     */
    @Test(enabled = true, dependsOnMethods = {"listviewQueryPerformanceFeedback"}, description = "salesforcerest {listViewResults} integration test with mandatory parameters.")
    public void listViewResultsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "listViewResults";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "listViewResults.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/" + connectorProperties.getProperty("sObjectName") + "/listviews/" + connectorProperties.getProperty("listViewID") + "/results";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("columns").toString(), apiRestResponse.getBody().getJSONArray("columns").toString());
    }

    /**
     * Test case for recentListViews method.
     */
    @Test(enabled = true, description = "salesforcerest {recentListViews} integration test.")
    public void recentListViews() throws IOException, JSONException {

        String methodName = "recentListViews";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "recentListViews.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/" + connectorProperties.getProperty("sObjectName") + "/listviews/recent";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("listviews").toString(), apiRestResponse.getBody().getJSONArray("listviews").toString());
    }

    /**
     * Test case for listApprovals method.
     */
    @Test(enabled = true, description = "salesforcerest {listApprovals} integration test.")
    public void listApprovals() throws IOException, JSONException {

        String methodName = "listApprovals";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "listApprovals.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/process/approvals";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("approvals").toString(), apiRestResponse.getBody().getJSONObject("approvals").toString());
    }

    /**
     * Test case for create method.
     */
    @Test(enabled = true, description = "salesforcerest {create} integration test.")
    public void create() throws IOException, JSONException {

        String methodName = "create";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "create.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/" + connectorProperties.getProperty("sObjectName");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        String id = esbRestResponse.getBody().getString("id").toString();
        connectorProperties.put("id",id);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getJSONArray("recentItems").getJSONObject(0).getString("Id"));
    }

    /**
     * Test case for update method.
     */
    @Test(enabled = true, dependsOnMethods = {"create"}, description = "salesforcerest {update} integration test.")
    public void update() throws IOException, JSONException {

        String methodName = "update";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "update.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/" + connectorProperties.getProperty("sObjectName") + "/" + connectorProperties.getProperty("id");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "updateApi.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Test case for sObjectRows method.
     */
    @Test(enabled = true, dependsOnMethods = {"update"}, description = "salesforcerest {sObjectRows} integration test.")
    public void sObjectRows() throws IOException, JSONException {

        String methodName = "sObjectRows";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "sObjectRows.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/" + connectorProperties.getProperty("sObjectName") + "/" + connectorProperties.getProperty("id");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("LastModifiedDate"), apiRestResponse.getBody().getString("LastModifiedDate"));
    }

    /**
     * Test case for retrieveFieldValues method.
     */
    @Test(enabled = true, dependsOnMethods = {"sObjectRows"}, description = "salesforcerest {retrieveFieldValues} integration test.")
    public void retrieveFieldValues() throws IOException, JSONException {

        String methodName = "retrieveFieldValues";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "retrieveFieldValues.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/" + connectorProperties.getProperty("sObjectName") + "/" + connectorProperties.getProperty("id") + "?fields=" + connectorProperties.getProperty("fields");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Test case for delete method.
     */
    @Test(enabled = true, dependsOnMethods = {"retrieveFieldValues"}, description = "salesforcerest {delete} integration test.")
    public void delete() throws IOException, JSONException {

        String methodName = "delete";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "delete.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/" + connectorProperties.getProperty("sObjectName") + "/" + connectorProperties.getProperty("id");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for getListOfAction method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {getListOfAction} integration test with mandatory parameters.")
    public void getListOfActionWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getListOfAction";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "getListOfAction.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/actions";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for getSpecificListOfAction method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {getSpecificListOfAction} integration test with mandatory parameters.")
    public void getSpecificListOfActionWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getSpecificListOfAction";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "getSpecificListOfAction.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/actions/" + connectorProperties.getProperty("actionType");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for getAttributeOfSpecificAction method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {getAttributeOfSpecificAction} integration test with mandatory parameters.")
    public void getAttributeOfSpecificActionWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getAttributeOfSpecificAction";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "getAttributeOfSpecificAction.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/actions/" + connectorProperties.getProperty("actionType") + "/" + connectorProperties.getProperty("attribute");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for listItemsInMenu method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {listItemsInMenu} integration test with mandatory parameters.")
    public void listItemsInMenuWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "listItemsInMenu";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "listItemsInMenu.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/appMenu/" + connectorProperties.getProperty("menuType");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for tabs method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {tabs} integration test with mandatory parameters.")
    public void tabsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "tabs";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "tabs.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/tabs";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for themes method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {themes} integration test with mandatory parameters.")
    public void themesWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "themes";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "themes.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/theme";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for listApprovals method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {listApprovals} integration test with mandatory parameters.")
    public void listApprovalsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "listApprovals";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "listApprovals.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/process/approvals";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for sObjectLayouts method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {sObjectLayouts} integration test with mandatory parameters.")
    public void sObjectLayoutsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "sObjectLayouts";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "sObjectLayouts.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/" + connectorProperties.getProperty("sObjectName") + "/describe/layouts";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("layouts").getJSONObject(0).toString(), apiRestResponse.getBody().getJSONArray("layouts").getJSONObject(0).toString());
    }

    /**
     * Positive test case for globalSObjectLayouts method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {globalSObjectLayouts} integration test with mandatory parameters.")
    public void globalSObjectLayoutsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "globalSObjectLayouts";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "globalSObjectLayouts.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/Global/describe/layouts";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("layouts").getJSONObject(0).toString(), apiRestResponse.getBody().getJSONArray("layouts").getJSONObject(0).toString());
    }

    /**
     * Positive test case for compactLayouts method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {compactLayouts} integration test with mandatory parameters.")
    public void compactLayoutsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "compactLayouts";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "compactLayouts.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/compactLayouts?q=" + connectorProperties.getProperty("sObjectNameList");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Account").toString(), apiRestResponse.getBody().getJSONObject("Account").toString());
    }

    /**
     * Positive test case for sObjectApprovalLayouts method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {sObjectApprovalLayouts} integration test with mandatory parameters.")
    public void sObjectApprovalLayoutsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "sObjectApprovalLayouts";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "sObjectApprovalLayouts.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/" + connectorProperties.getProperty("sObjectName") + "/describe/approvalLayouts";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for sObjectCompactLayouts method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {sObjectCompactLayouts} integration test with mandatory parameters.")
    public void sObjectCompactLayoutsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "sObjectCompactLayouts";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "sObjectCompactLayouts.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/" + connectorProperties.getProperty("sObjectName") + "/describe/compactLayouts";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("compactLayouts").getJSONObject(0).toString(), apiRestResponse.getBody().getJSONArray("compactLayouts").getJSONObject(0).toString());
    }

    /**
     * Positive test case for sObjectNamedLayouts method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {sObjectNamedLayouts} integration test with mandatory parameters.")
    public void sObjectNamedLayoutsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "sObjectNamedLayouts";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "sObjectNamedLayouts.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/" + connectorProperties.getProperty("sobject") + "/describe/namedLayouts/" + connectorProperties.getProperty("layoutName");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("layouts").getJSONObject(0).toString(), apiRestResponse.getBody().getJSONArray("layouts").getJSONObject(0).toString());
    }

    /**
     * Positive test case for listAvailableApiVersion method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {listAvailableApiVersion} integration test with mandatory parameters.")
    public void listAvailableApiVersionWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "listAvailableApiVersion";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "listAvailableApiVersion.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for listOrganizationLimits method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {listOrganizationLimits} integration test with mandatory parameters.")
    public void listOrganizationLimitsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "listOrganizationLimits";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "listOrganizationLimits.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/limits";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ConcurrentAsyncGetReportInstances").toString(), apiRestResponse.getBody().getJSONObject("ConcurrentAsyncGetReportInstances").toString());
    }

    /**
     * Positive test case for sObjectPlatformAction method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {sObjectPlatformAction} integration test with mandatory parameters.")
    public void sObjectPlatformActionWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "sObjectPlatformAction";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "sObjectPlatformAction.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/PlatformAction";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for listProcessRules method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {listProcessRules} integration test with mandatory parameters.")
    public void listProcessRulesWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "listProcessRules";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "listProcessRules.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/process/rules";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getSpecificProcessRule method with mandatory parameters.
     */
    @Test(enabled = false, description = "salesforcerest {getSpecificProcessRule} integration test with mandatory parameters.")
    public void getSpecificProcessRuleWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getSpecificProcessRule";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "getSpecificProcessRule.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/"+connectorProperties.getProperty("apiVersion")+"/process/rules/"+connectorProperties.getProperty("sObjectName")+"/"+connectorProperties.getProperty("objectId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getDefaultValueOfAction method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {getDefaultValueOfAction} integration test with mandatory parameters.")
    public void getDefaultValueOfActionWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getDefaultValueOfAction";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "getDefaultValueOfAction.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/quickActions/" + connectorProperties.getProperty("actionName") + "/defaultValues";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getDescribeSpecificAction method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {getDescribeSpecificAction} integration test with mandatory parameters.")
    public void getDescribeSpecificActionWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getDescribeSpecificAction";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "getDescribeSpecificAction.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/quickActions/" + connectorProperties.getProperty("actionName") + "/describe";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for searchSuggestedRecords method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {searchSuggestedRecords} integration test with mandatory parameters.")
    public void searchSuggestedRecordsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "searchSuggestedRecords";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "searchSuggestedRecords.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/search/suggestions?q=" + connectorProperties.getProperty("stringForSearch") + "&sobject=" + connectorProperties.getProperty("sObjectName");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for describeEventMonitoring method with mandatory parameters.
     */
    @Test(enabled = true, description = "salesforcerest {describeEventMonitoring} integration test with mandatory parameters.")
    public void describeEventMonitoringWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "describeEventMonitoring";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURLHttp(methodName), "POST", esbRequestHeadersMap, "describeEventMonitoring.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/services/data/" + connectorProperties.getProperty("apiVersion") + "/sobjects/EventLogFile/describe";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }
}
