# Working with Invocable Actions

[[Overview]](#overview)  [[Operation details]](#operation-details)  [[Sample configuration]](#sample-configuration)

### Overview 

The following operations allow you to work with invocable actions for the current organization.  Click an operation name to see details on how to use it.
For a sample proxy service that illustrates how to work with invocable actions, see [Sample configuration](#sample-configuration).

**Info**

These resources are available in the Salesforce REST API version 32.0 and later.

| Operation        | Description |
| ------------- |-------------|
| [getListOfAction](#retrieving-a-list-of-general-action-types)    | 	Retrieves a list of general action types for the current organization. |
| [getSpecificListOfAction](#retrieving-a-list-of-standard-actions)      | Retrieves a list of standard actions for the current organization. |
| [getAttributeOfSpecificAction](#retrieving-the-details-of-a-given-attribute-of-an-action)      | Retrieves the details of a given attribute of a single standard action.|

### Operation details

This section provides more details on each of the operations related to invocable actions.

#### Retrieving a list of general action types
To retrieve the list of general action types for the current organization, use salesforcerest.getListOfAction and specify the following properties.

**getListOfAction**
```xml
<salesforcerest.getListOfAction/>
```
**Sample request**

Following is a sample request that can be handled by the getListOfAction operation.

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

Given below is a sample response for the getListOfAction operation.

```json
{
   "standard":"/services/data/v32.0/actions/standard",
   "custom":"/services/data/v32.0/actions/custom"
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_actions_invocable.htm?search_text=action](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_actions_invocable.htm?search_text=action)

#### Retrieving a list of standard actions

To retrieve a list of actions that can be statically invoked, and to get basic information for each type of action, use salesforcerest.getSpecificListOfAction and specify the following property.

**getSpecificListOfAction**
```xml
<salesforcerest.getSpecificListOfAction>
    <actionType>{$ctx:actionType}</actionType>
</salesforcerest.getSpecificListOfAction>
```
**Properties**
* actionType: The type of the invocable action.

**Sample request**

Following is a sample request that can be handled by the getSpecificListOfAction operation.

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "apiVersion": "v32.0",
  "actionType": "standard",
  "intervalTime" : "100000",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the getSpecificListOfAction operation.

```json
{
   "standard":"/services/data/v32.0/actions/standard",
   "custom":"/services/data/v32.0/actions/custom"
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_actions_invocable_standard.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_actions_invocable_standard.htm)

#### Retrieving the details of a given attribute of an action

To retrieve an attribute of a single action, use salesforcerest.getAttributeOfSpecificAction and specify the following properties.

**getAttributeOfSpecificAction**
```xml
<salesforcerest.getAttributeOfSpecificAction>
    <actionType>{$ctx:actionType}</actionType>
    <attribute>{$ctx:attribute}</attribute>
</salesforcerest.getAttributeOfSpecificAction>
```
**Properties**
* actionType: The type of the action.
* attribute: The attribute whose details you want to retrieve.

**Sample request**

Following is a sample request that can be handled by the getAttributeOfSpecificAction operation.

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
  "actionType": "standard",
  "attribute": "emailSimple",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the getAttributeOfSpecificAction operation.

```json
{
   "actions":[
      {
         "name":"chatterPost",
         "label":"Post to Chatter",
         "type":"CHATTERPOST"
      },
      {
         "name":"emailSimple",
         "label":"Send Email",
         "type":"EMAILSIMPLE"
      }
      .
   ]
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_actions_invocable_standard.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_actions_invocable_standard.htm)

### Sample configuration

Following example illustrates how to connect to Salesforce with the init operation and getAttributeOfSpecificAction operation.

1. Create a sample proxy as below :

```xml
<proxy xmlns="http://ws.apache.org/ns/synapse" name="getAttributeOfSpecificAction"
       statistics="disable" trace="disable" transports="https http">
    <target>
        <inSequence>
            <property name="accessToken" expression="json-eval($.accessToken)"/>
            <property name="apiUrl" expression="json-eval($.apiUrl)"/>
            <property name="actionType" expression="json-eval($.actionType)"/>
            <property name="attribute" expression="json-eval($.attribute"/>
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
            <salesforcerest.getAttributeOfSpecificAction>
                <actionType>{$ctx:actionType}</actionType>
                <attribute>{$ctx:attribute}</attribute>
            </salesforcerest.getAttributeOfSpecificAction>
            <send/>
        </inSequence>
    </target>
</proxy>
```

2. Create a json file named getAttributeOfSpecificAction.json and copy the configurations given below to it:

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
  "actionType": "standard",
  "attribute": "emailSimple",
  "registryPath": "connectors/SalesforceRest"
}
```
3. Replace the credentials with your values.

4. Execute the following curl command:

```bash
curl http://localhost:8280/services/getAttributeOfSpecificAction -H "Content-Type: application/json" -d @getAttributeOfSpecificAction.json
```
5. Salesforce returns a json response similar to the one shown below:
 
```json
{
   "actions":[
      {
         "name":"chatterPost",
         "label":"Post to Chatter",
         "type":"CHATTERPOST"
      },
      {
         "name":"emailSimple",
         "label":"Send Email",
         "type":"EMAILSIMPLE"
      }
      .
   ]
}
```
