package com.kolohelios.reactnative.localapi;

import com.facebook.react.bridge.ReadableArray;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiInterface {
	public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

	String get(String url) throws IOException {
		Request request = new Request.Builder()
			.url(url)
			.get()
			.build();

			try (Response response = OkHttpProvider.client.newCall(request).execute()) {
				return response.body().string();
		}
	}

	String post(String url, String json) throws IOException {
		RequestBody body = RequestBody.create(json, JSON);

		Request request = new Request.Builder()
			.url(url)
			.post(body)
			.build();

			try (Response response = OkHttpProvider.client.newCall(request).execute()) {
				return response.body().string();
		}
	}
}
