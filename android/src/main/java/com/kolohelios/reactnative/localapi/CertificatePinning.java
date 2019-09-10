package com.kolohelios.reactnative.localapi;

import com.facebook.react.bridge.ReadableArray;
import java.io.IOException;
import java.security.cert.Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class CertificatePinning {
  private final OkHttpClient client;

  public CertificatePinning() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.hostnameVerifier(new HostnameVerifier() {
      @Override
      public boolean verify(String hostname, SSLSession sslSession) {
        if (hostname.contains("192.168.50.1")) {
          return true;
        }
        return false;
      }
    });
    builder.certificatePinner(
      new CertificatePinner.Builder()
          .add("192.168.50.1", "sha256/wtRD2QevGVnabD+LLu0Z0TlWAQyiF2z7ZpfyMcVwVOA=")
          .build());
    client = builder.build();
  }

  public void run(String hostname, ReadableArray publicKeys, String verificationURL) throws Exception {
    Request request = new Request.Builder()
        .url("https://192.168.50.1/api/login")
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

      for (Certificate certificate : response.handshake().peerCertificates()) {
        System.out.println(CertificatePinner.pin(certificate));
      }
    }
  }
}
