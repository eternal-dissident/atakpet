// Created by Joshua Koshy. Licensed under the MIT License.

package com.dev.koshy.SimbaJosh;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static Retrofit authRetrofit;
    private static String accessToken;
    private static final String BASE_URL = "https://simba-sbx-api.blocks.simbachain.com/";
    String TAG = "SimbaJosh";

    static HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

    public static Retrofit getInstance() {
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        if (retrofit == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor);

            if (accessToken != null && !accessToken.isEmpty()) {
                // Add the Authorization header with the access token to every request
                clientBuilder.addInterceptor(chain -> {
                    Request originalRequest = chain.request();
                    Request.Builder requestBuilder = originalRequest.newBuilder()
                            .header("Authorization", "Bearer " + accessToken);
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                });
            }

            OkHttpClient client = clientBuilder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    public static Retrofit getAuthInstance(String clientId, String clientSecret) {
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        if (authRetrofit == null) {
            String credentials = Credentials.basic(clientId, clientSecret);

            OkHttpClient authClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request originalRequest = chain.request();
                        Request.Builder requestBuilder = originalRequest.newBuilder()
                                .header("Authorization", credentials)
                                .header("Content-Type", "application/x-www-form-urlencoded");
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    })
                    .addInterceptor(loggingInterceptor)
                    .build();

            authRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(authClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return authRetrofit;
    }

    public static void setAccessToken(String token) {
        accessToken = token;
        // Reset Retrofit instance to include the new access token
        retrofit = null;
        getInstance();
    }
}
