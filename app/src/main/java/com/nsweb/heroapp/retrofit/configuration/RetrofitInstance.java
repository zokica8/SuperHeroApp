package com.nsweb.heroapp.retrofit.configuration;

import com.nsweb.heroapp.application.SuperHeroApplication;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import timber.log.Timber;

// creating a singleton retrofit instance
public class RetrofitInstance {

    private static Retrofit retrofit = null;
    private static HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    private static final String HTTP_HEADER_CACHE_CONTROL = "Cache-Control";
    private static final String HTTP_HEADER_PRAGMA = "Pragma";
    private static final long cacheSize = 10 * 1024 * 1024;
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .cache(cache())
            .addInterceptor(loggingInterceptor)
            .addNetworkInterceptor(networkInterceptor()) // used ONLY when network is ON (connected to the internet)
            .addInterceptor(offlineInterceptor())  // used when network is OFFLINE OR ONLINE
            .build();

    private RetrofitInstance() {

    }

    public static Retrofit getRetrofitInstance(String protocol, String host, String port) {
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if(retrofit == null) {
            return new Retrofit.Builder()
                    .baseUrl(String.format("%s://%s:%s", protocol, host, port))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(JacksonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

    private static Cache cache() {
        SuperHeroApplication superHeroApplication = SuperHeroApplication.getInstance();
        return new Cache(new File(superHeroApplication.getCacheDir(), "superheroes"), cacheSize);
    }

    // this will ONLY be called when the internet (network) is available
    private static Interceptor networkInterceptor() {
        return chain -> {
            Timber.i("Network interceptor called.");
            Response response = chain.proceed(chain.request());

            CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(3, TimeUnit.MINUTES)
                    .build();

            return response.newBuilder()
                    .removeHeader(HTTP_HEADER_PRAGMA)
                    .removeHeader(HTTP_HEADER_CACHE_CONTROL)
                    .header(HTTP_HEADER_CACHE_CONTROL, cacheControl.toString())
                    .build();
        };
    }

    // used when the network is offline or online
    private static Interceptor offlineInterceptor() {
        return chain -> {
            Timber.i("Offline interceptor called.");
            Request request = chain.request();

            // prevent caching when the network is on. For that you use networkInterceptor
            if(!SuperHeroApplication.hasNetwork()) {
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(10, TimeUnit.DAYS)
                        .build();
                request = request.newBuilder()
                        .removeHeader(HTTP_HEADER_PRAGMA)
                        .removeHeader(HTTP_HEADER_CACHE_CONTROL)
                        .cacheControl(cacheControl)
                        .build();
            }
            return chain.proceed(request);
        };
    }

}
