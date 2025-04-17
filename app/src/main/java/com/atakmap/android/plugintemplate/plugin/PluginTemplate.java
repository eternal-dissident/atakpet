package com.atakmap.android.plugintemplate.plugin;

import static com.atakmap.android.maps.MapView.getMapView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import com.atak.plugins.impl.PluginContextProvider;
import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.importexport.ExportFilters;
import com.atakmap.android.importexport.Exportable;
import com.atakmap.android.importexport.FormatNotSupportedException;
import com.atakmap.android.importexport.ImportExportMapComponent;
import com.atakmap.android.importfiles.ui.ImportManagerFileBrowser;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.importexport.send.SendDialog;
import com.atakmap.android.missionpackage.export.MissionPackageExportMarshal;
import com.atakmap.android.missionpackage.export.MissionPackageExportWrapper;
import com.dev.koshy.SimbaJosh.StartupCode;
//import com.simbachain.SimbaConfigFile;
//import com.simbachain.SimbaException;
//import com.simbachain.auth.blocks.BlocksConfig;
//import com.simbachain.simba.management.AuthenticatedUser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.text.SimpleDateFormat;
import java.util.Locale;

import gov.tak.api.plugin.IPlugin;
import gov.tak.api.plugin.IServiceController;
import gov.tak.api.ui.IHostUIService;
import gov.tak.api.ui.Pane;
import gov.tak.api.ui.PaneBuilder;
import gov.tak.api.ui.ToolbarItem;
import gov.tak.api.ui.ToolbarItemAdapter;
import gov.tak.platform.marshal.MarshalManager;

import org.json.JSONArray;
import org.json.JSONObject;



public class PluginTemplate implements IPlugin {

    private static final String TAG = "PluginTemplate";
    private IServiceController serviceController;
    private Context pluginContext;
    private IHostUIService uiService;
    private ToolbarItem toolbarItem;
    private Pane templatePane;
    private String selectedImagePath; // Variable to store the selected image path

    private String selectedMsvPath; // Variable to store the selected MSV path

    StartupCode SimbaJoshInstance;

    private final CameraActivity.CameraDataListener cdl = new CameraActivity.CameraDataListener();

    private final CameraActivity.CameraDataReceiver cdr = new CameraActivity.CameraDataReceiver() {
        public void onCameraDataReceived(Bitmap b) {
            com.atakmap.coremap.log.Log.d(TAG, "==========img received======>" + b);
            b.recycle();
        }
    };

    /* private void initializeSimbaConfig() {
        SimbaConfigNew simbaConfigNew = new SimbaConfigNew();
        try {
            Log.d(TAG, "Authenticated user: " + simbaConfigNew.user.whoami());
        } catch (SimbaException e) {
            Log.e(TAG, "Failed to authenticate user", e);
        }
    } */

    public PluginTemplate(IServiceController serviceController) {
        this.serviceController = serviceController;
        final PluginContextProvider ctxProvider = serviceController.getService(PluginContextProvider.class);
        if (ctxProvider != null) {
            pluginContext = ctxProvider.getPluginContext();
            pluginContext.setTheme(R.style.ATAKPluginTheme);
        }


        AtakBroadcast.init(pluginContext);

        uiService = serviceController.getService(IHostUIService.class);

        toolbarItem = new ToolbarItem.Builder(
                pluginContext.getString(R.string.app_name),
                MarshalManager.marshal(
                        pluginContext.getResources().getDrawable(R.drawable.ic_launcher),
                        android.graphics.drawable.Drawable.class,
                        gov.tak.api.commons.graphics.Bitmap.class))
                .setListener(new ToolbarItemAdapter() {
                    @Override
                    public void onClick(ToolbarItem item) {
                        showPane();
                    }
                })
                .build();
    }

    @Override
    public void onStart() {
        if (uiService == null)
            return;
        Log.d(TAG, "onStart: OnStart Called. SimbaJosh will load soon.");
        uiService.addToolbarItem(toolbarItem);

        SimbaJoshInstance = new StartupCode("OaBpqubF7w6zhBepG7ncWTR0wrzvBy4s9qs5Ooef", "PbPxkIEQZRGRWkK56ZZleWZgzQSVQkhyALrsQNfVbVGIEETltM2BEWhEdl5Rjwd3TiTGn2UjOoTvAP0hxFoywVsyzmKsPKYLBav4tp11Sw04NRhyYoXPJui3HuLw3Xo9");
        SimbaJoshInstance.start(); // Start the connection with our SimbaClient

        new Thread(() -> {
            try {
                // Get the current year
                int unixTimeSeconds = (int) System.currentTimeMillis() / 1000;
                String newName = "" + unixTimeSeconds;
                UUID uuid = UUID.randomUUID();
                String uuidAsString = uuid.toString();
                Log.d("SimbaJosh", "onStart: Sending Response");
                String response = SimbaJoshInstance.addFilePost("HELLO" + newName, uuidAsString, 2838392);
                Log.d("SimbaJosh", response + "addFilePost: CALL FROM DIRECT");
            } catch (Exception e) {
                Log.d("SimbaJosh", "onStart: EXCEPTION" + e);
            }
        }).start();

    }


