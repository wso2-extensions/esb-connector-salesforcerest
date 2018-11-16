# Working with Queries

[[Overview]](#overview)  [[Operation details]](#operation-details)  [[Sample configuration]](#sample-configuration)

### Overview 

The following operations allow you to work with  queries.  Click an operation name to see details on how to use it.
For a sample proxy service that illustrates how to work with queries, see [Sample configuration](#sample-configuration).

| Operation        | Description |
| ------------- |-------------|
| [query](#querying-records)    | 	Retrieves data from an object. |
| [queryMore](#querying-additional-records)      | 	Retrieves additional data if the response is too large.|
| [queryPerformanceFeedback](#getting-feedback-on-query-performance)      | Uses the query resource along with the explain parameter to get feedback on how Salesforce will execute your query, report, or list view. |
| [listviewQueryPerformanceFeedback](#getting-feedback-on-query-performance-in-listview)    | Retrieves query performance feedback on a report or list view.|

### Operation details

This section provides more details on each of the operations related to queries.

#### Querying records

To retrieve data from an object, use salesforcerest.query and specify the following properties. For more information on how Salesforce queries records, see https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_query.htm
If you want your results to include deleted records in the Recycle Bin, use salesforcerest.queryAll in place of salesforcerest.query.

**query**
```xml
<salesforcerest.query>
    <queryString>{$ctx:queryString}</queryString>
</salesforcerest.query>
```
**Properties**
* queryString: The SQL query to use to search for records.

**queryAll**
```xml
<salesforcerest.queryAll>
    <queryString>{$ctx:queryString}</queryString>
</salesforcerest.queryAll>
```

**Sample request**

Following is a sample request that can be handled by the query operation.

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "apiVersion": "v32.0",
  "intervalTime" : "100000",
  "queryString": "select id, name from Account",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the query operation.

```json
{
    "done" : false,
    "totalSize" : 2014,
    "nextRecordsUrl" : "/services/data/v20.0/query/01gD0000002HU6KIAW-2000",
    "records" : 
    [ 
        {  
            "attributes" : 
            {    
                "type" : "Account",    
                "url" : "/services/data/v20.0/sobjects/Account/001D000000IRFmaIAH"  
            },  
            "Name" : "Test 1"
        }, 
        {  
            "attributes" : 
            {    
                "type" : "Account",    
                "url" : "/services/data/v20.0/sobjects/Account/001D000000IomazIAB"  
            },  
            "Name" : "Test 2"
        }, 

        ...

    ]
}
```

**Related Salesforce documentation**

query : [ https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_query.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_query.htm)
queryAll : [ https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_query.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_query.htm)


#### Querying additional records

If the results from the query or queryAll operations are too large, the first batch of results is returned along with an ID that you can use with salesforcerest.queryMore to get additional results. If you want your results to include deleted records in the Recycle Bin, use salesforcerest.queryAllMore in place of salesforcerest.queryMore.

**queryMore**
```xml
<salesforcerest.queryMore>
    <nextRecordsUrl>{$ctx:nextRecordsUrl}</nextRecordsUrl>
</salesforcerest.queryMore>
```
**Properties**
* nextRecordsUrl: The query identifier for retrieving additional results.

**queryAllMore**
```xml
<salesforcerest.queryAllMore>
    <nextRecordsUrl>{$ctx:nextRecordsUrl}</nextRecordsUrl>
</salesforcerest.queryAllMore>
```

**Sample request**

Following is a sample request that can be handled by the queryMore operation.

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "apiVersion": "v32.0",
  "intervalTime" : "100000",
  "nextRecordsUrl": "QWE45HUJ39D9UISD00",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the queryMore operation.

```json
{
    "done" : true,
    "totalSize" : 3214,
    "records" : [...]
}
```

**Related Salesforce documentation**

queryMore : [ https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_query.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_query.htm)
queryAllMore : [ https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_query.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_query.htm)

#### Getting feedback on query performance

To get feedback on how Salesforce will execute your query, use the salesforcerest.queryPerformanceFeedback operation. It uses the Query resource along with the explain parameter to get feedback. Salesforce analyzes each query to find the optimal approach to obtain the query results. Depending on the query and query filters, an index or internal optimization might be used.

**queryPerformanceFeedback**
```xml
<salesforcerest.queryPerformanceFeedback>
    <queryString>{$ctx:queryString}</queryString>
</salesforcerest.queryPerformanceFeedback>
```
**Properties**
* queryString: The SQL query to use to get feedback for a query.

**Sample request**

Following is a sample request that can be handled by the queryPerformanceFeedback operation.

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "apiVersion": "v32.0",
  "intervalTime" : "100000",
  "queryString": "select id, name from Account",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the queryPerformanceFeedback operation.

```json
{
   "plans":[
      {
         "leadingOperationType":"TableScan",
         "relativeCost":2.8324836601307193,
         "sobjectCardinality":2549,
         "fields":[

         ],
         "cardinality":2549,
         "sobjectType":"Account"
      }
   ]
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_query_explain.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_query_explain.htm)

#### Getting feedback on query performance in listview
For retrieving query performance feedback on a report or list view, use salesforcerest.listviewQueryPerformanceFeedback and specify the following properties.

**listviewQueryPerformanceFeedback**
```xml
<salesforcerest.listviewQueryPerformanceFeedback>
    <listViewID>{$ctx:listViewID}</listViewID>
</salesforcerest.listviewQueryPerformanceFeedback>
```
**Properties**
* listViewID: The ID of the report or list view to get feedback for a query.

**Sample request**

Following is a sample request that can be handled by the listviewQueryPerformanceFeedback operation.

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "apiVersion": "v32.0",
  "intervalTime" : "100000",
  "listViewID":"00B28000002yqeVEAQ",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the listviewQueryPerformanceFeedback operation.

```json
{
   "plans":[
      {
         "leadingOperationType":"Index",
         "relativeCost":0,
         "sobjectCardinality":2549,
         "fields":[
            "CreatedDate"
         ],
         "cardinality":0,
         "sobjectType":"Account"
      },
      .
      .
   ]
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_query_explain.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_query_explain.htm)
### Sample configuration

Following example illustrates how to connect to Salesforce with the init operation and query operation.

1. Create a sample proxy as below :
```xml
<proxy xmlns="http://ws.apache.org/ns/synapse" name="query"
       statistics="disable" trace="disable" transports="https http">
    <target>
        <inSequence>
            <property name="accessToken" expression="json-eval($.accessToken)"/>
            <property name="apiUrl" expression="json-eval($.apiUrl)"/>
            <property name="queryString" expression="json-eval($.queryString)"/>
            <property name="clientId" expression="json-eval($.clientId)"/>
            <property name="refreshToken" expression="json-eval($.refreshToken)"/>
            <property name="clientSecret" expression="json-eval($.clientSecret)"/>
            <property name="hostName" expression="json-eval($.hostName)"/>
            <property name="apiVersion" expression="json-eval($.apiVersion)"/>
            <property name="registryPath" expression="json-eval($.registryPath)"/>
            <property name="intervalTime" expression="json-eval($.intervalTime)"/>
            <salesforcerest.init>
                <accessToken>{$ctx:accessToken}</accessToken>
                <apiUrl>{$ctx:apiUrl}</apiUrl>
                <apiVersion>{$ctx:apiVersion}</apiVersion>
                <hostName>{$ctx:hostName}</hostName>
                <clientSecret>{$ctx:clientSecret}</clientSecret>
                <clientId>{$ctx:clientId}</clientId>
                <refreshToken>{$ctx:refreshToken}</refreshToken>
                <registryPath>{$ctx:registryPath}</registryPath>
                <intervalTime>{$ctx:intervalTime}</intervalTime>
            </salesforcerest.init>
            <log category="INFO" level="full" separator=","/>
            <salesforcerest.query>
                <queryString>{$ctx:queryString}</queryString>
            </salesforcerest.query>
            <send/>
        </inSequence>
    </target>
</proxy>
```

2. Create a json file named query.json and copy the configurations given below to it:

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "apiVersion": "v32.0",
  "intervalTime" : "100000",
  "queryString": "select id, name from Account",
  "registryPath": "connectors/SalesforceRest"
}
```
3. Replace the credentials with your values.

4. Execute the following curl command:

```bash
curl http://localhost:8280/services/query -H "Content-Type: application/json" -d @query.json
```
5. Salesforce returns a json response similar to the one shown below:
 
```json
{
    "done" : false,
    "totalSize" : 2014,
    "nextRecordsUrl" : "/services/data/v20.0/query/01gD0000002HU6KIAW-2000",
    "records" : 
    [ 
        {  
            "attributes" : 
            {    
                "type" : "Account",    
                "url" : "/services/data/v20.0/sobjects/Account/001D000000IRFmaIAH"  
            },  
            "Name" : "Test 1"
        }, 
        {  
            "attributes" : 
            {    
                "type" : "Account",    
                "url" : "/services/data/v20.0/sobjects/Account/001D000000IomazIAB"  
            },  
            "Name" : "Test 2"
        }, 

        ...

    ]
}
