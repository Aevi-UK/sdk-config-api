package com.aevi.sdk.config.impl;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.io.InputStream;

public final class ConfigResource {

    private final int id;
    private final Resources resources;

    ConfigResource(int id, Resources resources) {
        this.id = id;
        this.resources = resources;
    }

    public ConfigResource(int id, Context context) {
        this(id, context.getResources());
    }

    public int getId() {
        return id;
    }

    public Resources getResources() {
        return getResources();
    }

    public String asString(String... args) {
        return resources.getString(id, args);
    }

    public Drawable asDrawable() {
        return resources.getDrawable(id, null);
    }

    public InputStream asInputStream() {
        return resources.openRawResource(id);
    }
}
