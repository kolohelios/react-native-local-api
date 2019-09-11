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
	public void apiRequest(final String url, final String method, final ReadableMap body, final Boolean setCookie, final Promise promise) {
		try {
			ApiInterface apiInterface = new ApiInterface();
			// TODO implement better method handling
			String response = "";
			switch (method) {
				case "GET":
					response = apiInterface.get(url);
					break;
				case "POST":
					String jsonBody = ReadablesToJSON.convertMapToJson(body).toString();
					response = apiInterface.post(url, jsonBody);
			}
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
	public void pinCertificate(final String hostname, final ReadableArray publicKeys, final String verificationURL, final Promise promise) {
		try {
			OkHttpProvider.setPin(hostname, publicKeys, verificationURL, promise);
		} catch (Exception e) {
			System.out.print(e.toString());
		}
	}
}
