package cole.matthew.vivace.Helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import cole.matthew.vivace.Activities.MainActivity;
import cole.matthew.vivace.R;

public abstract class VivacePermissions {
    /**
     * Requests all the permissions required by the application.
     *
     * @param activity The activity to request the permissions for.
     */
    public static void requestAllPermissions(Activity activity) {
        requestPermission(activity, VivacePermissionCodes.RECORD_AUDIO);
        requestPermission(activity, VivacePermissionCodes.INTERNET);
        requestPermission(activity, VivacePermissionCodes.READ_EXTERNAL_STORAGE);
        requestPermission(activity, VivacePermissionCodes.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * Check if the given Context has been granted the given permission.
     *
     * @param context    The Context to check if the permission has been granted for.
     * @param permission The permission to check that has been granted to the Context.
     *
     * @return true if the permission has been granted, false if not.
     */
    public static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests permissions for the given activity.
     *
     * @param activity       The activity we're requesting permissions for.
     * @param permissionCode Application specific request code to match with a result reported to
     *                       {@link ActivityCompat.OnRequestPermissionsResultCallback#onRequestPermissionsResult(int, String[], int[])}.
     *                       Should be >= 0.
     *
     * @return true if the activity has been given the requested permission, false if not.
     */
    public static boolean requestPermission(Activity activity, int permissionCode) {
        boolean result = true;

        switch (permissionCode) {
            case VivacePermissionCodes.RECORD_AUDIO:
                if (!hasPermission(activity, Manifest.permission.RECORD_AUDIO)) {
                    requestPermission(activity, Manifest.permission.RECORD_AUDIO, permissionCode, R.string.audio_permissions_explanation);
                    result = hasPermission(activity, Manifest.permission.RECORD_AUDIO);
                } else {       // permission is already granted
                    Log.d(MainActivity.APPLICATION_TAG, "Audio Recording permission granted.");
                }

                break;
            case VivacePermissionCodes.INTERNET:
                if (!hasPermission(activity, Manifest.permission.INTERNET)) {
                    requestPermission(activity, Manifest.permission.INTERNET, permissionCode, R.string.internet_permissions_explanation);
                    result = hasPermission(activity, Manifest.permission.INTERNET);
                } else {       // permission is already granted
                    Log.d(MainActivity.APPLICATION_TAG, "Internet permission granted.");
                }

                break;
            case VivacePermissionCodes.READ_EXTERNAL_STORAGE:
                if (!hasPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    requestPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, permissionCode, R.string.external_storage_permissions_explanation);
                    result = hasPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {       // permission is already granted
                    Log.d(MainActivity.APPLICATION_TAG, "Read External Storage permission granted.");
                }

                break;
            case VivacePermissionCodes.WRITE_EXTERNAL_STORAGE:
                if (!hasPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requestPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, permissionCode, R.string.external_storage_permissions_explanation);
                    result = hasPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {       // permission is already granted
                    Log.d(MainActivity.APPLICATION_TAG, "Write External Storage permission granted.");
                }

                break;
            default:
                result = false;
                break;
        }

        return result;
    }

    /**
     * Requests the given permission for the activity.
     *
     * @param activity       The activity we're requesting permissions for.
     * @param permission     The permission we're requesting.
     * @param permissionCode Application specific request code to match with a result reported to
     *                       {@link ActivityCompat.OnRequestPermissionsResultCallback#onRequestPermissionsResult(int, String[], int[])}.
     *                       Should be >= 0.
     * @param explanationID  The ID of the string resource to show if {@link ActivityCompat#shouldShowRequestPermissionRationale(Activity, String)}
     *                       returns true.
     */
    private static void requestPermission(Activity activity, String permission, int permissionCode, @StringRes int explanationID) {
        // Show an explanation to the user *asynchronously* -- don't block this thread waiting
        // for the user's response! After the user sees the explanation, try again to request
        // the permission.
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            Snackbar.make(activity.findViewById(R.id.activity_layout), explanationID, Snackbar.LENGTH_LONG).show();
        } else {               // No explanation needed; request the permission
            ActivityCompat.requestPermissions(activity, new String[] { permission }, permissionCode);
        }
    }
}
