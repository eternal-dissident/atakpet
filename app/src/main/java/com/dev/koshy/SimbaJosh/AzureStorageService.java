package com.dev.koshy.SimbaJosh;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AzureStorageService {
    private static final String TAG = "AzureStorage";
    private static final String SAS_URL = "https://atakpetdata.blob.core.windows.net/test?sp=racwdli&st=2025-04-16T21:54:32Z&se=2026-04-18T05:54:32Z&sv=2024-11-04&sr=c&sig=islqKXl7xi%2FKJAQwIEtNM9y89jkIFRVIh7JvxHghvHQ%3D";

    public interface UploadCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }

    public static void uploadToAzure(File file, UploadCallback callback) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .build();

                // Create request body from file
                RequestBody requestBody = RequestBody.create(
                        MediaType.parse("application/octet-stream"),
                        file
                );

                // Create the full URL for the blob
                String uploadUrl = SAS_URL;
                // Make sure the URL ends with a slash before adding the filename
                if (!uploadUrl.contains("/")) {
                    // Extract base URL (before the "?")
                    String baseUrl = uploadUrl.substring(0, uploadUrl.indexOf("?"));
                    String sasToken = uploadUrl.substring(uploadUrl.indexOf("?"));
                    uploadUrl = baseUrl + "/" + file.getName() + sasToken;
                } else {
                    // If URL already has a path, just add the filename before query string
                    String baseUrl = uploadUrl.substring(0, uploadUrl.indexOf("?"));
                    if (!baseUrl.endsWith("/")) {
                        baseUrl += "/";
                    }
                    String sasToken = uploadUrl.substring(uploadUrl.indexOf("?"));
                    uploadUrl = baseUrl + file.getName() + sasToken;
                }

                // Format current date in RFC 1123 format (required by Azure)
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                String date = dateFormat.format(new Date());

                // Build the request with required headers
                Request request = new Request.Builder()
                        .url(uploadUrl)
                        .put(requestBody)
                        .header("x-ms-date", date)
                        .header("x-ms-blob-type", "BlockBlob")
                        .build();

                Log.d(TAG, "Uploading to URL: " + uploadUrl);

                // Execute the request
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Upload successful: " + response.code());
                        callback.onSuccess("File uploaded successfully: " + file.getName());
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                        Log.e(TAG, "Upload failed: " + response.code() + " - " + response.message() + " - " + errorBody);
                        callback.onFailure("Upload failed: " + response.message() + " (" + response.code() + ")");
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Exception during upload: " + e.getMessage(), e);
                callback.onFailure("Upload failed: " + e.getMessage());
            }
        }).start();
    }
}