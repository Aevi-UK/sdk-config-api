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

import android.util.Log;

import java.util.*;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static android.content.ContentValues.TAG;

class ConfigKeyStore {

    private final Set<ConfigApp> configApps = new HashSet<>();
    private final Map<String, ConfigApp> keys = new HashMap<>();
    private final PublishSubject<ConfigUpdate> updatePublisher = PublishSubject.create();

    Observable<ConfigUpdate> observeUpdates() {
        return updatePublisher;
    }

    Set<String> getKeys() {
        synchronized (keys) {
            return keys.keySet();
        }
    }

    ConfigApp getApp(String key) {
        synchronized (keys) {
            return keys.get(key);
        }
    }

    void save(List<ConfigApp> configApps) {
        synchronized (keys) {
            keys.clear();
            this.configApps.clear();
            for (ConfigApp configApp : configApps) {
                save(configApp);
            }
            notifySubscribers();
        }
    }

    ConfigUpdate getCurrentConfig() {
        synchronized (keys) {
            return new ConfigUpdate(configApps);
        }
    }

    private void save(ConfigApp configApp) {
        Log.d(TAG, "Got external config(s) from application: " + configApp.getAuthority());
        configApps.add(configApp);
        Set<String> appKeys = configApp.getKeys();
        for (String key : appKeys) {
            if (keys.containsKey(key)) {
                ConfigApp configAppComp = keys.get(key);
                if (!configAppComp.equals(configApp)) {
                    Log.i(TAG, "Conflicting key found from authority: " + configApp.getAuthority());
                }
            }
            keys.put(key, configApp);
        }
    }

    private void notifySubscribers() {
        updatePublisher.onNext(new ConfigUpdate(configApps));
    }
}
