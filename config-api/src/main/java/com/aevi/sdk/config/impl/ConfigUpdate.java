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

import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a configuration update and wraps information about all the provider apps, including what keys they support.
 */
public class ConfigUpdate {

    private final Set<ConfigApp> configApps;

    public ConfigUpdate(Set<ConfigApp> configApps) {
        this.configApps = configApps;
    }

    /**
     * Get a collection of all the config provider applications.
     *
     * @return The config provider applications info
     */
    @NonNull
    public Set<ConfigApp> getConfigApps() {
        return configApps;
    }

    /**
     * Get all the keys supported across all the config providers.
     *
     * Note that a single key can be supported by multiple providers.
     *
     * @return A set of the supported keys across all providers
     */
    @NonNull
    public Set<String> getAllKeys() {
        Set<String> keys = new HashSet<>();
        for (ConfigApp configApp : configApps) {
            keys.addAll(configApp.getKeys());
        }
        return keys;
    }
}
