package com.aevi.sdk.config.model;

import com.aevi.util.json.JsonConverter;
import com.aevi.util.json.Jsonable;

public class ConfigApp implements Jsonable {

    private final String[] keys;
    private final String authority;

    public ConfigApp(String authority, String[] keys) {
        this.authority = authority;
        this.keys = keys;
    }

    public String getAuthority() {
        return authority;
    }

    public String[] getKeys() {
        return keys;
    }

    @Override
    public String toJson() {
        return JsonConverter.serialize(this);
    }
}
