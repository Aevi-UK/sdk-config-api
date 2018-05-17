package com.aevi.sdk.config;

import android.content.Context;

import com.aevi.sdk.config.impl.Component;
import com.aevi.sdk.config.impl.ConfigClientImpl;

public final class ConfigApi {

    /**
     * Get a new instance of a {@link ConfigClient} to obtain config parameters
     *
     * @param context The Android context
     * @return An instance of {@link ConfigClient}
     */
    public static ConfigClient getConfigClient(Context context) {
        Component component = new Component(context);
        return new ConfigClientImpl(component);
    }
}