    @Override
    public void onStop() {

        if (uiService == null)
            return;

        uiService.removeToolbarItem(toolbarItem);
    }

    private void showPane() {
        // Instantiate the plugin view if necessary
        if (templatePane == null) {
            // Inflate the layout
            View paneView = PluginLayoutInflater.inflate(pluginContext, R.layout.main_layout, null);

            Button uploadButton = paneView.findViewById(R.id.upload_photos_videos_button);
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(pluginContext, "Upload button clicked", Toast.LENGTH_SHORT).show();
                    onUploadButtonClick();
                }
            });

            Button watchDirectoryButton = paneView.findViewById(R.id.watch_directory_button);
            watchDirectoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(pluginContext, "Watch Directory button clicked", Toast.LENGTH_SHORT).show();
                    onWatchDirectoryButtonClick();
                }
            });

            Button viewReceivedFilesButton = paneView.findViewById(R.id.view_recieved_files);
            viewReceivedFilesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBlockchainStoredHashesPopup(v);
                }
            });


            Button createMissionPackageButton = paneView.findViewById(R.id.create_mission_packages);
            createMissionPackageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createNewMissionPackage();
                }
            });

            Button TakePhotoTAK = paneView.findViewById(R.id.take_photo_video_button);
            TakePhotoTAK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TakePhotoFunc();
                }
            });

            Button petSendTestButton = paneView.findViewById(R.id.PET_send_test);
            petSendTestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSendOptionsPopup(v);
                }
            });

            Button selectOptionButton = paneView.findViewById(R.id.select_option_button);
            selectOptionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(pluginContext, "Select Option button clicked", Toast.LENGTH_SHORT).show();
                    showOptionsPopup(v);
                }
            });

            Button viewFilesTestButton = paneView.findViewById(R.id.view_files_test);
            viewFilesTestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFilesPopup(v);
                }
            });

            Button viewFilesTestButtonDP = paneView.findViewById(R.id.view_files_dp_test);
            viewFilesTestButtonDP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDPFilesPopup(v);
                }
            });

            Button viewFilesTestButtonDynamic = paneView.findViewById(R.id.view_files_configure_test);
            viewFilesTestButtonDynamic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDynamicFilesPopup(v);
                }
            });

            Button filePickerButton = paneView.findViewById(R.id.file_picker_button);
            filePickerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PromptForJPGFileSelection();
                }
            });

            Button filePickerButtonMSV = paneView.findViewById(R.id.file_msv_picker_button);
            filePickerButtonMSV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PromptForMSVFileSelection();
                }
            });

            Button dialogTestingButton = paneView.findViewById(R.id.dialog_testing_button);
            dialogTestingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(pluginContext, "Fragment Testing Option clicked", Toast.LENGTH_SHORT).show();
                    showTestingPopup(v);
                }
            });

            Button JPGSecureMain = paneView.findViewById(R.id.tak_server_send);
            JPGSecureMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(pluginContext, "Send JPG Secure File Transfer Clicked", Toast.LENGTH_SHORT).show();
                    sendJpgFileSecurely(v);
                }
            });

            Button selectedPicButton = paneView.findViewById(R.id.selected_pic_button);
            selectedPicButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSelectedImagePopup(v);
                }
            });

            Button hashJpgButton = paneView.findViewById(R.id.Hashed_Value_JPG);
            hashJpgButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedImagePath != null) {
                        String fileHash = generateFileHash(selectedImagePath);
                        if (fileHash != null) {
                            showHashPopup(v, fileHash);
                        } else {
                            Toast.makeText(pluginContext, "Failed to generate hash", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(pluginContext, "No file selected", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Button selectedMsvButton = paneView.findViewById(R.id.selected_msv_button);
            selectedMsvButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSelectedMsvPopup(v);
                }
            });

            Button sendMsvSecurelyButton = paneView.findViewById(R.id.securly_send_file_msv);
            sendMsvSecurelyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedMsvPath != null) {
                        sendMsvFileSecurely();
                    } else {
                        Toast.makeText(pluginContext, "No mission package selected", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Button hashMsvButton = paneView.findViewById(R.id.Hashed_Value_MSV);
            hashMsvButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedMsvPath != null) {
                        String fileHash = generateFileHash(selectedMsvPath);
                        if (fileHash != null) {
                            showHashPopup(v, fileHash); // Method to show the hash value in a popup
                        } else {
                            Toast.makeText(pluginContext, "Failed to generate hash", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(pluginContext, "No file selected", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Create the Pane
            templatePane = new PaneBuilder(paneView)
                    .setMetaValue(Pane.RELATIVE_LOCATION, Pane.Location.Default)
                    .setMetaValue(Pane.PREFERRED_WIDTH_RATIO, 0.5D)
                    .setMetaValue(Pane.PREFERRED_HEIGHT_RATIO, 0.5D)
                    .build();
        }

        if (!uiService.isPaneVisible(templatePane)) {
            uiService.showPane(templatePane, null);
        }
    }

    private void onUploadButtonClick() {
        Toast.makeText(pluginContext, "Upload Photos and Videos button clicked", Toast.LENGTH_SHORT).show();
        sendImportBroadcast();

        SharedPreferences sharedPreferences = pluginContext.getSharedPreferences("WatchDirectoryPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> filesSet = sharedPreferences.getStringSet("uploadedFiles", new HashSet<String>());
        filesSet.add("Photo1.jpg - 12:00 PM"); // Replace with actual file name and timestamp
        editor.putStringSet("uploadedFiles", filesSet);
        editor.apply();

        // Log the saved files
        Log.d(TAG, "Files saved: " + filesSet.toString());
    }

    private void showFilesPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) pluginContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_view_files, null);

        int width = (int) (anchorView.getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = (int) (anchorView.getResources().getDisplayMetrics().heightPixels * 0.85);
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        ListView listView = popupView.findViewById(R.id.camera_files_list_view);
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        if (!directory.exists()) {
            Toast.makeText(pluginContext, "Directory not found", Toast.LENGTH_SHORT).show();
            return;
        }
        File[] files = directory.listFiles();
        List<File> filesList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if(file.getName().toLowerCase().endsWith(".jpg")) {
                    filesList.add(file);
                }
            }
        }
        if (filesList.isEmpty()) {
            Toast.makeText(pluginContext, "No files found", Toast.LENGTH_SHORT).show();
        } else {
            CameraFileAdapter adapter = new CameraFileAdapter(pluginContext, filesList);
            listView.setAdapter(adapter);
        }

        popupWindow.showAtLocation(anchorView.getRootView(), Gravity.CENTER, 0, 0);
    }

    private void showDPFilesPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) pluginContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_view_files_dp, null);

        int width = (int) (anchorView.getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = (int) (anchorView.getResources().getDisplayMetrics().heightPixels * 0.85);
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        ListView listView = popupView.findViewById(R.id.camera_files_list_view);
        File directory = new File(Environment.getExternalStorageDirectory(), "ATAK/Tools/datapackage");
        if (!directory.exists()) {
            Toast.makeText(pluginContext, "Directory not found", Toast.LENGTH_SHORT).show();
            return;
        }
        File[] files = directory.listFiles();
        List<File> filesList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.getName().toLowerCase().endsWith(".zip")) {  // Filter for zip files
                    filesList.add(file);
                }
            }
        }
        if (filesList.isEmpty()) {
            Toast.makeText(pluginContext, "No zip files found", Toast.LENGTH_SHORT).show();
        } else {
            UIntSC adapter = new UIntSC(pluginContext, filesList);
            listView.setAdapter(adapter);
        }

        popupWindow.showAtLocation(anchorView.getRootView(), Gravity.CENTER, 0, 0);
    }





    private void showBlockchainStoredHashesPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) pluginContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.blockchain_stored_hash, null);

        int width = (int) (anchorView.getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = (int) (anchorView.getResources().getDisplayMetrics().heightPixels * 0.85);
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        ListView listView = popupView.findViewById(R.id.hash_list_view);

        // Initialize the HashMap with sample data
        Map<String, String> hashMap = new HashMap<>();
        /* hashMap.put("file1.jpg", "a1b2c3d4e5f6...");
        hashMap.put("file2.jpg", "f7e6d5c4b3a2...");
        hashMap.put("file3.jpg", "bhvguyfyyuc...");
        hashMap.put("newsecurefile.jpg", "bhvguyfyyuc...");
         */

        // Create the initial hashList and adapter
        List<Map.Entry<String, String>> hashList = new ArrayList<>(hashMap.entrySet());
        ArrayAdapter<Map.Entry<String, String>> adapter = new ArrayAdapter<Map.Entry<String, String>>(pluginContext, android.R.layout.simple_list_item_2, android.R.id.text1, hashList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(pluginContext).inflate(android.R.layout.simple_list_item_2, parent, false);
                }

                Map.Entry<String, String> entry = getItem(position);

                TextView text1 = convertView.findViewById(android.R.id.text1);
                TextView text2 = convertView.findViewById(android.R.id.text2);

                text1.setText("File Name: " + entry.getKey());
                text2.setText("Hash Value: " + entry.getValue());

                return convertView;
            }
        };
        listView.setAdapter(adapter);
        popupWindow.showAtLocation(anchorView.getRootView(), Gravity.CENTER, 0, 0);

        // Perform the API call in a background thread
        new Thread(() -> {
            try {
                Handler mainHandler = new Handler(Looper.getMainLooper());

                String response = SimbaJoshInstance.getFileCount();
                JSONObject jsonObject = new JSONObject(response);
                int value = 0;
                try {
                    value = jsonObject.getInt("value");
                } catch (Exception ex) {

                }
                String valueAsString = "" + value;
                // hashMap.put("filecount", valueAsString);

                for (int i = 0; i < value ; i++) {
                    String a = "" + (i+1);
                    String res = SimbaJoshInstance.getFile(i);
                    Log.d("SimbaJosh", "showBlockchainStoredHashesPopup: " + res);
                    // Parse the JSON string into a JSONObject
                    JSONObject resObject = new JSONObject(res);

                    // Extract the "value" array
                    JSONArray valueArray = resObject.getJSONArray("value");

                    // Assign elements to variables
                    String fileName = valueArray.getString(0); // "HELLO-1495204"
                    Log.d("SimbaJosh", "showBlockchainStoredHashesPopup: " + fileName);
                    String fileHash = valueArray.getString(1); // "918a36a7-1380-47c4-83ae-808440c619f2"
                    int timestamp = valueArray.getInt(2);       // 2838392
                    hashMap.put(fileName, fileHash);

                    mainHandler.post(() -> {
                        hashList.clear();
                        hashList.addAll(hashMap.entrySet());
                        adapter.notifyDataSetChanged();
                    });
                }

                // Update the UI on the main thread using a Handler
                /*
                mainHandler.post(() -> {
                    // Update the hashList and notify the adapter
                    hashList.clear();
                    hashList.addAll(hashMap.entrySet());
                    adapter.notifyDataSetChanged();
                });
                 */

                Log.d("SimbaJosh", response + " CALL FROM DIRECT");
            } catch (Exception e) {
                Log.e("SimbaJosh", "API call failed", e);
            }
        }).start();
    }




    private void onWatchDirectoryButtonClick() {
        Toast.makeText(pluginContext, "Watch Directory button clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(pluginContext, WatchDirectoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // This is important
        pluginContext.startActivity(intent);
    }

    private void TakePhotoFunc(){
        cdl.register(getMapView().getContext(), cdr);

        Intent intent = new Intent();
        intent.setClassName("com.atakmap.android.helloworld.plugin", "com.atakmap.android.helloworld.CameraActivity");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getMapView().getContext().startActivity(intent);
    }

     /*private class CameraFileAdapter extends ArrayAdapter<File> {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        public CameraFileAdapter(@NonNull Context context, List<File> files) {
            super(context, 0, files);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_camera_file, parent, false);
            }

            File file = getItem(position);
            TextView fileNameTextView = convertView.findViewById(R.id.file_name);
            TextView fileTimestampTextView = convertView.findViewById(R.id.file_timestamp);
            Button sendButton = convertView.findViewById(R.id.send_button);

            fileNameTextView.setText(file.getName());
            fileTimestampTextView.setText(dateFormat.format(new Date(file.lastModified())));

            sendButton.setOnClickListener(v -> {
                new SendDialog.Builder(getMapView()).addFile(file).show();
            });

            return convertView;
        }
    } */
     private class CameraFileAdapter extends ArrayAdapter<File> {
         private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

         public CameraFileAdapter(@NonNull Context context, List<File> files) {
             super(context, 0, files);
         }

         @NonNull
         @Override
         public View getView(int position, View convertView, @NonNull ViewGroup parent) {
             if (convertView == null) {
                 convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_camera_file, parent, false);
             }

             File file = getItem(position);
             TextView fileNameTextView = convertView.findViewById(R.id.file_name);
             TextView fileTimestampTextView = convertView.findViewById(R.id.file_timestamp);
             Button sendButton = convertView.findViewById(R.id.send_button);

             // Display file name and timestamp
             fileNameTextView.setText(file.getName());
             String timestamp = dateFormat.format(new Date(file.lastModified()));
             fileTimestampTextView.setText(timestamp);

             // Handle send button click
             sendButton.setOnClickListener(v -> {
                 String fileHash = generateFileHash(file.getAbsolutePath()); // Generate file hash
                 if (fileHash != null) {
                     sendFileToSimbaBlockchain(file.getName(), fileHash, file.lastModified());
                     new SendDialog.Builder(getMapView()).addFile(file).show();
                 } else {
                     Toast.makeText(pluginContext, "Failed to generate file hash", Toast.LENGTH_SHORT).show();
                 }
             });

             return convertView;
         }

         // Generate SHA-256 hash of the file
         private String generateFileHash(String filePath) {
             try {
                 MessageDigest digest = MessageDigest.getInstance("SHA-256");
                 InputStream fis = new FileInputStream(filePath);
                 byte[] buffer = new byte[1024];
                 int bytesRead;
                 while ((bytesRead = fis.read(buffer)) != -1) {
                     digest.update(buffer, 0, bytesRead);
                 }
                 fis.close();

                 // Convert hash bytes to hex string
                 StringBuilder sb = new StringBuilder();
                 for (byte b : digest.digest()) {
                     sb.append(String.format("%02x", b));
                 }
                 return sb.toString();
             } catch (Exception e) {
                 e.printStackTrace();
                 return null;
             }
         }

         // Send the file details to Simba blockchain
         private void sendFileToSimbaBlockchain(String fileName, String fileHash, long timestamp) {
             // Convert timestamp to seconds (Unix timestamp)
             long unixTimestamp = timestamp / 1000;

             // Create a background thread for network operations
             new Thread(() -> {
                 try {
                     String response = SimbaJoshInstance.addFilePost(fileName, fileHash, unixTimestamp);
                     Log.d("SimbaJosh", "Simba API Response: " + response);
                     new Handler(Looper.getMainLooper()).post(() ->
                             Toast.makeText(pluginContext, "File uploaded to blockchain", Toast.LENGTH_SHORT).show()
                     );
                 } catch (Exception e) {
                     Log.e("SimbaJosh", "Failed to send file to blockchain", e);
                     new Handler(Looper.getMainLooper()).post(() ->
                             Toast.makeText(pluginContext, "Error uploading file", Toast.LENGTH_SHORT).show()
                     );
                 }
             }).start();
         }
     }




    private class UIntSC extends ArrayAdapter<File> {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        public UIntSC(@NonNull Context context, List<File> files) {
            super(context, 0, files);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_camera_file, parent, false);
            }

            File file = getItem(position);
            TextView fileNameTextView = convertView.findViewById(R.id.file_name);
            TextView fileTimestampTextView = convertView.findViewById(R.id.file_timestamp);
            Button sendButton = convertView.findViewById(R.id.send_button);

            // Display file name and timestamp
            fileNameTextView.setText(file.getName());
            fileTimestampTextView.setText(String.valueOf(file.lastModified() / 1000)); // Unix timestamp

            // Send button click logic
            sendButton.setOnClickListener(v -> {
                String fileHash = generateFileHash(file.getAbsolutePath()); // Generate hash
                if (fileHash != null) {
                    // Send file details to blockchain
                    sendFileToSimbaBlockchain(file.getName(), fileHash, file.lastModified());
                    new SendDialog.Builder(getMapView()).addFile(file).show();
                } else {
                    Toast.makeText(getContext(), "Failed to generate file hash", Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;
        }

        // Generate SHA-256 hash of the file
        private String generateFileHash(String filePath) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                InputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
                fis.close();
                StringBuilder sb = new StringBuilder();
                for (byte b : digest.digest()) {
                    sb.append(String.format("%02x", b));
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        // Send file details to SIMBA blockchain
        private void sendFileToSimbaBlockchain(String fileName, String fileHash, long timestamp) {
            long unixTimestamp = timestamp / 1000; // Convert to Unix timestamp

            // Perform network operation in background thread
            new Thread(() -> {
                try {
                    String response = SimbaJoshInstance.addFilePost(fileName, fileHash, unixTimestamp);
                    Log.d("SimbaJosh", "Blockchain Response: " + response);
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(getContext(), "File uploaded to blockchain", Toast.LENGTH_SHORT).show()
                    );
                } catch (Exception e) {
                    Log.e("SimbaJosh", "Blockchain upload failed", e);
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(getContext(), "Error uploading file", Toast.LENGTH_SHORT).show()
                    );
                }
            }).start();
        }
    }




    private void sendImportBroadcast() {
        Intent intent = new Intent(ImportExportMapComponent.USER_IMPORT_FILE_ACTION);
        AtakBroadcast.getInstance().sendBroadcast(intent);
    }

    private void showOptionsPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) pluginContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_options, null);

        final PopupWindow popupWindow = new PopupWindow(popupView,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true); // Set width and height to WRAP_CONTENT

        Button takGeocamButton = popupView.findViewById(R.id.tak_geocam_button);
        takGeocamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(pluginContext, "TAK Geocam button clicked", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
                showGeocamDirectoryPopup(anchorView);
            }
        });

        Button photosButton = popupView.findViewById(R.id.photos_button);
        photosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(pluginContext, "Photos button clicked", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
                showTakPhotosDirectoryPopup(anchorView);
            }
        });

        Button videosButton = popupView.findViewById(R.id.videos_button);
        videosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(pluginContext, "Videos button clicked", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();

            }
        });

        popupWindow.showAtLocation(anchorView.getRootView(), Gravity.CENTER, 0, 0);
    }

    private void showGeocamDirectoryPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) pluginContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_geocam_directory, null);

        int width = (int) (anchorView.getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = (int) (anchorView.getResources().getDisplayMetrics().heightPixels * 0.85);
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        ListView listView = popupView.findViewById(R.id.new_button_geotach);
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "takgeocam");
        if (!directory.exists()) {
            Toast.makeText(pluginContext, "Directory not found", Toast.LENGTH_SHORT).show();
            return;
        }
        File[] files = directory.listFiles();
        List<File> filesList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                filesList.add(file);
            }
        }
        if (filesList.isEmpty()) {
            Toast.makeText(pluginContext, "No files found", Toast.LENGTH_SHORT).show();
        } else {
            FileAdapter adapter = new FileAdapter(pluginContext, filesList);
            listView.setAdapter(adapter);
        }

        popupWindow.showAtLocation(anchorView.getRootView(), Gravity.CENTER, 0, 0);
    }

    private void showDynamicFilesPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) pluginContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_dynamic_files, null);

        int width = (int) (anchorView.getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = (int) (anchorView.getResources().getDisplayMetrics().heightPixels * 0.85);
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        EditText pathInput = popupView.findViewById(R.id.path_input);
        Button loadDirectoryButton = popupView.findViewById(R.id.load_directory_button);
        ListView listView = popupView.findViewById(R.id.dynamic_files_list_view);

        loadDirectoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String directoryPath = pathInput.getText().toString();
                File directory = new File(directoryPath);
                if (!directory.exists()) {
                    Toast.makeText(pluginContext, "Directory not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                File[] files = directory.listFiles();
                List<File> filesList = new ArrayList<>();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {  // Display only files
                            filesList.add(file);
                        }
                    }
                }
                if (filesList.isEmpty()) {
                    Toast.makeText(pluginContext, "No files found", Toast.LENGTH_SHORT).show();
                } else {
                    CameraFileAdapter adapter = new CameraFileAdapter(pluginContext, filesList);
                    listView.setAdapter(adapter);
                }
            }
        });

        popupWindow.showAtLocation(anchorView.getRootView(), Gravity.CENTER, 0, 0);
    }


    private void showTakPhotosDirectoryPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) pluginContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_tak_photos_directory, null);

        int width = (int) (anchorView.getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = (int) (anchorView.getResources().getDisplayMetrics().heightPixels * 0.85);
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        ListView listView = popupView.findViewById(R.id.tak_photos_list_view);
        File directory = new File(Environment.getExternalStorageDirectory(), "atak/attachments");
        if (!directory.exists()) {
            Toast.makeText(pluginContext, "Directory not found", Toast.LENGTH_SHORT).show();
            return;
        }
        File[] files = directory.listFiles();
        List<File> filesList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                filesList.add(file);
            }
        }
        if (filesList.isEmpty()) {
            Toast.makeText(pluginContext, "No files found", Toast.LENGTH_SHORT).show();
        } else {
            HashFileAdapter adapter = new HashFileAdapter(pluginContext, filesList);
            listView.setAdapter(adapter);
        }

        popupWindow.showAtLocation(anchorView.getRootView(), Gravity.CENTER, 0, 0);
    }


    private void showTestingPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) pluginContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_work_in_progress, null);

        final PopupWindow popupWindow = new PopupWindow(popupView,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true); // Set width and height to WRAP_CONTENT

        Button closeButton = popupView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                showNewDirectoryPopup(anchorView);
            }
        });

        Button takGalleryButton = popupView.findViewById(R.id.tak_gallery_button);
        takGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                showDataPackagesDirectoryPopup(anchorView); // Show the new popup for data packages
            }
        });

        popupWindow.showAtLocation(anchorView.getRootView(), Gravity.CENTER, 0, 0);
    }

    private void showSelectedMsvPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) pluginContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_mission_package_preview, null);

        int width = (int) (anchorView.getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = (int) (anchorView.getResources().getDisplayMetrics().heightPixels * 0.85);
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        // Set up the ListView in the popup
        ListView listView = popupView.findViewById(R.id.mission_package_list_view);
        if (selectedMsvPath != null) {
            try {
                List<String> fileNames = getZipContents(selectedMsvPath);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(pluginContext, android.R.layout.simple_list_item_1, fileNames);
                listView.setAdapter(adapter);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(pluginContext, "Error reading ZIP file", Toast.LENGTH_SHORT).show();
            }
        }


        popupWindow.showAtLocation(anchorView.getRootView(), Gravity.CENTER, 0, 0);
    }

    private List<String> getZipContents(String zipFilePath) throws IOException {
        List<String> fileNames = new ArrayList<>();
        ZipFile zipFile = new ZipFile(new File(zipFilePath));
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            fileNames.add(entry.getName());
        }
        zipFile.close();
        return fileNames;
    }

    private void sendJpgFileSecurely(View anchorView) {
        if (selectedImagePath != null) {
            File jpgFile = new File(selectedImagePath);
            new SendDialog.Builder(getMapView()).addFile(jpgFile).show();
        } else {
            Toast.makeText(pluginContext, "No JPG file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMsvFileSecurely() {
        if (selectedMsvPath != null) {
            File msvFile = new File(selectedMsvPath);
            new SendDialog.Builder(getMapView()).addFile(msvFile).show();
        } else {
            Toast.makeText(pluginContext, "No MSV file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNewDirectoryPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) pluginContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newPopupView = inflater.inflate(R.layout.popup_new_directory, null);

        int width = (int) (anchorView.getResources().getDisplayMetrics().widthPixels * 0.9); // 90% of screen width
        int height = (int) (anchorView.getResources().getDisplayMetrics().heightPixels * 0.9); // 90% of screen height
        final PopupWindow newPopupWindow = new PopupWindow(newPopupView, width, height, true); // Set width and height to a larger percentage

        ListView listView = newPopupView.findViewById(R.id.tak_camera_list_view);
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        if (!directory.exists()) {
            Toast.makeText(pluginContext, "Directory not found", Toast.LENGTH_SHORT).show();
            return;
        }
        File[] files = directory.listFiles();
        List<File> filesList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                filesList.add(file);
            }
        }
        if (filesList.isEmpty()) {
            Toast.makeText(pluginContext, "No files found", Toast.LENGTH_SHORT).show();
        } else {
            FileAdapter adapter = new FileAdapter(pluginContext, filesList);
            listView.setAdapter(adapter);
        }

        Button newButton = newPopupView.findViewById(R.id.new_button);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pluginContext instanceof FragmentActivity) {
                    showFilePicker((FragmentActivity) pluginContext);
                } else {
                    Toast.makeText(pluginContext, "Invalid context for file picker", Toast.LENGTH_SHORT).show();
                }
            }
        });

        newPopupWindow.showAtLocation(anchorView.getRootView(), Gravity.CENTER, 0, 0);
    }

    private void showDataPackagesDirectoryPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) pluginContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newPopupView = inflater.inflate(R.layout.popup_data_packages, null);

        int width = (int) (anchorView.getResources().getDisplayMetrics().widthPixels * 0.9); // 90% of screen width
        int height = (int) (anchorView.getResources().getDisplayMetrics().heightPixels * 0.9); // 90% of screen height
        final PopupWindow newPopupWindow = new PopupWindow(newPopupView, width, height, true); // Set width and height to a larger percentage

        ListView listView = newPopupView.findViewById(R.id.tak_datap_list_view);
        File directory = new File(Environment.getExternalStorageDirectory(), "ATAK/Tools/datapackage");
        if (!directory.exists()) {
            Toast.makeText(pluginContext, "Directory not found", Toast.LENGTH_SHORT).show();
            return;
        }
        File[] files = directory.listFiles();
        List<File> filesList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                filesList.add(file);
            }
        }
        if (filesList.isEmpty()) {
            Toast.makeText(pluginContext, "No files found", Toast.LENGTH_SHORT).show();
        } else {
            FileAdapter adapter = new FileAdapter(pluginContext, filesList);
            listView.setAdapter(adapter);
        }

        Button navigateButton = newPopupView.findViewById(R.id.navigate_data);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(pluginContext, "Data Packages Directory button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        newPopupWindow.showAtLocation(anchorView.getRootView(), Gravity.CENTER, 0, 0);
    }

    private void showHashPopup(View anchorView, String hashValue) {
        LayoutInflater inflater = (LayoutInflater) pluginContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_hash_value, null);

        TextView hashTextView = popupView.findViewById(R.id.hash_value_text);
        hashTextView.setText(hashValue);

        final PopupWindow popupWindow = new PopupWindow(popupView,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

        popupWindow.showAtLocation(anchorView.getRootView(), Gravity.CENTER, 0, 0);
    }


    private void showFilePicker(FragmentActivity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            activity.startActivityForResult(
                    Intent.createChooser(intent, "Select a File"),
                    1
            );
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    private class FileAdapter extends ArrayAdapter<File> {
        public FileAdapter(@NonNull Context context, List<File> files) {
            super(context, 0, files);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_geocam_directory_with_buttons, parent, false);
            }

            File file = getItem(position);
            TextView fileNameTextView = convertView.findViewById(R.id.file_name);
            Button openButton = convertView.findViewById(R.id.open_button);

            fileNameTextView.setText(file.getName() + " - " + new Date(file.lastModified()).toString());

            openButton.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Open button clicked for file: " + file.getName(), Toast.LENGTH_SHORT).show();
            });

            return convertView;
        }
    }

    private void PromptForJPGFileSelection() { //In progress for creating File Picker (JPG File Selection)
        final String[] fileExtensions = {"jpg"};
        final String startingDirectory = "/sdcard/DCIM/Camera";

        final ImportManagerFileBrowser fileBrowser = ImportManagerFileBrowser.inflate(getMapView());
        fileBrowser.setTitle("Select a JPG File");
        fileBrowser.setStartDirectory(startingDirectory);
        fileBrowser.setExtensionTypes(fileExtensions);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getMapView().getContext());
        alertBuilder.setView(fileBrowser);

        alertBuilder.setNegativeButton("Cancel", null);

        alertBuilder.setPositiveButton("Select", (dialog, num) -> {
            List<File> chosenFiles = fileBrowser.getSelectedFiles();

            if (chosenFiles.isEmpty()) {
                Toast.makeText(pluginContext, "No Files Selected", Toast.LENGTH_SHORT).show();
            } else if (chosenFiles.size() > 1) {
                Toast.makeText(pluginContext, "More Than One File Selected", Toast.LENGTH_SHORT).show();
            } else {
                selectedImagePath = chosenFiles.get(0).getAbsolutePath();
                Toast.makeText(pluginContext, "One File Selected: " + selectedImagePath, Toast.LENGTH_SHORT).show();
            }
        });

        final AlertDialog alertDialog = alertBuilder.create();
        fileBrowser.setAlertDialog(alertDialog);
        alertDialog.show();
    }

    private void PromptForMSVFileSelection() {
        final String[] fileExtensions = {"zip"};
        final String startingDirectory = "/sdcard/atak/tools/datapackage";

        final ImportManagerFileBrowser fileBrowser = ImportManagerFileBrowser.inflate(getMapView());
        fileBrowser.setTitle("Select a Mission Package");
        fileBrowser.setStartDirectory(startingDirectory);
        fileBrowser.setExtensionTypes(fileExtensions);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getMapView().getContext());
        alertBuilder.setView(fileBrowser);

        alertBuilder.setNegativeButton("Cancel", null);

        alertBuilder.setPositiveButton("Select", (dialog, num) -> {
            List<File> chosenFiles = fileBrowser.getSelectedFiles();
            if (chosenFiles.isEmpty()) {
                Toast.makeText(pluginContext, "No Files Selected", Toast.LENGTH_SHORT).show();
            } else {
                selectedMsvPath = chosenFiles.get(0).getAbsolutePath();
                Toast.makeText(pluginContext, "Selected File: " + selectedMsvPath, Toast.LENGTH_SHORT).show();
            }
        });

        final AlertDialog alertDialog = alertBuilder.create();
        fileBrowser.setAlertDialog(alertDialog);
        alertDialog.show();
    }

    private void sendMsvFileSecurely(View anchorView) {
        if (selectedMsvPath != null) {
            File msvFile = new File(selectedMsvPath);
            new SendDialog.Builder(getMapView()).addFile(msvFile).show();
        } else {
            Toast.makeText(pluginContext, "No mission package selected", Toast.LENGTH_SHORT).show();
        }
    }

    private class HashFileAdapter extends ArrayAdapter<File> {
        public HashFileAdapter(@NonNull Context context, List<File> files) {
            super(context, 0, files);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_hash_directory, parent, false);
            }

            File file = getItem(position);
            TextView hashTextView = convertView.findViewById(R.id.hash_value);
            Button openButton = convertView.findViewById(R.id.open_button);

            hashTextView.setText(file.getName());

            openButton.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Open button clicked for file: " + file.getName(), Toast.LENGTH_SHORT).show();
            });

            return convertView;
        }
    }


    private void showSendOptionsPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) pluginContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pet_testing_beta, null);

        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

        Button directMessageButton = popupView.findViewById(R.id.button_direct_message);
        Button selectTakServerButton = popupView.findViewById(R.id.button_select_tak_server);
        Button bothButton = popupView.findViewById(R.id.button_both);

        directMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "This is a DEBUG message");
                popupWindow.dismiss();
                Toast.makeText(pluginContext, "Direct Message selected", Toast.LENGTH_SHORT).show();
            }
        });

        selectTakServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                Toast.makeText(pluginContext, "Select TAK Server selected", Toast.LENGTH_SHORT).show();
            }
        });

        bothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                Toast.makeText(pluginContext, "Both selected", Toast.LENGTH_SHORT).show();
            }
        });

        popupWindow.showAtLocation(anchorView.getRootView(), Gravity.CENTER, 0, 0);
    }
    private void showSelectedImagePopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) pluginContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_image_preview, null);

        final PopupWindow popupWindow = new PopupWindow(popupView,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

        ImageView imageView = popupView.findViewById(R.id.image_preview);
        if (selectedImagePath != null) {
            imageView.setImageURI(Uri.fromFile(new File(selectedImagePath)));
        }

        popupWindow.showAtLocation(anchorView.getRootView(), Gravity.CENTER, 0, 0);
    }

    private void createNewMissionPackage() {
        MapView mapView = MapView.getMapView();
        MissionPackageExportMarshal missionPackageExportMarshal = new MissionPackageExportMarshal(mapView.getContext(), true);
        List<Exportable> exportables = new ArrayList<>();
        exportables.add(new Exportable() {
            @Override
            public boolean isSupported(Class<?> aClass) {
                return true;
            }

            @Override
            public Object toObjectOf(Class<?> aClass, ExportFilters exportFilters) throws FormatNotSupportedException {
                return new MissionPackageExportWrapper(false, "/sdcard/atak/support/support.inf");
            }
        });
        try {
            missionPackageExportMarshal.execute(exportables);
        } catch (Exception e) {
            Log.d(TAG, "Error creating a new data package", e);
        }
    }

    private String generateFileHash(String filePath) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            InputStream fis = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            fis.close();

            byte[] hashBytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

