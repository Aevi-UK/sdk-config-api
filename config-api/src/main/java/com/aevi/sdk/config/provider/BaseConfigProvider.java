package com.aevi.sdk.config.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.annotations.NonNull;

public abstract class BaseConfigProvider extends ContentProvider {

    private static final String TAG = BaseConfigProvider.class.getSimpleName();

    public static final String METHOD_GET = "get";
    public static final String METHOD_GET_INT = "getInt";
    public static final String METHOD_GET_ARRAY = "getArray";
    public static final String METHOD_GET_KEYS = "getKeys";

    public static final String CONFIG_KEYS = "keys";
    public static final String CONFIG_VALUE = "value";
    public static final String CONFIG_VALUES = "values";

    public abstract String[] getConfigKeys();

    public abstract String getConfigValue(String key);

    public abstract int getIntConfigValue(String key);

    public abstract String[] getConfigArrayValue(String key);

    /**
     * Return a list of package names that are allowed to ask for configuration parameters from this provider
     *
     * @return String[] of package names or an empty array to indicate all package names are allowed
     */
    @NonNull
    protected abstract String[] getAllowedCallingPackageNames();

    public final Bundle call(String method, String key, Bundle extras) {

        String callingPackageName = getContext().getPackageManager().getNameForUid(Binder.getCallingUid());
        Log.d(TAG, String.format("Got call: %s %s from %s", method, key, callingPackageName));
        final Bundle b = new Bundle();
        if (isAllowedCallingPackageName(callingPackageName)) {
            if (method != null) {
                switch (method) {
                    case METHOD_GET_KEYS:
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
        if(allowedPackageNames.length == 0) {
           return true;
        }
        for(String allowedPackageName : allowedPackageNames) {
            if(allowedPackageName.equals(callingPackageName)) {
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
}
