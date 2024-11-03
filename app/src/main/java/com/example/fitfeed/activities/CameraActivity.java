package com.example.fitfeed.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fitfeed.R;
import com.example.fitfeed.models.Post;
import com.example.fitfeed.fragments.FeedFragment;

import java.io.File;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 23;
    private final String FILENAME = "photo";
    private File imageFile;
    private ImageView imageView;
    private boolean fileError;

    public static final int RESULT_OK = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camera);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainCameraContainer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // find take picture button and set listener
        Button takePictureButton = findViewById(R.id.cameraActivityTakePictureButton);
        takePictureButton.setOnClickListener(this::openCamera);

        // find take picture button and set listener
        Button postButton = findViewById(R.id.cameraActivityPostButton);
        postButton.setOnClickListener(this::savePost);

        // find image view
        imageView = findViewById(R.id.cameraActivityImageView);
        if (imageView.getTag() == null) {
            imageView.setTag(R.drawable.ic_launcher_foreground);
        }

        // handle image file creation if needed
        if (imageFile == null) {
            // check for file system permissions and handle error gracefully
            try {
                imageFile = getImageFile(FILENAME);
                fileError = false;
            } catch (IOException e) {
                Toast.makeText(this, getString(R.string.camera_file_error), Toast.LENGTH_SHORT).show();
                fileError = true;
            }
        }
    }


    private File getImageFile(String filename) throws IOException {
        return File.createTempFile(filename, ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
    }

    private void bitmapFromImageFile() {
        Bitmap image = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        imageView.setImageBitmap(image);
        imageView.setTag(imageFile.hashCode());
    }


    /**
     * On click listener for Take Picture button.
     * @param view context of click event.
     */
    private void openCamera(View view) {
        // don't attempt to take picture if file permissions arent granted
        if (!fileError) {
            // intent targets system camera application
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // use file provider to allow for higher resolution images
            Uri fileProvider = FileProvider.getUriForFile(this, "com.example.fitfeed.fileprovider", imageFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

            // handle errors opening camera (no permissions, no camera)
            try {
                int numCameras = ((CameraManager)getSystemService(Context.CAMERA_SERVICE)).getCameraIdList().length;
                if (cameraIntent.resolveActivity(getPackageManager()) != null && numCameras > 0) {
                    startActivityForResult(cameraIntent, REQUEST_CODE);
                } else {
                    Toast.makeText(this, getString(R.string.camera_error), Toast.LENGTH_SHORT).show();
                }
            } catch (CameraAccessException e) {
                Toast.makeText(this, getString(R.string.camera_error), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.camera_file_error), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * On click listener for Post button.
     * @param view context of click event.
     */
    private void savePost(View view) {
        Intent postIntent = new Intent(view.getContext(), FeedFragment.class);
        EditText editText = findViewById(R.id.cameraActivityEditText);
        String filename = ((Integer) imageView.getTag() != R.drawable.ic_launcher_foreground) ? imageFile.getAbsolutePath() : null;

        postIntent.putExtra("post", new Post(editText.getText().toString(), "holtster2000", filename, null));

        setResult(CameraActivity.RESULT_OK, postIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // called after returning from camera application, check request and result codes for success
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            bitmapFromImageFile();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // save file and bitmap to bundle
        outState.putString("cameraActivityCachedImageFilename", imageFile.getAbsolutePath());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // reload file from bundle and set bitmap if exists
        String restoredFilename = savedInstanceState.getString("cameraActivityCachedImageFilename");
        if (restoredFilename != null) {
            File restoredFile = new File(restoredFilename);
            if (restoredFile.exists()) {
                imageFile = new File(restoredFilename);
                bitmapFromImageFile();
            }
        }
    }
}