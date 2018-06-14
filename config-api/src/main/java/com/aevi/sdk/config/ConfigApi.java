/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aevi.sdk.config;

import android.content.Context;

import com.aevi.sdk.config.impl.Component;
import com.aevi.sdk.config.impl.ConfigClientImpl;

public final class ConfigApi {

    /**
     * Get a new instance of a {@link ConfigClient} to obtain config parameters
     *
     * @param context The Android context
     * @return An instance of {@link ConfigClient}
     */
    public static ConfigClient getConfigClient(Context context) {
        Component component = new Component(context);
        return new ConfigClientImpl(component);
    }
}