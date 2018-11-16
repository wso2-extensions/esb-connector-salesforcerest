# Working with AppMenu

[[Overview]](#overview)  [[Operation details]](#operation-details)  [[Sample configuration]](#sample-configuration)

### Overview 

The following operations allow you to work with AppMenu, including tabs and themes, for the current organization. Click an operation name to see details on how to use it.
For a sample proxy service that illustrates how to work with AppMenu, see [Sample configuration](#sample-configuration).

| Operation        | Description |
| ------------- |-------------|
| [listItemsInMenu](#retrieving-a-list-of-appmenu-items)    | Retrieves a list of items in either the Salesforce app drop-down menu or the Salesforce1 navigation menu. |
| [tabs](#retrieving-a-list-of-all-tabs)      | Retrieves a list of all tabs. |
| [themes](#retrieving-a-list-of-icons-and-colors-used-by-themes-in-the-salesforce-application)      | Retrieves the list of icons and colors used by themes in the Salesforce application.|

### Operation details

This section provides more details on each of the operations.

#### Retrieving a list of AppMenu items
To retrieve the list of items in either the Salesforce app drop-down menu or the Salesforce1 navigation menu, use salesforcerest.listItemsInMenu and specify the following property.

**listItemsInMenu**
```xml
<salesforcerest.listItemsInMenu>
    <menuType>{$ctx:menuType}</menuType>
</salesforcerest.listItemsInMenu>
```

**Properties**
* menuType: The type of the menu, either AppSwitcher or Salesforce.

**Sample request**

Following is a sample request that can be handled by the listItemsInMenu operation.

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
  "menuType": "AppSwitcher",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the listItemsInMenu operation.

```json
{"NetworkTabs":"/services/data/v32.0/appMenu/NetworkTabs","Salesforce1":"/services/data/v32.0/appMenu/Salesforce1","AppSwitcher":"/services/data/v32.0/appMenu/AppSwitcher"}  
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_appmenu.htm?search_text=menu](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_appmenu.htm?search_text=menu)

#### Retrieving a list of all tabs

To retrieve a list of all tabs, use salesforcerest.tabs.

**tabs**
```xml
<salesforcerest.tabs/>
```

**Sample request**

Following is a sample request that can be handled by the tabs operation.

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

Given below is a sample response for the tabs operation.

```json
{"output":"[{\"colors\":[{\"color\":\"4dca76\",\"context\":\"primary\",\"theme\":\"theme4\"},{\"color\":\"319431\",\"context\":\"primary\",\"theme\":\"theme3\"}],\"custom\":true,\"iconUrl\":\"https://kesavan-dev-ed.my.salesforce.com/img/icon/form32.png\",..}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_tabs.htm?search_text=tabs](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_tabs.htm?search_text=tabs)

#### Retrieving a list of icons and colors used by themes in the Salesforce application

To retrieve a list of icons and colors used by themes in the Salesforce application, use salesforcerest.themes.

**themes**
```xml
<salesforcerest.themes/>
```

**Sample request**

Following is a sample request that can be handled by the themes operation.

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

Given below is a sample response for the themes operation.

```json
{
   "themeItems":[
      {
         "name":"Account",
         "icons":[
            {
               "width":32,
               "theme":"theme3",
               "contentType":"image/png",
               "url":"https://kesavan-dev-ed.my.salesforce.com/img/icon/accounts32.png",
               "height":32
            }
         ]
      }
   ]
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_themes.htm?search_text=themes](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/resources_themes.htm?search_text=themes)

### Sample configuration

Following example illustrates how to connect to Salesforce with the init operation and listItemsInMenu operation.

1. Create a sample proxy as below :

```xml
<proxy xmlns="http://ws.apache.org/ns/synapse" name="listItemsInMenu"
       statistics="disable" trace="disable" transports="https http">
    <target>
        <inSequence>
            <property name="accessToken" expression="json-eval($.accessToken)"/>
            <property name="apiUrl" expression="json-eval($.apiUrl)"/>
            <property name="menuType" expression="json-eval($.menuType"/>
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
            <salesforcerest.listItemsInMenu>
                <menuType>{$ctx:menuType}</menuType>
            </salesforcerest.listItemsInMenu>
            <send/>
        </inSequence>
    </target>
</proxy>
```

2. Create a json file named listItemsInMenu.json and copy the configurations given below to it:

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
  "menuType": "AppSwitcher",
  "registryPath": "connectors/SalesforceRest"
}                        
```
3. Replace the credentials with your values.

4. Execute the following curl command:

```bash
curl http://localhost:8280/services/listItemsInMenu -H "Content-Type: application/json" -d @listItemsInMenu.json
```
5. Salesforce returns a json response similar to the one shown below:
 
```json
{
    "AppSwitcher": "/services/data/v32.0/appMenu/AppSwitcher",
    "Salesforce1": "/services/data/v32.0/appMenu/Salesforce1",
    "NetworkTabs": "/services/data/v32.0/appMenu/NetworkTabs"
}
```
