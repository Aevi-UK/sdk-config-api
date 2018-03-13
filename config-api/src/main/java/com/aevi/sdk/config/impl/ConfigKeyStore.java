package com.aevi.sdk.config.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

class ConfigKeyStore {

    private final Map<String, ConfigApp> keys = new HashMap<>();
    private final PublishSubject<Set<String>> keyChangePublisher = PublishSubject.create();

    void save(ConfigApp configApp) {
        String[] keys = configApp.getKeys();
        if (keys != null) {
            for (String key : keys) {
                this.keys.put(key, configApp);
            }
            notifyKeyChange();
        }
    }

    Observable<Set<String>> observeKeyChanges() {
        return keyChangePublisher;
    }

    private void notifyKeyChange() {
        if (keys.size() > 0) {
            keyChangePublisher.onNext(keys.keySet());
        }
    }

    Set<String> getKeys() {
        return keys.keySet();
    }

    ConfigApp getApp(String key) {
        return keys.get(key);
    }
}
