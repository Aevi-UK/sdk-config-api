package com.aevi.sdk.config.impl;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static android.content.ContentValues.TAG;

class ConfigKeyStore {

    private final Map<String, ConfigApp> keys = new HashMap<>();
    private final PublishSubject<Set<String>> keyChangePublisher = PublishSubject.create();

    void save(List<ConfigApp> configApps) {
        if (configApps.size() == 0) {
            keys.clear();
            notifyKeyChange();
        } else {
            for (ConfigApp configApp : configApps) {
                save(configApp);
            }
        }
    }

    void save(ConfigApp configApp) {
        Log.d(TAG, "Got external config(s) from application: " + configApp.getAuthority());
        String[] appKeys = configApp.getKeys();
        if (appKeys != null) {
            for (String key : appKeys) {
                if (keys.containsKey(key)) {
                    ConfigApp configAppComp = keys.get(key);
                    if (!configAppComp.equals(configApp)) {
                        Log.i(TAG, "Conflicting key found from authority: " + configApp.getAuthority());
                    }
                }
                keys.put(key, configApp);
            }
            notifyKeyChange();
        }
    }

    Observable<Set<String>> observeKeyChanges() {
        return keyChangePublisher;
    }

    private void notifyKeyChange() {
        keyChangePublisher.onNext(keys.keySet());
    }

    Set<String> getKeys() {
        return keys.keySet();
    }

    ConfigApp getApp(String key) {
        return keys.get(key);
    }
}
