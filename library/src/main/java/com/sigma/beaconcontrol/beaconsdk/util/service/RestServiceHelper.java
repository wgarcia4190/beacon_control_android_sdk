package com.sigma.beaconcontrol.beaconsdk.util.service;

import android.support.annotation.NonNull;
import android.util.Base64;

import com.sigma.beaconcontrol.beaconsdk.core.error.ExceptionHandler;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Wilson on 7/13/17.
 */

public class RestServiceHelper {
    private static final String TAG = RestServiceHelper.class.getSimpleName();
    private static int responseCode = 0;
    private static boolean isLastVersion = true;

    public static String createGetParameters(String url, HashMap<String, String> params) throws UnsupportedEncodingException {
        if (params != null && params.size() > 0) {
            url = generateURL(url, params);
            url += "?";
            for (Map.Entry<String, String> entry : params.entrySet()) {
                try {
                    url += URLEncoder.encode(entry.getKey(), "utf-8") + "=" + URLEncoder.encode(entry.getValue(), "utf-8");
                    url += '&';
                } catch (Exception ex) {
                    continue;
                }
            }
        }
        return url;
    }

    private static String generateURL(String template, Map<String, String> model) {
        if (template.contains("${")) {
            for (String key : model.keySet()) {
                String flag = "${" + key + "}";

                if (template.contains(flag)) {
                    template = template.replace(flag, model.get(key));
                    model.remove(key);
                }

            }
        }
        return template;
    }

    public static HttpURLConnection createConnection(HttpScheme httpScheme, String urlString, String methodType, String contentType, HashMap<String, String> headers, String cookie) throws IOException {
        return httpScheme == HttpScheme.HTTP ?
                RestServiceHelper.createHttpConnection(urlString, methodType,
                        contentType, headers, cookie) :
                RestServiceHelper.createHttpsConnection(urlString, methodType,
                        contentType, headers, cookie);
    }

    public static HttpURLConnection createHttpConnection(String urlString, String methodType, String contentType, HashMap<String, String> headers, String cookie) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(10000 /* milliseconds */);
        conn.setRequestMethod(methodType);
        conn.setDoInput(true);

        if (!"GET".equals(methodType)) {
            conn.setDoOutput(true);
        }

        conn.setRequestProperty("Content-type", contentType);
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Connection", "keep-alive");

        if (cookie != null && !cookie.isEmpty()) {
            conn.setRequestProperty("Cookie", cookie);
        }

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                conn.setRequestProperty(header.getKey(), header.getValue());
            }
        }

        try {
            conn.connect();
        } catch (ConnectException e) {
            ExceptionHandler.handleException(e);

        }
        return conn;
    }

    public static HttpsURLConnection createHttpsConnection(String urlString, String methodType, String contentType, HashMap<String, String> headers, String cookie) throws IOException {
        URL url = new URL(urlString);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(10000 /* milliseconds */);
        conn.setRequestMethod(methodType);
        conn.setDoInput(true);

        if (!"GET".equals(methodType)) {
            conn.setDoOutput(true);
        }

        conn.setRequestProperty("Content-type", contentType);
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Connection", "keep-alive");

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                conn.setRequestProperty(header.getKey(), header.getValue());
            }
        }

        try {
            conn.connect();
        } catch (ConnectException e) {
            ExceptionHandler.handleException(e);

        }
        return conn;
    }

    public static String getBasicAuthString(String username, String password) {
        String userCredentials = username.concat(":").concat(password);
        String basicAuth = "Basic " + new String(Base64.encode(userCredentials.getBytes(), Base64.NO_WRAP));

        return basicAuth;
    }

    @NonNull
    public static String getResponse(InputStream is, URLConnection conn) throws IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection) conn).disconnect();
        } else {
            ((HttpsURLConnection) conn).disconnect();
        }

        return result.toString();
    }

    public static void setBody(String body, URLConnection conn) throws IOException, JSONException {
        if (body != null) {
            DataOutputStream printout = new DataOutputStream(conn.getOutputStream());

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(printout, "UTF-8"));
            writer.write(body);
            writer.close();

            printout.flush();
            printout.close();
        }
    }
}
