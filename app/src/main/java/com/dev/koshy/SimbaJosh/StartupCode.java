// Created by Joshua Koshy. Licensed under the MIT License.

package com.dev.koshy.SimbaJosh;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartupCode {
    private SimbaJosh simbaJosh2;
    private String TAG = "SimbaJosh";

    public StartupCode(String clientID, String clientSecret) {
        simbaJosh2 = new SimbaJosh(clientID, clientSecret);
        Log.d(TAG, "SimbaJosh Initialized");
    }

    public void start() {
        simbaJosh2.getFileCountRequest(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        Log.d(TAG, "API Response: " + responseBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "API call failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "API call error: " + t.getMessage());
            }
        });
    }

    // Existing method for getFileCount
    public String getFileCount() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] responseHolder = new String[1];
        final Exception[] exceptionHolder = new Exception[1];

        simbaJosh2.getFileCountRequest(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        responseHolder[0] = response.body().string();
                    } catch (IOException e) {
                        exceptionHolder[0] = e;
                    }
                } else {
                    exceptionHolder[0] = new Exception("API call failed: " + response.message());
                }
                latch.countDown();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                exceptionHolder[0] = new Exception("API call error: " + t.getMessage(), t);
                latch.countDown();
            }
        });

        // Wait for the response
        latch.await();

        // Check for exceptions
        if (exceptionHolder[0] != null) {
            throw exceptionHolder[0];
        }

        // Return the response or an empty string
        return responseHolder[0] != null ? responseHolder[0] : "";
    }

    // New method for getFile
    public String getFile(int index) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] responseHolder = new String[1];
        final Exception[] exceptionHolder = new Exception[1];

        simbaJosh2.getFileRequest(index, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        responseHolder[0] = response.body().string();
                    } catch (IOException e) {
                        exceptionHolder[0] = e;
                    }
                } else {
                    exceptionHolder[0] = new Exception("API call failed: " + response.message());
                }
                latch.countDown();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                exceptionHolder[0] = new Exception("API call error: " + t.getMessage(), t);
                latch.countDown();
            }
        });

        // Wait for the response
        latch.await();

        // Check for exceptions
        if (exceptionHolder[0] != null) {
            throw exceptionHolder[0];
        }

        return responseHolder[0] != null ? responseHolder[0] : "";
    }

    // New method for addFilePost
    public String addFilePost(String fileName, String fileHash, long timestamp) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] responseHolder = new String[1];
        final Exception[] exceptionHolder = new Exception[1];

        simbaJosh2.addFilePostRequest(fileName, fileHash, timestamp, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        responseHolder[0] = response.body().string();
                    } catch (IOException e) {
                        exceptionHolder[0] = e;
                    }
                } else {
                    exceptionHolder[0] = new Exception("API call failed: " + response.message());
                }
                latch.countDown();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                exceptionHolder[0] = new Exception("API call error: " + t.getMessage(), t);
                latch.countDown();
            }
        });

        latch.await();

        if (exceptionHolder[0] != null) {
            throw exceptionHolder[0];
        }

        return responseHolder[0] != null ? responseHolder[0] : "";
    }

    // New method for addFileGet
    public String addFileGet() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] responseHolder = new String[1];
        final Exception[] exceptionHolder = new Exception[1];

        simbaJosh2.addFileGetRequest(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        responseHolder[0] = response.body().string();
                    } catch (IOException e) {
                        exceptionHolder[0] = e;
                    }
                } else {
                    exceptionHolder[0] = new Exception("API call failed: " + response.message());
                }
                latch.countDown();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                exceptionHolder[0] = new Exception("API call error: " + t.getMessage(), t);
                latch.countDown();
            }
        });

        latch.await();

        if (exceptionHolder[0] != null) {
            throw exceptionHolder[0];
        }

        return responseHolder[0] != null ? responseHolder[0] : "";
    }

    // New method for transactions
    public String transactions() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] responseHolder = new String[1];
        final Exception[] exceptionHolder = new Exception[1];

        simbaJosh2.transactionsRequest(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        responseHolder[0] = response.body().string();
                    } catch (IOException e) {
                        exceptionHolder[0] = e;
                    }
                } else {
                    exceptionHolder[0] = new Exception("API call failed: " + response.message());
                }
                latch.countDown();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                exceptionHolder[0] = new Exception("API call error: " + t.getMessage(), t);
                latch.countDown();
            }
        });

        latch.await();

        if (exceptionHolder[0] != null) {
            throw exceptionHolder[0];
        }

        return responseHolder[0] != null ? responseHolder[0] : "";
    }
}
