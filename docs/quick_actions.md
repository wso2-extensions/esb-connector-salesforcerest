# Working with Quick Actions

[[Overview]](#overview)  [[Operation details]](#operation-details)  [[Sample configuration]](#sample-configuration)

### Overview 

The following operations allow you to work with quick actions for the current organization.  Click an operation name to see details on how to use it.
For a sample proxy service that illustrates how to work with quick actions, see [Sample configuration](#sample-configuration).


| Operation        | Description |
| ------------- |-------------|
| [quickActions](#retrieving-quick-actions)    | 	Retrieves a list of global actions. |
| [sObjectAction](#retrieving-quick-actions-for-a-specific-object)      | Retrieves a list of object-specific actions.|
| [getSpecificAction](#retrieving-a-specific-action)      | Retrieves a specific action for a specific object. |
| [getDescribeSpecificAction](#retrieving-the-description-of-a-specific-action)    | Retrieves the description of a specific action for a specific object.|
| [getDefaultValueOfAction](#retrieving-a-specific-action’s-default-values)      | Retrieves a specific action’s default values, including default field values. |

### Operation details

This section provides more details on each of the operations related to quick actions.

#### Retrieving quick actions

To retrieve a list of global actions, use salesforcerest.quickActions.

**quickActions**
```xml
<salesforcerest.quickActions/>
```

**Sample request**

Following is a sample request that can be handled by the quickActions operation.

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

Given below is a sample response for the quickActions operation.

```json
{
   "output":"[
   {\"label\":\"Log a Call\",
   \"name\":\"LogACall\",
   \"type\":\"LogACall\",
   \"urls\":{\"defaultValues\":\"/services/data/v32.0/quickActions/LogACall/defaultValues\",\"quickAction\":\"/services/data/v32.0/quickActions/LogACall\",\"describe\":\"/services/data/v32.0/quickActions/LogACall/describe\",\"defaultValuesTemplate\":\"/services/data/v32.0/quickActions/LogACall/defaultValues/{ID}\"}},
   .
   .
   ]"
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_quickactions.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_quickactions.htm)

#### Retrieving quick actions for a specific object

To retrieve a list of object-specific actions, use salesforcerest.sObjectAction and specify the following property.

**sObjectAction**
```xml
<salesforcerest.sObjectAction>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
</salesforcerest.sObjectAction>
```
**Properties**
* sObjectName: The type of object for which you want to retrieve a list of quick action.

**Sample request**

Following is a sample request that can be handled by the sObjectAction operation.

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

Given below is a sample response for the sObjectAction operation.

```json
{
   "output":"[
{\"label\":\"Log a Call\",
\"name\":\"LogACall\",\"type\":\"LogACall\",
\"urls\":{\"defaultValues\":\"/services/data/v32.0/quickActions/LogACall/defaultValues\",
\"quickAction\":\"/services/data/v32.0/quickActions/LogACall\",
\"describe\":\"/services/data/v32.0/quickActions/LogACall/describe\",
\"defaultValuesTemplate\":\"/services/data/v32.0/quickActions/LogACall/defaultValues/{ID}\"}},
.
.
]"
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_sobject_quickactions.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_sobject_quickactions.htm)

#### Retrieving a specific action

To retrieve a specific action, use salesforcerest.getSpecificAction and specify the following properties.

**getSpecificAction**
```xml
<salesforcerest.getSpecificAction>
    <actionName>{$ctx:actionName}</actionName>
</salesforcerest.getSpecificAction>
```
**Properties**
* actionName: The action to return.

**Sample request**

Following is a sample request that can be handled by the getSpecificAction operation.

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
  "actionName":"hariprasath__LogACall",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the getSpecificAction operation.

```json
{
   "iconName":null,
   "targetRecordTypeId":null,
   "targetSobjectType":"Task",
   "canvasApplicationName":null,
   "label":"Log a Call",
   "accessLevelRequired":null,
   "icons":[
      {
         "width":0,
         "theme":"theme4",
         "contentType":"image/svg+xml",
         "url":"https://kesavan-dev-ed.my.salesforce.com/img/icon/t4v32/action/log_a_call.svg",
         "height":0
      },
   .
   . 
   ],
   "targetParentField":null,
   "iconUrl":"https://kesavan-dev-ed.my.salesforce.com/img/icon/log_a_call_32.png",
   "height":null
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_sobject_quickactions.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_sobject_quickactions.htm)

#### Retrieving the description of a specific action
To retrieve the description of a specific action, use salesforcerest.getDescribeSpecificAction and specify the following properties.

**getDescribeSpecificAction**
```xml
<salesforcerest.getDescribeSpecificAction>
    <actionName>{$ctx:actionName}</actionName>
</salesforcerest.getDescribeSpecificAction>
```
**Properties**
* actionName: The action whose description you want to retrieve.

**Sample request**

Following is a sample request that can be handled by the getDescribeSpecificAction operation.

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

Given below is a sample response for the getDescribeSpecificAction operation.

```json
{
   "iconName":null,
   "targetRecordTypeId":null,
   "targetSobjectType":"Task",
   "canvasApplicationName":null,
   "label":"Log a Call",
   "accessLevelRequired":null,
   "icons":[
      {
         "width":0,
         "theme":"theme4",
         "contentType":"image/svg+xml",
         "url":"https://kesavan-dev-ed.my.salesforce.com/img/icon/t4v32/action/log_a_call.svg",
         "height":0
      }
   ],
   .
   .
   "targetParentField":null,
   "iconUrl":"https://kesavan-dev-ed.my.salesforce.com/img/icon/log_a_call_32.png",
   "height":null
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_sobject_quickactions.htm](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_sobject_quickactions.htm)

#### Retrieving a specific action’s default values

To return a specific action’s default values, including default field values, use salesforcerest.getDefaultValueOfAction and specify the following properties.

**getDefaultValueOfAction**
```xml
<salesforcerest.getDefaultValueOfAction>
    <actionName>{$ctx:actionName}</actionName>
</salesforcerest.getDefaultValueOfAction>
```
**Properties**
* actionName: The specific action.

**Sample request**

Following is a sample request that can be handled by the getDefaultValueOfAction operation.

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
  "actionName":"hariprasath__LogACall",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the getDefaultValueOfAction operation.

```json
{
   "WhoId":null,
   "Description":null,
   "WhatId":null,
   "attributes":{
      "type":"Task"
   },
   "Subject":"Call"
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_sobject_quickactions.htm](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_sobject_quickactions.htm)

### Sample configuration

Following example illustrates how to connect to Salesforce with the init operation and getSpecificAction operation.

1. Create a sample proxy as below :

```xml
<proxy xmlns="http://ws.apache.org/ns/synapse" name="getSpecificAction"
       statistics="disable" trace="disable" transports="https http">
    <target>
        <inSequence>
            <property name="accessToken" expression="json-eval($.accessToken)"/>
            <property name="apiUrl" expression="json-eval($.apiUrl)"/>
            <property name="actionName" expression="json-eval($.actionName)"/>
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
            <salesforcerest.getSpecificAction>
                <actionName>{$ctx:actionName}</actionName>
            </salesforcerest.getSpecificAction>
            <send/>
        </inSequence>
    </target>
</proxy>
```

2. Create a json file named getSpecificAction.json and copy the configurations given below to it:

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
  "actionName":"hariprasath__LogACall",
  "registryPath": "connectors/SalesforceRest"
}
```
3. Replace the credentials with your values.

4. Execute the following curl command:

```bash
curl http://localhost:8280/services/getSpecificAction -H "Content-Type: application/json" -d @getSpecificAction.json
```
5. Salesforce returns a json response similar to the one shown below:
 
```json
{
   "iconName":null,
   "targetRecordTypeId":null,
   "targetSobjectType":"Task",
   "canvasApplicationName":null,
   "label":"Log a Call",
   "accessLevelRequired":null,
   "icons":[
      {
         "width":0,
         "theme":"theme4",
         "contentType":"image/svg+xml",
         "url":"https://kesavan-dev-ed.my.salesforce.com/img/icon/t4v32/action/log_a_call.svg",
         "height":0
      },
   .
   . 
   ],
   "targetParentField":null,
   "iconUrl":"https://kesavan-dev-ed.my.salesforce.com/img/icon/log_a_call_32.png",
   "height":null
}
```
