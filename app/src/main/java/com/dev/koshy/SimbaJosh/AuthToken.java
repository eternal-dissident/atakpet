// Created by Joshua Koshy. Licensed under the MIT License.

package com.dev.koshy.SimbaJosh;

import com.google.gson.annotations.SerializedName;

public class AuthToken {

    String TAG = "SimbaJosh";
    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("expires_in")
    public int expiresIn;

    @SerializedName("token_type")
    public String tokenType;

    public String scope;
}
