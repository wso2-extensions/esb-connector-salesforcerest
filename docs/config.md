# Configuring Salesforce REST Operations

[[Prerequisites]](#Prerequisites) [[Initializing the connector]](#initializing-the-connector)

## Prerequisites

> NOTE: To work with the Salesforce REST connector, you need to have a Salesforce account. If you do not have a Salesforce account, go to [https://developer.salesforce.com/signup](https://developer.salesforce.com/signup) and create a Salesforce developer account.

To use the Salesforce REST connector, add the <salesforcerest.init> element in your configuration before carrying out any other Salesforce REST operation. 

Salesforce REST API uses the OAuth protocol to allow application users to securely access data without having to reveal their user credentials.  For more information on authentication is done in Salesforce, see [Understanding Authentication](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/intro_understanding_authentication.htm).

### Obtaining user credentials

* **Follow the steps below to create a connected application using Salesforce and to obtain the consumer key as well as the consumer secret for the created connected application:**

    1. Go to https://developer.salesforce.com/, and sign up to get a free developer environment. 
    2. Go to **Setup** -> **Create** -> **Apps**, and click **New**. This creates a new connected application.
    3. Specify a name for your application, your email address as well as any other basic information applicable to your application.
    4. Specify an **Info URL**. This is where a user can go for more information about your application.
    5. Select **Enable OAuth Settings**. This displays the **API (Enable OAuth Settings)** section.
    6. Under the **API (Enable OAuth Settings)** section, specify a **Callback URL**. Depending on the OAuth flow you use, this is typically the URL that your browser is redirected to, after successful authentication. Since this URL is used in some OAuth flows to pass an access token, the URL must use secure HTTP or a custom URI scheme.
    7. Select the required OAuth scopes from the **Available OAuth Scopes** list and click **Add** to include those as **Selected OAuth Scopes**. These OAuth scopes include permission given by the user running the connected application.
    8. Click **Save**. This saves the details of the connected application that you created, and displays the **Consumer Key** as well as the **Consumer Secret** of the connected application under the **API (Enable OAuth Settings)** section.
    
* **Follow the steps below to obtain an access token and refresh token to access the connected application that you created:**

    1. In the following URL, first replace <your_client_id> place holder with the consumer key that you obtained. Next, replace <your_redirect_uri> place holder with the value that you specified as the callback URL when creating the connected application, and then open the URL via a web browser. 
        ```xml
        https://login.salesforce.com/services/oauth2/authorize?response_type=code&client_id=<your_client_id>&redirect_uri=<your_redirect_uri>
        ```
    2. Approve the application to access your Salesforce account. You will see that the browser redirects you to the callback URL that you specified when creating the connected application:
       ```xml
       https://app.com/oauth_callback?code=aWe...c4w%3D%3D
       ```
    3. Extract the authorization code from the callback URL.
.
    4. Send a direct POST request to the authorization server using the following request: 
        > NOTE: Be sure to replace the place holders with values applicable to the connected application that you created.
        ```xml
        https://login.salesforce.com/services/oauth2/token?code=aWe...c4w==&grant_type=authorization_code&client_id=<your_client_id>&client_secret=<your_client_secret>&redirect_uri=<your_redirect_uri>&format=json
        ```
        > NOTE:In the above request, you can set the format to one of the following based on the format in which you want to retrieve the response:
        * **url encoded**
        * **json**
        * **xml**
    5. From the response that you get, extract the access token to access Salesforce via the created application. 
       > NOTE: You will also get a refresh token to renew the access token when it expires.

### Importing the Salesforce Certificate

Before you start configuring the connector, import the Salesforce certificate to your EI client keystore.

* Follow the steps below to import the Salesforce certificate into the EI client keystore:

    1. To view the certificate, log in to your Salesforce account using your browser (e.g., https://login.salesforce.com), and click the lock on the address bar.
    2. Export the certificate to the file system.
    3. Import the certificate to the EI client keystore using either the following command or the EI Management Console:
    ```
    keytool -importcert -file <certificate file> -keystore <EI>/repository/resources/security/client-truststore.jks -alias "Salesforce"
    ```
    4. Restart the server and deploy the Salesforce configuration. In this case, you can define the registry path where you can store the variables, then use the init operation with defining registry path to configure the Salesforce REST connector.
    
In this connector we have two flow to get the accessToken. First one is the  [Web Server OAuth Authentication Flow](#init) and the other one is  [Username-Password OAuth Authentication Flow](#).

## Initializing the connector

Add the following <salesforcerest.init> method in your configuration:
 
#### init
```xml
<salesforcerest.init>
    <accessToken>{$ctx:accessToken}</accessToken>
    <apiUrl>{$ctx:apiUrl}</apiUrl>
    <clientId>{$ctx:clientId}</clientId>
    <clientSecret>{$ctx:clientSecret}</clientSecret>
    <refreshToken>{$ctx:refreshToken}</refreshToken>
    <hostName>{$ctx:hostName}</hostName>
    <apiVersion>{$ctx:apiVersion}</apiVersion>
    <registryPath>{$ctx:registryPath}</registryPath>
    <intervalTime>{$ctx:intervalTime}</intervalTime>
    <blocking>{$ctx:blocking}</blocking>
  </salesforcerest.init>
```
**Properties** 
* apiVersion:  The version of the Salesforce API. 
* accessToken:  The access token to authenticate your API calls.
* apiUrl:  The instance URL for your organization.
* hostName:  SalesforceOAuth endpoint when issuing authentication requests in your application.
* refreshToken:  The refresh token that you received to refresh the API access token.
* clientId:  The consumer key of the connected application that you created.
* clientSecret:  The consumer secret of the connected application that you created.
* intervalTime:  The time interval in milliseconds, after which you need to check the validity of the access token.
* registryPath:  The registry path of the connector. You must specify the registry path as follows: registryPath = “connectors/salesforcerest”.
* blocking: Indicates whether the connector needs to perform blocking invocations to Salesforce. (Supported in WSO2 ESB 4.9.0 and later.)

**Sample Request**

Following is a sample REST request that can be handled by the init operation.

```json
{
  "clientId": "3MVG9ZL0ppGP5UrBztM9gSLYyUe7VwAVhD9.yQnZX2mmCu_48Uwc._doxrBTgY4jqmOSDhxRAiUBf8gCr2mk7",
  "refreshToken": "5Aep861TSESvWeug_ztpnAk6BGQxRdovMLhHso81iyYKO6hTm6kHoL4.YfwIi9cHLwga.pPTsTuJlmKjo05x.o.",
  "clientSecret": "1187341468789253319",
  "hostName": "https://login.salesforce.com",
  "apiVersion": "v32.0",
  "registryPath":"connectors/salesforcerest",
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "intervalTime" : "100000",
  "apiUrl":"https://ap2.salesforce.com",
  "blocking" : "false"
}
```

**Related  documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/intro_understanding_web_server_oauth_flow.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/intro_understanding_web_server_oauth_flow.htm)


#### init for username password flow
```xml
<salesforcerest.init>
    <apiUrl>{$ctx:apiUrl}</apiUrl>
    <clientId>{$ctx:clientId}</clientId>
    <clientSecret>{$ctx:clientSecret}</clientSecret>
    <hostName>{$ctx:hostName}</hostName>
    <apiVersion>{$ctx:apiVersion}</apiVersion>
    <username>{$ctx:username}</username>
    <password>{$ctx:password}</password>
    <blocking>{$ctx:blocking}</blocking>
</salesforcerest.init>
```
**Properties** 
* apiVersion:  The version of the Salesforce API. 
* apiUrl:  The instance URL for your organization.
* hostName:  SalesforceOAuth endpoint when issuing authentication requests in your application.
* clientId:  The consumer key of the connected application that you created.
* clientSecret:  The consumer secret of the connected application that you created.
* username: The username for the salesforce.
* password: The password for the salesforce (Need to append the password with security key).
* blocking: Indicates whether the connector needs to perform blocking invocations to Salesforce. (Supported in WSO2 ESB 4.9.0 and later.)

**Sample Request**

Following is a sample REST request that can be handled by the init operation for username and password flow.

```json
{
  "clientId": "3MVG9ZL0ppGP5UrBztM9gSLYyUe7VwAVhD9.yQnZX2mmCu_48Uwc._doxrBTgY4jqmOSDhxRAiUBf8gCr2mk7",
  "clientSecret": "xxxxxxxxxxxxxxxxxxxxxxxx",
  "hostName": "https://login.salesforce.com",
  "apiVersion": "v32.0",
  "username": "tharis63@outlook.com",
  "password": "xxxxxxxxxxxxxxxxxxxxxx",
  "apiUrl":"https://ap2.salesforce.com",
  "blocking" : "false"
}
```

**Related  documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/intro_understanding_username_password_oauth_flow.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/intro_understanding_username_password_oauth_flow.htm)


Now that you have connected to Salesforce, use the information in the following topics to perform various operations with the connector. Also, see [Configuring the Salesforce REST API Connector Fault Handler Sequence]((fault_handler_sequence.md)).

[Working with Appmenu](appmenu.md)

[Working with Approvals](approvals.md)

[Working with Event Monitoring](event_monitoring.md)

[Working with Invocable Actions](invocable_actions.md)

[Working with Layouts in Salesforce](layouts.md)

[Working with List Views](list_views.md)

[Working with Process Rules](process_rules.md)

[Working with Queries](queries.md)

[Working with Quick Actions](quick_actions.md)

[Working with Records](sObjects.md)

[Working with Salesforce Users](users.md)

[Working with Search](search.md)

[Working with sObjets](sObjects.md)
