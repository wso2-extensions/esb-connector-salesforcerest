# Working with Search

[[Overview]](#overview)  [[Operation details]](#operation-details)  [[Sample configuration]](#sample-configuration)

### Overview 

The following operations allow you to work with  search.  Click an operation name to see details on how to use it.
For a sample proxy service that illustrates how to work with  search, see [Sample configuration](#sample-configuration).

| Operation        | Description |
| ------------- |-------------|
| [search](#searching-for-records)    | 	Searches for records in Salesforce. |
| [searchScopeAndOrder](#retrieving-the-search-scope-and-order)      | Retrieves the search scope and order for the current user.|
| [searchResultLayout](#retrieving-the-search-result-layouts)      | Retrieves the search result layouts for one or more sObjects. |
| [searchSuggestedRecords](#returning-a-list-of-suggested-records)    | Returns a list of suggested records whose names match the user’s search string. |

### Operation details

This section provides more details on each of the operations related to search.

#### Searching for records
To search for records, use salesforcerest.search and specify the search string. For more information on how Salesforce performs the search, see https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_search.htm.

**search**
```xml
<salesforcerest.search>
    <searchString>{$ctx:searchString}</searchString>
</salesforcerest.search>
```
**Properties**
* searchString: The SQL query to use to search for records.

**Sample request**

Following is a sample request that can be handled by the search operation.

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
  "searchString": "FIND {map*} IN ALL FIELDS RETURNING Account (Id, Name), Contact, Opportunity, Lead",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the search operation.

```json
{"output":"[{\"attributes\":{\"type\":\"Account\",\"url\":\"/services/data/v32.0/sobjects/Account/00128000005dMcSAAU\"},\"Id\":\"00128000005dMcSAAU\",\"Name\":\"GenePoint\"}]"}
```

**Related Salesforce documentation**

[ https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_search.htm.]( https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_search.htm)

#### Retrieving the search scope and order

To retrieve the search scope and order for the currently logged-in user, use salesforcerest.searchScopeAndOrder. For more information on search scope and order, see https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_search_scope_order.htm.

**searchScopeAndOrder**
```xml
<salesforcerest.searchScopeAndOrder/>
```

**Sample request**

Following is a sample request that can be handled by the searchScopeAndOrder operation.

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
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the searchScopeAndOrder operation.

```json
{"output":"[]"}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_search_scope_order.htm.](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_search_scope_order.htm.)

#### Retrieving the search result layouts

To retrieve the search result layouts for one or more sObjects, use salesforcerest.searchResultLayout and specify the following properties. For more information on search result layouts, see https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_retrieve_search_layouts.htm.

**searchResultLayout**
```xml
<salesforcerest.searchResultLayout>
    <sObjectNameList>{$ctx:sObjectNameList}</sObjectNameList>
</salesforcerest.searchResultLayout>
```
**Properties**
* sObjectNameList: A comma-delimited list of the objects whose search result layouts you want to retrieve.

**Sample request**

Following is a sample request that can be handled by the searchResultLayout operation.

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
  "sObjectNameList": "Account,User",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the searchResultLayout operation.

```json
{"output":"[{\"errorMsg\":null,\"label\":\"Search Results\",\"limitRows\":25,\"objectType\":\"Account\",\"searchColumns\":[{\"field\":\"Account.Name\",\"format\":null,\"label\":\"Account Name\",\"name\":\"Name\"},{\"field\":\"Account.Site\",\"format\":null,\"label\":\"Account Site\",\"name\":\"Site\"},.]"}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_retrieve_search_layouts.htm.](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_retrieve_search_layouts.htm.)

#### Returning a list of suggested records
To return a list of suggested records whose names match the user’s search string, use salesforcerest.searchSuggestedRecords and specify the following properties.

**searchSuggestedRecords**
```xml
<salesforcerest.searchSuggestedRecords>
    <stringForSearch>{$ctx:stringForSearch}</stringForSearch>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
</salesforcerest.searchSuggestedRecords>
```
**Properties**
* sObjectName: The object type that the search is scoped to.
* stringForSearch: The SOQL query to execute the search.

**Sample request**

Following is a sample request that can be handled by the searchSuggestedRecords operation.

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
  "stringForSearch": "hari",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the searchSuggestedRecords operation.

```json
{"autoSuggestResults":[],"hasMoreResults":false}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_search_suggest_records.htm?search_text=search%20Suggested%20records](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_search_suggest_records.htm?search_text=search%20Suggested%20records)

### Sample configuration

Following example illustrates how to connect to Salesforce with the init operation and search operation.

1. Create a sample proxy as below :
```xml
<proxy xmlns="http://ws.apache.org/ns/synapse" name="search"
       statistics="disable" trace="disable" transports="https http">
    <target>
        <inSequence>
            <property name="accessToken" expression="json-eval($.accessToken)"/>
            <property name="apiUrl" expression="json-eval($.apiUrl)"/>
            <property name="searchString" expression="json-eval($.searchString)"/>
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
            <salesforcerest.search>
                <searchString>{$ctx:searchString}</searchString>
            </salesforcerest.search>
            <send/>
        </inSequence>
    </target>
</proxy>
```

2. Create a json file named search.json and copy the configurations given below to it:

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
  "searchString": "FIND {map*} IN ALL FIELDS RETURNING Account (Id, Name), Contact, Opportunity, Lead",
  "registryPath": "connectors/SalesforceRest"
}
```

3. Replace the credentials with your values.

4. Execute the following curl command:

```bash
curl http://localhost:8280/services/search -H "Content-Type: application/json" -d @search.json
```
5. Salesforce returns a json response similar to the one shown below:
 
```json
{"output":"[{\"attributes\":{\"type\":\"Account\",\"url\":\"/services/data/v32.0/sobjects/Account/00128000005dMcSAAU\"},\"Id\":\"00128000005dMcSAAU\",\"Name\":\"GenePoint\"}]"}
```
