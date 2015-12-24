package com.dailyselfie.utils;

/**
 * Class that contains all the Constants required in our Selfie Upload
 * client App.
 */
public class Constants {
    /**
     * URL of the VideoWebService.  Please Read the Instructions in
     * README.md to set up the SERVER_URL.
     */
//    public static final String SERVER_URL =
//            "https://192.168.0.8:8443";

    public static final String SERVER_URL =
            "https://192.168.56.1:8443";

    /**
     * Define a constant for 1 MB.
     */
    public static final long MEGA_BYTE = 1024 * 1024;

    /**
     * Maximum size of Selfie to be uploaded in MB.
     */
    public static final long MAX_SIZE_MEGA_BYTE = 50 * MEGA_BYTE;
}
