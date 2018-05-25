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
