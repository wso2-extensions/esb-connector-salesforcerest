# Working with Records

[[Overview]](#overview)  [[Operation details]](#operation-details)  [[Sample configuration]](#sample-configuration)

### Overview 

The following operations allow you to work with records for the current organization.  Click an operation name to see details on how to use it.
For a sample proxy service that illustrates how to work with records, see [Sample configuration](#sample-configuration).

| Operation        | Description |
| ------------- |-------------|
| [create](#creating-a-record-for-a-specific-sobject)    | Creates a record in Salesforce. |
| [createMultipleRecords](#creating-multiple-records)      | Creates multiple sObject(s) in Salesforce.|
| [createNestedRecords](#creating-nested-records)      | Create nested records for sObjects. |
| [update](#updating-a-record)    | Updates a record in Salesforce. |
| [delete](#deleting-a-record)      | Deletes a record in Salesforce. |
| [recentlyViewedItem](#retrieving-the-recently-viewed-items)   | Retrieves the recently viewed item in Salesforce.|
| [retrieveFieldValues](#retrieving-specific-field-values)      | Retrieves specific field values for a specific sObject. |
| [upsert](#upserting-a-record-using-an-external-id)    | Creates or updates (upserts) a record using external ID. |

### Operation details

This section provides more details on each of the operations related to records.

#### Creating a record for a specific sObject
To create a record, use salesforcerest.create and specify the following properties.

**create**
```xml
<salesforcerest.create>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
    <fieldAndValue>{$ctx:fieldAndValue}</fieldAndValue>
</salesforcerest.create>
```
**Properties**
* sObjectName: The type of object for which you will create a reco
* FieldAndValue: The .json format property used to create the record. Include all mandatory fields according to the requirements for the specified sobject. For example, if you are creating a record for the Account sObject, "name" is a mandatory parameter, and you might want to include the optional description, so the fieldAndValue property would look like this:
    ```json
    {
        "name":"wso2",
        "description":"This account belongs to WSO2"
    }
    ```
**Sample request**

Following is a sample request that can be handled by the create operation.

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "intervalTime" : "100000",
  "apiVersion": "v32.0",
  "sObjectName":"Account",
  "registryPath": "connectors/SalesforceRest",
  "fieldAndValue": {
    "name": "wso2",
    "description":"This Account belongs to WSO2"
  }
}
```
**Sample response**

Given below is a sample response for the create operation.

```json
{
   "success":true,
   "id":"0010K00001uiAn8QAE",
   "errors":[

   ]
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_sobject_create.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_sobject_create.htm)

#### Creating multiple records

To create multiple records of the same sObject type, use salesforcerest.createMultipleRecords and specify the following properties.

**describeSObject**
```xml
<salesforcerest.createMultipleRecords>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
    <fieldAndValue>{$ctx:fieldAndValue}</fieldAndValue>
</salesforcerest.createMultipleRecords>
```
**Properties**
* sObjectName: The object type for which you are creating records.
* fieldAndValue: The .json format property, which specifies each record as an entry within the records array. Include all mandatory fields according to the requirements for the specified sobject. For example, if you are creating records for the Account sObject, "name" is a mandatory parameter, and you might want to include additional optional values for each record, so the fieldAndValue property might look like this:
    ```json
    {
      "records": [
        {
          "attributes": {"type": "Account", "referenceId": "ref1"},
          "name": "wso2",
          "phone": "1111111",
          "website": "www.salesforce1.com"
        },
        {
          "attributes": {"type": "Account", "referenceId": "ref2"},
          "name": "slwso2",
          "phone": "22222222",
          "website": "www.salesforce2.com"
        }]
    }
    ```

**Sample request**

Following is a sample request that can be handled by the createMultipleRecords operation.

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
  "registryPath": "connectors/SalesforceRest",
  "fieldAndValue": {
  "records": [
    {
      "attributes": {"type": "Account", "referenceId": "ref1"},
      "name": "wso2",
      "phone": "1111111",
      "website": "www.salesforce1.com"
    },
    {
      "attributes": {"type": "Account", "referenceId": "ref2"},
      "name": "slwso2",
      "phone": "22222222",
      "website": "www.salesforce2.com"
    }]
}
}
```
**Sample response**

Given below is a sample response for the createMultipleRecords operation.

```json
{
    "hasErrors" : false,
    "results" : [{
     "referenceId" : "ref1",
     "id" : "001D000000K1YFjIAN"
     },{
     "referenceId" : "ref2",
     "id" : "001D000000K1YFkIAN"
     },{
     "referenceId" : "ref3",
     "id" : "001D000000K1YFlIAN"
     },{
     "referenceId" : "ref4",
     "id" : "001D000000K1YFmIAN"     
     }]
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/dome_composite_sobject_tree_flat.htm#topic-title](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/dome_composite_sobject_tree_flat.htm#topic-title)

#### Creating nested records

To create nested records for a specific sObject, use salesforcerest.createNestedRecords and specify the following properties.

**createNestedRecords**
```xml
<salesforcerest.createNestedRecords>
    <sObjectName>{$ctx:sObjectName}</sobject>
    <fieldAndValue>{$ctx:fieldAndValue}</fieldAndValue>
</salesforcerest.createNestedRecords>
```
**Properties**
* sObjectName: The object type for which you are creating records.
* fieldAndValue: The .json format property, which specifies each record as an entry within the records array. Include all mandatory fields according to the requirements for the specified sobject. For example, if you are creating records for the Account sObject, "name" is a mandatory parameter, and you might want to include additional optional values for each record, so the fieldAndValue property might look like this:
    ```json
    {
      "records" :[{
        "attributes" : {"type" : "Account", "referenceId" : "ref1"},
        "name" : "SampleAccount1",
        "phone" : "1234567890",
        "website" : "www.salesforce.com",
        "numberOfEmployees" : "100",
        "type" : "Analyst",
        "industry" : "Banking",
        "Contacts" : {
          "records" : [{
            "attributes" : {"type" : "Contact", "referenceId" : "ref2"},
            "lastname" : "Smith",
            "Title" : "President",
            "email" : "sample@salesforce.com"
          },{
            "attributes" : {"type" : "Contact", "referenceId" : "ref3"},
            "lastname" : "Evans",
            "title" : "Vice President",
            "email" : "sample@salesforce.com"
          }]
        }
      },{
        "attributes" : {"type" : "Account", "referenceId" : "ref4"},
        "name" : "SampleAccount2",
        "phone" : "1234567890",
        "website" : "www.salesforce.com",
        "numberOfEmployees" : "52000",
        "type" : "Analyst",
        "industry" : "Banking",
        "childAccounts" : {
          "records" : [{
            "attributes" : {"type" : "Account", "referenceId" : "ref5"},
            "name" : "SampleChildAccount1",
            "phone" : "1234567890",
            "website" : "www.salesforce.com",
            "numberOfEmployees" : "100",
            "type" : "Analyst",
            "industry" : "Banking"
          }]
        },
        "Contacts" : {
          "records" : [{
            "attributes" : {"type" : "Contact", "referenceId" : "ref6"},
            "lastname" : "Jones",
            "title" : "President",
            "email" : "sample@salesforce.com"
          }]
        }
      }]
    }
    ```

**Sample request**

Following is a sample request that can be handled by the createNestedRecords operation.

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "intervalTime" : "100000",
  "apiVersion": "v32.0",
  "sObjectName":"Account",
  "registryPath": "connectors/SalesforceRest",
  "fieldAndValue":
    {
  "records" :[{
    "attributes" : {"type" : "Account", "referenceId" : "ref1"},
    "name" : "SampleAccount1",
    "phone" : "1234567890",
    "website" : "www.salesforce.com",
    "numberOfEmployees" : "100",
    "type" : "Analyst",
    "industry" : "Banking",
    "Contacts" : {
      "records" : [{
        "attributes" : {"type" : "Contact", "referenceId" : "ref2"},
        "lastname" : "Smith",
        "Title" : "President",
        "email" : "sample@salesforce.com"
      },{
        "attributes" : {"type" : "Account", "referenceId" : "ref3"},
        "lastname" : "Evans",
        "title" : "Vice President",
        "email" : "sample@salesforce.com"
      }]
    }
  },{
    "attributes" : {"type" : "Account", "referenceId" : "ref4"},
    "name" : "SampleAccount2",
    "phone" : "1234567890",
    "website" : "www.salesforce.com",
    "numberOfEmployees" : "52000",
    "type" : "Analyst",
    "industry" : "Banking",
    "childAccounts" : {
      "records" : [{
        "attributes" : {"type" : "Account", "referenceId" : "ref5"},
        "name" : "SampleChildAccount1",
        "phone" : "1234567890",
        "website" : "www.salesforce.com",
        "numberOfEmployees" : "100",
        "type" : "Analyst",
        "industry" : "Banking"
      }]
    },
    "Contacts" : {
      "records" : [{
        "attributes" : {"type" : "Contact", "referenceId" : "ref6"},
        "lastname" : "Jones",
        "title" : "President",
        "email" : "sample@salesforce.com"
      }]
    }
  }]
}
}
```
**Sample response**

Given below is a sample response for the createNestedRecords operation.

```json
{
    "hasErrors" : false,
    "results" : [{
     "referenceId" : "ref1",
     "id" : "001D000000K0fXOIAZ"
     },{
     "referenceId" : "ref4",
     "id" : "001D000000K0fXPIAZ"
     },{
     "referenceId" : "ref2",
     "id" : "003D000000QV9n2IAD"
     },{
     "referenceId" : "ref3",
     "id" : "003D000000QV9n3IAD"
     },{
     "referenceId" : "ref5",
     "id" : "001D000000K0fXQIAZ"
     },{
     "referenceId" : "ref6",
     "id" : "003D000000QV9n4IAD"
     }]
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/dome_composite_sobject_tree_create.htm](https://developer.salesforce.com/docs/atlas.en-us.198.0.api_rest.meta/api_rest/dome_composite_sobject_tree_create.htm)

#### Updating a record
To update a record, use salesforcerest.update and specify the following properties.

**update**
```xml
<salesforcerest.update>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
    <fieldAndValue>{$ctx:fieldAndValue}</fieldAndValue>
    <Id>{$ctx:Id}</Id>
</salesforcerest.update>
```
* sObjectName: The object type of the record.
* fieldAndValue: The json format property with the new definition for the record.
* Id: The ID of the record you are updating.

**Sample request**

Following is a sample request that can be handled by the update operation.

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "intervalTime" : "100000",
  "apiVersion": "v32.0",
  "sObjectName":"Account",
  "Id":"00128000002OOhD",
  "registryPath": "connectors/SalesforceRest",
  "fieldAndValue": {
    "name": "wso2",
    "description":"This Account belongs to WSO2"
  }
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_update_fields.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_update_fields.htm)

#### Deleting a record

To delete a record, use salesforcerest.delete and specify the following properties.

**delete**
```xml
<salesforcerest.delete>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
    <idToDelete>{$ctx:idToDelete}</idToDelete>
</salesforcerest.delete>
```
* sObjectName: The object type of the record.
* Id: The ID of the record you are deleting.

**Sample request**

Following is a sample request that can be handled by the delete operation.

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "intervalTime" : "100000",
  "apiVersion": "v32.0",
  "sObjectName":"Account",
  "idToDelete":"00128000002OOhD",
  "registryPath": "connectors/SalesforceRest"
 }
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_delete_record.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_delete_record.htm)

#### Retrieving the recently viewed items

To retrieve the recently viewed items, use salesforcerest.recentlyViewedItem and specify the following properties.

**recentlyViewedItem**
```xml
<salesforcerest.recentlyViewedItem>
    <limit>{$ctx:limit}</limit>
</salesforcerest.recentlyViewedItem>
```
**Properties**
* limit: The maximum number of records to be returned.

**Sample request**

Following is a sample request that can be handled by the recentlyViewedItem operation.

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "intervalTime" : "100000",
  "apiVersion": "v32.0",
  "limit":"5",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the recentlyViewedItem operation.

```json
{"output":"[{\"attributes\":
   {\"type\":\"User\",
   \"url\":\"/services/data/v32.0/sobjects/User/00528000000ToIrAAK\"},
   \"Id\":\"00528000000ToIrAAK\",
   \"Name\":\"kesan yoga\"},
   .
   .
   ]"
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_recent_items.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/resources_recent_items.htm)


#### Retrieving specific field values

To retrieve specific field values for a specific sObject, use salesforcerest.retrieveFieldValues and specify the following properties.

**retrieveFieldValues**
```xml
<salesforcerest.retrieveFieldValues>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
    <rowId>{$ctx:rowId}</rowId>
    <fields>{$ctx:fields}</fields>
</salesforcerest.retrieveFieldValues>
```
**Properties**
* sObjectName: The object type whose metadata you want to retrieve.
* rowId: The ID of the record whose values you want to retrieve.
* fields: A comma-separated list of fields whose values you want to retrieve. 

**Sample request**

Following is a sample request that can be handled by the retrieveFieldValues operation.

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "intervalTime" : "100000",
  "apiVersion": "v32.0",
  "sObjectName": "Account",
  "rowId":"00128000005YjDnAAK",
  "fields":"AccountNumber,BillingPostalCode",
  "registryPath": "connectors/SalesforceRest"
}
```
**Sample response**

Given below is a sample response for the retrieveFieldValues operation.

```json
{
    "AccountNumber" : "CD656092",
    "BillingPostalCode" : "27215",
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_get_field_values.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_get_field_values.htm)

#### Upserting a record using an external ID
To create or update (upsert) a record using an external ID, use salesforcerest.upsert and specify the following properties. This method is used to create records or update existing records based on the value of a specified external ID field.

    * If the specified value doesn't exist, a new record is created.
    * If a record does exist with that value, the field values specified in the request body are updated.
    
**upsert**
```xml
<salesforcerest.upsert>
    <sObjectName>{$ctx:sObjectName}</sObjectName>
    <externalIDField>{$ctx:externalIDField}</externalIDField>
    <Id>{$ctx:Id}</Id>
    <fieldAndValue>{$ctx:fieldAndValue}</fieldAndValue>
</salesforcerest.upsert>
```
**Properties**
* sObjectName: The object type whose metadata you want to retrieve.
* externalIDField: The external Id Field of the subject.
* Id: The value of the customExtIdField.
* fieldAndValue: The json format property/payload used to create the record.

**Sample request**

Following is a sample request that can be handled by the upsert operation.

```json
{
  "accessToken":"00D280000017q6q!AQoAQMMZWoN9MQZcXLW475YYoIdJFUICTjbGh67jEfAeV7Q57Ac2Ov.0ZuM_2Zx6SnrOmwpml8Qf.XclstTQiXtCYSGRBcEv",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_ztpnAk6BGQxRdovMLhHso81iyYKO6hTm68KfebpK7UYtEzF0ku8JCz7CNto8b3YMRmZrhy",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "apiVersion": "v32.0",
  "sObjectName":"Account",
  "registryPath": "connectors/Salesforcerest",
  "intervalTime" : "2400000",
  "externalIDField":"sample__c",
  "Id":"15222",
  "fieldAndValue":
  {
      "Name":"john"
  }
 }
```
**Sample response**

Given below is a sample response for the upsert operation.

```json
{
    "id" : "00190000001pPvHAAU",
    "errors" : [ ],
    "success" : true
}
```

**Related Salesforce documentation**

[https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_upsert.htm](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_upsert.htm)

### Sample configuration

Following example illustrates how to connect to Salesforce with the init operation and create operation.

1. Create a sample proxy as below :
```xml
<proxy xmlns="http://ws.apache.org/ns/synapse" name="create"
       statistics="disable" trace="disable" transports="https http">
    <target>
        <inSequence>
            <property name="accessToken" expression="json-eval($.accessToken)"/>
            <property name="apiUrl" expression="json-eval($.apiUrl)"/>
            <property name="fieldAndValue" expression="json-eval($.fieldAndValue)"/>
            <property name="sObjectName" expression="json-eval($.sObjectName)"/>
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
            <salesforcerest.create>
                <fieldAndValue>{$ctx:fieldAndValue}</fieldAndValue>
                <sObjectName>{$ctx:sObjectName}</sObjectName>
            </salesforcerest.create>
            <send/>
        </inSequence>
    </target>
    <description/>
</proxy>
```

2. Create a json file named create.json and copy the configurations given below to it:

```json
{
  "accessToken":"00D280000017q6q!AQoAQOeXcp7zKo3gUdy6r064_LsJ5bYYrUn_qAZG9TtKFLPfUMRxiato.E162_2XAtCTZLFQTbNk2Rz6Zm_juSakFE_aaBPp",
  "apiUrl":"https://ap2.salesforce.com",
  "clientId": "3MVG9ZL0ppGP5UrBrnsanGUZRgHqc8gTV4t_6tfuef8Zz4LhFPipmlooU6GBszpplbTzVXXWjqkGHubhRip1s",
  "refreshToken": "5Aep861TSESvWeug_xvFHRBTTbf_YrTWgEyjBJo7Xr34yOQ7GCFUN5DnNPxzDIoGoWi4evqOl_lT1B9nE5dAtSb",
  "clientSecret": "9104967092887676680",
  "hostName": "https://login.salesforce.com",
  "intervalTime" : "100000",
  "apiVersion": "v32.0",
  "sObjectName":"Account",
  "registryPath": "connectors/SalesforceRest",
  "fieldAndValue": {
    "name": "wso2",
    "description":"This Account belongs to WSO2"
  }
}
```
3. Replace the credentials with your values.

4. Execute the following curl command:

```bash
curl http://localhost:8280/services/create -H "Content-Type: application/json" -d @create.json
```
5. Salesforce returns a json response similar to the one shown below:
 
```json
{
   "success":true,
   "id":"0010K00001uiAn8QAE",
   "errors":[

   ]
}
```
