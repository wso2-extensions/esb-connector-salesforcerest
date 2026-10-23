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
    
In this connector, we have two flows to get the accessToken. The first one is the  [Web Server OAuth Authentication Flow](#init) and the other one is  [Username-Password OAuth Authentication Flow](#).

## Salesforce REST 2.0.0 Connector Connection Configuration

In the 'Properties' section of each operation, users can configure connection-related information. Once the configuration is created, it can be reused in other operations.

<table>
        <tr>
            <th>Parameter Name</th>
            <th>Description</th>
            <th>Required</th>
            <th>Sample Value</th>
        </tr>
        <tr>
            <td>apiVersion</td>
            <td>The version of the Salesforce API.</td>
            <td>Yes</td>
            <td>v59.0</td>
        </tr>
        <tr>
            <td>accessToken</td>
            <td>The access token to authenticate your API calls.</td>
            <td>No</td>
            <td>XXXXXXXXXXXX (Replace with your access token)</td>
        </tr>
        <tr>
            <td>apiUrl</td>
            <td>The instance URL for your organization.</td>
            <td>Yes</td>
            <td>https://ap2.salesforce.com</td>
        </tr>
        <tr>
            <td>hostName</td>
            <td>SalesforceOAuth endpoint when issuing authentication requests in your application.</td>
            <td>Yes</td>
            <td>https://login.salesforce.com</td>
        </tr>
        <tr>
            <td>refreshToken</td>
            <td>The refresh token that you received to refresh the API access token.</td>
            <td>No</td>
            <td>XXXXXXXXXXXX (Replace with your refresh token)</td>
        </tr>
        <tr>
            <td>tokenEndpointHostname</td>
            <td>The endpoint of the refresh token that you invoke to refresh the API access token. </td>
            <td>No</td>
            <td>XXXXXXXXXXXX (Replace this with your refresh token endpoint)</td>
        </tr>
        <tr>
            <td>clientId</td>
            <td>The consumer key of the connected application that you created.</td>
            <td>No</td>
            <td>XXXXXXXXXXXX (Replace with your client ID)</td>
        </tr>
        <tr>
            <td>clientSecret</td>
            <td>The consumer secret of the connected application that you created.</td>
            <td>No</td>
            <td>XXXXXXXXXXXX (Replace with your client secret)</td>
        </tr>
        <tr>
            <td>blocking</td>
            <td>Indicates whether the connector needs to perform blocking invocations to Salesforce.</td>
            <td>Yes</td>
            <td>false</td>
        </tr>
      </table>
 
#### Sample Configuration
```xml
    <salesforcerest.init>
        <accessToken>{$ctx:accessToken}</accessToken>
        <apiUrl>{$ctx:apiUrl}</apiUrl>
        <hostName>{$ctx:hostName}</hostName>
        <apiVersion>{$ctx:apiVersion}</apiVersion>
        <blocking>{$ctx:blocking}</blocking>
    </salesforcerest.init>
```
#### Sample Request

```json
    {
        "hostName": "https://login.salesforce.com",
        "apiVersion": "v32.0",
        "accessToken":"XXXXXXXXXXXX (Replace with your access token)",
        "apiUrl":"https://(your_instance).salesforce.com",
        "blocking" : "false"
    }
```

Or if you want the connector to handle token expiry

#### Sample Configuration

```xml
    <salesforcerest.init>
        <apiUrl>{$ctx:apiUrl}</apiUrl>
        <hostName>{$ctx:hostName}</hostName>
        <apiVersion>{$ctx:apiVersion}</apiVersion>
        <refreshToken>{$ctx:refreshToken}</refreshToken>
        <clientId>{$ctx:clientId}</clientId>
        <clientSecret>{$ctx:clientSecret}</clientSecret>
        <blocking>{$ctx:blocking}</blocking>
    </salesforcerest.init>
```
#### Sample request

```json
    {
        "hostName": "https://login.salesforce.com",
        "apiVersion": "v32.0",
        "refreshToken":"XXXXXXXXXXXX (Replace with your refresh token)",
        "apiUrl":"https://(your_instance).salesforce.com",
        "clientId": "XXXXXXXXXXXX (Replace with your client ID)",
        "clientSecret": "XXXXXXXXXXXX (Replace with your client secret)",
        "blocking" : "false"
    }
```

**Related  documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/intro_understanding_web_server_oauth_flow.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/intro_understanding_web_server_oauth_flow.htm)

### Connection Configuration Parameters for username/password flow

<table>
        <tr>
            <th>Parameter Name</th>
            <th>Description</th>
            <th>Required</th>
            <th>Sample Value</th>
        </tr>
        <tr>
            <td>username</td>
            <td>The username for Salesforce.</td>
            <td>Yes</td>
            <td>youruser@gmail.com</td>
        </tr>
        <tr>
            <td>password</td>
            <td>The password for Salesforce (need to append the password with security key).</td>
            <td>Yes</td>
            <td>xxxxxxxxxxxxxxxxxxxxxx</td>
        </tr>
</table>

 #### Sample configuration

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

#### Sample request
    
```json
    {
        "clientId": "xxxxxxxxxxxxxxxxxxxxxxxx",
        "clientSecret": "xxxxxxxxxxxxxxxxxxxxxxxx",
        "hostName": "https://login.salesforce.com",
        "apiVersion": "v32.0",
        "username": "youruser@gmail.com",
        "password": "xxxxxxxxxxxxxxxxxxxxxx",
        "apiUrl":"https://(your_instance).salesforce.com",
        "blocking" : "false"
    }
```

**Related  documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/intro_understanding_username_password_oauth_flow.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/intro_understanding_username_password_oauth_flow.htm)

### Connection Configuration Parameters for the OAuth 2.0 JWT bearer flow

