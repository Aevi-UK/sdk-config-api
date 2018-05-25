package com.aevi.sdk.config.impl;

import android.content.pm.PackageManager;

import com.aevi.sdk.config.ConfigClient;

import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class ConfigClientImpl implements ConfigClient {

    private final Component component;

    public ConfigClientImpl(Component component) {
        this.component = component;
    }

    @Override
    public Set<String> getConfigKeys() {
        return component.getConfigKeyStore().getKeys();
    }

    @Override
    public ConfigUpdate getLatestConfig() {
        return component.getConfigKeyStore().getCurrentConfig();
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
    public ConfigResource getConfigResource(String key, ConfigResource defaultValue) {
        ConfigApp app = component.getConfigKeyStore().getApp(key);
        if (app != null) {
            try {
                return new ConfigResource(component.getConfigScanner().getIntValue(app.getAuthority(), key),
                        app.getPackageName(), component.getContext().getPackageManager());
            } catch (PackageManager.NameNotFoundException e) {
                // ...if the package has been uninstalled in the meanwhile
            }
        }
        return defaultValue;
    }

    @Override
    public Observable<Set<String>> subscribeToConfigurationChanges() {
        scanAndRegisterForBroadcasts();
        return component.getConfigKeyStore().observeUpdates().map(new Function<ConfigUpdate, Set<String>>() {
            @Override
            public Set<String> apply(ConfigUpdate configUpdate) throws Exception {
                return configUpdate.getAllKeys();
            }
        });
    }

    @Override
    public Observable<ConfigUpdate> subscribeToConfigurationUpdates() {
        scanAndRegisterForBroadcasts();
        return component.getConfigKeyStore().observeUpdates();
    }

    private void scanAndRegisterForBroadcasts() {
        component.getConfigProviderChangeReceiver().registerForBroadcasts(component.getContext());
        component.getConfigProviderChangeReceiver().scanForConfigProviders();
    }

    @Override
    public void close() {
        try {
            component.getContext().unregisterReceiver(component.getConfigProviderChangeReceiver());
        } catch (Exception e) {
            //... if not found
        }
    }
}
