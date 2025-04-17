package com.dev.koshy.SimbaJosh;

// Created by Joshua Koshy. Licensed under the MIT License.

import com.google.gson.annotations.SerializedName;

public class AddFileRequest {

    @SerializedName("_fileName")
    private String fileName;

    @SerializedName("_fileHash")
    private String fileHash;

    @SerializedName("_timestamp")
    private long timestamp;

    public AddFileRequest(String fileName, String fileHash, long timestamp) {
        this.fileName = fileName;
        this.fileHash = fileHash;
        this.timestamp = timestamp;
    }

    // Getters and setters (if needed)
    public String getFileName() {
        return fileName;
    }

    public String getFileHash() {
        return fileHash;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
