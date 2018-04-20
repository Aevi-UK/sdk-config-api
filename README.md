# Configuration API

This API contains a simple interface to expose configuration settings and information to other applications.

## Configuration application implementation

Any application can expose a ContentProvider that will be detected by the config-api client and made available to other applications.

The applications that the values are exposed to can be restricted by package name.

To implement a configuration provider for this API you should implement a class that extends the `BaseConfigProvider` class from this API.
The implementation should return the list of keys it wants to expose as well as implement the functionality required to obtain
the corresponding configuration values. 

Once implemented the provider should be registered in the `AndroidManifest.xml` as shown below.

```xml
    <application ... >

        <provider
            android:name=".ExampleConfigProvider"
            android:authorities="com.aevi.sdk.config.provider.example"
            android:exported="true">
            <intent-filter>
                <action android:name="com.aevi.sdk.config.ConfigProvider"/>
            </intent-filter>
        </provider>
    </application>
```

Any number of these providers can be installed on a single device. The API will collate all key values and ensure they are
all exposed to clients using the API.

## Client Usage

The example below shows simple configuration client usage. All configuration parameters are obtained using a String key. 

Configuration values are returned as one of three types; A simple String, An Array of Strings or a `ConfigResource`. 

```java
        ConfigClient configClient = ConfigApi.getConfigClient(context);
        String setting = configClient.getConfigValue("mySettingKey");
        String[] settingArray = configClient.getConfigArrayValue("myArraySettingKey");
```
The full set of keys across "ALL" configuration applications installed can be obtained by calling:

```java
        Set<String> keys = configClient.getConfigKeys();
```


A `ConfigResource` object represents an Android resource object that should be obtained from the standard Android Resources. This
 allows for the resources to internationalised or tailored for specific device dimensions etc.

```java
        ConfigResource defaultValue = new ConfigResource(R.id.myRes, this);
        ConfigResource resource = ConfigResource getConfigResource("myResource", defaultValue);
```

Changes to configuration values can be subscribed to using the `subscribeToConfigurationChanges()` method as shown below.

```java

        configClient.subscribeToConfigurationChanges()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(keys -> {
                    handleExternalConfigValues(configClient, keys);
                });

        handleExternalConfigValues(configClient, configClient.getConfigKeys());

        private void handleExternalConfigValues(ConfigClient configClient, Set<String> keys) {
            // handle new values as required here.... 
        }

```

# Binaries


In your main gradle.build you'll need to include our public bintray in your main repositories section.

repositories {
    maven {
        url "http://dl.bintray.com/aevi/aevi-uk"
    }
}
And then add to your dependencies section

implementation compile 'com.aevi.sdk.config:config-api:<version>'

# Bugs and Feedback

For bugs, feature requests and discussion please use [GitHub Issues](https://github.com/Aevi-UK/sdk-config-api/issues)

# LICENSE

Copyright 2018 AEVI International GmbH

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
