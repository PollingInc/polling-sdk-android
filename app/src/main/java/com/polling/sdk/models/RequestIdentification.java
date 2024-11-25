package com.polling.sdk.models;

import java.net.URLEncoder;

public class RequestIdentification
{
    public String customerId = null;
    public String apiKey = null;
    private Boolean keyApplied = false;


    public RequestIdentification(){};
    public RequestIdentification(String customerId, String apiKey) {
        this.customerId = customerId;
        this.apiKey = apiKey;
    }

    //----------------------------------------------------------------------------------------------
    public String ApplyKeyToURL(String url)
    {
        return ApplyKeyToURL(url, "customer_id", "api_key");
    }

    /*
    url: base url.
    customerIdField: name of the customerId field. If null, it won't be included.
    apiKeyField: name of the apiKey field. If null, it won't be included.
    */
    public String ApplyKeyToURL(String url, String customerIdField, String apiKeyField)
    {
        StringBuilder buffer = new StringBuilder(url);

        if (customerId != null && !customerId.isEmpty() && apiKey != null && !apiKey.isEmpty()) {

            String urlArtifact = "?";

            if(customerIdField != null)
            {
                buffer.append(urlArtifact + customerIdField + "=").append(URLEncoder.encode(customerId));
                urlArtifact = "&";
            }

            if(apiKeyField != null)
            {
                buffer.append(urlArtifact + apiKeyField + "=").append(URLEncoder.encode(apiKey));
            }

            keyApplied = true;
        }
        return buffer.toString();
    }

    //----------------------------------------------------------------------------------------------
    public String ApplyCompletionBypassToURL(String url)
    {
        if(!keyApplied) return url;

        StringBuilder buffer = new StringBuilder(url);
        buffer.append("&skipCompletedCheck=true");
        return url;
    }
}
