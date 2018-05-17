package com.aevi.sdk.config.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.functions.Consumer;

public class Component {

    private final ConfigProviderChangeReceiver configProviderChangeReceiver;
    private final ConfigKeyStore configKeyStore;
    private final ConfigScanner configScanner;
    private final Context context;

    public Component(Context context) {
        this.context = context;
        this.configKeyStore = new ConfigKeyStore();
        this.configScanner = new ConfigScanner(context);
        this.configProviderChangeReceiver = new ConfigProviderChangeReceiver(configKeyStore, configScanner);
        configScanner.scan().toList().subscribe(new Consumer<List<ConfigApp>>() {
            @Override
            public void accept(List<ConfigApp> configApps) {
                configKeyStore.save(configApps);
            }
        });
    }

    @NonNull
    ConfigProviderChangeReceiver getConfigProviderChangeReceiver() {
        return configProviderChangeReceiver;
    }

    @NonNull
    ConfigKeyStore getConfigKeyStore() {
        return configKeyStore;
    }

    @NonNull
    ConfigScanner getConfigScanner() {
        return configScanner;
    }

    @NonNull
    Context getContext() {
        return context;
    }
}
