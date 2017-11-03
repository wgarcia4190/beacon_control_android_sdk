package com.sigma.beaconcontrol.beaconsdk.backend.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.sigma.beaconcontrol.beaconsdk.backend.model.Token;

/**
 * Created by Wilson on 10/23/17.
 */

public class SdkTokenStorage {
    private static final String PREFERENCES_STORE_NAME = "SdkTokenStorage.preferences";

    private static final String PROPERTY_ACCESS_TOKEN = "TokenStorage.PROPERTY_ACCESS_TOKEN";
    private static final String PROPERTY_TOKEN_TYPE = "TokenStorage.PROPERTY_TOKEN_TYPE";
    private static final String PROPERTY_EXPIRES_IN = "TokenStorage.PROPERTY_EXPIRES_IN";
    private static final String PROPERTY_REFRESH_TOKEN = "TokenStorage.PROPERTY_REFRESH_TOKEN";
    private static final String PROPERTY_CREATED_AT = "TokenStorage.PROPERTY_CREATED_AT";

    private final SharedPreferences prefs;

    public SdkTokenStorage(Context context) {
        prefs = context.getSharedPreferences(PREFERENCES_STORE_NAME, Context.MODE_PRIVATE);
    }

    public void storeToken(Token token) {
        prefs.edit()
                .putString(PROPERTY_ACCESS_TOKEN, token.getAccessToken())
                .putString(PROPERTY_TOKEN_TYPE, token.getTokenType())
                .putLong(PROPERTY_EXPIRES_IN, token.getExpiresIn())
                .putString(PROPERTY_REFRESH_TOKEN, token.getRefreshToken())
                .putLong(PROPERTY_CREATED_AT, token.getCreatedAt())
                .apply();
    }

    public Token retrieveToken() {
        if (!prefs.contains(PROPERTY_ACCESS_TOKEN)) {
            return null;
        }
        Token token = new Token();
        token.setAccessToken(prefs.getString(PROPERTY_ACCESS_TOKEN, ""));
        token.setTokenType(prefs.getString(PROPERTY_TOKEN_TYPE, ""));
        token.setExpiresIn(prefs.getLong(PROPERTY_EXPIRES_IN, -1));
        token.setRefreshToken(prefs.getString(PROPERTY_REFRESH_TOKEN, ""));
        token.setCreatedAt(prefs.getLong(PROPERTY_CREATED_AT, -1));
        return token;
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
