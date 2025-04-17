package com.atakmap.android.plugintemplate.plugin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WatchDirectoryActivity extends AppCompatActivity {

    private static final String TAG = "WatchDirectoryActivity";
    private ListView listView;
    private List<String> filesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_directory);

        listView = findViewById(R.id.watch_directory_list);
        Log.d(TAG, "onCreate: Activity created");

        loadSavedFiles();

        Button geocamButton = findViewById(R.id.tak_camera_directory);
        geocamButton.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to GeocamDirectoryActivity");
            Intent intent = new Intent(WatchDirectoryActivity.this, GeocamDirectoryActivity.class);
            startActivity(intent);
        });

        Button photosButton = findViewById(R.id.tak_photo_directory);
        photosButton.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to TakPhotosDirectoryActivity");
            Intent intent = new Intent(WatchDirectoryActivity.this, TakPhotosDirectoryActivity.class);
            startActivity(intent);
        });
    }

    private void loadSavedFiles() {
        SharedPreferences sharedPreferences = getSharedPreferences("WatchDirectoryPrefs", MODE_PRIVATE);
        Set<String> filesSet = sharedPreferences.getStringSet("uploadedFiles", new HashSet<String>());
        filesList = new ArrayList<>(filesSet);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filesList);
        listView.setAdapter(adapter);
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "WatchDirectoryActivity stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "WatchDirectoryActivity destroyed");
    }
}
