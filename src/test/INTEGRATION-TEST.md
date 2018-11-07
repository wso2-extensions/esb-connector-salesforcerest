##  Integration tests for WSO2 ESB Salesforce REST connector
### Pre-requisites:

    - Maven 3.x
    - Java 1.8

 ### Tested Platforms:

    - Mac OS 10.12.6
    - WSO2 EI 6.4.0


Steps to follow in setting integration test.


 1. Download EI 6.4.0 from official website.

 2. Create a Salesforce account and create a connected app.

       i. Using the URL "https://developer.salesforce.com" create an account.
        
       ii. Create a connected app to get the clientId and clientSecret. Refer below link to create connected app.
            https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/intro_defining_remote_access_applications.htm.

 3. Before you start performing various operations with the connector, make sure to place the Salesforce certificate to the location
   "<SALESFORCEREST_CONNECTOR_HOME>/repository/.

 4. Update the esb-connector-salesforcerest properties file at location "<SALESFORCEREST_CONNECTOR_HOME>/repository/ with the suited values.

 5. Following are the properties used in the 'esb-connector-salesforcerest.properties' file and salesforcerest properties file at location "<SALESFORCEREST_CONNECTOR_HOME>/src/test/resources/artifacts/ESB/connector/config" to run the integration tests.

        i)apiUrl 							- 	The URL of the salesforce connected app.
        ii)apiVersion                        -   The api version for the connected app(New version is better because some of the features are supported only on the new version).
        iii)refreshToken                     -   The refresh token to access the API.
        iv)clientId                          -   The Consumer key of API to access the connected app.
        v)clientSecret                       -   The consumer key of API to access the connected app.
        vi)hostName                          -   Value of the hostname to authenticate salesforce(If you are using sandbox then the hostName is "https://test.salesforce.com" instead of "https://login.salesforce.com").
        vii)sobjectType                      -   The sObject type.
	    viii)sobject                         -   The type of the sObject.
	    ix)startTime                         -   The start time,for that the date and time should be provided in ISO 8601 format.
        x)endTime                            -   The end time , for that the date and time should be provided in ISO 8601 format.
	    xi)actionName                        -   The action name.
	    xii)queryString                      -   The SOQL query string.
	    xiii)nextRecordsUrl                  -   The url is return at the query or queryAll response to retrieve additional query results. If the records is too large only it will return at the query/queryAll response.
	    xiv)newPassword                      -   The new password of saesforce to be set to access.
        xv)fields                            -   The comma separated fields of a particular sObject.
	    xvi)searchString                     -   The SOQL search string.
	    xvii)sObjectList                     -   The comma separated sObject type.
	    xviii)createName                     -   The name of the newly created record/updated record.
	    xix)createDescription                -   The description of the newly created/updated record.
        xx)apiStartTime                      -   The start time for api, for that the date and time should be provided in ISO 8601 format.
        xxi)apiEndTime                       -   The end time for api, for that the date and time should be provided in ISO 8601 format.
        xxii)updateName                      -   The name to update the record.
        xxiii)updateDescription              -   The description to update the record.
        xxiv)updateApiName                   -   The name to update the record for API.
        xxv)updateApiDescription             -   The description to update the record for API.
        xxvi)limit                           -   The value of the response object limit.
        xxvii)actionType                     -   The actionType is either standard or custom
        xxviii)attribute                     -   The attribute of a single standard object
        xxix)menuType                        -   The type is either AppSwitcher or Salesforce1
        xxx)sObjectList                      -   Object list with comma separator
        xxxi)object                          -   The type of sobject
        xxxii)layoutName                     -   The name of the layout of the object
        xxxiii)stringForSearch               -   The SOQL query to execute the search
        xxxiv)refId1                         -   The reference id to create multiple records
        xxxv)refId2                          -   The reference id to create multiple records
        xxxvi)sobject1                       -   The type of Sobject to create mutiple records
        xxxvii)workflowRuleId                -   The work flow Id of a specific rule for specific rule
        xxxviii)registryPath                 -   Registry Path of the Connector where the values are stored
        xxxix)intervalTime                   -   The Interval time to check the accessToken validity

 6.  Navigate to "{SALESFORCEREST_CONNECTOR_HOME}/" and run the following command. <br/>
             `$ mvn clean install -Dskip-tests=false`
