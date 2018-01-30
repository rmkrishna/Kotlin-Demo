package com.muthu.salesmanager.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthukrishnan on 19/01/17.
 */

public final class PermissionUtil {

    public static final int CONTACT_REQUEST_CODE = 501;
    public static final int CALL_PERMISSION_REQUEST_CODE = 502;

    public static final int PHOTO_PERMISSION_REQUEST_CODE = 503;
    public static final int CONVERSATION_PHOTO_PERMISSION_REQUEST_CODE = 504;
    public static final int CONVERSATION_AUDIO_PERMISSION_REQUEST_CODE = 505;
    public static final int CONVERSATION_CONTACT_PERMISSION_REQUEST_CODE = 506;
    public static final int CONVERSATION_LOCATION_PERMISSION_REQUEST_CODE = 507;
    public static final int HOME_SCREEN_EXTERNAL_STORAGE = 508;
    public static final int UTIL_CALL_PERMISSION_REQUEST_CODE = 509;

    private PermissionUtil() {

    }

    public static List<String> getAllPermissions(Context context) {

        List<String> listPermissionsNeeded = new ArrayList<String>();

        if (!hasPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!hasPermission(context, Manifest.permission.RECORD_AUDIO)) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }

        if (!hasPermission(context, Manifest.permission.READ_CONTACTS)) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }

        if (!hasPermission(context, Manifest.permission.CAMERA)) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (!hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        return listPermissionsNeeded;
    }

    public static List<String> getPhotoPermissions(Context context) {

        List<String> listPermissionsNeeded = new ArrayList<String>();

        if (!hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!hasPermission(context, Manifest.permission.CAMERA)) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        return listPermissionsNeeded;
    }

    public static List<String> getCallPermissions(Context context) {

        List<String> listPermissionsNeeded = new ArrayList<String>();

        if (!hasPermission(context, Manifest.permission.RECORD_AUDIO)) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }

        if (!hasPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        return listPermissionsNeeded;
    }

    public static List<String> getRecordAudioPermissions(Context context) {

        List<String> listPermissionsNeeded = new ArrayList<String>();

        if (!hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!hasPermission(context, Manifest.permission.RECORD_AUDIO)) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }

        return listPermissionsNeeded;
    }

    public static List<String> askInitScreenPermissions(Activity activity) {

        List<String> listPermissionsNeeded = new ArrayList<String>();

        if (!hasPermission(activity, Manifest.permission.READ_CONTACTS)) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }

        if (!hasPermission(activity, Manifest.permission.WRITE_CONTACTS)) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_CONTACTS);
        }

        if (!hasPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        return listPermissionsNeeded;
    }

    public static boolean askInitScreenPermissions(Activity activity, int requestCode) {

        List<String> listPermissionsNeeded = askInitScreenPermissions(activity);

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), requestCode);

            return true;
        }

        return false;
    }

    public static void getAllPermissions(Activity activity, int requestCode) {

        List<String> listPermissionsNeeded = getAllPermissions(activity);

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), requestCode);
        }
    }

    public static boolean hasCameraPermission(Activity activity, int requestCode) {
        if (PermissionUtil.hasPermissions(activity, Manifest.permission.CAMERA)) {
            return true;
        } else {
            if (requestCode != -1) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, requestCode);
            }
        }
        return false;
    }

    public static boolean hasReadSMSPermission(Activity activity, int requestCode) {
        if (PermissionUtil.hasPermissions(activity, Manifest.permission.READ_SMS)) {
            return true;
        } else {
            if (requestCode != -1) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_SMS}, requestCode);
            }
        }
        return false;
    }

    public static boolean hasLocationAccessPermission(Context activity) {
        return PermissionUtil.hasPermissions(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                && PermissionUtil.hasPermissions(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public static boolean hasLocationAccessPermission(Activity activity, int requestCode) {
        if (PermissionUtil.hasPermissions(activity, Manifest.permission.ACCESS_FINE_LOCATION) && PermissionUtil.hasPermissions(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            return true;
        } else {
            if (requestCode != -1) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
            }
        }
        return false;
    }


    public static boolean hasExternalStoragePermission(Activity activity, int requestCode) {
        if (PermissionUtil.hasPermissions(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return true;
        } else {
            if (requestCode != -1) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
            }
        }
        return false;
    }

    public static boolean hasExternalStoragePermission(Context activity) {
        return PermissionUtil.hasPermissions(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static boolean hasContactsReadPermission(Context activity) {
        return PermissionUtil.hasPermissions(activity, Manifest.permission.READ_CONTACTS)
                && PermissionUtil.hasPermissions(activity, Manifest.permission.WRITE_CONTACTS);
    }

    public static boolean hasContactsReadPermission(Activity activity, int requestCode) {
        if (PermissionUtil.hasPermissions(activity, Manifest.permission.READ_CONTACTS)
                && PermissionUtil.hasPermissions(activity, Manifest.permission.WRITE_CONTACTS)) {
            return true;
        } else {
            if (requestCode != -1) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, requestCode);
            }
        }
        return false;
    }

    public static boolean hasRecordAudioPermission(Activity activity, int requestCode) {
        if (PermissionUtil.hasPermissions(activity, Manifest.permission.RECORD_AUDIO)) {
            return true;
        } else {
            if (requestCode != -1) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, requestCode);
            }
        }
        return false;
    }

    public static boolean hasPermissionReadPhoneState(Activity activity, int requestCode) {
        if (PermissionUtil.hasPermissions(activity, Manifest.permission.READ_PHONE_STATE)) {
            return true;
        } else {
            if (requestCode != -1) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, requestCode);
            }
        }
        return false;
    }

    /**
     * @param context
     * @param permissions
     *
     * @return
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (!hasPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param context
     * @param permission
     *
     * @return
     */
    private static boolean hasPermission(Context context, String permission) {
        try {
            int hasPermission = ContextCompat.checkSelfPermission(context, permission);

            return hasPermission == PackageManager.PERMISSION_GRANTED;
        } catch (RuntimeException e) {

            return false;
        }
    }

    /**
     * @param grantResults
     *
     * @return
     */
    public static boolean verifyPermissions(int... grantResults) {
        if (grantResults.length == 0) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
