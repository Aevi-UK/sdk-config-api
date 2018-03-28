package com.aevi.sdk.config.impl;

import android.content.Context;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Set;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigApiImplTest {

    private ConfigApiImpl configApi;

    @Mock
    Component component;

    ConfigKeyStore configKeyStore;
    @Mock
    AppInstallOrUpdateReceiver appInstallOrUpdateReceiver;
    @Mock
    ConfigScanner configScanner;
    @Mock
    Context context;

    @Before
    public void setup() {
        initMocks(this);
        setupMockComponent();
        configApi = new ConfigApiImpl(component);
    }

    private void setupMockComponent() {
        configKeyStore = new ConfigKeyStore();
        when(component.getConfigKeyStore()).thenReturn(configKeyStore);
        when(component.getAppInstallOrUpdateReceiver()).thenReturn(appInstallOrUpdateReceiver);
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

        TestObserver<Set<String>> testObserver = configApi.subscribeToConfigurationChanges().test();

        ConfigApp configApp = new ConfigApp(context.getPackageName(), "respectMyAutoritiiiii", new String[]{"car", "house"});
        configKeyStore.save(configApp);

        testObserver.assertNotComplete();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);

        verify(appInstallOrUpdateReceiver).registerForBroadcasts(any(Context.class));
        verify(appInstallOrUpdateReceiver).scanForConfigProviders();
    }

    @Test
    public void willCallContentProviderForKey() {
        String authority = "deadOrAliveYourComingWithMe";
        ConfigApp configApp = new ConfigApp(context.getPackageName(), authority, new String[]{"clarence", "alex"});
        configKeyStore.save(configApp);

        configApi.getConfigValue("alex");

        verify(configScanner).getValue(authority, "alex");
    }

    @Test
    public void willCallContentProviderForArrayKey() {
        String authority = "iNeverBrokeTheLaw";
        ConfigApp configApp = new ConfigApp(context.getPackageName(), authority, new String[]{"fargo", "dredd"});
        configKeyStore.save(configApp);

        configApi.getConfigArrayValue("dredd");

        verify(configScanner).getArrayValue(authority, "dredd");
    }

    @Test
    public void canCloseAndReleaseReceiver() {
        configApi.close();

        verify(context).unregisterReceiver(any(AppInstallOrUpdateReceiver.class));
    }

}
