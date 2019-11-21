package com.kolohelios.reactnative.localapi;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;
import java.io.IOException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import okhttp3.CertificatePinner;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

public final class OkHttpProvider {
  public static OkHttpClient client;

  public static void setPin(String hostname, ReadableArray publicKeys, String verificationURL, Promise promise) throws Exception {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
      builder.hostnameVerifier(new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession sslSession) {
          if (hostname.contains(hostname)) {
            return true;
          }
          return false;
        }
      });

    // 60 seconds as a default matches what iOS's default timeout is and should support API calls that take a relatively long time, such as those with side-effects
    builder.connectTimeout(60, TimeUnit.SECONDS)
      .callTimeout(60, TimeUnit.SECONDS)
      .writeTimeout(60, TimeUnit.SECONDS)
      .readTimeout(60, TimeUnit.SECONDS)
      .retryOnConnectionFailure(false);

    CertificatePinner.Builder certificatePinnerBuilder = new CertificatePinner.Builder();

    List<Object> publicKeysArrayObjects = publicKeys.toArrayList();

    List<String> publicKeysArrayStrings = new ArrayList<>(publicKeysArrayObjects.size());
    for (Object object : publicKeysArrayObjects) {
      publicKeysArrayStrings.add(object != null ? object.toString() : null);
    }

    for (String key : publicKeysArrayStrings) {
      certificatePinnerBuilder.add(hostname, key);
    }

    builder.certificatePinner(certificatePinnerBuilder.build());
    builder.cookieJar(new CookieJar() {
      private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

      @Override
      public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> cookies) {
        cookieStore.put(HttpUrl.parse(httpUrl.host()), cookies);
      }

      @NotNull
      @Override
      public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        List<Cookie> cookies = cookieStore.get(HttpUrl.parse(httpUrl.host()));

        return cookies != null ? cookies : new ArrayList<Cookie>();
      }
    });

    client = builder.build();

    Request request = new Request.Builder()
        .url(verificationURL)
        .header("Content-Type", "application/json")
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

      promise.resolve(response.handshake().peerCertificates().toString());
    } catch (Exception e) {
      promise.reject(e);
    }
  }
}
