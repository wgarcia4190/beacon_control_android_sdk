package com.sigma.beaconcontrol.beaconsdk.backend.http.parsers;

import com.sigma.beaconcontrol.beaconsdk.core.error.ExceptionHandler;
import com.sigma.beaconcontrol.beaconsdk.util.service.HttpStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.List;
import java.util.Map;

/**
 * Created by Wilson on 7/13/17.
 */

public class JsonResponseParser implements Parser {

    private JSONObject response = new JSONObject();
    private JSONArray responseArray;
    private String responseString;
    private HttpStatus statusCode;
    private Map<String, List<String>> headers;

    @Override
    public JsonResponseParser parse(String responseString, int statusCodeInt, Map<String, List<String>> headers) {
        try {
            statusCode = HttpStatus.getHttpStatusFromCode(statusCodeInt);

            this.headers = headers;
            this.responseString = responseString;

            Object json = new JSONTokener(responseString).nextValue();
            if (json instanceof JSONObject) {
                response = new JSONObject(responseString);
            } else if (json instanceof JSONArray) {
                responseArray = new JSONArray(responseString);
            }

        } catch (JSONException e) {
            ExceptionHandler.handleException(e);
        }
        return this;
    }

    public JSONObject getResponse() {
        return response;
    }

    public JSONArray getResponseArray() {
        return responseArray;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public String getResponseString() {
        return responseString;
    }

    public String getMessage() {
        try {
            return getResponse().getString("message");
        } catch (JSONException e) {
            return "Error";
        }
    }
}
