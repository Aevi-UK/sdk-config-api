package com.aevi.sdk.config;

import android.content.Context;
import android.util.Log;

import com.aevi.sdk.config.provider.BaseConfigProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import okio.Okio;

import static com.aevi.sdk.config.constants.FlowConstants.FLOW_CONFIGS;
import static com.aevi.sdk.config.constants.FlowConstants.REQUEST_TYPE_CONFIGS;

public class DefaultConfigProvider extends BaseConfigProvider {

    private static final String TAG = DefaultConfigProvider.class.getSimpleName();

    @Override
    public String[] getConfigKeys() {
        Log.d(TAG, "Returning config keys");
        return new String[]{FLOW_CONFIGS, REQUEST_TYPE_CONFIGS};
    }

    @Override
    public String getConfigValue(String key) {
        return "";
    }

    @Override
    public String[] getConfigArrayValue(String key) {
        switch (key) {
            case FLOW_CONFIGS:
                return getFlowConfigs();
            case REQUEST_TYPE_CONFIGS:
                return getRequestTypeConfigs();
        }
        return new String[0];
    }

    public String[] getFlowConfigs() {
        String saleFlow = readFile(R.raw.flow_config_sale);
        if (saleFlow != null) {
            return new String[]{saleFlow};
        }
        return new String[0];
    }

    public String[] getRequestTypeConfigs() {
        return new String[]{
                readFile(R.raw.request_type_sale),
                readFile(R.raw.request_type_refund),
                readFile(R.raw.request_type_preauth),
                readFile(R.raw.request_type_complete),
                readFile(R.raw.request_type_tokenisation)
        };
    }

    protected String readFile(int resourceFile) {
        Context context = getContext();
        if (context != null) {
            try {
                InputStream is = context.getResources().openRawResource(resourceFile);
                return Okio.buffer(Okio.source(is)).readString(Charset.defaultCharset());
            } catch (IOException e) {
                Log.e(TAG, "Failed to read config", e);
            }
        }
        return "";
    }
}
