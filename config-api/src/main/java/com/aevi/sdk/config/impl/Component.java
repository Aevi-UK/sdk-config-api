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

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.functions.Consumer;

public class Component {

    private final ConfigProviderChangeReceiver configProviderChangeReceiver;
    private final ConfigKeyStore configKeyStore;
    private final ConfigScanner configScanner;
    private final Context context;

    public Component(Context context) {
        this.context = context;
        this.configKeyStore = new ConfigKeyStore();
        this.configScanner = new ConfigScanner(context);
        this.configProviderChangeReceiver = new ConfigProviderChangeReceiver(configKeyStore, configScanner);
        configScanner.scan().toList().subscribe(new Consumer<List<ConfigApp>>() {
            @Override
            public void accept(List<ConfigApp> configApps) {
                configKeyStore.save(configApps);
            }
        });
    }

    @NonNull
    ConfigProviderChangeReceiver getConfigProviderChangeReceiver() {
        return configProviderChangeReceiver;
    }

    @NonNull
    ConfigKeyStore getConfigKeyStore() {
        return configKeyStore;
    }

    @NonNull
    ConfigScanner getConfigScanner() {
        return configScanner;
    }

    @NonNull
    Context getContext() {
        return context;
    }
}
