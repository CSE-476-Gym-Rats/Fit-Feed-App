package com.example.fitfeed.util;

import android.util.Log;

import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.text.NumberFormat;
import java.util.Map;

public class CloudinaryCallback implements UploadCallback {

    @Override
    public void onStart(String requestId) {
        Log.d("CloudinaryCallback.onStart", "Upload began. id: " + requestId);
    }

    @Override
    public void onProgress(String requestId, long bytes, long totalBytes) {
        NumberFormat percentFormatter = NumberFormat.getPercentInstance();
        double progress = (double) bytes / totalBytes;
        Log.d("ImageManager.uploadImage", String.format("[%s] Upload progress: %s", requestId, percentFormatter.format(progress)));
    }

    @Override
    public void onSuccess(String requestId, Map resultData) {
        Log.d("CloudinaryCallback.onSuccess", "Upload succeeded! Result Data: " + resultData.toString());
        Log.d("CloudinaryCallback.onSuccess", "Url = " + resultData.get("secure_url"));
    }

    @Override
    public void onError(String requestId, ErrorInfo error) {
        Log.e("CloudinaryCallback.onError", "Error on upload. Error: " + error.toString());
    }

    @Override
    public void onReschedule(String requestId, ErrorInfo error) {
        Log.d("CloudinaryCallback.onReschedule", "Rescheduling upload. Error: " + error.toString());
    }
}
