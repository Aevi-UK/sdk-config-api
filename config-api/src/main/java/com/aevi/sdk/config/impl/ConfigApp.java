package com.aevi.sdk.config.impl;

class ConfigApp {

    private final String[] keys;
    private final String authority;
    private final String packageName;

    ConfigApp(String packageName, String authority, String[] keys) {
        this.packageName = packageName;
        this.authority = authority;
        this.keys = keys;
    }

    String getPackageName() {
        return packageName;
    }

    String getAuthority() {
        return authority;
    }

    String[] getKeys() {
        return keys;
    }
}
