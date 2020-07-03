# Working with Users

[[Overview]](#overview)  [[Operation details]](#operation-details)  [[Sample configuration]](#sample-configuration)

### Overview 

The following operations allow you to work with users. Click an operation name to see details on how to use it.
For a sample proxy service that illustrates how to work with users, see [Sample configuration](#sample-configuration).

| Operation        | Description |
| ------------- |-------------|
| [getUserInformation](#retrieving-information-for-a-user)    | Retrieves information about a specific user. |
| [resetPassword](#resetting-the-password)      | Resets the password of a specific user to a system generated password. |
| [setPassword](#setting-the-password)      | Sets the password of a specific user to a specified password. |

### Operation details

This section provides more details on each of the operations related to users.

#### Retrieving information for a user
To retrieve information about a specific user, use salesforcerest.getUserInformation and specify the following property.

**getUserInformation**
```xml
<salesforcerest.getUserInformation>
    <userId>{$ctx:userId}</userId>
</salesforcerest.getUserInformation>
```
**Properties**
* userId: The ID of the user whose information you want to retrieve.

**Sample request**

Following is a sample request that can be handled by the getUserInformation operation.

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
  "userId": "00528000000yl7j",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the listProcessRules operation.

```json
{
   "ProfileId":"00e28000000xIEQAA2",
   "LastModifiedDate":"2016-11-29T05:40:45.000+0000",
   "Address":{
      "country":"LK",
      "city":null,
      "street":null,
      "latitude":null,
      "postalCode":null,
      "geocodeAccuracy":null,
      "state":null,
      "longitude":null
   },
   "LanguageLocaleKey":"en_US",
   "EmailPreferencesAutoBccStayInTouch":false
   .
   .
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/dome_process_rules.htm](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/dome_process_rules.htm)

#### Resetting the password

To reset the password of a specific user, use salesforcerest.resetPassword and specify the following property.

**resetPassword**
```xml
<salesforcerest.resetPassword>
    <userId>{$ctx:userId}</userId>
</salesforcerest.resetPassword>
```
**Properties**
* userId: The ID of the user.

**Sample request**

Following is a sample request that can be handled by the resetPassword operation.

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
  "userId": "00528000000yl7j",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the resetPassword operation.

```json
{
    "NewPassword" : "myNewPassword1234"
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_sobject_user_password.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_sobject_user_password.htm)

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_sobject_user_password.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_sobject_user_password.htm)

#### Setting the password

To set the password of a specific user, use salesforcerest.setPassword and specify the following property.

**resetPassword**
```xml
<salesforcerest.setPassword>
    <userId>{$ctx:userId}</userId>
    <fieldAndValue>{$ctx:fieldAndValue}</fieldAndValue>
</salesforcerest.setPassword>
```
**Properties**
* userId: The ID of the user.
* fieldAndValue: JSON containing a value with field 'NewPassword'.

**Sample request**

Following is a sample request that can be handled by the resetPassword operation.

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
  "userId": "00528000000yl7j",
  "fieldAndValue": {
      	"NewPassword": "sterergf3baAsWe"
      }
}
```
**Sample response**

No response body is returned on successfull password change. You will get a 204 response code

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_sobject_user_password.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_sobject_user_password.htm)

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_sobject_user_password.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_sobject_user_password.htm)

### Sample configuration

Following example illustrates how to connect to Salesforce with the init operation and resetPassword operation.

1. Create a sample proxy as below :

```xml
<proxy xmlns="http://ws.apache.org/ns/synapse" name="resetPassword"
       statistics="disable" trace="disable" transports="https http">
    <target>
        <inSequence>
            <property name="accessToken" expression="json-eval($.accessToken)"/>
            <property name="apiUrl" expression="json-eval($.apiUrl)"/>
            <property name="userId" expression="json-eval($.userId)"/>
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
            <salesforcerest.resetPassword>
                <userId>{$ctx:userId}</userId>
            </salesforcerest.resetPassword>
            <send/>
        </inSequence>
    </target>
</proxy>
```

2. Create a json file named resetPassword.json and copy the configurations given below to it:

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
  "userId": "00528000000yl7j",
  "registryPath": "connectors/SalesforceRest"
}
```

3. Replace the credentials with your values.

4. Execute the following curl command:

```bash
curl http://localhost:8280/services/resetPassword -H "Content-Type: application/json" -d @resetPassword.json
```
5. Salesforce returns a json response similar to the one shown below:
 
```json
{
    "NewPassword" : "myNewPassword1234"
}
