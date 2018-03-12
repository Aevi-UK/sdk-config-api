package com.aevi.sdk.config;

import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;

public interface ConfigClient {

    @NonNull
    Set<String> getConfigKeys();

    @NonNull
    String getConfigValue(String key);

    @NonNull
    String[] getConfigArrayValue(String key);

    @NonNull
    Observable<Set<String>> subscribeToConfigurationChanges();
}
