package com.razard.search.app.api.config;

import okhttp3.Interceptor;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class KakaoHttpInterceptor implements Interceptor {

    @Value("${api.kakao.rest-api-key}")
    private String kakaoAppKey;

    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(
                chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json;charset=UTF-8")
                        .addHeader("Cache-Control", "no-cache")
                        .addHeader("Authorization", "KakaoAK " + kakaoAppKey)
                        .build()
        );
    }
}
