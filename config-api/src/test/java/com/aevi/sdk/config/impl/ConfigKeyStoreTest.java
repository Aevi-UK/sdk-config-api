package com.aevi.sdk.config.impl;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import io.reactivex.observers.TestObserver;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ConfigKeyStoreTest {

    private ConfigKeyStore configKeyStore;

    @Before
    public void setup() {
        configKeyStore = new ConfigKeyStore();
    }

    @Test
    public void willNotifyOnKeysChanged() {

        TestObserver<Set<String>> testObserver = configKeyStore.observeKeyChanges().test();

        ConfigApp configApp = new ConfigApp("respectMyAutoritiiiii", new String[]{"car", "house"});
        configKeyStore.save(configApp);

        testObserver.assertNotComplete();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);

        assertThat(testObserver.values().get(0).size()).isEqualTo(2);
        assertThat(testObserver.values().get(0)).contains("car");
        assertThat(testObserver.values().get(0)).contains("house");
    }

    @Test
    public void canGetConfigAppFromKeyNames() {
        String authority = "deadOrAliveYourComingWithMe";
        ConfigApp configApp = new ConfigApp(authority, new String[]{"clarence", "alex"});
        configKeyStore.save(configApp);

        String authority2 = "user";
        ConfigApp configApp2 = new ConfigApp(authority2, new String[]{"flynn", "rinzler"});
        configKeyStore.save(configApp2);

        assertThat(configKeyStore.getApp("clarence")).isEqualTo(configApp);
        assertThat(configKeyStore.getApp("rinzler")).isEqualTo(configApp2);
        assertThat(configKeyStore.getApp("alex")).isEqualTo(configApp);
        assertThat(configKeyStore.getApp("flynn")).isEqualTo(configApp2);
        assertThat(configKeyStore.getKeys()).hasSize(4);
        assertThat(configKeyStore.getKeys()).contains("clarence", "alex", "flynn", "rinzler");
    }
}
