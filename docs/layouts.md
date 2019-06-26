# Working with Layouts

[[Overview]](#overview)  [[Operation details]](#operation-details)  [[Sample configuration]](#sample-configuration)

### Overview 

The following operations allow you to work with layouts for the current organization.  Click an operation name to see details on how to use it.
For a sample proxy service that illustrates how to work with layouts, see [Sample configuration](#sample-configuration).

**Info**

These resources are available in the Salesforce REST API version 32.0 and later.

| Operation        | Description |
| ------------- |-------------|
| [sObjectLayouts](#retrieving-a-list-of-layouts-and-descriptions-for-a-specific-object)    | 	Returns a list of layouts and descriptions (including for actions) for a specific object. |
| [globalSObjectLayouts](#retrieving-descriptions-of-global-publisher-layouts)      | Return descriptions of global publisher layouts.|
| [compactLayouts](#retrieving-a-list-of-compact-layouts-for-multiple-objects)      | Returns a list of compact layouts for multiple objects. |
| [sObjectApprovalLayouts](#retrieving-a-list-of-approval-layouts-for-a-specified-object)    | Returns a list of approval layouts for a specific object. |
| [sObjectCompactLayouts](#retrieving-a-list-of-ompact-layouts-for-a-specific-object)      | Returns a list of compact layouts for a specific object. |
| [sObjectNamedLayouts](#retrieving-information-about-alternate-named-layouts-for-a-specific-object)   | Returns information about alternate named layouts for a specific object.|

### Operation details

This section provides more details on each of the operations related to layouts.

#### Retrieving a list of layouts and descriptions for a specific object
To retrieve a list of layouts and descriptions (including for actions) for a specific object, use salesforcerest.sObjectLayouts and specify the following properties.

**sObjectLayouts**
```xml
<salesforcerest.sObjectLayouts>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
</salesforcerest.sObjectLayouts>
```
**Properties**
* sObjectName: The type of object whose layouts and descriptions you want to retrieve.

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
   "layouts":[
      {
         "detailLayoutSections":[
            {
               "heading":"Account Information",
               "columns":2,
               "tabOrder":"TopToBottom",
               "useCollapsibleSection":false,
               "rows":8,
               "useHeading":false,
               "layoutRows":[
                  {
                     "layoutItems":[
                        {
                           "editableForUpdate":false,
                           "editableForNew":false,
                           "layoutComponents":[
                              {
                                 "tabOrder":1,
                                 "details":{
                                    "defaultValue":null,
                                    "precision":0,
                                    "nameField":false,
                                    "type":"reference",
                                    "restrictedDelete":false,
                                    "relationshipName":"Owner",
                                    "calculatedFormula":null,
                                    "controllerName":null,
                                    "namePointing":false,
                                    "defaultValueFormula":null,
                                    "calculated":false,
                                    "writeRequiresMasterRead":false,
                                    "inlineHelpText":null,
                                    "picklistValues":[

                                    ]
                               }
                        }
                    ]
                 }
.
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_sobject_layouts.htm?search_text=layouts](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_sobject_layouts.htm?search_text=layouts)

#### Retrieving descriptions of global publisher layouts

To retrieve descriptions of global publisher layouts, use salesforcerest.globalSObjectLayouts.

**globalSObjectLayouts**
```xml
<salesforcerest.globalSObjectLayouts/>
```

**Sample request**

Following is a sample request that can be handled by the globalSObjectLayouts operation.

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

Given below is a sample response for the globalSObjectLayouts operation.

```json
{
   "layouts":[
      {
         "detailLayoutSections":[

         ],
         "relatedContent":null,
         "editLayoutSections":[

         ],
         "relatedLists":[

         ],
         "buttonLayoutSection":null,
         "id":"00h28000001hExeAAE",
         "offlineLinks":[

         ],
         .
         .
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_sobject_layouts.htm?search_text=layouts](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_sobject_layouts.htm?search_text=layouts)

#### Retrieving a list of compact layouts for multiple objects

To retrieve a list of compact layouts for multiple objects, use salesforcerest.compactLayouts and specify the following properties.

**compactLayouts**
```xml
<salesforcerest.compactLayouts>
    <sObjectNameList>{$ctx:sObjectNameList}</sObjectNameList>
</salesforcerest.compactLayouts>
```
**Properties**
* sObjectNameList: A comma-separated list of the objects whose compact layouts you want to retrieve.

**Sample request**

Following is a sample request that can be handled by the compactLayouts operation.

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
  "sObjectNameList":"Account,User",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the compactLayouts operation.

```json
{
   "Account":{
      "name":"SYSTEM",
      "id":null,
      "label":"System Default",
      "actions":[
         {
            "showsStatus":false,
            "custom":false,
            "label":"Call",
            "overridden":false,
            "encoding":null,
            "icons":[
               {
                  "width":0,
                  "theme":"theme4",
                  "contentType":"image/svg+xml",
                  "url":"https://kesavan-dev-ed.my.salesforce.com/img/icon/t4v32/action/call.svg",
                  "height":0
               },
            ],
            "windowPosition":null,
            "colors":[
               {
                  "color":"F2CF5B",
                  "context":"primary",
                  "theme":"theme4"
               }
            ],
      .
      .
      ],
      "objectType":"User"
   }
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_compact_layouts.htm?search_text=layouts](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_compact_layouts.htm?search_text=layouts)

#### Retrieving a list of approval layouts for a specified object
To retrieve a list of approval layouts for a specified object, use salesforcerest.sObjectApprovalLayouts and specify the following properties.

**sObjectApprovalLayouts**
```xml
<salesforcerest.sObjectApprovalLayouts>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
</salesforcerest.sObjectApprovalLayouts>
```
**Properties**
* sObjectName: The type of object whose layouts you want to retrieve.

**Sample request**

Following is a sample request that can be handled by the sObjectApprovalLayouts operation.

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

Given below is a sample response for the sObjectApprovalLayouts operation.

```json
{"approvalLayouts":[]}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_sobject_approvallayouts.htm?search_text=layouts](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_sobject_approvallayouts.htm?search_text=layouts)

#### Retrieving a list of compact layouts for a specific object

To retrieve a list of compact layouts for a specific object, use salesforcerest.sObjectCompactLayouts and specify the following properties.

**sObjectCompactLayouts**
```xml
<salesforcerest.sObjectCompactLayouts>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
</salesforcerest.sObjectCompactLayouts>
```
**Properties**
* sObjectName: The type of object whose layouts you want to retrieve.

**Sample request**

Following is a sample request that can be handled by the sObjectCompactLayouts operation.

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

Given below is a sample response for the sObjectCompactLayouts operation.

```json
{
   "compactLayouts":[
      {
         "name":"SYSTEM",
         "id":null,
         "label":"System Default",
         "actions":[
            {
               "showsStatus":false,
               "custom":false,
               "label":"Call",
               "overridden":false,
               "encoding":null,
               "icons":[
                  {
                     "width":0,
                     "theme":"theme4",
                     "contentType":"image/svg+xml",
                     "url":"https://kesavan-dev-ed.my.salesforce.com/img/icon/t4v32/action/call.svg",
                     "height":0
                  }
               ],
               "defaultCompactLayoutId":null
    .
    ]
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_sobject_compactlayouts.htm?search_text=layouts](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_sobject_compactlayouts.htm?search_text=layouts)

#### Retrieving information about alternate named layouts for a specific object

To retrieve information about alternative named layouts for a specific object, use salesforcerest.sObjectNamedLayouts and specify the following properties.

**sObjectNamedLayouts**
```xml
<salesforcerest.sObjectNamedLayouts>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
    <layoutName>{$ctx:layoutName}</layoutName>
</salesforcerest.sObjectNamedLayouts>
```
**Properties**
* sObjectName: The type of object whose layouts you want to retrieve.
* layoutName: The type of the layout.

**Sample request**

Following is a sample request that can be handled by the sObjectNamedLayouts operation.

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
  "layoutName": "UserAlt",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the sObjectNamedLayouts operation.

```json
{
   "layouts":[
      {
         "detailLayoutSections":[
            {
               "heading":"About",
               "columns":2,
               "tabOrder":"LeftToRight",
               "useCollapsibleSection":false,
               "rows":2,
               "useHeading":false,
               "layoutRows":[
                  {
                     "layoutItems":[
                        {
                           "editableForUpdate":false,
                           "editableForNew":false,
                           "layoutComponents":[
                              {
                                 "components":[
                                    {
                                       "tabOrder":2,
                                       "details":{
                                          "defaultValue":null,
                                          "precision":0,
                                          "nameField":false,
                                          "type":"string",
                                          "restrictedDelete":false,
                                          "relationshipName":null,
                                          "calculatedFormula":null,
                                          "controllerName":null,
                                          "namePointing":false,
                                          "defaultValueFormula":null,
                                          "calculated":false,
                                          "writeRequiresMasterRead":false,
                                          "inlineHelpText":null,
                                          "picklistValues":[

                                          ]
                                    }
                             }     
                         ]
                    }
.
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_sobject_named_layouts.htm?search_text=layouts](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_sobject_named_layouts.htm?search_text=layouts)

### Sample configuration

Following example illustrates how to connect to Salesforce with the init operation and sObjectNamedLayouts operation.

1. Create a sample proxy as below :
```xml
<proxy xmlns="http://ws.apache.org/ns/synapse" name="sObjectNamedLayouts"
       statistics="disable" trace="disable" transports="https http">
    <target>
        <inSequence>
            <property name="accessToken" expression="json-eval($.accessToken)"/>
            <property name="apiUrl" expression="json-eval($.apiUrl)"/>
            <property name="sObjectName" expression="json-eval($.sObjectName)"/>
            <property name="layoutName" expression="json-eval($.layoutName}"/>
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
            <salesforcerest.sObjectNamedLayouts>
                <sObjectName>{$ctx:sObjectName}</sObjectName>
                <layoutName>{$ctx:layoutName}</layoutName>
            </salesforcerest.sObjectNamedLayouts>
            <send/>
        </inSequence>
    </target>
</proxy>
```

2. Create a json file named sObjectNamedLayouts.json and copy the configurations given below to it:

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
  "layoutName": "UserAlt",
  "registryPath": "connectors/SalesforceRest"
}
```
3. Replace the credentials with your values.

4. Execute the following curl command:

```bash
curl http://localhost:8280/services/sObjectNamedLayouts -H "Content-Type: application/json" -d @sObjectNamedLayouts.json
```
5. Salesforce returns a json response similar to the one shown below:
 
```json
{
   "layouts":[
      {
         "detailLayoutSections":[
            {
               "heading":"About",
               "columns":2,
               "tabOrder":"LeftToRight",
               "useCollapsibleSection":false,
               "rows":2,
               "useHeading":false,
               "layoutRows":[
                  {
                     "layoutItems":[
                        {
                           "editableForUpdate":false,
                           "editableForNew":false,
                           "layoutComponents":[
                              {
                                 "components":[
                                    {
                                       "tabOrder":2,
                                       "details":{
                                          "defaultValue":null,
                                          "precision":0,
                                          "nameField":false,
                                          "type":"string",
                                          "restrictedDelete":false,
                                          "relationshipName":null,
                                          "calculatedFormula":null,
                                          "controllerName":null,
                                          "namePointing":false,
                                          "defaultValueFormula":null,
                                          "calculated":false,
                                          "writeRequiresMasterRead":false,
                                          "inlineHelpText":null,
                                          "picklistValues":[

                                          ]
                                    }
                             }     
                         ]
                    }
.
}
```
