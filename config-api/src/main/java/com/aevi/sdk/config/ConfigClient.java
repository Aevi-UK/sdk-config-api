package com.aevi.sdk.config;

import com.aevi.sdk.config.impl.ConfigResource;

import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;

public interface ConfigClient {

    /**
     * Returns the full set of all known configuration keys
     *
     * @return The full set of all known configuration keys. Will be empty if no config implementations found
     */
    @NonNull
    Set<String> getConfigKeys();

    /**
     * Returns the value of a particular key
     *
     * @param key The key to get
     * @return The value of the key or an empty string if not found
     */
    @NonNull
    String getConfigValue(String key);

    /**
     * Returns an array value of a particular key
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
     * Subscribe to changes in configurations
     *
     * @return An observable stream that emits the set of strings for all keys found when a configuration change occurs
     */
    @NonNull
    Observable<Set<String>> subscribeToConfigurationChanges();

    /**
     * This API currently relies on a receiver to discover configuration applications
     *
     * To release this receiver and avoid memory leaks please close this client when you are done
     */
    void close();
}
