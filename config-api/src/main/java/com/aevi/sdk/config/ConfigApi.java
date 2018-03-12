package com.aevi.sdk.config;

import android.content.Context;

import com.aevi.sdk.config.scanner.ConfigApiImpl;

public final class ConfigApi {

    /**
     * Get a new instance of a {@link ConfigClient} to obtain config parameters
     *
     * @param context The Android context
     * @return An instance of {@link ConfigClient}
     */
    public static ConfigClient getConfigClient(Context context) {
        return new ConfigApiImpl(context);
    }
}