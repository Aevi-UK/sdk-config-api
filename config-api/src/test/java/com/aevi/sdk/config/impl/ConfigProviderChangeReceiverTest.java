package com.aevi.sdk.config.impl;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import io.reactivex.Observable;

import static android.content.Intent.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigProviderChangeReceiverTest {

    private ConfigProviderChangeReceiver configProviderChangeReceiver;

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
        when(configScanner.scan()).thenReturn(Observable.just(new ConfigApp("vendor", "1.0.0", "mypackage", "kiwi", new HashSet<String>(Arrays.asList("apple", "tomato")))));
        configProviderChangeReceiver = new ConfigProviderChangeReceiver(configKeyStore, configScanner);
    }

    @Test
    public void canRegisterForBroadcasts() {
        configProviderChangeReceiver.registerForBroadcasts(context);

        verify(context).registerReceiver(eq(configProviderChangeReceiver), any(IntentFilter.class));
    }

    @Test
    public void willIgnoreIncorrectIntent() {
        Intent intent = new Intent();

        configProviderChangeReceiver.onReceive(context, intent);

        verify(configScanner, times(0)).scan();
    }

    @Test
    public void willIgnoreNullPackageNameInIntent() {
        setupInstallActionIntent(null);

        configProviderChangeReceiver.onReceive(context, intent);

        verify(configScanner, times(0)).scan();
    }

    @Test
    public void willIgnoreNoPackageNameInIntent() {
        setupInstallActionIntent("");

        configProviderChangeReceiver.onReceive(context, intent);

        verify(configScanner, times(0)).scan();
    }

    @Test
    public void willScanOnIntent() {
        setupInstallActionIntent("arthur.bishop");

        configProviderChangeReceiver.onReceive(context, intent);

        verify(configScanner).scan();
    }

    @Test
    public void willNotifyForRemoval() {
        setupRemoveActionIntent();

        configProviderChangeReceiver.onReceive(context, intent);

        verify(configScanner).scan();
    }

    private void setupInstallActionIntent(String data) {
        when(intent.getAction()).thenReturn(ACTION_PACKAGE_ADDED);
        Uri uri = mock(Uri.class);
        when(uri.getEncodedSchemeSpecificPart()).thenReturn(data);
        when(intent.getData()).thenReturn(uri);
    }

    private void setupRemoveActionIntent() {
        when(intent.getAction()).thenReturn(ACTION_PACKAGE_REMOVED);
    }
}
