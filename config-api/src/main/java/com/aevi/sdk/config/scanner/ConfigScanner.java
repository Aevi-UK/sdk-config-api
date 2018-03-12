package com.aevi.sdk.config.scanner;


import android.content.Context;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.aevi.sdk.config.model.ConfigApp;
import com.aevi.sdk.config.provider.ConfigProviderHelper;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

class ConfigScanner extends BaseAppScanner<ConfigApp> {

    private static final String TAG = ConfigScanner.class.getSimpleName();

    static final String CONFIG_PROVIDER_ACTION = "com.aevi.sdk.config.ConfigProvider";

    private ConfigProviderHelper configHelper;

    ConfigScanner(Context context) {
        super(context, new ArrayList<String>());
        this.configHelper = new ConfigProviderHelper(context);
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
            String[] keys = configHelper.getKeys(providerInfo.authority);
            emitter.onNext(new ConfigApp(providerInfo.authority, keys));
        } catch (Exception e) {
            Log.i(TAG, "Failed to get app information. App will be ignored", e);
        }
    }
}