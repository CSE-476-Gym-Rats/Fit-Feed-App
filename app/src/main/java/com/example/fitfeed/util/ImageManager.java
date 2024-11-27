package com.example.fitfeed.util;

import android.util.Log;

import com.cloudinary.android.MediaManager;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageManager {

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void uploadImage(String filename) {
        executorService.submit(() -> {
            File file;
            String absPath;
            CloudinaryCallback callback = new CloudinaryCallback();

            try {
                file = new File(filename);
                absPath = file.getAbsolutePath();
            } catch (NullPointerException e) {
                Log.e("ImageManager.uploadImage", "Filename invalid: " + filename, e);
                return null;
            }

            return MediaManager.get()
                    .upload(absPath)
                    .unsigned("unsigned_default")
                    .callback(callback).dispatch();
        });
    }
}
