## Custom HTTP Client Mediator for WSO2

This project implements a custom HTTP client mediator for WSO2, which allows you to send HTTP GET requests, process the responses in JSON format, and recursively follow pagination links until no further "next" link is available.

### Features
Sends HTTP GET requests to a specified URL.
Recursively fetches data from multiple pages if "next" pagination links are available.
Logs items from the JSON response.
Handles HTTP response errors and logs them.
Easy integration into WSO2 Micro Integrator or WSO2 ESB.


### Requirements
- Java 8 or higher.
- Maven 3.6 or higher.
- WSO2 Micro Integrator (WSO2 MI) or WSO2 Enterprise Service Bus (ESB).

### Installation
1. Clone the repository or navigate to your project directory.

2. Open a terminal or command prompt in the project directory.

3. Run the following Maven command to compile and build the JAR:
```dtd
mvn clean install
```
This will generate a JAR file in the target/ directory, e.g., `target/custom-http-client-mediator-1.0.0.jar`.

4. Deploy to WSO2 Micro Integrator or ESB : Copy the generated JAR file (`custom-http-client-mediator-1.0.0.jar`) from the target/ folder.
   Paste the JAR file into the `{wso2mi}/lib/` (or `{wso2esb}/lib/`) directory.
   Restart your WSO2 Micro Integrator or ESB to load the new mediator.

##  Configuration
### Set Initial URL
You need to set the initial URL for the mediator to make the first HTTP GET request. You can do this programmatically by calling the setInitialUrl() method or configure it via a Synapse configuration file.

**Example:**
```dtd
<class name="org.custom.CustomHttpClient">     
    <property name="initialUrl" value="https://run.mocky.io/v3/7fdb6b65-6e71-4c44-bc37-06b0f844e201"/>
</class>
```

## Functionality
### Recursion on Pagination
The mediator follows pagination links automatically. If the response JSON contains a links array with a "next" link, the mediator will recursively send GET requests to the new URL indicated by the "href" value of the "next" link.

### Logging Items
The mediator logs each item in the response. Each item in the items array is logged as a string representation of the item object.

### Error Handling
If any error occurs during the request, such as an invalid URL, a network failure, or an issue processing the response, it is logged with the message and stack trace.

## Example JSON Response
Here is an example of the expected JSON response structure from the API being queried:
```json
{
  "hasMore": null,
  "limit": null,
  "count": null,
  "links": [
    {
      "href": null,
      "rel": "describedBy"
    },
    {
      "href": "http://nexturl.com",
      "rel": "next"
    }
  ],
  "items": [
    {
      "msgType": null,
      "item": null,
      "location": null,
      "loctype": null,
      "price": null,
      "pricetype": null,
      "effective": null,
      "eventid": null,
      "resetind": null,
      "action": null
    }
  ]
}
```