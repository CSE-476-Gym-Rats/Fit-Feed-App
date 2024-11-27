package com.example.fitfeed.util;

import android.content.Context;

import com.cloudinary.android.MediaManager;

public final class CloudinaryHelper {
    public static void setMediaManager(Context context) {
        MediaManager.init(context);
    }
}
