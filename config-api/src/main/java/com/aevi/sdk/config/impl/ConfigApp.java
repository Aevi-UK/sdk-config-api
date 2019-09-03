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

import java.util.Objects;
import java.util.Set;

/**
 * Represents a config application (aka provider) and the keys it supports.
 */
public class ConfigApp {

    private final String vendor;
    private final String version;
    private final String authority;
    private final String packageName;
    private final Set<String> keys;

    public ConfigApp(String vendor, String version, String packageName, String authority, Set<String> keys) {
        this.packageName = packageName;
        this.authority = authority;
        this.keys = keys;
        this.vendor = vendor;
        this.version = version;
    }

    @NonNull
    public String getVendor() {
        return vendor;
    }

    @NonNull
    public String getVersion() {
        return version;
    }

    @NonNull
    String getAuthority() {
        return authority;
    }

    @NonNull
    public String getPackageName() {
        return packageName;
    }

    @NonNull
    public Set<String> getKeys() {
        return keys;
    }

    // We consider ConfigApps to be "equals" purely by package name and authority, allowing us to replace apps when keys changes, etc
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigApp configApp = (ConfigApp) o;
        return Objects.equals(authority, configApp.authority) &&
                Objects.equals(packageName, configApp.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authority, packageName);
    }
}
