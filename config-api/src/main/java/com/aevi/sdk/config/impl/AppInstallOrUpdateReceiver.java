package com.aevi.sdk.config.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import io.reactivex.functions.Consumer;

import static android.content.Intent.ACTION_PACKAGE_ADDED;
import static android.content.Intent.ACTION_PACKAGE_CHANGED;
import static android.content.Intent.ACTION_PACKAGE_REMOVED;

class AppInstallOrUpdateReceiver extends BroadcastReceiver {

    private static final String TAG = AppInstallOrUpdateReceiver.class.getSimpleName();
    private static final String PACKAGE_DATA = "package";

    private final ConfigKeyStore configKeyStore;
    private final ConfigScanner configScanner;

    public AppInstallOrUpdateReceiver(ConfigKeyStore configKeyStore, ConfigScanner configScanner) {
        this.configKeyStore = configKeyStore;
        this.configScanner = configScanner;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received broadcast: " + intent.getAction());

        if (isChangeToConfigProvider(context, intent)) {
            scanForConfigProviders();
        }
    }

    public void registerForBroadcasts(Context context) {
        IntentFilter intentFilter = new IntentFilter(ACTION_PACKAGE_ADDED);
        intentFilter.addAction(ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(ACTION_PACKAGE_CHANGED);
        intentFilter.addDataScheme(PACKAGE_DATA);
        context.registerReceiver(this, intentFilter);
    }

    boolean isChangeToConfigProvider(Context context, Intent intent) {
        return intent.getAction() != null && (isProviderPermanentlyRemoved(intent) || isProviderAdded(context, intent));
    }

    void scanForConfigProviders() {
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
        return intent.getAction().equals(ACTION_PACKAGE_ADDED) && packageIsConfigProviderService(context,
                intent.getData().getEncodedSchemeSpecificPart());
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