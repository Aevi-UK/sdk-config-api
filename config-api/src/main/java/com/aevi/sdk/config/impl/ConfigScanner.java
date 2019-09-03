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

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import static com.aevi.sdk.config.provider.BaseConfigProvider.*;

class ConfigScanner extends BaseAppScanner<ConfigApp> {

    private static final String TAG = ConfigScanner.class.getSimpleName();

    static final String CONFIG_PROVIDER_ACTION = "com.aevi.sdk.config.ConfigProvider";

    private static final String CONTENT_URI = "content://";

    private final ContentResolver contentResolver;

    ConfigScanner(Context context) {
        super(context.getPackageManager(), new ArrayList<String>());
        this.contentResolver = context.getContentResolver();
    }

    @Override
    public Observable<ConfigApp> scan() {
        Log.d(TAG, "Scanning for config applications");
        return Observable.create(new ObservableOnSubscribe<ConfigApp>() {
            @Override
            public void subscribe(final ObservableEmitter<ConfigApp> emitter) throws Exception {
                scanForContentProviders(asIntents(CONFIG_PROVIDER_ACTION)).doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        emitter.onComplete();
                    }
                }).subscribe(new Consumer<ResolveInfo>() {
                    @Override
                    public void accept(ResolveInfo providerResolveInfo) throws Exception {
                        handleDiscoveredConfigApps(providerResolveInfo.providerInfo, emitter);
                    }
                });

            }
        });
    }

    private void handleDiscoveredConfigApps(ProviderInfo providerInfo, ObservableEmitter<ConfigApp> emitter) {
        Log.i(TAG, String.format("Found config provider in package: %s authority: %s", providerInfo.packageName, providerInfo.authority));
        try {
            ConfigApp configApp = getConfigAppDetails(providerInfo.packageName, providerInfo.authority);
            if (configApp != null) {
                emitter.onNext(configApp);
            }
        } catch (Exception e) {
            Log.i(TAG, "Failed to get app information. App will be ignored", e);
        }
    }

    private ConfigApp getConfigAppDetails(String packageName, String authority) {
        Bundle bundle = getBundleFromProvider(authority, METHOD_GET_KEYS, null);
        String[] keys = bundle.getStringArray(CONFIG_KEYS);
        if (keys == null || keys.length == 0) {
            Log.i(TAG, "Config provider (" + authority + ") supports no keys - ignoring...");
            return null;
        }
        if (!bundle.containsKey(CONFIG_VENDOR)) {
            // Older provider impl - set a default
            bundle.putString(CONFIG_VENDOR, "UNKNOWN");
        }
        String vendor = bundle.getString(CONFIG_VENDOR);
        String version = getProviderVersion(packageName);
        return new ConfigApp(vendor, version, packageName, authority, new HashSet<>(Arrays.asList(keys)));
    }

    private String[] getKeys(String authority) {
        return getValueForMethod(authority, METHOD_GET_KEYS, null, CONFIG_KEYS, new String[0]);
    }

    String getValue(String authority, String key) {
        return getValueForMethod(authority, METHOD_GET, key, CONFIG_VALUE, "");
    }

    String[] getArrayValue(String authority, String key) {
        return getValueForMethod(authority, METHOD_GET_ARRAY, key, CONFIG_VALUES, new String[0]);
    }

    int getIntValue(String authority, String key) {
        return getValueForMethod(authority, METHOD_GET_INT, key, CONFIG_VALUE, 0);
    }

    private <T> T getValueForMethod(String authority, String method, String getKey, String responseKey, T defaultValue) {
        Bundle bundle = getBundleFromProvider(authority, method, getKey);
        if (bundle != null && bundle.containsKey(responseKey)) {
            try {
                T value = (T) bundle.get(responseKey);
                if (value != null) {
                    return value;
                } else {
                    Log.i(TAG, "Empty config from provider - ignoring: " + authority);
                }
            } catch (ClassCastException e) {
                Log.i(TAG, "Wrong config type from provider - ignoring: " + authority);
            }
        } else {
            Log.i(TAG, "No config from provider - ignoring: " + authority);
        }

        return defaultValue;
    }

    private Bundle getBundleFromProvider(String authority, String method, String getKey) {
        Uri uri = Uri.parse(CONTENT_URI + authority + "/");
        return contentResolver.call(uri, method, getKey, new Bundle());
    }
}