package com.sigma.beaconcontrol.beaconsdk.backend;

import android.content.Context;

import com.sigma.beaconcontrol.beaconsdk.backend.http.parsers.JsonResponseParser;
import com.sigma.beaconcontrol.beaconsdk.backend.http.parsers.Parser;
import com.sigma.beaconcontrol.beaconsdk.backend.http.services.rest.Callable;
import com.sigma.beaconcontrol.beaconsdk.backend.http.services.rest.HttpClient;
import com.sigma.beaconcontrol.beaconsdk.backend.model.Configuration;
import com.sigma.beaconcontrol.beaconsdk.backend.model.CreateEventsRequest;
import com.sigma.beaconcontrol.beaconsdk.backend.model.Token;
import com.sigma.beaconcontrol.beaconsdk.backend.storage.SdkTokenStorage;
import com.sigma.beaconcontrol.beaconsdk.core.Config;
import com.sigma.beaconcontrol.beaconsdk.core.SDKPreferences;
import com.sigma.beaconcontrol.beaconsdk.util.service.HttpListener;
import com.sigma.beaconcontrol.beaconsdk.util.service.HttpMethod;
import com.sigma.beaconcontrol.beaconsdk.util.service.HttpScheme;
import com.sigma.beaconcontrol.beaconsdk.util.service.HttpStatus;
import com.sigma.beaconcontrol.beaconsdk.util.service.MapBuilder;

import org.json.JSONException;

/**
 * @author Ing. Wilson Garcia
 * Created on 10/23/17
 */

public final class SDKControlManagerImpl implements SDKControlManager{

    private static final String TAG = SDKControlManagerImpl.class.getSimpleName();
    private static final int REFRESH_TOKEN_STRATEGY_NOT_USED = 0;

    private static SDKControlManagerImpl instance;

    private final SdkTokenStorage tokenStorage;
    private final SDKTokenManager tokenManager;
    private final SDKPreferences preferences;
    private final HttpClient httpClient;

    public static synchronized SDKControlManagerImpl getInstance(Context context, Config config,
                                                                 SDKPreferences preferences) {
        if (instance == null) {
            instance = new SDKControlManagerImpl(context, config, preferences);
        }
        return instance;
    }

    private SDKControlManagerImpl(Context context, Config config, SDKPreferences preferences) {
        this.preferences = preferences;
        this.tokenStorage = new SdkTokenStorage(context);
        this.httpClient = getHttpClient(config.getServiceBaseUrl(), config.getServiceHTTPScheme());
        this.tokenManager = new SDKTokenManagerImpl(preferences, httpClient);

        getToken();

    }

    private HttpClient getHttpClient(String serviceBaseUrl, String httpScheme) {
        return new HttpClient.Builder()
                .setBaseUrl(serviceBaseUrl)
                .setScheme(HttpScheme.getScheme(httpScheme)).build();
    }

    private void getToken(){
        this.tokenManager.getToken(result -> {
            Token token = new Token();
            token.setAccessToken(result);

            storeToken(token);
        });
    }

    private void storeToken(Token token) {
        tokenStorage.storeToken(token);
    }


    @Override
    public void getConfigurationsCall(HttpListener<Configuration> httpListener) {
        httpClient.setEndPoint(CONFIGURATION_ENDPOINT)
                .setHttpMethod(HttpMethod.GET)
                .setParameters(new MapBuilder().addParam("user_id",
                        preferences.getOAuthCredentials().getUserId()).build())
                .call(new Callable<JsonResponseParser>(JsonResponseParser.class) {
                    @Override
                    public void onPostExecute(JsonResponseParser parser) throws JSONException {
                        if(parser.getStatusCode() == HttpStatus.OK){
                            Configuration configuration = new Configuration();

                            httpListener.onResult(configuration);
                        }
                    }
                });
    }

    @Override
    public void createEventsCall(CreateEventsRequest request, HttpListener<String> httpListener) {

    }

    @Override
    public void clearToken() {
        tokenStorage.clear();
    }
}
