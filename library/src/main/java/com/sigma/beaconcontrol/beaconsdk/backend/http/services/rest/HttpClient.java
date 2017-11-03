package com.sigma.beaconcontrol.beaconsdk.backend.http.services.rest;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.sigma.beaconcontrol.beaconsdk.backend.http.parsers.Parser;
import com.sigma.beaconcontrol.beaconsdk.core.error.ExceptionHandler;
import com.sigma.beaconcontrol.beaconsdk.util.ApplicationUtils;
import com.sigma.beaconcontrol.beaconsdk.util.service.HttpMethod;
import com.sigma.beaconcontrol.beaconsdk.util.service.HttpScheme;
import com.sigma.beaconcontrol.beaconsdk.util.service.RestServiceHelper;

import org.json.JSONException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.HashMap;


/**
 * Created by Wilson on 7/13/17.
 */

public class HttpClient {

    private String baseUrl;
    private ThreadLocal<String> endPoint = new ThreadLocal<>();
    private HttpScheme httpScheme;
    private ThreadLocal<String> httpMethod = new ThreadLocal<>();
    private ThreadLocal<String> cookie = new ThreadLocal<>();
    private ThreadLocal<HashMap<String, String>> parameters = new ThreadLocal<>();
    private ThreadLocal<HashMap<String, String>> headers = new ThreadLocal<>();

    public HttpClient(String baseUrl, HttpScheme httpScheme) {
        this.baseUrl = baseUrl;
        this.httpScheme = httpScheme;
    }

    public HttpClient setEndPoint(@NonNull String endPoint) {
        this.endPoint.set(endPoint);
        return this;
    }

    public HttpClient setHttpMethod(@Nullable HttpMethod httpMethod) {
        if (httpMethod == null) {
            this.httpMethod.set(HttpMethod.GET.getMethod());
            return this;
        }
        this.httpMethod.set(httpMethod.getMethod());
        return this;
    }

    public HttpClient setCookie(@Nullable String cookie) {
        this.cookie.set(cookie);
        return this;
    }

    public HttpClient setParameters(@NonNull HashMap<String, String> parameters) {
        if (this.parameters.get() == null) {
            this.parameters.set(parameters);
        } else {
            this.parameters.get().putAll(parameters);
        }
        return this;
    }

    public HttpClient setHeaders(@NonNull HashMap<String, String> headers) {
        this.headers.set(headers);
        return this;
    }

    public HttpClient setBody(@NonNull String body) {
        if (this.parameters.get() == null) {
            this.parameters.set(new HashMap<String, String>());
        }

        this.parameters.get().put("body", body);

        return this;
    }


    public <T extends Parser> void call(final Callable<T> callable) {
        final Parser parser = callable.getParserInstance();

        final String threadEndPoint = endPoint.get();
        final HttpMethod threadMethod = HttpMethod.valueOf(httpMethod.get());
        final String threadCookie = cookie.get();
        final HashMap<String, String> threadHeaders = headers.get();

        new AsyncTask<HashMap<String, String>, T, T>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                callable.onPreExecute();
            }

            @Override
            protected T doInBackground(HashMap<String, String>... parameters) {
                InputStream inputStream = null;
                try {
                    StringBuilder urlBuilder = new StringBuilder(httpScheme.getScheme());
                    urlBuilder.append(baseUrl);
                    urlBuilder.append(threadEndPoint);

                    String url = urlBuilder.toString();
                    if (!parameters[0].isEmpty() && !parameters[0].containsKey("body")) {
                        url = RestServiceHelper.createGetParameters(url, parameters[0]);
                    }

                    HttpURLConnection connection = RestServiceHelper.createConnection(httpScheme, url, threadMethod.getMethod(),
                            "application/json", threadHeaders, threadCookie);

                    if (threadMethod != HttpMethod.GET && threadMethod != HttpMethod.DELETE) {
                        RestServiceHelper.setBody(parameters[0].get("body"), connection);
                    }

                    int statusCode = connection.getResponseCode();

                    inputStream = statusCode >= 200 && statusCode < 400 ? connection.getInputStream() : connection.getErrorStream();
                    parser.parse(RestServiceHelper.getResponse(inputStream, connection), statusCode, connection.getHeaderFields());

                    if (parameters[0] != null) {
                        parameters[0].remove("body");
                    }

                } catch (SocketTimeoutException ex) {
                    ExceptionHandler.handleException(ex);
                } catch (Exception e) {
                    ExceptionHandler.handleException(e);
                } finally {
                    ApplicationUtils.close(inputStream);
                }

                return (T) parser;
            }

            @Override
            protected void onPostExecute(T parser) {
                super.onPostExecute(parser);
                try {
                    callable.onPostExecute(parser);
                } catch (JSONException e) {
                    ExceptionHandler.handleException(e);
                }
            }
        }.execute(parameters.get());
    }

    public static final class Builder {

        private String baseUrl;
        private HttpScheme httpScheme;

        public Builder setBaseUrl(@NonNull String baseUrl) {
            checkNotNull(baseUrl, "baseUrl == null");
            this.baseUrl = baseUrl;

            return this;
        }

        public Builder setScheme(@NonNull HttpScheme scheme) {
            this.httpScheme = scheme;

            return this;
        }

        public HttpClient build() {
            return new HttpClient(baseUrl, httpScheme);
        }

        private static <T> T checkNotNull(T object, String message) {
            if (object == null) {
                throw new NullPointerException(message);
            }
            return object;
        }
    }

}
