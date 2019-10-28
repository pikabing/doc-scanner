package com.technology.singularium.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class ImageHandler extends AppCompatActivity {
    private static final int REQUEST_STORAGE_PERMISSION = 100;
    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private static Context mContext;
    public static int requestReadPermission(Context context) {

        mContext = context;
        if(PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else return 2;
        return 0;
    }

    public static int requestCameraPermission(Context context) {

        mContext = context;
        if(PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else return 2;
        return 0;
    }
}