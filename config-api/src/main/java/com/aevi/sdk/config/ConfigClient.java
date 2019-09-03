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
package com.aevi.sdk.config;

import android.support.annotation.NonNull;

import com.aevi.sdk.config.impl.ConfigResource;
import com.aevi.sdk.config.impl.ConfigUpdate;

import java.util.Set;

import io.reactivex.Observable;

public interface ConfigClient {

    /**
     * Returns the full set of all known configuration keys
     *
     * See {@link #getLatestConfig()} for a way to retrieve keys plus info about the providers.
     *
     * @return The full set of all known configuration keys. Will be empty if no config implementations found
     * @deprecated Please use {@link #getLatestConfig()} instead
     */
    @NonNull
    @Deprecated
    Set<String> getConfigKeys();

    /**
     * Returns the latest config update with list of provider apps and associated keys.
     *
     * @return Latest config update
     */
    ConfigUpdate getLatestConfig();

    /**
     * Returns the value of a particular key
     *
     * Note that in the case where multiple providers support the same key, it is arbitrary which provider value is retrieved here.
     *
     * @param key The key to get
     * @return The value of the key or an empty string if not found
     */
    @NonNull
    String getConfigValue(String key);

    /**
     * Returns an array value of a particular key
     *
     * Note that in the case where multiple providers support the same key, it is arbitrary which provider value is retrieved here.
     *
     * @param key The key to get
     * @return An array of String values of the key or an empty string array if not found
     */
    @NonNull
    String[] getConfigArrayValue(String key);


    /**
     * Returns the resource of a particular key
     *
     * @param key          The key to get
     * @param defaultValue The default value to return if this key is not found
     * @return The resource matching the given key or defaultValue if not found
     */
    @NonNull
    ConfigResource getConfigResource(String key, @NonNull ConfigResource defaultValue);

    /**
     * Subscribe to changes in configurations.
     *
     * @return An observable stream that emits the set of strings for all keys found when a configuration change occurs
     * @deprecated Please use {@link #subscribeToConfigurationUpdates()} instead
     */
    @NonNull
    @Deprecated
    Observable<Set<String>> subscribeToConfigurationChanges();

    /**
     * Subscribe to configuration updates from config providers.
     *
     * @return An observable stream that emits a {@link ConfigUpdate} model that wraps information about the providers and what keys they support
     */
    @NonNull
    Observable<ConfigUpdate> subscribeToConfigurationUpdates();

    /**
     * This API currently relies on a receiver to discover configuration applications
     *
     * To release this receiver and avoid memory leaks please close this client when you are done
     */
    void close();
}
