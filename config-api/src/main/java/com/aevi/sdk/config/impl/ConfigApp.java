package com.aevi.sdk.config.impl;

class ConfigApp {

    private final String[] keys;
    private final String authority;

    ConfigApp(String authority, String[] keys) {
        this.authority = authority;
        this.keys = keys;
    }

    String getAuthority() {
        return authority;
    }

    String[] getKeys() {
        return keys;
    }
}
