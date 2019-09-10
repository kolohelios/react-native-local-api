package com.kolohelios.reactnative.localapi;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import org.json.JSONException;
import java.io.IOException;

public class LocalApiModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;

    public LocalApiModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "LocalApi";
    }

    @ReactMethod
    public void apiRequest(final String url, final String method, final ReadableMap params, final ReadableMap body, final Boolean setCookie, final Promise promise) {
        try {
            PostExample example = new PostExample();
            // TODO implement method handling
            String jsonBody = ReadablesToJSON.convertMapToJson(body).toString();
            String response = example.post(url, jsonBody);
            promise.resolve(response);
        } catch (JSONException e) {
            promise.reject(e);
        } catch (IOException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void clearCookies() {
        // TODO implement this
    }

    @ReactMethod
    public void pinCertificate(final String hostname, final ReadableArray publicKeys, final String verificationURL) {
        CertificatePinning certPinning = new CertificatePinning();

        try {
            certPinning.run(hostname, publicKeys, verificationURL);
        } catch (Exception e) {
            System.out.print(e.toString());
        }
    }
}
