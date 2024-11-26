package com.polling.sdk.core.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;



public class WebRequestHandler {

    public interface ResponseCallback {
        void onResponse(String response);
        void onError(String error);
    }


    public static void makeRequest(final String urlString, final WebRequestType method, final String data, final ResponseCallback callback)
    {
        makeRequest(urlString, method, data, callback, "application/json");
    }


    /**
     * Makes an HTTP request with the specified method and data.
     *
     * @param urlString   The URL to make the request to.
     * @param method      The HTTP method (GET, POST, PUT, DELETE, etc.).
     * @param data        The data to send with the request (for POST/PUT), or null for methods that don't send data.
     * @param callback    The callback to handle the response or error.
     */
    public static void makeRequest(final String urlString, final WebRequestType method, final String data, final ResponseCallback callback, String contentType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urlString);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod(method.toString());

                    if (data != null && !data.isEmpty() && (method.equals(WebRequestType.POST) || method.equals(WebRequestType.PUT))){
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Content-Type", contentType);
                        try (OutputStream os = connection.getOutputStream()) {
                            byte[] input = data.getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }
                    }

                    connection.connect();

                    InputStream stream = connection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST ? connection.getInputStream() : connection.getErrorStream();
                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line).append('\n');
                    }

                    if (callback != null) {
                        callback.onResponse(response.toString());
                    }
                } catch (IOException e) {
                    if (callback != null) {
                        callback.onError(e.getMessage());
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
}
