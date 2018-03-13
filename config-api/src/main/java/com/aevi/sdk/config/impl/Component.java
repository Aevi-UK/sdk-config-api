package com.aevi.sdk.config.impl;

import android.content.Context;

import io.reactivex.annotations.NonNull;

public class Component {

    private final AppInstallOrUpdateReceiver appInstallOrUpdateReceiver;
    private final ConfigKeyStore configKeyStore;
    private final ConfigScanner configScanner;
    private final Context context;

    public Component(Context context) {
        this.context = context;
        this.configKeyStore = new ConfigKeyStore();
        this.configScanner = new ConfigScanner(context);
        this.appInstallOrUpdateReceiver = new AppInstallOrUpdateReceiver(configKeyStore, configScanner);
    }

    @NonNull
    AppInstallOrUpdateReceiver getAppInstallOrUpdateReceiver() {
        return appInstallOrUpdateReceiver;
    }

    @NonNull
    ConfigKeyStore getConfigKeyStore() {
        return configKeyStore;
    }

    @NonNull
    public ConfigScanner getConfigScanner() {
        return configScanner;
    }

    @NonNull
    Context getContext() {
        return context;
    }
}
