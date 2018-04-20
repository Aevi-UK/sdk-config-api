package com.aevi.sdk.config.impl;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import io.reactivex.Observable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.ACTION_PACKAGE_ADDED;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AppInstallOrUpdateReceiverTest {

    private AppInstallOrUpdateReceiver appInstallOrUpdateReceiver;

    @Mock
    ConfigKeyStore configKeyStore;

    @Mock
    ConfigScanner configScanner;

    @Mock
    Context context;

    @Mock
    PackageManager packageManager;

    @Mock
    Intent intent;

    @Before
    public void setup() {
        initMocks(this);
        List<ResolveInfo> mockInfos = new ArrayList<>();
        mockInfos.add(mock(ResolveInfo.class));
        when(packageManager.queryIntentContentProviders(any(Intent.class), eq(0))).thenReturn(mockInfos);
        when(context.getPackageManager()).thenReturn(packageManager);
        when(configScanner.scan()).thenReturn(Observable.just(new ConfigApp("mypackage", "kiwi", new String[]{"apple", "tomato"})));
        appInstallOrUpdateReceiver = new AppInstallOrUpdateReceiver(configKeyStore, configScanner);
    }

    @Test
    public void canRegisterForBroadcasts() {
        appInstallOrUpdateReceiver.registerForBroadcasts(context);

        verify(context).registerReceiver(eq(appInstallOrUpdateReceiver), any(IntentFilter.class));
    }

    @Test
    public void willIgnoreIncorrectIntent() {
        Intent intent = new Intent();

        appInstallOrUpdateReceiver.onReceive(context, intent);

        verify(configScanner, times(0)).scan();
    }

    @Test
    public void willIgnoreNullPackageNameInIntent() {
        setupInstallActionIntent(null);

        appInstallOrUpdateReceiver.onReceive(context, intent);

        verify(configScanner, times(0)).scan();
    }

    @Test
    public void willIgnoreNoPackageNameInIntent() {
        setupInstallActionIntent("");

        appInstallOrUpdateReceiver.onReceive(context, intent);

        verify(configScanner, times(0)).scan();
    }

    @Test
    public void willScanOnIntent() {
        setupInstallActionIntent("arthur.bishop");

        appInstallOrUpdateReceiver.onReceive(context, intent);

        verify(configScanner).scan();
    }

    private void setupInstallActionIntent(String data) {
        when(intent.getAction()).thenReturn(ACTION_PACKAGE_ADDED);
        Uri uri = mock(Uri.class);
        when(uri.getEncodedSchemeSpecificPart()).thenReturn(data);
        when(intent.getData()).thenReturn(uri);
    }
}
