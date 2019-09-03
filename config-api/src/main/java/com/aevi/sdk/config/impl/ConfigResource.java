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

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.io.InputStream;
import java.util.Locale;

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

    @NonNull
    public String getUniqueReference() {
        return String.format(Locale.getDefault(), "%s:%d:%d", packageName, versionCode, id);
    }

    public int getId() {
        return id;
    }

    @NonNull
    public Resources getResources(Context context) {
        return resources;
    }

    @NonNull
    public String asString(String... args) {
        return resources.getString(id, (Object[]) args);
    }

    @NonNull
    public Drawable asDrawable() {
        return resources.getDrawable(id, null);
    }

    @NonNull
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
