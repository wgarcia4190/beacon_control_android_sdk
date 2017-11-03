package com.sigma.beaconcontrol.beaconsdk.core.model;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents action that may be performed during Beacon SDK usage.
 *
 * @author Wilson Garcia
 * Created on 10/23/17
 */

public class Action implements Serializable {

    /**
     * This enum represents type of action.
     */
    public enum Type {
        /**
         * Url action type.
         */
        url,

        /**
         * Coupon action type.
         */
        coupon,

        /**
         * Custom action type.
         */
        custom
    }

    private Long id;
    private Type type;
    private String name;
    private Payload payload;
    private List<CustomAttribute> customAttributes;

    public static class Payload implements Serializable {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class CustomAttribute implements Serializable {

        private long id;
        private String name;
        private String value;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public List<CustomAttribute> getCustomAttributes() {
        return customAttributes;
    }

    public void setCustomAttributes(List<CustomAttribute> customAttributes) {
        this.customAttributes = customAttributes;
    }
}
