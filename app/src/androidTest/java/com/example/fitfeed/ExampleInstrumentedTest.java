package com.example.fitfeed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.fitfeed", appContext.getPackageName());
    }

    @Ignore("for manual testing")
    @Test
    public void cloudinaryUploadTest() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream os = new PrintStream(baos, true);

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        MediaManager.init(appContext);

        String requestId = MediaManager.get()
                .upload(drawableToFile(appContext, R.drawable.placeholder3).getAbsolutePath())
                .unsigned("unsigned_default")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) { os.println("Upload begin with id = " + requestId); }
                    @Override
                    public void onSuccess(String requestId, Map resultData) { System.out.println(resultData.toString()); }
                    @Override
                    public void onError(String requestId, ErrorInfo error) { System.out.println(error.toString()); }
                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) { System.out.println(error.toString()); }
                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        Double progress = (double) bytes/totalBytes;
                        os.println("Upload progress = " + progress);
                    }
                }).dispatch();

        for (int i = 0; i < 20 && !baos.toString().contains("Upload progress = 1.0"); i ++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        assertTrue(baos.toString().contains("Upload progress = 1.0"));
    }

    private File drawableToFile(Context c, int resId) throws IOException {
        File tempFileDir = c.getCacheDir();
        File tempFile = File.createTempFile("test", ".jpg", tempFileDir);
        Bitmap bm = BitmapFactory.decodeResource(c.getResources(), resId);
        try {
            FileOutputStream out = new FileOutputStream(tempFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return tempFile;
    }
}