package com.sigma.beaconcontrol.beaconsdk.backend;

import com.sigma.beaconcontrol.beaconsdk.backend.http.parsers.JsonResponseParser;
import com.sigma.beaconcontrol.beaconsdk.backend.http.parsers.Parser;
import com.sigma.beaconcontrol.beaconsdk.backend.http.services.rest.Callable;
import com.sigma.beaconcontrol.beaconsdk.backend.http.services.rest.HttpClient;
import com.sigma.beaconcontrol.beaconsdk.backend.model.MobileDevice;
import com.sigma.beaconcontrol.beaconsdk.backend.model.TokenCredentials;
import com.sigma.beaconcontrol.beaconsdk.core.SDKPreferences;
import com.sigma.beaconcontrol.beaconsdk.util.StringUtils;
import com.sigma.beaconcontrol.beaconsdk.util.service.HttpListener;
import com.sigma.beaconcontrol.beaconsdk.util.service.HttpMethod;
import com.sigma.beaconcontrol.beaconsdk.util.service.HttpStatus;

import org.json.JSONException;

/**
 * @author Ing. Wilson Garcia
 * Created on 10/29/17
 */

public class SDKTokenManagerImpl implements SDKTokenManager {

    private static final String ENDPOINT = "/oauth/token";
    private static final String TOKEN_HEADER = "X-API-TOKEN";

    private final SDKPreferences preferences;
    private final HttpClient oauthService;

    public SDKTokenManagerImpl(SDKPreferences preferences, HttpClient oauthService) {
        this.preferences = preferences;
        this.oauthService = oauthService;
    }

    @Override
    public void getToken(HttpListener<String> httpListener) {
        TokenCredentials tokenCredentials = preferences.getOAuthCredentials();

        MobileDevice mobileDevice = new MobileDevice(
                tokenCredentials.getClientId(),
                tokenCredentials.getClientSecret(),
                Long.parseLong(tokenCredentials.getUserId()),
                MobileDevice.OS.android,
                MobileDevice.Environment.sandbox,
                StringUtils.EMPTY
        );

        this.oauthService.setEndPoint(ENDPOINT)
                .setHttpMethod(HttpMethod.POST)
                .setBody(mobileDevice.toJSON())
                .call(new Callable<JsonResponseParser>(JsonResponseParser.class) {
                    @Override
                    public void onPostExecute(JsonResponseParser parser) throws JSONException {
                        if(parser.getStatusCode() == HttpStatus.OK){
                            String token = parser.getHeaders().get(TOKEN_HEADER).get(0);
                            httpListener.onResult(token);
                        }
                    }
                });
    }
}
