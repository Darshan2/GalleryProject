package com.darshan.android.fileexplorer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SelectMediaActivity extends AppCompatActivity {
    private static final String TAG = "SelectMediaActivity";
    private static final int REQUEST_STORAGE_PERMISSION = 20120 ;
    private RelativeLayout mMediaRL;
    private static final int GALLERY_ACTIVITY_REQUEST_CODE = 13025;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_media);

        final EditText mediaTypeET = findViewById(R.id.mediaType_ET);
        Button submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mediaType = mediaTypeET.getText().toString();
                String mediaConst = "";
                switch (mediaType) {
                    case "1" : mediaConst = GalleryConsts.IMAGE_TYPE; break;
                    case "2" : mediaConst = GalleryConsts.VIDEO_TYPE; break;
                    case "3" : mediaConst = GalleryConsts.IMAGE_VIDEO_TYPE; break;
                    default:break;
                }

                loadFoldersOfMedia(mediaConst);
            }
        });

        checkPermissions();

    }

    private void loadFoldersOfMedia(String mediaType) {
        Intent intent = new Intent(this, GalleryActivity.class);
        intent.putExtra(GalleryConsts.INTENT_MEDIA_TYPE, mediaType);
        startActivityForResult(intent, GALLERY_ACTIVITY_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == GALLERY_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<Image> selectedImages = intent.getParcelableArrayListExtra(GalleryConsts.INTENT_SELECT_GALLERY_ITEMS);
            for(Image image : selectedImages) {
                Log.d(TAG, "onActivityResult: " + image);
            }
        }
    }

    private void checkPermissions() {
        Log.d(TAG, "checkPermissions: ");
        // Check for the external storage permission
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)  {

            // If you do not have permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            //Permission is granted proceed
            startToGetMediaFilesInBackGround();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, proceed
                    startToGetMediaFilesInBackGround();

                } else {
                    // If you do not get permission, show a Toast and exit from app.
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }
        }
    }


    private void startToGetMediaFilesInBackGround() {
        //Start to get all files in folder in background thread
        GalleryLoader.getInstance()
                .startLoadingImages(getApplicationContext(), getContentResolver());
    }
}
