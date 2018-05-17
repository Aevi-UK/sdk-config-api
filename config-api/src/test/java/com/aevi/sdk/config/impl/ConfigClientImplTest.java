package com.aevi.sdk.config.impl;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import io.reactivex.observers.TestObserver;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigClientImplTest {

    private ConfigClientImpl configApi;

    @Mock
    Component component;

    ConfigKeyStore configKeyStore;
    @Mock
    ConfigProviderChangeReceiver configProviderChangeReceiver;
    @Mock
    ConfigScanner configScanner;
    @Mock
    Context context;

    @Before
    public void setup() {
        initMocks(this);
        setupMockComponent();
        configApi = new ConfigClientImpl(component);
    }

    private void setupMockComponent() {
        configKeyStore = new ConfigKeyStore();
        when(component.getConfigKeyStore()).thenReturn(configKeyStore);
        when(component.getConfigProviderChangeReceiver()).thenReturn(configProviderChangeReceiver);
        when(component.getConfigScanner()).thenReturn(configScanner);
        when(component.getContext()).thenReturn(context);
    }

    @Test
    public void willGetEmptyValuesInitially() {
        assertThat(configApi.getConfigKeys()).isEmpty();
        assertThat(configApi.getConfigValue("iAmLegend")).isEmpty();
        assertThat(configApi.getConfigArrayValue("imSorryDave")).isEmpty();
    }

    @Test
    public void willNotifyOnKeys() {

        TestObserver<ConfigUpdate> testObserver = configApi.subscribeToConfigurationUpdates().test();
        setupConfigApp(context.getPackageName(), "respectMyAutoritiiiii", "car", "house");

        testObserver.assertNotComplete();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);

        verify(configProviderChangeReceiver).registerForBroadcasts(any(Context.class));
        verify(configProviderChangeReceiver).scanForConfigProviders();
    }

    @Test
    public void willCallContentProviderForKey() {
        String authority = "deadOrAliveYourComingWithMe";
        setupConfigApp(context.getPackageName(), authority, "clarence", "alex");

        configApi.getConfigValue("alex");

        verify(configScanner).getValue(authority, "alex");
    }

    @Test
    public void willCallContentProviderForArrayKey() {
        String authority = "iNeverBrokeTheLaw";
        setupConfigApp(context.getPackageName(), authority, "fargo", "dredd");

        configApi.getConfigArrayValue("dredd");

        verify(configScanner).getArrayValue(authority, "dredd");
    }

    private void setupConfigApp(String packageName, String authority, String... keys) {
        ConfigApp configApp = new ConfigApp("AEVI", "1.0.0", packageName, authority, new HashSet<String>(Arrays.asList(keys)));
        List<ConfigApp> configApps = new ArrayList<>();
        configApps.add(configApp);
        configKeyStore.save(configApps);
    }

    @Test
    public void canCloseAndReleaseReceiver() {
        configApi.close();

        verify(context).unregisterReceiver(any(ConfigProviderChangeReceiver.class));
    }

}
