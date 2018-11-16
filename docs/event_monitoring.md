# Working with Event Monitoring

[[Overview]](#overview)  [[Operation details]](#operation-details)  [[Sample configuration]](#sample-configuration)

### Overview 

The following operations allow you to work with event monitoring. Click an operation name to see details on how to use it.
For a sample proxy service that illustrates how to work with event monitoring, see [Sample configuration](#sample-configuration).

| Operation        | Description |
| ------------- |-------------|
| [describeEventMonitoring](#retrieving-the-event-monitoring-log-description)    | Retrieves all metadata for an object, including information about fields, URLs, and child relationships. |
| [queryEventMonitoringData](#retrieving-field-values-from-a-record)      | Retrieves field values from a record. |

### Operation details

This section provides more details on each of the operations related to event monitoring.

#### Retrieving the event monitoring log description
To retrieve the description of the event monitoring log, use salesforcerest.describeEventMonitoring.

**describeEventMonitoring**
```xml
<salesforcerest.describeEventMonitoring/>
```

**Sample request**

Following is a sample request that can be handled by the describeEventMonitoring operation.

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

Given below is a sample response for the describeEventMonitoring operation.

```json
{
   "updateable":false,
   "activateable":false,
   "childRelationships":[

   ],
   "recordTypeInfos":[

   ],
   "deprecatedAndHidden":false,
   "searchLayoutable":false,
   "deletable":false,
   "replicateable":false,
   "actionOverrides":[

   ],
   .
   .
   ],
   "labelPlural":"Event Log Files",
   "triggerable":false
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/dome_event_log_file_describe.htm](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/dome_event_log_file_describe.htm)

#### Retrieving field values from a record

To retrieve the field values from a record, use salesforcerest.queryEventMonitoringData and specify the following properties.

**tabs**
```xml
<salesforcerest.queryEventMonitoringData>
    <queryStringForEventMonitoringData>{$ctx:queryStringForEventMonitoringData}</queryStringForEventMonitoringData>
</salesforcerest.queryEventMonitoringData>
```
**Properties**
* queryStringForEventMonitoringData: The query string to use to get the field values from the log.


**Sample request**

Following is a sample request that can be handled by the queryEventMonitoringData operation.

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBztM9gSLYyUe7VwAVhD9.yQnZX2mmCu_48Uwc._doxrBTgY4jqmOSDhxRAiUBf8gCr2mk7",
  "refreshToken": "5Aep861TSESvWeug_ztpnAk6BGQxRdovMLhHso81iyYKO6hTm45JVxz3FLewCKgI4BbUp19OzGfqG2TdCfqa2ZU",
  "clientSecret": "1187341468789253319",
  "hostName": "https://login.salesforce.com",
  "apiVersion": "v34.0",
  "intervalTime" : "100000",
  "queryStringForEventMonitoringData": "SELECT+Id+,+EventType+,+LogFile+,+LogDate+,+LogFileLength+FROM+EventLogFile+WHERE+LogDate+>+Yesterday+AND+EventType+=+'API'",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the queryEventMonitoringData operation.

```json
{ 
   "totalSize" : 4,
   "done" : true,
   "records" : [ {
     "attributes" : {
       "type" : "EventLogFile",
       "url" : "/services/data/v32.0/sobjects/EventLogFile/0ATD000000001bROAQ"     }
     "Id" : "0ATD000000001bROAQ",
     "EventType" : "API",
     "LogFile" : "/services/data/v32.0/sobjects/EventLogFile/0ATD000000001bROAQ/LogFile",
     "LogDate" : "2014-03-14T00:00:00.000+0000",
     "LogFileLength" : 2692.0
    }, 
    .
 ]
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/dome_event_log_file_query.htm](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/dome_event_log_file_query.htm)

### Sample configuration

Following example illustrates how to connect to Salesforce with the init operation and queryEventMonitoringData operation.

1. Create a sample proxy as below :

```xml
<proxy xmlns="http://ws.apache.org/ns/synapse" name="queryEventMonitoringData"
       statistics="disable" trace="disable" transports="https http">
    <target>
        <inSequence>
            <property name="accessToken" expression="json-eval($.accessToken)"/>
            <property name="apiUrl" expression="json-eval($.apiUrl)"/>
            <property name="queryStringForEventMonitoringData"
                      expression="json-eval($.queryStringForEventMonitoringData)"/>
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
            <salesforcerest.queryEventMonitoringData>
                <queryStringForEventMonitoringData>{$ctx:queryStringForEventMonitoringData}
                </queryStringForEventMonitoringData>
            </salesforcerest.queryEventMonitoringData>
            <send/>
        </inSequence>
    </target>
</proxy>
```

2. Create a json file named queryEventMonitoringData.json and copy the configurations given below to it:

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBztM9gSLYyUe7VwAVhD9.yQnZX2mmCu_48Uwc._doxrBTgY4jqmOSDhxRAiUBf8gCr2mk7",
  "refreshToken": "5Aep861TSESvWeug_ztpnAk6BGQxRdovMLhHso81iyYKO6hTm45JVxz3FLewCKgI4BbUp19OzGfqG2TdCfqa2ZU",
  "clientSecret": "1187341468789253319",
  "hostName": "https://login.salesforce.com",
  "apiVersion": "v34.0",
  "intervalTime" : "100000",
  "queryStringForEventMonitoringData": "SELECT+Id+,+EventType+,+LogFile+,+LogDate+,+LogFileLength+FROM+EventLogFile+WHERE+LogDate+>+Yesterday+AND+EventType+=+'API'",
  "registryPath": "connectors/SalesforceRest"
}
```
3. Replace the credentials with your values.

4. Execute the following curl command:

```bash
curl http://localhost:8280/services/queryEventMonitoringData -H "Content-Type: application/json" -d @queryEventMonitoringData.json
```

5. Salesforce returns a json response similar to the one shown below:
 
```json
{ 
   "totalSize" : 4,
   "done" : true,
   "records" : [ {
     "attributes" : {
       "type" : "EventLogFile",
       "url" : "/services/data/v32.0/sobjects/EventLogFile/0ATD000000001bROAQ"     }
     "Id" : "0ATD000000001bROAQ",
     "EventType" : "API",
     "LogFile" : "/services/data/v32.0/sobjects/EventLogFile/0ATD000000001bROAQ/LogFile",
     "LogDate" : "2014-03-14T00:00:00.000+0000",
     "LogFileLength" : 2692.0
    }, 
    .
 ]
}
```
