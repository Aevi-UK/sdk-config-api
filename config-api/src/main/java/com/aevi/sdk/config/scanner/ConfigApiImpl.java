package com.aevi.sdk.config.scanner;

import android.content.Context;

import com.aevi.sdk.config.ConfigClient;
import com.aevi.sdk.config.model.ConfigApp;
import com.aevi.sdk.config.provider.ConfigProviderHelper;

import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.functions.Action;

public final class ConfigApiImpl implements ConfigClient {

    private final AppInstallOrUpdateReceiver appInstallOrUpdateReceiver;
    private final ConfigProviderHelper configProviderHelper;
    private final ConfigKeyStore configKeyStore;
    private final ConfigChangeHandler configChangeHandler;
    private final Context context;

    public ConfigApiImpl(Context context) {
        this.context = context;
        this.configProviderHelper = new ConfigProviderHelper(context);
        this.configKeyStore = new ConfigKeyStore();
        this.configChangeHandler = new ConfigChangeHandler(configKeyStore);
        this.appInstallOrUpdateReceiver = new AppInstallOrUpdateReceiver(configChangeHandler);
    }

    @Override
    public Set<String> getConfigKeys() {
        return configKeyStore.getKeys();
    }

    @Override
    public String getConfigValue(String key) {
        ConfigApp app = configKeyStore.getApp(key);
        if (app != null) {
            return configProviderHelper.getValue(app.getAuthority(), key);
        }
        return "";
    }

    @Override
    public String[] getConfigArrayValue(String key) {
        ConfigApp app = configKeyStore.getApp(key);
        if (app != null) {
            return configProviderHelper.getArrayValue(app.getAuthority(), key);
        }
        return new String[0];
    }

    @Override
    public Observable<Set<String>> subscribeToConfigurationChanges() {
        Observable<Set<String>> obs = configKeyStore.observeKeyChanges().doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                context.unregisterReceiver(appInstallOrUpdateReceiver);
            }
        });

        appInstallOrUpdateReceiver.registerForBroadcasts(context);
        configChangeHandler.scanForConfigProviders(context);
        return obs;
    }
}
