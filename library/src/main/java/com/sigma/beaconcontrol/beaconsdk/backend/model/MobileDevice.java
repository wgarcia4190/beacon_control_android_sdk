package com.sigma.beaconcontrol.beaconsdk.backend.model;

import com.sigma.beaconcontrol.beaconsdk.core.error.ExceptionHandler;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author Ing. Wilson Garcia
 *         Created on 10/29/17
 */

public class MobileDevice implements Serializable {

    public enum OS {android}

    public enum Environment {production, sandbox}

    private String clientId;
    private String clientSecret;
    private long userId;
    private OS os;
    private Environment environment;
    private String pushToken;

    public MobileDevice(String clientId, String clientSecret, long userId, OS os,
                        Environment environment, String pushToken) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.userId = userId;
        this.os = os;
        this.environment = environment;
        this.pushToken = pushToken;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public OS getOs() {
        return os;
    }

    public void setOs(OS os) {
        this.os = os;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String toJSON(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("client_id", this.clientId);
            jsonObject.put("client_secret", this.clientSecret);
            jsonObject.put("user_id", this.userId);
            jsonObject.put("os", this.os);
            jsonObject.put("environment", this.environment);
            jsonObject.put("push_token", this.pushToken);
        }catch(Exception e){
            ExceptionHandler.handleException(e);
        }

        return jsonObject.toString();
    }
}
