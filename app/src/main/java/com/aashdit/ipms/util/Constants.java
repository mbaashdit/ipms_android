package com.aashdit.ipms.util;

import android.content.Intent;

/**
 * Created by Manabendu on 28/05/20
 */
public class Constants {

    public static final String API_PATH = "api/";

    public static String PLAN = "Plan";
    public static String PROGRESS = "Progress";

//    public static String BASE_URL = "http://103.148.157.194:8080/api/";//prod
    public static String BASE_URL = "http://209.97.136.18:8080/ipms/api/";//staging

    //"http://192.168.3.190:4040/ipms/api/";//
//    public static String BASE_URL = "http://209.97.136.18:8080/ipms_app/api/";

    public static String APP_LOGIN = "is_login";
    public static String PREF_APP = "pref_ipms";
    public static String APP_TOKEN = "token";

    public static String USER_NAME = "userName";
    public static String USER_FIRST_NAME = "firstName";
    public static String USER_PASSWORD = "password";
    public static String USER_LAST_NAME = "lastName";
    public static String USER_EMAIL = "email";
    public static String USER_MOBILE = "mobile";
    public static String USER_ROLE_CODE = "roleCode";
    public static String USER_ROLE_DESCRIPTION = "roleDescription";
    public static String AGENCY_ID = "agencyId";
    public static String USER_ID = "userId";
    public static String ESD = "PROJ_EST_START_DATE";
    public static String EED = "PROJ_EST_END_DATE";
    public static String PROJECT_ID = "PROJECT_ID";
    public static String PROJECT_NAME = "PROJECT_NAME";

    public static String TEMP_USER_ID = "tempuserId";



    /**
     * For restricted files
     */
    public static final String DOC = "application/msword";
    public static final String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String IMAGE = "image/*";
    public static final String AUDIO = "audio/*";
    public static final String TEXT = "text/*";
    public static final String PDF = "application/pdf";
    public static final String XLS = "application/vnd.ms-excel";
    public static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String PPT = "application/vnd.ms-powerpoint";
    public static final String PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";


    public static Intent getCustomFileChooserIntent(String... types) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // Filter to only show results that can be "opened"
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, types);
        return intent;
    }


}
