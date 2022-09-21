package com.razard.search.app.api.config;

import com.razard.search.app.api.blog.repository.KakaoApiRepository;
import com.razard.search.app.api.blog.repository.NaverApiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RetrofitConfig {

    @Value("${api.kakao.url}")
    private String kakaoApiUrl;

    @Value("${api.naver.url}")
    private String naverApiUrl;

    private final Interceptor kakaoHttpInterceptor;
    private final Interceptor naverHttpInterceptor;

    @Bean("kakaoHttpClient")
    public OkHttpClient kakaoHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(log::debug);
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(kakaoHttpInterceptor)
                .build();
    }

    @Bean("naverHttpClient")
    public OkHttpClient naverHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(log::debug);
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(naverHttpInterceptor)
                .build();
    }

    @Bean("kakaoRetrofit")
    public Retrofit kakaoRetrofit(@Qualifier("kakaoHttpClient") OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(kakaoApiUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Bean("naverRetrofit")
    public Retrofit naverRetrofit(@Qualifier("naverHttpClient") OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(naverApiUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Bean("kakaoApiRepository")
    public KakaoApiRepository kakaoApiRepository(@Qualifier("kakaoRetrofit") Retrofit retrofit) {
        return retrofit.create(KakaoApiRepository.class);
    }

    @Bean("naverApiRepository")
    public NaverApiRepository naverApiRepository(@Qualifier("naverRetrofit") Retrofit retrofit) {
        return retrofit.create(NaverApiRepository.class);
    }

}
