// Created by Joshua Koshy. Licensed under the MIT License.

package com.dev.koshy.SimbaJosh;

import android.util.Log;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SimbaJosh {
    private String clientId;
    private String clientSecret;
    private APIService apiService;
    private AuthService authService;
    private AuthToken token;
    String TAG = "SimbaJosh";

    long timeOfLastAuthCall;

    public SimbaJosh(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        // Create the AuthService for authentication
        this.authService = RetrofitClient.getAuthInstance(clientId, clientSecret).create(AuthService.class);
        // APIService will be initialized after authentication
    }

    public void authenticate(Callback<AuthToken> callback) {

        Log.d(TAG, "authenticate: Authentication Requested");
        String grantType = "client_credentials";
        Call<AuthToken> call = authService.authenticate(grantType);
        call.enqueue(new Callback<AuthToken>() {
            @Override
            public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Authentication Response Received");
                    token = response.body();
                    RetrofitClient.setAccessToken(token.accessToken);
                    // Initialize APIService after setting the access token
                    apiService = RetrofitClient.getInstance().create(APIService.class);
                    callback.onResponse(call, response);
                } else {
                    Log.e(TAG, "Authentication failed: " + response.message());
                    callback.onFailure(call, new Exception("Authentication failed: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<AuthToken> call, Throwable t) {
                Log.e(TAG, "Authentication error: " + t.getMessage());
                callback.onFailure(call, t);
            }
        });
    }

    // Existing method
    public void getFileCountRequest(Callback<ResponseBody> callback) {
        Log.d(TAG, "getFileCountRequest Called");
        if (token == null || token.accessToken == null || token.accessToken.isEmpty()) {
            authenticate(new Callback<AuthToken>() {
                @Override
                public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                    if (response.isSuccessful()) {
                        makeGetFileCountCall(callback);
                    } else {
                        Log.e(TAG, "Authentication failed: " + response.message());
                        callback.onFailure(null, new Exception("Authentication failed: " + response.message()));
                    }
                }

                @Override
                public void onFailure(Call<AuthToken> call, Throwable t) {
                    Log.e(TAG, "Authentication error: " + t.getMessage());
                    callback.onFailure(null, t);
                }
            });
        } else {
            makeGetFileCountCall(callback);
        }
    }

    private void makeGetFileCountCall(Callback<ResponseBody> callback) {
        Log.d(TAG, "makeGetFileCountCall Called");
        Call<ResponseBody> call = apiService.getFileCount();
        call.enqueue(callback);
    }

    // New methods

    public void getFileRequest(int index, Callback<ResponseBody> callback) {
        Log.d(TAG, "getFileRequest Called with index: " + index);
        if (token == null || token.accessToken == null || token.accessToken.isEmpty()) {
            authenticate(new Callback<AuthToken>() {
                @Override
                public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                    if (response.isSuccessful()) {
                        makeGetFileCall(index, callback);
                    } else {
                        Log.e(TAG, "Authentication failed: " + response.message());
                        callback.onFailure(null, new Exception("Authentication failed: " + response.message()));
                    }
                }

                @Override
                public void onFailure(Call<AuthToken> call, Throwable t) {
                    Log.e(TAG, "Authentication error: " + t.getMessage());
                    callback.onFailure(null, t);
                }
            });
        } else {
            makeGetFileCall(index, callback);
        }
    }

    // Modified makeGetFileCall method to accept index parameter
    private void makeGetFileCall(int index, Callback<ResponseBody> callback) {
        Log.d(TAG, "makeGetFileCall Called with index: " + index);
        Call<ResponseBody> call = apiService.getFile(index);
        call.enqueue(callback);
    }

    public void addFilePostRequest(String fileName, String fileHash, long timestamp, Callback<ResponseBody> callback) {
        Log.d(TAG, "addFilePostRequest Called");
        if (token == null || token.accessToken == null || token.accessToken.isEmpty()) {
            authenticate(new Callback<AuthToken>() {
                @Override
                public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                    if (response.isSuccessful()) {
                        makeAddFilePostCall(fileName, fileHash, timestamp, callback);
                    } else {
                        Log.e(TAG, "Authentication failed: " + response.message());
                        callback.onFailure(null, new Exception("Authentication failed: " + response.message()));
                    }
                }

                @Override
                public void onFailure(Call<AuthToken> call, Throwable t) {
                    Log.e(TAG, "Authentication error: " + t.getMessage());
                    callback.onFailure(null, t);
                }
            });
        } else {
            makeAddFilePostCall(fileName, fileHash, timestamp, callback);
        }
    }

    private void makeAddFilePostCall(String fileName, String fileHash, long timestamp, Callback<ResponseBody> callback) {
        Log.d(TAG, "makeAddFilePostCall Called");
        AddFileRequest addFileRequest = new AddFileRequest(fileName, fileHash, timestamp);
        Call<ResponseBody> call = apiService.addFilePost(addFileRequest);
        call.enqueue(callback);
    }

    public void addFileGetRequest(Callback<ResponseBody> callback) {
        Log.d(TAG, "addFileGetRequest Called");
        if (token == null || token.accessToken == null || token.accessToken.isEmpty()) {
            authenticate(new Callback<AuthToken>() {
                @Override
                public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                    if (response.isSuccessful()) {
                        makeAddFileGetCall(callback);
                    } else {
                        Log.e(TAG, "Authentication failed: " + response.message());
                        callback.onFailure(null, new Exception("Authentication failed: " + response.message()));
                    }
                }

                @Override
                public void onFailure(Call<AuthToken> call, Throwable t) {
                    Log.e(TAG, "Authentication error: " + t.getMessage());
                    callback.onFailure(null, t);
                }
            });
        } else {
            makeAddFileGetCall(callback);
        }
    }

    private void makeAddFileGetCall(Callback<ResponseBody> callback) {
        Log.d(TAG, "makeAddFileGetCall Called");
        Call<ResponseBody> call = apiService.addFileGet();
        call.enqueue(callback);
    }

    public void transactionsRequest(Callback<ResponseBody> callback) {
        Log.d(TAG, "transactionsRequest Called");
        if (token == null || token.accessToken == null || token.accessToken.isEmpty()) {
            authenticate(new Callback<AuthToken>() {
                @Override
                public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                    if (response.isSuccessful()) {
                        makeTransactionsCall(callback);
                    } else {
                        Log.e(TAG, "Authentication failed: " + response.message());
                        callback.onFailure(null, new Exception("Authentication failed: " + response.message()));
                    }
                }

                @Override
                public void onFailure(Call<AuthToken> call, Throwable t) {
                    Log.e(TAG, "Authentication error: " + t.getMessage());
                    callback.onFailure(null, t);
                }
            });
        } else {
            makeTransactionsCall(callback);
        }
    }

    private void makeTransactionsCall(Callback<ResponseBody> callback) {
        Log.d(TAG, "makeTransactionsCall Called");
        Call<ResponseBody> call = apiService.transactions();
        call.enqueue(callback);
    }
}
