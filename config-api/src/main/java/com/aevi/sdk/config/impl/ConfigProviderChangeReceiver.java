/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aevi.sdk.config.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.aevi.sdk.config.provider.BaseConfigProvider;

import java.util.List;

import io.reactivex.functions.Consumer;

import static android.content.Intent.*;

class ConfigProviderChangeReceiver extends BroadcastReceiver {

    private static final String TAG = ConfigProviderChangeReceiver.class.getSimpleName();
    private static final String PACKAGE_DATA = "package";

    private final ConfigKeyStore configKeyStore;
    private final ConfigScanner configScanner;

    public ConfigProviderChangeReceiver(ConfigKeyStore configKeyStore, ConfigScanner configScanner) {
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
        intentFilter.addAction(BaseConfigProvider.CONFIG_UPDATED_BROADCAST);
        intentFilter.addDataScheme(PACKAGE_DATA);
        context.registerReceiver(this, intentFilter);
    }

    boolean isChangeToConfigProvider(Context context, Intent intent) {
        return intent.getAction() != null && (isProviderPermanentlyRemoved(intent) || isProviderAdded(context, intent) || isProviderConfigUpdated(intent));
    }

    void scanForConfigProviders() {
        configScanner.scan()
                .toList()
                .subscribe(new Consumer<List<ConfigApp>>() {
                    @Override
                    public void accept(List<ConfigApp> configApps) throws Exception {
                        Log.d(TAG, String.format("Found %d external config applications", configApps.size()));
                        configKeyStore.save(configApps);
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

    private boolean isProviderConfigUpdated(Intent intent) {
        return intent.getAction().equals(BaseConfigProvider.CONFIG_UPDATED_BROADCAST);
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
