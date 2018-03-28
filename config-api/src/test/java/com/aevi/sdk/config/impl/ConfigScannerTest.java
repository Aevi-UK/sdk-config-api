package com.aevi.sdk.config.impl;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static com.aevi.sdk.config.provider.BaseConfigProvider.*;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigScannerTest {

    private ConfigScanner configScanner;

    @Mock
    Context context;

    @Mock
    PackageManager packageManager;

    @Mock
    ContentResolver contentResolver;

    @Mock
    ResolveInfo resolveInfo;

    @Mock
    ProviderInfo providerInfo;

    @Before
    public void setup() {
        initMocks(this);
        when(context.getPackageManager()).thenReturn(packageManager);
        when(context.getContentResolver()).thenReturn(contentResolver);
        configScanner = new ConfigScanner(context);
    }

    @Test
    public void canHandleEmptyScan() {
        TestObserver<ConfigApp> testObserver = configScanner.scan().test();

        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertNoValues();
    }

    @Test
    public void canHandleScanWithProvider() {
        setupContentResolver();

        TestObserver<ConfigApp> testObserver = configScanner.scan().test();

        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        assertThat(testObserver.values().get(0).getAuthority()).isEqualTo("mr.incredible");
        verify(contentResolver).call(any(Uri.class), eq(METHOD_GET_KEYS), isNull(String.class), any(Bundle.class));
    }

    @Test
    public void canCallGetValueFromProvider() {
        setupContentResolver();

        configScanner.getValue("com.the.incredibles", "elastigirl");

        verify(contentResolver).call(any(Uri.class), eq(METHOD_GET), eq("elastigirl"), any(Bundle.class));
    }

    @Test
    public void canCallGetArrayValueFromProvider() {
        setupContentResolver();

        configScanner.getArrayValue("com.the.incredibles", "dash");

        verify(contentResolver).call(any(Uri.class), eq(METHOD_GET_ARRAY), eq("dash"), any(Bundle.class));
    }

    @Test
    public void willReturnValueFromProvider() {
        setupContentResolver();
        setupContentProviderReturn(METHOD_GET, "dash", CONFIG_VALUE, "{superskill: \"speed\"}");

        String value = configScanner.getValue("com.the.incredibles", "dash");
        assertThat(value).isEqualTo("{superskill: \"speed\"}");
    }

    @Test
    public void willHandleNullReturnValueFromProvider() {
        setupContentResolver();
        setupContentProviderReturn(METHOD_GET, "dash", CONFIG_VALUE, null);

        String value = configScanner.getValue("com.the.incredibles", "dash");
        assertThat(value).isEmpty();
    }

    @Test
    public void willHandleNullReturnValueArrayFromProvider() {
        setupContentResolver();
        setupContentProviderReturn(METHOD_GET_ARRAY, "supers", CONFIG_VALUES, null);

        String[] values = configScanner.getArrayValue("com.the.incredibles", "supers");
        assertThat(values).isEmpty();
    }

    @Test
    public void willReturnArrayValueFromProvider() {
        setupContentResolver();
        String[] returnValues = new String[]{"dash", "elastigirl", "Mr Incredible"};
        setupContentProviderReturn(METHOD_GET_ARRAY, "supers", CONFIG_VALUES, returnValues);

        String[] values = configScanner.getArrayValue("com.the.incredibles", "supers");
        assertThat(values).contains(returnValues);
    }

    @Test
    public void willReturnIntValueFromProvider() {
        setupContentResolver();
        int returnValue = 1234567;
        setupContentProviderReturn(METHOD_GET_INT, "supers", CONFIG_VALUE, returnValue);

        int value = configScanner.getIntValue("com.the.incredibles", "supers");
        assertThat(value).isEqualTo(returnValue);
    }

    @Test
    public void willHandleNullReturnIntValueFromProvider() {
        setupContentResolver();
        setupContentProviderReturn(METHOD_GET_INT, "supers", CONFIG_VALUES, null);

        int value = configScanner.getIntValue("com.the.incredibles", "supers");
        assertThat(value).isEqualTo(0);
    }

    private void setupContentProviderReturn(String method, String key, String returnKey, Object value) {
        Bundle b = mock(Bundle.class);
        when(b.get(returnKey)).thenReturn(value);
        when(b.containsKey(returnKey)).thenReturn(true);
        when(contentResolver.call(any(Uri.class), eq(method), eq(key), any(Bundle.class))).thenReturn(b);
    }

    private void setupContentResolver() {
        setupPmResolve();
        resolveInfo.providerInfo = providerInfo;
        providerInfo.packageName = "com.the.incredibles";
        providerInfo.authority = "mr.incredible";
    }

    private void setupPmResolve() {
        List<ResolveInfo> mockInfos = new ArrayList<>();
        mockInfos.add(resolveInfo);
        when(packageManager.queryIntentContentProviders(any(Intent.class), eq(PackageManager.GET_META_DATA | PackageManager.GET_RESOLVED_FILTER)))
                .thenReturn(mockInfos);
    }
}
