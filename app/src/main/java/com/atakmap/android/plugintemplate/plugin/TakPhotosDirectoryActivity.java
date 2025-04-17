package com.atakmap.android.plugintemplate.plugin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

//import com.simbachain.auth.AuthConfig;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TakPhotosDirectoryActivity extends AppCompatActivity {

    private static final String TAG = "TakPhotosDirActivity";
    private static final int REQUEST_PERMISSIONS = 1;
    private ListView listView;
    private List<String> filesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tak_photos_directory);

        listView = findViewById(R.id.tak_photos_list_view); // Ensure this ID matches your layout
        Log.d(TAG, "onCreate: Activity created");

        checkPermissions();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 (API level 30) and above
            if (!Environment.isExternalStorageManager()) {
                Log.d(TAG, "Requesting manage external storage permission");
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_PERMISSIONS);
            } else {
                Log.d(TAG, "Manage external storage permission already granted");
                loadFilesFromDirectory();
            }
        } else {
            // Below Android 11
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Requesting read external storage permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
            } else {
                Log.d(TAG, "Read external storage permission already granted");
                loadFilesFromDirectory();
            }
        }
    }

    private void loadFilesFromDirectory() {
        Log.d(TAG, "Loading files from TAK Photos directory");
        File directory = new File(Environment.getExternalStorageDirectory(), "atak/attachments");
        if (!directory.exists()) {
            Log.d(TAG, "Directory does not exist");
            Toast.makeText(this, "Directory not found", Toast.LENGTH_SHORT).show();
            return;
        }
        File[] files = directory.listFiles();
        filesList = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                filesList.add(file.getName() + " - " + new Date(file.lastModified()).toString());
            }
        }

        if (filesList.isEmpty()) {
            Toast.makeText(this, "No files found", Toast.LENGTH_SHORT).show();
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filesList);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permissions granted");
                loadFilesFromDirectory();
            } else {
                Log.d(TAG, "Permissions denied");
                Toast.makeText(this, "Permission denied. Cannot access files.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Log.d(TAG, "Manage external storage permission granted");
                    loadFilesFromDirectory();
                } else {
                    Log.d(TAG, "Manage external storage permission denied");
                    Toast.makeText(this, "Permission denied. Cannot access files.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
