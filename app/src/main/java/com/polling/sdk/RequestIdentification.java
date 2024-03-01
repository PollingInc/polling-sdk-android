package com.polling.sdk;

import java.net.URLEncoder;

public class RequestIdentification
{
    public String customerId = null;
    public String apiKey = null;


    public RequestIdentification(String customerId, String apiKey) {
        this.customerId = customerId;
        this.apiKey = apiKey;
    }

    public String ApplyKeyToURL(String url)
    {
        StringBuilder buffer = new StringBuilder(url);

        if (customerId != null && !customerId.isEmpty() && apiKey != null && !apiKey.isEmpty()) {
            buffer.append("?customer_id=").append(URLEncoder.encode(customerId));
            buffer.append("&api_key=").append(URLEncoder.encode(apiKey));
        }
        return buffer.toString();
    }
}
