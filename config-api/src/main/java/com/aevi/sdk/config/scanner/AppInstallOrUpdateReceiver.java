package com.aevi.sdk.config.scanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import static android.content.Intent.ACTION_PACKAGE_ADDED;
import static android.content.Intent.ACTION_PACKAGE_CHANGED;
import static android.content.Intent.ACTION_PACKAGE_REMOVED;

class AppInstallOrUpdateReceiver extends BroadcastReceiver {

    private static final String TAG = AppInstallOrUpdateReceiver.class.getSimpleName();

    private static final String PACKAGE_DATA = "package";

    private final ConfigChangeHandler configChangeHandler;

    public AppInstallOrUpdateReceiver(ConfigChangeHandler configChangeHandler) {
        this.configChangeHandler = configChangeHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received broadcast: " + intent.getAction());

        if (configChangeHandler.isChangeToConfigProvider(context, intent)) {
            configChangeHandler.scanForConfigProviders(context);
        }
    }

    public void registerForBroadcasts(Context context) {
        IntentFilter intentFilter = new IntentFilter(ACTION_PACKAGE_ADDED);
        intentFilter.addAction(ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(ACTION_PACKAGE_CHANGED);
        intentFilter.addDataScheme(PACKAGE_DATA);
        context.registerReceiver(this, intentFilter);
    }
}
