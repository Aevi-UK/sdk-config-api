package com.aevi.sdk.config.scanner;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aevi.sdk.config.model.ConfigApp;

import io.reactivex.functions.Consumer;

import static android.content.Intent.ACTION_PACKAGE_ADDED;
import static android.content.Intent.ACTION_PACKAGE_REMOVED;

class ConfigChangeHandler {

    private static final String TAG = ConfigChangeHandler.class.getSimpleName();

    private final ConfigKeyStore configKeyStore;

    ConfigChangeHandler(ConfigKeyStore configKeyStore) {
        this.configKeyStore = configKeyStore;
    }

    boolean isChangeToConfigProvider(Context context, Intent intent) {
        return intent.getAction() != null && (isProviderPermanentlyRemoved(intent) || isProviderAdded(context, intent));
    }

    void scanForConfigProviders(Context context) {
        ConfigScanner configScanner = new ConfigScanner(context);
        configScanner.scan().subscribe(new Consumer<ConfigApp>() {
            @Override
            public void accept(ConfigApp configApp) throws Exception {
                Log.d(TAG, "Got external config(s) from application: " + configApp.getAuthority());
                configKeyStore.save(configApp);
            }
        });
    }

    private boolean isProviderPermanentlyRemoved(Intent intent) {
        return intent.getAction().equals(ACTION_PACKAGE_REMOVED);
    }

    private boolean isProviderAdded(Context context, Intent intent) {
        return intent.getAction().equals(ACTION_PACKAGE_ADDED) && packageIsConfigProviderService(context, intent.getData().getEncodedSchemeSpecificPart());
    }

    private static boolean packageIsConfigProviderService(Context context, String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            return false;
        }

        Intent intent = new Intent(ConfigScanner.CONFIG_PROVIDER_ACTION);
        intent.setPackage(packageName);

        return !context.getPackageManager().queryIntentContentProviders(intent, 0).isEmpty();
    }
}
