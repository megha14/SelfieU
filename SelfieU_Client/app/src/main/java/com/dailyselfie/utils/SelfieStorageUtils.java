package com.dailyselfie.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.client.Response;

/**
 * Helper class that contains methods to store and get Selfie
 * from Android Local Storage.
 */
public class SelfieStorageUtils {


    /**
     * Make SelfieStorageUtils a utility class by preventing instantiation.
     */
    private SelfieStorageUtils() {
        throw new AssertionError();
    }

    /**
     * Create a file Uri for saving a recorded selfie
     */
    @SuppressLint("SimpleDateFormat")
    public static Uri getRecordedSelfieUri(Context context) {

        // Check to see if external SDCard is mounted or not.
        if (isExternalStorageWritable()) {
            // Create a path where we will place our recorded selfie in
            // the user's public DCIM directory. Note that you should
            // be careful about what you place here, since the user
            // often manages these files.
            final File selfieStorageDir =
                    Environment.getExternalStoragePublicDirectory
                            (Environment.DIRECTORY_DCIM);

            // Create the storage directory if it does not exist
            if (!selfieStorageDir.exists()) {
                if (!selfieStorageDir.mkdirs()) {
                    return null;
                }
            }

            // Create a TimeStamp for the selfie file.
            final String timeStamp =
                    new SimpleDateFormat("MMM dd, yyyy_HH:mm:ss").format(new Date());


            // Create a selfie file name from the TimeStamp.
            final File selfieFile =
                    new File(selfieStorageDir.getPath() + File.separator
                            + timeStamp + ".jpg");

            // Always notify the MediaScanners after storing
            // the Selfie, so that it is immediately available to
            // the user.
            notifyMediaScanners(context, selfieFile);

            //Return Uri from Selfie file.
            return Uri.fromFile(selfieFile);

        } else
            //Return null if no SDCard is mounted.
            return null;
    }

    /**
     * Stores the Selfie in External Downloads directory in Android.
     */
    public static File storeSelfieInExternalDirectory(Context context,
                                                      Response response,
                                                      String selfieName) {
        // Try to get the File from the Directory where the Selfie
        // is to be stored.
        final File file =
                getSelfieStorageDir(selfieName);
        if (file != null) {
            try {
                // Get the InputStream from the Response.
                final InputStream inputStream =
                        response.getBody().in();

                // Get the OutputStream to the file
                // where Selfie data is to be written.
                final OutputStream outputStream =
                        new FileOutputStream(file);

                // Write the Selfie data to the File.
                IOUtils.copy(inputStream,
                        outputStream);

                // Close the streams to free the Resources used by the
                // stream.
                outputStream.close();
                inputStream.close();

                // Always notify the MediaScanners after Downloading
                // the Selfie, so that it is immediately available to
                // the user.
                notifyMediaScanners(context,
                        file);
                return file;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Notifies the MediaScanners after Downloading the Selfie, so it
     * is immediately available to the user.
     */
    private static void notifyMediaScanners(Context context,
                                            File selfieFile) {
        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile
                (context,
                        new String[]{selfieFile.toString()},
                        null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path,
                                                        Uri uri) {
                            }
                        });
    }

    /**
     * Checks if external storage is available for read and write.
     *
     * @return True-If the external storage is writable.
     */
    private static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals
                (Environment.getExternalStorageState());
    }

    /**
     * Get the External Downloads Directory to
     * store the Selfies.
     *
     * @param selfieName
     */
    private static File getSelfieStorageDir(String selfieName) {
        //Check to see if external SDCard is mounted or not.
        if (isExternalStorageWritable()) {
            // Create a path where we will place our selfie in the
            // user's public Downloads directory. Note that you should be
            // careful about what you place here, since the user often
            // manages these files.
            final File path =
                    Environment.getExternalStoragePublicDirectory
                            (Environment.DIRECTORY_DOWNLOADS);
            final File file = new File(path,
                    selfieName);
            // Make sure the Downloads directory exists.
            path.mkdirs();
            return file;
        } else {
            return null;
        }
    }
}
