package com.aevi.sdk.config.impl;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

abstract class BaseAppScanner<T> {

    private static final String TAG = BaseAppScanner.class.getSimpleName();

    private final List<String> packageIgnoreList;
    private final PackageManager pm;

    BaseAppScanner(PackageManager pm, List<String> packageIgnoreList) {
        this.packageIgnoreList = packageIgnoreList;
        this.pm = pm;
    }

    public abstract Observable<T> scan();

    Observable<ResolveInfo> scanForContentProviders(final List<Intent> contentProviderIntents) {
        return Observable.create(new ObservableOnSubscribe<ResolveInfo>() {
            @Override
            public void subscribe(ObservableEmitter<ResolveInfo> emitter) throws Exception {
                for (Intent intent : contentProviderIntents) {
                    scanForContentProviders(intent, emitter);
                }
                Log.d(TAG, "Scanning completed for all intents");
                emitter.onComplete();
            }
        });
    }

    List<Intent> asIntents(String... actions) {
        List<Intent> intents = new ArrayList<>();
        for (String action : actions) {
            intents.add(new Intent(action));
        }
        return intents;
    }

    private void scanForContentProviders(Intent intent, Emitter<ResolveInfo> emitter) {
        Log.d(TAG, String.format("Scanning for content providers with action [%s]", intent.getAction()));
        List<ResolveInfo> resolveInfoList = pm.queryIntentContentProviders(intent, PackageManager.GET_META_DATA | PackageManager.GET_RESOLVED_FILTER);
        if (resolveInfoList != null) {
            for (ResolveInfo resolveInfo : resolveInfoList) {
                if (resolveInfo.providerInfo != null && shouldIncludePackage(resolveInfo.providerInfo.packageName)) {
                    emitter.onNext(resolveInfo);
                }
            }
        } else {
            emitter.onComplete();
        }
    }

    private boolean shouldIncludePackage(String packageName) {
        return !packageIgnoreList.contains(packageName);
    }
}
