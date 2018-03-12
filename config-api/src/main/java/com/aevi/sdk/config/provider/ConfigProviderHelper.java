package com.aevi.sdk.config.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class ConfigProviderHelper {

    private static final String TAG = ConfigProviderHelper.class.getSimpleName();

    private static final String CONTENT_URI = "content://";

    private final Context context;

    public ConfigProviderHelper(Context context) {
        this.context = context;
    }

    public String[] getKeys(String authority) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = getContentProviderUri(authority);
        return getStringArrayForMethod(contentResolver, uri, BaseConfigProvider.METHOD_GET_KEYS, null, BaseConfigProvider.CONFIG_KEYS);
    }

    public String getValue(String authority, String key) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = getContentProviderUri(authority);
        return getStringForMethod(contentResolver, uri, BaseConfigProvider.METHOD_GET, key, BaseConfigProvider.CONFIG_VALUE);
    }

    public String[] getArrayValue(String authority, String key) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = getContentProviderUri(authority);
        return getStringArrayForMethod(contentResolver, uri, BaseConfigProvider.METHOD_GET_ARRAY, key, BaseConfigProvider.CONFIG_VALUES);
    }

    private Uri getContentProviderUri(String configProviderAuthority) {
        return Uri.parse(CONTENT_URI + configProviderAuthority + "/");
    }

    private String getStringForMethod(ContentResolver contentResolver, Uri uri, String method, String getKey, String responseKey) {
        Bundle bundle = contentResolver.call(uri, method, getKey, null);
        if (bundle == null || !bundle.containsKey(responseKey)) {
            Log.i(TAG, "No config from provider - ignoring: " + uri);
            return "";
        }
        String string = bundle.getString(responseKey);
        if (string == null) {
            Log.i(TAG, "Empty config from provider - ignoring: " + uri);
            return "";
        }
        return string;
    }

    private String[] getStringArrayForMethod(ContentResolver contentResolver, Uri uri, String method, String getKey, String responseKey) {
        Bundle bundle = contentResolver.call(uri, method, getKey, null);
        if (bundle == null || !bundle.containsKey(responseKey)) {
            Log.i(TAG, "No config from provider - ignoring: " + uri);
            return new String[0];
        }
        String[] stringArr = bundle.getStringArray(responseKey);
        if (stringArr == null || stringArr.length == 0) {
            Log.i(TAG, "Empty config from provider - ignoring: " + uri);
            return new String[0];
        }
        return stringArr;
    }
}
