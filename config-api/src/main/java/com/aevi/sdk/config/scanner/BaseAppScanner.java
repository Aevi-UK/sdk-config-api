package com.aevi.sdk.config.scanner;


import android.content.Context;
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
    private static final String APP_LABEL = "app-label";

    private final List<String> packageIgnoreList;
    private final Context context;
    private final PackageManager pm;

    BaseAppScanner(Context context, List<String> packageIgnoreList) {
        this.context = context;
        this.packageIgnoreList = packageIgnoreList;
        this.pm = context.getPackageManager();
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
        for (ResolveInfo resolveInfo : resolveInfoList) {
            if (resolveInfo.providerInfo != null && shouldIncludePackage(resolveInfo.providerInfo.packageName)) {
                emitter.onNext(resolveInfo);
            }
        }
    }

    private boolean shouldIncludePackage(String packageName) {
        return !packageIgnoreList.contains(packageName);
    }
}
