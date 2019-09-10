package com.kolohelios.reactnative.localapi;

import java.io.IOException;
import java.security.cert.Certificate;
import okhttp3.MediaType;
// import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiInterface {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();
    
    // CertificatePinner certificatePinner = new CertificatePinner.Builder()
    //       .add("192.168.50.1", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
    //       .build();
    // OkHttpClient client = new OkHttpClient.Builder()
    //   .certificatePinner(certificatePinner)
    //   .build();

    String post(String url, String json) throws IOException {
      // OkHttpClient client = OkHttpClient.Builder()
      //   .certificatePinner(certificatePinner)
      //   .build();

      // Request request = new Request.Builder()
      //     .url("https://192.168.50.1")
      //     .build();
      // try (Response response = client.newCall(request).execute()) {
      //     return response.body().string();
      // } catch (Exception e) {
      //   System.out.println(e);
      //   return e.getMessage();
      // }
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();
      try (Response response = client.newCall(request).execute()) {
          return response.body().string();
      }
    }
}
