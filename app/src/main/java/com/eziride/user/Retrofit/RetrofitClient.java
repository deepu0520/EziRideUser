package com.eziride.user.Retrofit;

import com.eziride.user.Helper.URLHelper;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by CSS on 8/4/2017.
 */

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static Retrofit retrofit_address = null;
    private static Retrofit send_retrofeit = null;


    public static Retrofit getClient() {
        if (retrofit_address == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder().connectTimeout(2, TimeUnit.MINUTES).readTimeout(2, TimeUnit.MINUTES);
            okHttpBuilder.addInterceptor(loggingInterceptor);

            retrofit_address = new Retrofit.Builder()
                    .baseUrl(URLHelper.map_address_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpBuilder.build())
                    .build();
        }
        return retrofit_address;
    }

    public static Retrofit getLiveTrackingClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder().connectTimeout(2, TimeUnit.MINUTES).readTimeout(2, TimeUnit.MINUTES);
            okHttpBuilder.addInterceptor(loggingInterceptor);

            retrofit = new Retrofit.Builder()
                    .baseUrl(URLHelper.base)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpBuilder.build())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit sendRequestAPI() {
        if (send_retrofeit == null) {

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder().connectTimeout(2, TimeUnit.MINUTES).readTimeout(2, TimeUnit.MINUTES);
            okHttpBuilder.addInterceptor(loggingInterceptor);


            send_retrofeit = new Retrofit.Builder()
                    .baseUrl(URLHelper.base)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpBuilder.build())
                    .build();
        }
        return send_retrofeit;
    }
}
