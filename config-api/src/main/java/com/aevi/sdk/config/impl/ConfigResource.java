package com.aevi.sdk.config.impl;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.io.InputStream;

public final class ConfigResource {

    private final int id;
    private final Resources resources;
    private final String packageName;
    private final int versionCode;

    ConfigResource(int id, String packageName, PackageManager packageManager) throws PackageManager.NameNotFoundException {
        this.id = id;
        this.packageName = packageName;
        this.resources = packageManager.getResourcesForApplication(packageName);
        PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
        this.versionCode = packageInfo.versionCode;
    }

    public ConfigResource(int id, Context context) {
        this.id = id;
        this.packageName = context.getPackageName();
        this.resources = context.getResources();
        this.versionCode = getCurrentVersionCode(context);
    }

    public String getUniqueReference() {
        return String.format("%s:%d:%d", packageName, versionCode, id);
    }

    public int getId() {
        return id;
    }

    public Resources getResources(Context context) {
        return resources;
    }

    public String asString(String... args) {
        return resources.getString(id, (Object[]) args);
    }

    public Drawable asDrawable() {
        return resources.getDrawable(id, null);
    }

    public InputStream asInputStream() {
        return resources.openRawResource(id);
    }

    private static int getCurrentVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

}
