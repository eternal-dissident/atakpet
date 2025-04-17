// Created by Joshua Koshy. Licensed under the MIT License.

package com.dev.koshy.SimbaJosh;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthService {

    String TAG = "SimbaJosh";
    @FormUrlEncoded
    @POST("o/token/")
    Call<AuthToken> authenticate(
            @Field("grant_type") String grantType
    );
}
