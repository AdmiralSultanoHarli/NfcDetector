package com.miral.nfcdetector.constants;

public class AppConstants {

    //Database name
    public static final String DATABASE_NAME = "nfcdetector.realm";
    public static final int REALM_AES_KEY_LENGTH = 64;

    public static final String APP_TAG = "MIRAL_NFC";

    public static final byte[] SELECT_OK = Util.hexStringToByteArray("9000");

    public static final String EMPTY_STRING = "";
    public static final String SINGLE_SPACE = " ";
    public static final String SEPARATOR = "/";
    public static final String SINGLE_SPACE_WITH_SEPARATOR = SINGLE_SPACE + SEPARATOR + SINGLE_SPACE;

}
