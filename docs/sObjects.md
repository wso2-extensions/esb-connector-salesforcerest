# Working with sObjects

[[Overview]](#overview)  [[Operation details]](#operation-details)  [[Sample configuration]](#sample-configuration)

### Overview 

The following operations allow you to work with sObjects for the current organization.  Click an operation name to see details on how to use it.
For a sample proxy service that illustrates how to work with sObjects, see [Sample configuration](#sample-configuration).

| Operation        | Description |
| ------------- |-------------|
| [describeGlobal](#retrieving-a-list-of-layouts-and-descripti)    | Retrieves the objects that are available in the system. |
| [describeSObject](#retrieving-metadata-for-a-specific-object-type)      | Retrieves metadata (such as name, label, and fields, including the field properties) for a specific object type, completely describing the individual metadata at all levels for the specified object.|
| [listAvailableApiVersion	](#retrieving-a-list-of-summary-information-about-each-rest-api-version)      | Retrieves a list of summary information about each REST API version currently available. |
| [listOrganizationLimits](#retrieving-the-limits-information-for-your-organization)    | Retrieves the limits information for your organization. |
| [listResourcesByApiVersion](#retrieving-resources-available-from-the-api-version)      | Retrieves the resources that are available in a particular API version. |
| [sObjectBasicInfo](#retrieving-basic-information-for-a-specific-object-type)   | Describes the individual metadata for the specified object.|
| [sObjectPlatformAction](#retrieving-the-description-of-the-platformaction)       | Retrieves the description of the PlatformAction. |
| [sObjectRows](retrieving-details-of-a-specific-record)   | Retrieves records based on the specified object ID.|

### Operation details

This section provides more details on each of the operations related to sObjects.
An sObject refers to any object that can be stored in the Force.com platform database. sObjects include all the standard and custom objects in your organization.

#### Retrieving a list of available objects
To retrieve a list of the objects that are available in the system, use salesforcerest.describeGlobal. You can then get metadata for an object or objects as described in the next sections.

**describeGlobal**
```xml
<salesforcerest.describeGlobal/>
```

**Sample request**

Following is a sample request that can be handled by the describeGlobal operation.

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

Given below is a sample response for the describeGlobal operation.

```json
{
   "maxBatchSize":200,
   "sobjects":[
      {
         "updateable":false,
         "activateable":false,
         "deprecatedAndHidden":false,
         "layoutable":false,
         "custom":false,
         "deletable":false,
         "replicateable":false,
         "undeletable":false,
         "label":"Accepted Event Relation",
         "keyPrefix":null,
         "searchable":false,
         "queryable":true,
         "mergeable":false,
         "urls":{
            "rowTemplate":"/services/data/v32.0/sobjects/AcceptedEventRelation/{ID}",
            "describe":"/services/data/v32.0/sobjects/AcceptedEventRelation/describe",
            "sobject":"/services/data/v32.0/sobjects/AcceptedEventRelation"
         },
         "createable":false,
         "feedEnabled":false,
         "retrieveable":true,
         "name":"AcceptedEventRelation",
         "customSetting":false,
         "labelPlural":"Accepted Event Relations",
         "triggerable":false
      },
      .
      .
   ],
   "encoding":"UTF-8"
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_describeGlobal.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_describeGlobal.htm)

#### Retrieving metadata for a specific object type

To get metadata (such as name, label, and fields, including the field properties) for a specific object type, use salesforcerest.describeSobject and specify the following properties. For more information on retrieving object metadata, see https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_sobject_describe.htm.

**describeSObject**
```xml
<salesforcerest.describeSObject>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
</salesforcerest.describeSObject>
```
**Properties**
* sObjectName: The object type whose metadata you want to retrieve.

**Sample request**

Following is a sample request that can be handled by the describeSObject operation.

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
  "sObjectName":"Account",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the describeSObject operation.

```json
{
   "updateable":true,
   "activateable":false,
   "childRelationships":[
      {
         "relationshipName":"ChildAccounts",
         "field":"ParentId",
         "deprecatedAndHidden":false,
         "childSObject":"Account",
         "cascadeDelete":false,
         "restrictedDelete":false
      },
      {
         "relationshipName":"AccountCleanInfos",
         "field":"AccountId",
         "deprecatedAndHidden":false,
         "childSObject":"AccountCleanInfo",
         "cascadeDelete":true,
         "restrictedDelete":false
      },
      .
   ]
}      
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_sobject_describe.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_sobject_describe.htm)

#### Retrieving a list of summary information about each REST API version

To retrieve a list of summary information about each REST API version that is currently available, use salesforcerest.listAvailableApiVersion.

**listAvailableApiVersion**
```xml
<salesforcerest.listAvailableApiVersion/>
```

**Sample request**

Following is a sample request that can be handled by the listAvailableApiVersion operation.

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

Given below is a sample response for the listAvailableApiVersion operation.

```json
{
   "output":"[
      {\"label\":\"Winter '11\",\"url\":\"/services/data/v20.0\",\"version\":\"20.0\"},
      .
      .
]"
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/dome_versions.htm](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/dome_versions.htm)

#### Retrieving the limits information for your organization
To retrieve the limit information for your organization, use salesforcerest.listOrganizationLimits

**listOrganizationLimits**
```xml
<salesforcerest.listOrganizationLimits/>
```

**Sample request**

Following is a sample request that can be handled by the listOrganizationLimits operation.

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

Given below is a sample response for the listOrganizationLimits operation.

```json
{
   "DailyApiRequests":{
      "Dataloader Bulk":{
         "Max":0,
         "Remaining":0
      },
      "test":{
         "Max":0,
         "Remaining":0
      },
      "Max":5000,
      "Salesforce Mobile Dashboards":{
         "Max":0,
         "Remaining":0
      },
      .
      .
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/dome_limits.htm](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/dome_limits.htm)

#### Retrieving resources available from the API version

To retrieve the resources that are available in the specified API version, use salesforcerest.listResourcesByApiVersion. You can then get the details of those resources.

**listResourcesByApiVersion**
```xml
<salesforcerest.listResourcesByApiVersion/>
```

**Sample request**

Following is a sample request that can be handled by the listResourcesByApiVersion operation.

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

Given below is a sample response for the listResourcesByApiVersion operation.

```json
{
   "tooling":"/services/data/v32.0/tooling",
   "folders":"/services/data/v32.0/folders",
   "eclair":"/services/data/v32.0/eclair",
   "prechatForms":"/services/data/v32.0/prechatForms",
   "chatter":"/services/data/v32.0/chatter",
   "tabs":"/services/data/v32.0/tabs",
   "appMenu":"/services/data/v32.0/appMenu",
   "quickActions":"/services/data/v32.0/quickActions",
   "queryAll":"/services/data/v32.0/queryAll",
   "commerce":"/services/data/v32.0/commerce",
   .
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_discoveryresource.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_discoveryresource.htm)

#### Retrieving basic information for a specific object type

To retrieve the individual metadata for the specified object, use salesforcerest.sObjectBasicInfo.

**sObjectBasicInfo**
```xml
<salesforcerest.sObjectBasicInfo>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
</salesforcerest.sObjectBasicInfo>
```
**Properties**
* sObjectName: The type of object whose metadata you want to retrieve.

**Sample request**

Following is a sample request that can be handled by the sObjectBasicInfo operation.

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
  "sObjectName":"Account",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the sObjectNamedLayouts operation.

```json
{
   "objectDescribe":{
      "updateable":true,
      "activateable":false,
      "deprecatedAndHidden":false,
      "layoutable":true,
      "custom":false,
      "deletable":true,
      "replicateable":true,
      "undeletable":true,
      "label":"Account",
      "keyPrefix":"001",
      "searchable":true,
      "queryable":true,
      "mergeable":true,
      "urls":{
         "compactLayouts":"/services/data/v32.0/sobjects/Account/describe/compactLayouts",
         "rowTemplate":"/services/data/v32.0/sobjects/Account/{ID}"
      },
      "createable":true,
      "feedEnabled":true,
      "retrieveable":true,
      "name":"Account",
      "customSetting":false,
      "labelPlural":"Accounts",
      "triggerable":true
   },
   .
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_sobject_basic_info.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_sobject_basic_info.htm)
                                                                                                 )

#### Retrieving the description of the PlatformAction

To retrieve the description of the PlatformAction, use salesforcerest.sObjectPlatformAction.

**sObjectPlatformAction**
```xml
<salesforcerest.sObjectPlatformAction/>
```

**Sample request**

Following is a sample request that can be handled by the sObjectPlatformAction operation.

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

Given below is a sample response for the sObjectPlatformAction operation.

```json
{
   "objectDescribe":{
      "updateable":false,
      "activateable":false,
      "deprecatedAndHidden":false,
      "layoutable":false,
      "custom":false,
      "deletable":false,
      "replicateable":false,
      "undeletable":false,
      "label":"Platform Action",
      "keyPrefix":"0JV",
      "searchable":false,
      "queryable":true,
      "mergeable":false,
      "urls":{
         "rowTemplate":"/services/data/v32.0/sobjects/PlatformAction/{ID}",
         "describe":"/services/data/v32.0/sobjects/PlatformAction/describe",
         "sobject":"/services/data/v32.0/sobjects/PlatformAction"
      },
      "createable":false,
      "feedEnabled":false,
      "retrieveable":false,
      "name":"PlatformAction",
      "customSetting":false,
      "labelPlural":"Platform Actions",
      "triggerable":false
   },
   "recentItems":[

   ]
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_sobject_platformaction.htm?search_text=PlatformAction](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_sobject_platformaction.htm?search_text=PlatformAction)

#### Retrieving details of a specific record 

To retrieve details of a specific record, use salesforcerest.sObjectRows.
 
**sObjectRows**
```xml
<salesforcerest.sObjectRows>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
    <rowId>{$ctx:rowId}</rowId>
</salesforcerest.sObjectRows>
```
**Properties**
* sObjectName: The object type of the record.
* rowId: The ID of the record whose details you want to retrieve.

**Sample request**

Following is a sample request that can be handled by the sObjectRows operation.

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "apiVersion": "v32.0",
  "sObjectName":"Account",
  "intervalTime" : "100000",
  "rowId":"00128000005YjDnAAK",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the sObjectRows operation.

```json
{
    "AccountNumber" : "CD656092",
    "BillingPostalCode" : "27215",
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_sobject_retrieve.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_sobject_retrieve.htm)

### Sample configuration

Following example illustrates how to connect to Salesforce with the init operation and sObjectGetUpdated operation.

1. Create a sample proxy as below :

```xml
<proxy xmlns="http://ws.apache.org/ns/synapse" name="sObjectGetUpdated"
       statistics="disable" trace="disable" transports="https http">
    <target>
        <inSequence>
            <property name="accessToken" expression="json-eval($.accessToken)"/>
            <property name="apiUrl" expression="json-eval($.apiUrl)"/>
            <property name="sObjectName" expression="json-eval($.sObjectName)"/>
            <property name="startTime" expression="json-eval($.startTime)"/>
            <property name="endTime" expression="json-eval($.endTime)"/>
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
            <salesforcerest.sObjectGetUpdated>
                <sObjectName>{$ctx:sObjectName}</sObjectName>
                <startTime>{$ctx:startTime}</startTime>
                <endTime>{$ctx:endTime}</endTime>
            </salesforcerest.sObjectGetUpdated>
            <send/>
        </inSequence>
    </target>
</proxy>
```

2. Create a json file named sObjectGetUpdated.json and copy the configurations given below to it:

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "apiVersion": "v32.0",
  "sObjectName":"Account",
  "intervalTime" : "100000",
  "startTime":"2015-10-05T12:30:30+05:30",
  "endTime":"2015-10-10T20:30:30+05:30",
  "registryPath": "connectors/SalesforceRest"
}
```

3. Replace the credentials with your values.

4. Execute the following curl command:

```bash
curl http://localhost:8280/services/sObjectGetUpdated -H "Content-Type: application/json" -d @sObjectGetUpdated.json
```
5. Salesforce returns a json response similar to the one shown below:
 
```json
{
   "ids":[

   ],
   "latestDateCovered":"2018-10-27T15:00:00.000+0000"
}
```
