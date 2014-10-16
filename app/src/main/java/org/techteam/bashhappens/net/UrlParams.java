package org.techteam.bashhappens.net;

public class UrlParams {
    private String key;
    private String value;

    public UrlParams(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
