package com.sandbox.parker.sandboxapi.http;


import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by parker on 9/15/17.
 */

public class HTTPRequest {

    interface Progress {
        int ERROR = -1;
        int CONNECT_SUCCESS = 0;
        int GET_INPUT_STREAM_SUCCESS = 1;
        int PROCESS_INPUT_STREAM_IN_PROGRESS = 2;
        int PROCESS_INPUT_STREAM_SUCCESS = 3;
    }

    public static final String HTTP_REQUEST_METHOD_GET = "GET";
    public static final String HTTP_REQUEST_METHOD_HEAD = "HEAD";
    public static final String HTTP_REQUEST_METHOD_POST = "POST";
    public static final String HTTP_REQUEST_METHOD_PUT = "PUT";
    public static final String HTTP_REQUEST_METHOD_DELETE = "DELETE";
    public static final String HTTP_REQUEST_METHOD_TRACE = "TRACE";
    public static final String HTTP_REQUEST_METHOD_OPTIONS = "OPTIONS";
    public static final String HTTP_REQUEST_METHOD_CONNECT = "CONNECT";
    public static final String HTTP_REQUEST_METHOD_PATCH = "PATCH";

    private String mBaseURL;

    public HTTPRequest(String baseURL) {

        if (baseURL.isEmpty() || baseURL == null) {
            mBaseURL = "";
            Log.e(this.getClass().getSimpleName(), "URL is emtpy/null");
        }

        if (!baseURL.endsWith("/")) {
            baseURL += "/";
        }

        mBaseURL = baseURL;

    }

    public void get(String endpoint) {

        try {

            URL url = new URL(mBaseURL + endpoint);
            executeHTTPRequest(url, HTTP_REQUEST_METHOD_GET);

        } catch (MalformedURLException e) {

            Log.e(
                    this.getClass().getSimpleName(),
                    "cannot perform get request, url not formatted correctly"
            );
        }
    }

    public String post(String endpoint, Map<String, String> parameters) {

        String result = "";

        try {

            URL url = new URL(mBaseURL + endpoint);

            result = executeHTTPRequest(
                    url,
                    HTTP_REQUEST_METHOD_POST,
                    getPostParamsAsString(parameters)
            );

        } catch (MalformedURLException e) {

            Log.e(
                    this.getClass().getSimpleName(),
                    "cannot perform post request, url not formatted correctly"
            );
        }
        return result;
    }

    // url endpoint ? params
    // user="Trent"&age="22"

    private String getPostParamsAsString(Map<String, String> params) {

        String paramsString = "";

        if (params != null && !params.isEmpty()) {
            int count = 0;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                paramsString += (entry.getKey() + "=" + entry.getValue());
                if (count < (params.size() -1)) {
                    paramsString += "&";
                }
                count++;
            }
        } else {
            Log.e(this.getClass().getSimpleName(), "Parameters provided to POST is empty");
        }

        return paramsString;
    }

    private void executeHTTPRequest(URL url, String HTTPMethod) {
        executeHTTPRequest(url, HTTPMethod, null);
    }

    private String executeHTTPRequest(URL url, String HTTPMethod, String params) {

        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;

        try {

            connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestMethod(HTTPMethod);
            connection.setDoInput(true);
            connection.connect();

            if (HTTP_REQUEST_METHOD_POST.equals(HTTPMethod)) {

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(params);
                wr.flush();
                wr.close();
            }

            publishProgress(Progress.CONNECT_SUCCESS, 0);

            int responseCode = connection.getResponseCode();
            Log.d(getClass().getSimpleName(), "Response Code: " + responseCode);
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            stream = connection.getInputStream();
            publishProgress(Progress.GET_INPUT_STREAM_SUCCESS, 0);
            if (stream != null) {
                result = readStream(stream, 500);
            }
        } catch (IOException e) {

            Log.e(
                    this.getClass().getSimpleName(),
                    "the request threw an IO exception"
            );

        } finally {

            try {

                if (stream != null) {
                    stream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }

            } catch (IOException e) {

                Log.e(
                        this.getClass().getSimpleName(),
                        "warning: request not closed properly"
                );

            }
        }

        return result;
    }

    private void publishProgress(int getInputStreamSuccess, int i) {
        switch (getInputStreamSuccess) {
            case 0:
                Log.d("Sandbox", "Connection was successful!");
            case 1:
                Log.d("Sandbox", "Obtaining input stream was successful!");
                break;
        }
    }

    private String readStream(InputStream stream, int maxReadSize)
            throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] rawBuffer = new char[maxReadSize];
        int readSize;
        StringBuffer buffer = new StringBuffer();
        while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0) {
            if (readSize > maxReadSize) {
                readSize = maxReadSize;
            }
            buffer.append(rawBuffer, 0, readSize);
            maxReadSize -= readSize;
        }
        return buffer.toString();
    }

    public String getBaseURL() {
        return mBaseURL;
    }

}
