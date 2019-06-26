# Working with List Views

[[Overview]](#overview)  [[Operation details]](#operation-details)  [[Sample configuration]](#sample-configuration)

### Overview 

The following operations allow you to work with  list views.  Click an operation name to see details on how to use it.
For a sample proxy service that illustrates how to work with  list views, see [Sample configuration](#sample-configuration).

| Operation        | Description |
| ------------- |-------------|
| [listViews](#retrieving-a-list-of-list-views-for-the-specified-sObject)    | 	Retrieves the list of list views for the specified sObject. |
| [listViewById](#retrieving-basic-information-about-one-list-view-for-the-specified-sobject)      | Retrieves the basic information about one list view for the specified sObject.|
| [recentListViews](#retrieving-the-list-of-recently-used-list-views)      | Retrieves the list of recently used list views for the given sObject type. |
| [describeListViewById](#retrieving-detailed-information-about-a-list-view)    | Returns detailed information about a list view, including the ID, the columns, and the SOQL query. |
| [listViewResults](#xecuting-the-soql-query-for-the-list-view)      | Executes the SOQL query for the list view and return the resulting data and presentation information. |

### Operation details

This section provides more details on each of the operations related to list views.

#### Retrieving a list of list views for the specified sObject
To retrieve a list of list views for the specific sObject, use salesforcerest.listViews and specify the following properties.

**listViews**
```xml
<salesforcerest.listViews>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
</salesforcerest.listViews>
```
**Properties**
* sObjectName: The type of object whose list biews you want to retrieve.

**Sample request**

Following is a sample request that can be handled by the sObjectLayouts operation.

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
  "sObjectName": "Account",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the sObjectLayouts operation.

```json
{
   "nextRecordsUrl":null,
   "size":7,
   "listviews":[
      {
         "resultsUrl":"/services/data/v32.0/sobjects/Account/listviews/00B280000032AihEAE/results",
         "soqlCompatible":true,
         "id":"00B280000032AihEAE",
         "label":"New This Week",
         "describeUrl":"/services/data/v32.0/sobjects/Account/listviews/00B280000032AihEAE/describe",
         "developerName":"NewThisWeek",
         "url":"/services/data/v32.0/sobjects/Account/listviews/00B280000032AihEAE"
      },
      .
      .
   ],
   "done":true,
   "sobjectType":"Account"
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_listviews.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_listviews.htm)

#### Retrieving basic information about one list view for the specified sObject

To retrieve the basic information about one list view for the specific sObject, use salesforcerest.listViewById and specify the following properties.

**listViewById**
```xml
<salesforcerest.listViewById>
    <listViewID>{$ctx:listViewID}</listViewID>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
</salesforcerest.listViewById>
```
**Properties**
* sObjectName: The type of the object to get the list of list views.
* listViewId: The ID of the specific list view whose information you want to return.

**Sample request**

Following is a sample request that can be handled by the listViewById operation.

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "apiVersion": "v32.0",
  "sObjectName": "Account",
  "intervalTime" : "100000",
  "listViewID":"00B28000002yqeVEAQ",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the listViewById operation.

```json
{
   "resultsUrl":"/services/data/v32.0/sobjects/Account/listviews/00B280000032AihEAE/results",
   "soqlCompatible":true,
   "id":"00B280000032AihEAE",
   "label":"New This Week",
   "describeUrl":"/services/data/v32.0/sobjects/Account/listviews/00B280000032AihEAE/describe",
   "developerName":"NewThisWeek",
   "url":"/services/data/v32.0/sobjects/Account/listviews/00B280000032AihEAE"
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_listviews.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_listviews.htm)

#### Retrieving the list of recently used list views

To retrieve the list of recently used list views for the given sObject type, use salesforcerest.recentListViews and specify the following properties.

**recentListViews**
```xml
<salesforcerest.recentListViews>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
</salesforcerest.recentListViews>
```
**Properties**
* sObjectName: The type of object whose recently used list views you want to return.

**Sample request**

Following is a sample request that can be handled by the recentListViews operation.

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "apiVersion": "v32.0",
  "sObjectName": "Account",
  "intervalTime" : "100000",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the recentListViews operation.

```json
{
   "nextRecordsUrl":null,
   "size":2,
   "listviews":[
      {
         "resultsUrl":"/services/data/v32.0/sobjects/Account/listviews/00B280000032AihEAE/results",
         "soqlCompatible":true,
         "id":"00B280000032AihEAE",
         "label":"New This Week",
         "describeUrl":"/services/data/v32.0/sobjects/Account/listviews/00B280000032AihEAE/describe",
         "developerName":"NewThisWeek",
         "url":"/services/data/v32.0/sobjects/Account/listviews/00B280000032AihEAE"
      }
      .
      .
   ],
   "done":true,
   "sobjectType":"Account"
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_recentlistviews.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_recentlistviews.htm)

#### Retrieving detailed information about a list view
To retrieve detailed information (ID, columns, and SOQL query) about a specific list view for the given sObject type, use salesforcerest.describeListViewById and specify the following properties.

**describeListViewById**
```xml
<salesforcerest.describeListViewById>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
    <listViewID>{$ctx:listViewID}</listViewID>
</salesforcerest.describeListViewById>
```
**Properties**
* sObjectName: The type of object to which the list view applies.
* listViewID: The ID of the list view.

**Sample request**

Following is a sample request that can be handled by the describeListViewById operation.

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "apiVersion": "v32.0",
  "sObjectName": "Account",
  "listViewID":"00B28000002yqeVEAQ",
  "intervalTime" : "100000",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the describeListViewById operation.

```json
{
   "whereCondition":{
      "field":"CreatedDate",
      "values":[
         "THIS_WEEK"
      ],
      "operator":"equals"
   },
   "columns":[
      {
         "fieldNameOrPath":"Name",
         "sortDirection":"ascending",
         "hidden":false,
         "sortIndex":0,
         "ascendingLabel":"Z-A",
         "label":"Account Name",
         "sortable":true,
         "type":"string",
         "descendingLabel":"A-Z",
         "selectListItem":"Name"
      },
      .
      .
   ],
   "query":"SELECT Name, Site, BillingState, Phone, toLabel(Type), Owner.Alias, Id, CreatedDate, LastModifiedDate, SystemModstamp FROM Account WHERE CreatedDate = THIS_WEEK ORDER BY Name ASC NULLS FIRST, Id ASC NULLS FIRST",
   "scope":null,
   "orderBy":[
      {
         "fieldNameOrPath":"Name",
         "sortDirection":"ascending",
         "nullsPosition":"first"
      },
      {
         "fieldNameOrPath":"Id",
         "sortDirection":"ascending",
         "nullsPosition":"first"
      }
   ],
   "id":"00B280000032Aih",
   "sobjectType":"Account"
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_listviewdescribe.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_listviewdescribe.htm)

#### Executing the SOQL query for the list view

To execute the SOQL query for the list view and return the resulting data and presentation information, use salesforcerest.listViewResults and specify the following properties.

**listViewResults**
```xml
<salesforcerest.listViewResults>
    <listViewID>{$ctx:listViewID}</listViewID>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
</salesforcerest.listViewResults>
```
**Properties**
* sObjectName: The type of object to which the list view applies.
* listViewID: The ID of the list view.

**Sample request**

Following is a sample request that can be handled by the listViewResults operation.

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "apiVersion": "v32.0",
  "sObjectName": "Account",
  "intervalTime" : "100000",
  "listViewID":"00B28000002yqeVEAQ",
  "registryPath": "connectors/SalesforceRest"
 }
```
**Sample response**

Given below is a sample response for the listViewResults operation.

```json
{
   "size":0,
   "records":[

   ],
   "columns":[
      {
         "fieldNameOrPath":"Name",
         "sortDirection":"ascending",
         "hidden":false,
         "sortIndex":0,
         "ascendingLabel":"Z-A",
         "label":"Account Name",
         "sortable":true,
         "type":"string",
         "descendingLabel":"A-Z",
         "selectListItem":"Name"
      },
      .
      .
   ],
   "id":"00B280000032Aih",
   "label":"New This Week",
   "developerName":"NewThisWeek",
   "done":true
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_listviewresults.htm?search_text=list%20view](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_listviewresults.htm?search_text=list%20view)

### Sample configuration

Following example illustrates how to connect to Salesforce with the init operation and recentListViews operation.

1. Create a sample proxy as below :
```xml
<proxy xmlns="http://ws.apache.org/ns/synapse" name="recentListViews"
       statistics="disable" trace="disable" transports="https http">
    <target>
        <inSequence>
            <property name="accessToken" expression="json-eval($.accessToken)"/>
            <property name="apiUrl" expression="json-eval($.apiUrl)"/>
            <property name="sObjectName" expression="json-eval($.sObjectName)"/>
            <property name="clientId" expression="json-eval($.clientId)"/>
            <property name="refreshToken" expression="json-eval($.refreshToken)"/>
            <property name="clientSecret" expression="json-eval($.clientSecret)"/>
            <property name="hostName" expression="json-eval($.hostName)"/>
            <property name="apiVersion" expression="json-eval($.apiVersion)"/>
            <property name="registryPath" expression="json-eval($.registryPath)"/>
            <property name="intervalTime" expression="json-eval($.intervalTime)"/>
            <property name="blocking" expression="json-eval($.blocking)"/>
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
                <blocking>{$ctx:blocking}</blocking>
            </salesforcerest.init>
            <log category="INFO" level="full" separator=","/>
            <salesforcerest.recentListViews>
                <sObjectName>{$ctx:sObjectName}</sObjectName>
            </salesforcerest.recentListViews>
            <send/>
        </inSequence>
    </target>
</proxy>
```

2. Create a json file named recentListViews.json and copy the configurations given below to it:

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "apiVersion": "v32.0",
  "sObjectName": "Account",
  "intervalTime" : "100000",
  "registryPath": "connectors/SalesforceRest"
}                       
```
3. Replace the credentials with your values.

4. Execute the following curl command:

```bash
curl http://localhost:8280/services/recentListViews -H "Content-Type: application/json" -d @recentListViews.json
```
5. Salesforce returns a json response similar to the one shown below:
 
```json
{
   "nextRecordsUrl":null,
   "size":2,
   "listviews":[
      {
         "resultsUrl":"/services/data/v32.0/sobjects/Account/listviews/00B280000032AihEAE/results",
         "soqlCompatible":true,
         "id":"00B280000032AihEAE",
         "label":"New This Week",
         "describeUrl":"/services/data/v32.0/sobjects/Account/listviews/00B280000032AihEAE/describe",
         "developerName":"NewThisWeek",
         "url":"/services/data/v32.0/sobjects/Account/listviews/00B280000032AihEAE"
      }
      .
      .
   ],
   "done":true,
   "sobjectType":"Account"
}
```
