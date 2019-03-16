package com.microphonelistener;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import javax.annotation.Nonnull;

public class MicrophoneCallback extends ReactContextBaseJavaModule {
    public MicrophoneCallback(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void onNewData(byte[] data){

    }

    @Nonnull
    @Override
    public String getName() {
        return "IOMicrophoneCallback";
    }
}
