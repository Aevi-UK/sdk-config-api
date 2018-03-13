package com.aevi.sdk.config.impl;


import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

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
            String[] keys = getKeys(providerInfo.authority);
            emitter.onNext(new ConfigApp(providerInfo.authority, keys));
        } catch (Exception e) {
            Log.i(TAG, "Failed to get app information. App will be ignored", e);
        }
    }

    private String[] getKeys(String authority) {
        return getStringArrayForMethod(authority, METHOD_GET_KEYS, null, CONFIG_KEYS);
    }

    String getValue(String authority, String key) {
        return getStringForMethod(authority, METHOD_GET, key, CONFIG_VALUE);
    }

    String[] getArrayValue(String authority, String key) {
        return getStringArrayForMethod(authority, METHOD_GET_ARRAY, key, CONFIG_VALUES);
    }

    private String getStringForMethod(String authority, String method, String getKey, String responseKey) {
        Bundle bundle = getBundleFromProvider(authority, method, getKey);
        if (bundle == null || !bundle.containsKey(responseKey)) {
            Log.i(TAG, "No config from provider - ignoring: " + authority);
            return "";
        }
        String string = bundle.getString(responseKey);
        if (string == null) {
            Log.i(TAG, "Empty config from provider - ignoring: " + authority);
            return "";
        }
        return string;
    }

    private String[] getStringArrayForMethod(String authority, String method, String getKey, String responseKey) {
        Bundle bundle = getBundleFromProvider(authority, method, getKey);
        if (bundle == null || !bundle.containsKey(responseKey)) {
            Log.i(TAG, "No config from provider - ignoring: " + authority);
            return new String[0];
        }
        String[] stringArr = bundle.getStringArray(responseKey);
        if (stringArr == null || stringArr.length == 0) {
            Log.i(TAG, "Empty config from provider - ignoring: " + authority);
            return new String[0];
        }
        return stringArr;
    }

    private Bundle getBundleFromProvider(String authority, String method, String getKey) {
        Uri uri = Uri.parse(CONTENT_URI + authority + "/");
        return contentResolver.call(uri, method, getKey, new Bundle());
    }
}