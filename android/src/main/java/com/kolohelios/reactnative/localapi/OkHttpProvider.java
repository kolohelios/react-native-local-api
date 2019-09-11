package com.kolohelios.reactnative.localapi;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import okhttp3.CertificatePinner;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class OkHttpProvider {
  public static OkHttpClient client;

  // public OkHttpProvider() {
  //   OkHttpClient.Builder builder = new OkHttpClient.Builder();
  //   builder.hostnameVerifier(new HostnameVerifier() {
  //     @Override
  //     public boolean verify(String hostname, SSLSession sslSession) {
  //       if (hostname.contains("192.168.50.1")) {
  //         return true;
  //       }
  //       return false;
  //     }
  //   });
  //   builder.certificatePinner(
  //     new CertificatePinner.Builder()
  //         .add("192.168.50.1", "sha256/wtRD2QevGVnabD+LLu0Z0TlWAQyiF2z7ZpfyMcVwVOA=")
  //         .build());
  //   client = builder.build();
  // }

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
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

//      for (Certificate certificate : response.handshake().peerCertificates()) {
//        System.out.println(CertificatePinner.pin(certificate));
//      }
      promise.resolve(response.handshake().peerCertificates().toString());
    } catch (Exception e) {
      promise.reject(e);
    }
  }
}
