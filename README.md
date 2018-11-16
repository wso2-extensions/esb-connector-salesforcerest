# Salesforce REST EI Connector

The Salesforce REST [Connector](https://docs.wso2.com/display/EI640/Working+with+Connectors) allows you to work with records in Salesforce, a web-based service that allows organizations to manage contact relationship management (CRM) data. You can use the Salesforce connector to create, query, retrieve, update, and delete records in your organization's Salesforce data. The connector uses the [Salesforce REST API](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/intro_what_is_rest_api.htm) to interact with Salesforce.

## Compatibility

| Connector version | Supported Salesforce REST API version | Supported WSO2 ESB/EI version |
| ------------- | ------------- | ------------- |
| [1.0.5](https://github.com/wso2-extensions/esb-connector-salesforcerest/tree/org.wso2.carbon.connector.salesforcerest-1.0.5) | v32.0 | ESB 5.0.0, EI 6.1.0, 6.1.1, 6.2.0, 6.3.0, 6.4.0 |
| [1.0.4](https://github.com/wso2-extensions/esb-connector-salesforcerest/tree/org.wso2.carbon.connector.salesforcerest-1.0.4) | v32.0 | ESB 4.9.0, 5.0.0  |

## Getting started

#### Download and install the connector

1. Download the connector from the [WSO2 Store](https://store.wso2.com/store/assets/esbconnector/details/43e44763-0d73-4ab3-8ae9-d6f73532d164) by clicking the Download Connector button.
2. Then you can follow this [Documentation](https://docs.wso2.com/display/EI640/Working+with+Connectors+via+the+Management+Console) to add and enable the connector via the Management Console in your EI instance.
3. For more information on using connectors and their operations in your EI configurations, see [Using a Connector](https://docs.wso2.com/display/EI640/Using+a+Connector).
4. If you want to work with connectors via EI tooling, see [Working with Connectors via Tooling](https://docs.wso2.com/display/EI640/Working+with+Connectors+via+Tooling).

#### Configuring the connector operations

To get started with Salesforce REST connector and their operations, see [Configuring Salesforce REST Operations](docs/config.md).


## Building From the Source

Follow the steps given below to build the Salesforce REST connector from the source code:

1. Get a clone or download the source from [Github](https://github.com/wso2-extensions/esb-connector-salesforcerest).
2. Run the following Maven command from the `esb-connector-salesforcerest` directory: `mvn clean install`.
3. The Salesforce connector zip file is created in the `esb-connector-salesforcerest/target` directory

## How You Can Contribute

As an open source project, WSO2 extensions welcome contributions from the community.
Check the [issue tracker](https://github.com/wso2-extensions/esb-connector-salesforcerest/issues) for open issues that interest you. We look forward to receiving your contributions.
