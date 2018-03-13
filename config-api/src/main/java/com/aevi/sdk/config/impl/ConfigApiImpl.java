package com.aevi.sdk.config.impl;

import com.aevi.sdk.config.ConfigClient;

import java.util.Set;

import io.reactivex.Observable;

public final class ConfigApiImpl implements ConfigClient {

    private final Component component;

    public ConfigApiImpl(Component component) {
        this.component = component;
    }

    @Override
    public Set<String> getConfigKeys() {
        return component.getConfigKeyStore().getKeys();
    }

    @Override
    public String getConfigValue(String key) {
        ConfigApp app = component.getConfigKeyStore().getApp(key);
        if (app != null) {
            return component.getConfigScanner().getValue(app.getAuthority(), key);
        }
        return "";
    }

    @Override
    public String[] getConfigArrayValue(String key) {
        ConfigApp app = component.getConfigKeyStore().getApp(key);
        if (app != null) {
            return component.getConfigScanner().getArrayValue(app.getAuthority(), key);
        }
        return new String[0];
    }

    @Override
    public Observable<Set<String>> subscribeToConfigurationChanges() {
        Observable<Set<String>> obs = component.getConfigKeyStore().observeKeyChanges();
        component.getAppInstallOrUpdateReceiver().registerForBroadcasts(component.getContext());
        component.getAppInstallOrUpdateReceiver().scanForConfigProviders();
        return obs;
    }

    @Override
    public void close() {
        try {
            component.getContext().unregisterReceiver(component.getAppInstallOrUpdateReceiver());
        } catch (Exception e) {
            //... if not found
        }
    }
}
