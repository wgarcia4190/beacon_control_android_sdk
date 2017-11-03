package com.sigma.beaconcontrol.beaconsdk.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.sigma.beaconcontrol.beaconsdk.backend.model.CreateEventsRequest;
import com.sigma.beaconcontrol.beaconsdk.backend.model.TokenCredentials;

import org.apache.commons.lang3.SerializationUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Wilson on 10/23/17.
 */

public final class SDKPreferencesImpl implements SDKPreferences {

    private static final String TAG = SDKPreferencesImpl.class.getSimpleName();

    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String CLIENT_SECRET = "CLIENT_SECRET";
    private static final String CHARSET_NAME = "UTF-8";
    private static final String USER_ID = "USER_ID";
    private static final String SDK_PREFERENCES_FILE_NAME = "SDK_PREFERENCES";
    private static final String LAST_EVENTS_SENDOUT = "LAST_EVENTS_SENDOUT";
    private static final String EVENTS_LIST = "EVENTS_LIST";

    private static final Object instanceLock = new Object();
    private static SDKPreferences instance;
    private SharedPreferences preferences;

    public static SDKPreferences getInstance(Context context) {
        synchronized (instanceLock) {
            if (instance == null) {
                instance = new SDKPreferencesImpl(context);
            }
            return instance;
        }
    }

    private SDKPreferencesImpl(Context context) {
        preferences = context.getSharedPreferences(SDK_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        setDefaultValues();
    }

    private void setDefaultValues() {
        setEventsSentTimestamp(System.currentTimeMillis());
        setEventsList(new LinkedList<CreateEventsRequest.Event>());
    }

    private void setClientId(String clientId) {
        preferences.edit().putString(CLIENT_ID, clientId).apply();
    }

    private String getClientId() {
        return preferences.getString(CLIENT_ID, "");
    }

    private void setClientSecret(String clientSecret) {
        preferences.edit().putString(CLIENT_SECRET, clientSecret).apply();
    }

    private String getClientSecret() {
        return preferences.getString(CLIENT_SECRET, "");
    }

    private void setUserId(String userId) {
        System.out.println(userId);System.out.println(userId);System.out.println(userId);System.out.println(userId);System.out.println(userId);System.out.println(userId);System.out.println(userId);System.out.println(userId);System.out.println(userId);System.out.println(userId);System.out.println(userId);System.out.println(userId);System.out.println(userId);System.out.println(userId);System.out.println(userId);System.out.println(userId);System.out.println(userId);System.out.println(userId);
        preferences.edit().putString(USER_ID, userId).apply();
    }

    private String getUserId() {
        return preferences.getString(USER_ID, "");
    }

    @Override
    public void setOAuthCredentials(TokenCredentials tokenCredentials) {
        setClientId(tokenCredentials.getClientId());
        setClientSecret(tokenCredentials.getClientSecret());
        setUserId(tokenCredentials.getUserId());
    }

    @Override
    public TokenCredentials getOAuthCredentials() {
        return new TokenCredentials(getClientId(), getClientSecret(), getUserId());
    }

    @Override
    public void setEventsSentTimestamp(long timestamp) {
        preferences.edit().putLong(LAST_EVENTS_SENDOUT, timestamp).apply();
    }

    @Override
    public long getEventsSentTimestamp() {
        return preferences.getLong(LAST_EVENTS_SENDOUT, 0);
    }

    @Override
    public void setEventsList(List<CreateEventsRequest.Event> events) {
        try {
            String value = new String(SerializationUtils.serialize((LinkedList) events), CHARSET_NAME);
            preferences.edit().putString(EVENTS_LIST, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(TAG + ", cannot convert events list to string.");
        }
    }

    @Override
    public List<CreateEventsRequest.Event> getEventsList() {
        try {
            return (List<CreateEventsRequest.Event>) SerializationUtils
                    .deserialize(preferences.getString(EVENTS_LIST, null).getBytes(CHARSET_NAME));
        } catch (Exception e) {
            throw new RuntimeException(TAG + ", cannot convert string to events list.");
        }
    }
}
