package com.example.fitfeed.util;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.ListenerService;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class CloudinaryListener extends ListenerService {

    public static final String ACTION_SNACKBAR = "EVENT_SNACKBAR";
    public static final String ACTION_URL = "EVENT_CLOUDINARY_URL";

    public static final String EXTRA_START_MESSAGE = "CloudinaryListener.onStart";
    public static final String EXTRA_PROGRESS_MESSAGE = "CloudinaryListener.onProgress.message";
    public static final String EXTRA_PROGRESS_VALUE = "CloudinaryListener.onProgress.value";
    public static final String EXTRA_SUCCESS_MESSAGE = "CloudinaryListener.onSuccess";
    public static final String EXTRA_SUCCESS_URL = "CloudinaryListener.onSuccess.url";
    public static final String EXTRA_ERROR_MESSAGE = "CloudinaryListener.onError";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void doSendBroadcast(String action, String type, String message) {
        Intent it = new Intent(action);
        it.putExtra(type, message);
        Log.d("doSendBroadcast", "sending broadcast message = " + message);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(it);
        Log.d("doSendBroadcast", "sent!");
    }

    private void doSendBroadcastWithProgress(String message, int progress) {
        Intent it = new Intent("EVENT_SNACKBAR");
        it.putExtra(EXTRA_PROGRESS_MESSAGE, message);
        it.putExtra(EXTRA_PROGRESS_VALUE, progress);
        Log.d("doSendBroadcast", "sending broadcast message = " + message + ", value = " + progress);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(it);
        Log.d("doSendBroadcast", "sent!");
    }

    @Override
    public void onStart(String requestId) {
        doSendBroadcast(ACTION_SNACKBAR, EXTRA_START_MESSAGE, "Uploading: 0%");
    }

    @Override
    public void onProgress(String requestId, long bytes, long totalBytes) {
        NumberFormat percentFormatter = NumberFormat.getPercentInstance();
        double progress = (double) bytes / totalBytes;
        doSendBroadcastWithProgress(
                String.format(
                        Locale.getDefault(),
                        "Uploading: %s",
                        percentFormatter.format(progress)),
                (int) (progress * 100)
        );
    }

    @Override
    public void onSuccess(String requestId, Map resultData) {
        doSendBroadcast(ACTION_SNACKBAR, EXTRA_SUCCESS_MESSAGE, "Upload done!");
        doSendBroadcast(ACTION_URL, EXTRA_SUCCESS_URL, (String) resultData.get("secure_url"));
    }

    @Override
    public void onError(String requestId, ErrorInfo error) {
        doSendBroadcast(ACTION_SNACKBAR, EXTRA_ERROR_MESSAGE, "Upload failed.");
    }

    @Override
    public void onReschedule(String requestId, ErrorInfo error) {

    }
}
