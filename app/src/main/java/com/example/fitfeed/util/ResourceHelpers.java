package com.example.fitfeed.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;

import androidx.annotation.AnyRes;
import androidx.annotation.NonNull;

/**
 * Code from answer on Stack Overflow.
 * <a href="https://stackoverflow.com/a/36062748">[Link]</a>
 */
public class ResourceHelpers {
    /**
     * get uri to any resource type Via Context Resource instance
     * @param context - context
     * @param resId - resource id
     * @throws Resources.NotFoundException if the given ID does not exist.
     * @return - Uri to resource by given id
     */
    public static Uri getUriToResource(@NonNull Context context,
                                       @AnyRes int resId)
            throws Resources.NotFoundException {
        Resources res = context.getResources();
        return getUriToResource(res,resId);
    }

    /**
     * get uri to any resource type via given Resource Instance
     * @param res - resources instance
     * @param resId - resource id
     * @throws Resources.NotFoundException if the given ID does not exist.
     * @return - Uri to resource by given id
     */
    public static Uri getUriToResource(@NonNull Resources res,
                                             @AnyRes int resId)
            throws Resources.NotFoundException {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + res.getResourcePackageName(resId)
                + '/' + res.getResourceTypeName(resId)
                + '/' + res.getResourceEntryName(resId));
    }
}
