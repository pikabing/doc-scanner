package com.technology.singularium;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.technology.singularium.api.APIUtils;
import com.technology.singularium.api.FileInfo;
import com.technology.singularium.api.FileService;
import com.technology.singularium.utils.ImageHandler;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class MainActivity extends AppCompatActivity {

    private static int SELECT_FILE = 1001;
    ImageView mainImage;
    FloatingActionButton extract, add;
    TextInputEditText imageText;
    TextInputLayout imageTextLayout;
    MaterialCardView progressBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add = findViewById(R.id.addImages);
        extract = findViewById(R.id.extractText);
        mainImage = findViewById(R.id.imageMain);
        progressBar = findViewById(R.id.bar);
        imageText = findViewById(R.id.imageTextField);
        imageTextLayout = findViewById(R.id.imageTextLayout);

        progressBar.setVisibility(View.GONE);

        disableFab(extract);
        add.setOnClickListener(view -> uploadPhoto());
        extract.setOnClickListener(view -> {
            if(extract.isEnabled()) {

                String filePath;
                if(photoFile == null) {
                    Cursor cursor = this.getContentResolver().query(photoImageUri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
                    cursor.moveToFirst();
                    try {
                        filePath = cursor.getString(0);
                    } catch (Exception e) {
                        filePath = photoImageUri.getPath();
                    }
                    cursor.close();
                    photoFile = new File(filePath);
                }
                progressBar.setVisibility(ProgressBar.VISIBLE);
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), photoFile);
                RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"), photoFile.getName());
                MultipartBody.Part body =MultipartBody.Part.createFormData("file",photoFile.getName(),requestBody);
                FileService fileService = APIUtils.getFileService();
                Call<FileInfo> call = fileService.extractText(body, requestBody1);
                call.enqueue(new Callback<FileInfo>() {
                    @Override
                    public void onResponse(Call<FileInfo> call, Response<FileInfo> response) {
                        String res = response.body().getStatus();
                        if("null".equals(res))
                            res = "No text could be detected in this image";
                        Log.e("res", res);
                        imageText.setText(res);
                        imageTextLayout.setVisibility(TextInputLayout.VISIBLE);
                        progressBar.setVisibility(ProgressBar.GONE);
                    }

                    @Override
                    public void onFailure(Call<FileInfo> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Error occured. Try again.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } else {
                Toast.makeText(this, "Extraction feature disabled.Please upload a photo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void disableFab(FloatingActionButton extract) {
        extract.setEnabled(false);
    }

    public void enableFab(FloatingActionButton extract) {
        extract.setEnabled(true);
    }

    public void uploadPhoto() {
        final String[] options = {"Select from Gallery", "Take photo from Camera", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Image");
        builder.setItems(options, (dialogInterface, i) -> {

            if(i == 0) {
                int permission = ImageHandler.requestReadPermission(MainActivity.this);
                if(permission == 2) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FILE);
                }
            } else if (i == 1) {

                int permission = ImageHandler.requestCameraPermission(MainActivity.this);
                if(permission == 2) {
                    dispatchTakePictureIntent();
                }

            } else {
                dialogInterface.dismiss();
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {

            if(requestCode == REQUEST_TAKE_PHOTO) {
                mainImage.setImageURI(photoImageUri);
            } else if(requestCode == SELECT_FILE) {
                photoImageUri = data.getData();
                mainImage.setImageURI(photoImageUri);
            }
            enableFab(extract);
        }
    }

    String currentPhotoPath;
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private static int REQUEST_TAKE_PHOTO = 1;
    Uri photoImageUri;
    File photoFile = null;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error while processing image", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                photoImageUri = FileProvider.getUriForFile(this,
                        "com.technology.singularium.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoImageUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    boolean backPressed = false;
    @Override
    public void onBackPressed() {
        if(backPressed) {
            finish();
            return;
        }

        backPressed = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> backPressed = false, 2000);
    }
}