The JWT bearer flow is the flow Salesforce recommends for server-to-server integrations. The connector signs a
short-lived JWT (RS256) with your private key and exchanges it for an access token. No client secret or user password
is used. The token is kept in memory and reused, and it is renewed automatically when Salesforce reports an expired
session (HTTP 401). The failed request is then retried once. Tokens are not written to disk; after a restart, a new
token is obtained on first use.

Each connection (by `name`) holds one token. If `username` changes from message to message, a new token is obtained
each time it changes, so use a separate connection per user.

Salesforce setup:
1. Create a key and certificate, for example:
   `keytool -genkeypair -alias sfjwt -keyalg RSA -keysize 2048 -sigalg SHA256withRSA -validity 730 -dname "CN=wso2-mi" -storetype PKCS12 -keystore salesforce-jwt.p12`,
   then export the certificate: `keytool -exportcert -rfc -alias sfjwt -keystore salesforce-jwt.p12 -file salesforce-jwt.crt`.
2. In the connected app, enable OAuth, select **Use digital signatures**, upload `salesforce-jwt.crt` and add the
   `api` and `refresh_token, offline_access` scopes.
3. Under **Manage > Edit Policies**, set **Permitted Users** to **Admin approved users are pre-authorized** and assign
   the integration user's profile or permission set.

<table>
        <tr>
            <th>Parameter Name</th>
            <th>Description</th>
            <th>Required</th>
            <th>Sample Value</th>
        </tr>
        <tr><td>name</td><td>Name of the connection. Each connection keeps its own token.</td><td>Yes</td><td>SALESFORCE_JWT</td></tr>
        <tr><td>authType</td><td>Set to <code>JWT</code>.</td><td>Yes</td><td>JWT</td></tr>
        <tr><td>clientId</td><td>Consumer key of the connected app.</td><td>Yes</td><td>3MVG9XXXXXXXX</td></tr>
        <tr><td>username</td><td>Pre-authorized Salesforce user.</td><td>Yes</td><td>integration@example.com</td></tr>
        <tr><td>instanceUrl</td><td>URL for the API calls. If omitted, the instance URL returned with the access token
            is used. Set it only to send the calls through another URL, such as a proxy. <code>apiUrl</code> is not
            used with JWT.</td><td>No</td><td>https://mydomain.my.salesforce.com</td></tr>
        <tr><td>tokenEndpointHostname</td><td>Login URL, https with only a host: <code>https://login.salesforce.com</code> (default),
            <code>https://test.salesforce.com</code> for sandboxes, or your My Domain URL.</td><td>No</td><td>https://login.salesforce.com</td></tr>
        <tr><td>keyStorePath</td><td>Location of the keystore or PEM key file with the signing key (RSA, at least 2048
            bits). Relative paths are resolved against the MI home.</td><td>Yes</td><td>repository/resources/security/salesforce-jwt.p12</td></tr>
        <tr><td>keyStoreType</td><td><code>PKCS12</code>, <code>JKS</code> or <code>PEM</code>. If omitted, it is
            inferred from the file extension (<code>.jks</code>: JKS, <code>.pem</code>/<code>.key</code>: PEM,
            otherwise PKCS12).</td><td>No</td><td>PKCS12</td></tr>
        <tr><td>keyStorePassword</td><td>Keystore password (JKS, PKCS12). Use Secure Vault.</td><td>For JKS and PKCS12</td><td>{wso2:vault-lookup('sf_jwt_keystore_password')}</td></tr>
        <tr><td>keyAlias</td><td>Alias of the signing key (JKS, PKCS12). Optional if the keystore holds a single
            key.</td><td>No</td><td>sfjwt</td></tr>
        <tr><td>keyPassword</td><td>Password of the private key, if it differs from the keystore password, or of an
            encrypted PEM key. Use Secure Vault.</td><td>No</td><td>{wso2:vault-lookup('sf_jwt_key_password')}</td></tr>
</table>

A PEM key file must contain a PKCS#8 RSA key: `-----BEGIN PRIVATE KEY-----`, or `-----BEGIN ENCRYPTED PRIVATE KEY-----`
together with `keyPassword`. Other blocks in the file, such as the certificate, are ignored. Convert a PKCS#1 key
(`-----BEGIN RSA PRIVATE KEY-----`) with `openssl pkcs8 -topk8 -in key.pem -out key-pkcs8.pem`.

`password`, `refreshToken` and `accessToken` must not be set when `authType` is `JWT`. On failure the fault sequence
runs with `ERROR_CODE` `SF_JWT_CONFIG` (invalid configuration or key) or `SF_JWT_TOKEN` (Salesforce rejected the
request, e.g. `invalid_grant`, the endpoint could not be reached, or Salesforce rejected a token whose session is
still valid, which happens when `instanceUrl` belongs to another org or the org locks sessions to an IP address).

Limitations:
- The JWT audience is `https://test.salesforce.com` for `test.salesforce.com`, sandbox and scratch org login URLs,
  and `https://login.salesforce.com` otherwise. Experience Cloud site login URLs are not supported.
- The token request uses the JVM proxy settings (`https.proxyHost`, `https.proxyPort`), not the proxy of the MI
  HTTP transport.

```xml
    <salesforcerest.init>
        <name>SALESFORCE_JWT</name>
        <authType>JWT</authType>
        <clientId>3MVG9XXXXXXXX</clientId>
        <username>integration@example.com</username>
        <apiVersion>v59.0</apiVersion>
        <keyStorePath>repository/resources/security/salesforce-jwt.p12</keyStorePath>
        <keyStorePassword>{wso2:vault-lookup('sf_jwt_keystore_password')}</keyStorePassword>
        <keyAlias>sfjwt</keyAlias>
    </salesforcerest.init>
```


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
