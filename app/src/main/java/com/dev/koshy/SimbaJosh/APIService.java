// Created by Joshua Koshy. Licensed under the MIT License.

package com.dev.koshy.SimbaJosh;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query; // Import this

public interface APIService {

    String TAG = "SimbaJosh";
    @GET("v2/apps/SCTest/contract/SCTest1/getFileCount/")
    Call<ResponseBody> getFileCount();

    @GET("v2/apps/SCTest/contract/SCTest1/getFile/")
    Call<ResponseBody> getFile(@Query("index") int index);

    @POST("v2/apps/SCTest/contract/SCTest1/addFile/")
    Call<ResponseBody> addFilePost(@Body AddFileRequest addFileRequest);

    @GET("v2/apps/SCTest/contract/SCTest1/addFile/")
    Call<ResponseBody> addFileGet();

    @GET("v2/apps/SCTest/contract/SCTest1/transactions/")
    Call<ResponseBody> transactions();
}
