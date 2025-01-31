package org.custom;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

public class CustomHttpClient extends AbstractMediator implements ManagedLifecycle {
    private static final Log log = LogFactory.getLog(CustomHttpClient.class);
    private HttpClient httpClient;
    private String initialUrl;

    @Override
    public boolean mediate(MessageContext messageContext) {
        try {
            if (initialUrl == null || initialUrl.isEmpty()) {
                log.error("Initial URL is empty. Please provide a valid URL.");
                return true;
            }
            processUrl(initialUrl);
        } catch (Exception e) {
            handleException("Exception occurred in CustomHttpClient mediator", e);
        }
        return true;
    }

    private void processUrl(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                logItems(jsonResponse.getJSONArray("items"));
                findNextUrl(jsonResponse);
            } else {
                log.error("Failed to fetch data from: " + url + ", Status Code: " + response.statusCode());
            }
        } catch (Exception e) {
            handleException("Error processing URL: " + url, e);
        }
    }

    // log the items
    private void logItems(JSONArray items) {
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            log.info("Item: " + item.toString());
        }
    }

    private void findNextUrl(JSONObject jsonResponse) {
        if (jsonResponse.has("links")) {
            JSONArray links = jsonResponse.getJSONArray("links");
            for (int i = 0; i < links.length(); i++) {
                JSONObject link = links.getJSONObject(i);
                if ("next".equals(link.optString("rel", ""))) {  // Avoid null issues with optString
                    String nextUrl = link.optString("href", null);
                    if (nextUrl != null && !nextUrl.isEmpty()) {
                        log.info("Following next link: " + nextUrl);
                        processUrl(nextUrl);  // Recursive call to fetch next page
                    }
                }
            }
        }
    }

    @Override
    public void init(SynapseEnvironment synapseEnvironment) {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Override
    public void destroy() {}

    public static void handleException(String msg, Throwable t) throws SynapseException {
        log.error(msg, t);
        throw new SynapseException(msg, t);
    }

    public void setInitialUrl(String initialUrl) {
        this.initialUrl = initialUrl;
    }
}
