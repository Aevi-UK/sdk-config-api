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
package com.aevi.sdk.config.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

public abstract class BaseConfigProvider extends ContentProvider {

    private static final String TAG = BaseConfigProvider.class.getSimpleName();

    public static final String METHOD_GET = "get";
    public static final String METHOD_GET_INT = "getInt";
    public static final String METHOD_GET_ARRAY = "getArray";
    public static final String METHOD_GET_KEYS = "getKeys";

    public static final String CONFIG_VENDOR = "vendor";
    public static final String CONFIG_KEYS = "keys";
    public static final String CONFIG_VALUE = "value";
    public static final String CONFIG_VALUES = "values";

    public static final String CONFIG_UPDATED_BROADCAST = "com.aevi.intent.action.CONFIG_UPDATED";

    /**
     * Get the array of config keys this provider supports.
     *
     * @return The supported array of config keys
     */
    public abstract String[] getConfigKeys();

    /**
     * Get the config value for the provided key.
     *
     * @param key The config key
     * @return The config value for the provided key
     */
    public abstract String getConfigValue(String key);

    /**
     * Get the config integer value for the provided key.
     *
     * @param key The config key
     * @return The integer config value for the provided key
     */
    public abstract int getIntConfigValue(String key);

    /**
     * Get the config array value for the provided key.
     *
     * @param key The config key
     * @return The array config value for the provided key
     */
    public abstract String[] getConfigArrayValue(String key);

    /**
     * Return a list of package names that are allowed to ask for configuration parameters from this provider
     *
     * @return String[] of package names or an empty array to indicate all package names are allowed
     */
    @NonNull
    protected abstract String[] getAllowedCallingPackageNames();

    /**
     * Return the name of the vendor responsible for this config provider.
     *
     * @return The name of the vendor responsible for this config provider
     */
    @NonNull
    protected abstract String getVendorName();

    public final Bundle call(String method, String key, Bundle extras) {

        String callingPackageName = getContext().getPackageManager().getNameForUid(Binder.getCallingUid());
        Log.d(TAG, String.format("Got call: %s %s from %s", method, key, callingPackageName));
        final Bundle b = new Bundle();
        if (isAllowedCallingPackageName(callingPackageName)) {
            if (method != null) {
                switch (method) {
                    case METHOD_GET_KEYS:
                        b.putString(CONFIG_VENDOR, getVendorName());
                        b.putStringArray(CONFIG_KEYS, getConfigKeys());
                        break;
                    case METHOD_GET:
                        if (key != null) {
                            b.putString(CONFIG_VALUE, getConfigValue(key));
                        }
                        break;
                    case METHOD_GET_INT:
                        if (key != null) {
                            b.putInt(CONFIG_VALUE, getIntConfigValue(key));
                        }
                    case METHOD_GET_ARRAY:
                        if (key != null) {
                            b.putStringArray(CONFIG_VALUES, getConfigArrayValue(key));
                        }
                        break;
                }
            }
        }
        return b;
    }

    private boolean isAllowedCallingPackageName(String callingPackageName) {
        String[] allowedPackageNames = getAllowedCallingPackageNames();
        if (allowedPackageNames.length == 0) {
            return true;
        }
        for (String allowedPackageName : allowedPackageNames) {
            if (allowedPackageName.equals(callingPackageName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    /**
     * Notify clients that the config has been updated.
     *
     * @param context     The Android context
     * @param updatedKeys Optionally, the keys that themselves or associated values have changed can be specified
     */
    public static void notifyConfigUpdated(Context context, String... updatedKeys) {
        String pkg = "package:" + context.getPackageName();
        Uri pkgUri = Uri.parse(pkg);
        Intent broadcast = new Intent(CONFIG_UPDATED_BROADCAST);
        broadcast.setData(pkgUri);
        if (updatedKeys == null) {
            updatedKeys = new String[0];
        }
        broadcast.putExtra(CONFIG_KEYS, updatedKeys);
        context.sendBroadcast(broadcast);
    }
}
