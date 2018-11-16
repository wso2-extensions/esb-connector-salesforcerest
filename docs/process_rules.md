# Working with Process Rules

[[Overview]](#overview)  [[Operation details]](#operation-details)  [[Sample configuration]](#sample-configuration)

### Overview 

The following operations allow you to work with process rules. Click an operation name to see details on how to use it.
For a sample proxy service that illustrates how to work with process rules, see [Sample configuration](#sample-configuration).

| Operation        | Description |
| ------------- |-------------|
| [listProcessRules](#retrieving-a-list-of-process-rules)    | Retrieves a list of process rules. |
| [getSpecificProcessRule](#retrieving-the-metadata-for-a-specific-sobject-process-rule)      | Retrieves the metadata for a specific sObject process rule. |

### Operation details

This section provides more details on each of the operations related to process rules.

#### Retrieving a list of process rules
To retrieve the list of process rules in the organization, use salesforcerest.listProcessRules.

**listProcessRules**
```xml
<salesforcerest.listProcessRules/>
```

**Sample request**

Following is a sample request that can be handled by the listProcessRules operation.

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

Given below is a sample response for the listProcessRules operation.

```json
{
   "rules":{

   }
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/dome_process_rules.htm](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/dome_process_rules.htm)

#### Retrieving the metadata for a specific sObject process rule

To retrieve the metadata for a specific sObject process rule, use salesforcerest.getSpecificProcessRule and specify the following properties.

**getSpecificProcessRule**
```xml
<salesforcerest.getSpecificProcessRule>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
    <workflowRuleId>{$ctx:workflowRuleId}</workflowRuleId>
</salesforcerest.getSpecificProcessRule>
```
**Properties**
* sObjectName: The object whose process rule you want to retrieve.
* workflowRuleId: The ID of the process rule.

**Sample request**

Following is a sample request that can be handled by the getSpecificProcessRule operation.

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
  "workflowRuleId": "01QD0000000APli",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the getSpecificProcessRule operation.

```json
{
  "actions" : [ {
    "id" : "01VD0000000D2w7",
    "name" : "ApprovalProcessTask",
    "type" : "Task"
    } ],
    "description" : null,
    "id" : "01QD0000000APli",
    "name" : "My Rule",
    "namespacePrefix" : null,
    "object" : "Account"
}
```

**Related Salesforce Bulk documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/dome_process_rules_particular.htm](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/dome_process_rules_particular.htm)

### Sample configuration

Following example illustrates how to connect to Salesforce with the init operation and getSpecificProcessRule operation.

1. Create a sample proxy as below :

```xml
<proxy xmlns="http://ws.apache.org/ns/synapse" name="getSpecificProcessRule"
       statistics="disable" trace="disable" transports="https http">
    <target>
        <inSequence>
            <property name="accessToken" expression="json-eval($.accessToken)"/>
            <property name="apiUrl" expression="json-eval($.apiUrl)"/>
            <property name="sObjectName" expression="json-eval($.sObjectName)"/>
            <property name="workflowRuleId" expression="json-eval($.workflowRuleId)"/>
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
            <salesforcerest.getSpecificProcessRule>
                <sobject>{$ctx:sobject}</sobject>
                <workflowRuleId>{$ctx:workflowRuleId}</workflowRuleId>
            </salesforcerest.getSpecificProcessRule>
            <send/>
        </inSequence>
    </target>
</proxy>
```

2. Create a json file named getSpecificProcessRule.json and copy the configurations given below to it:

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
  "workflowRuleId": "01QD0000000APli",
  "registryPath": "connectors/SalesforceRest"
}
```
3. Replace the credentials with your values.

4. Execute the following curl command:

```bash
curl http://localhost:8280/services/getSpecificProcessRule -H "Content-Type: application/json" -d @getSpecificProcessRule.json
```
5. Salesforce returns a json response similar to the one shown below:
 
```json
{
  "actions" : [ {
    "id" : "01VD0000000D2w7",
    "name" : "ApprovalProcessTask",
    "type" : "Task"
    } ],
    "description" : null,
    "id" : "01QD0000000APli",
    "name" : "My Rule",
    "namespacePrefix" : null,
    "object" : "Account"
}
```
