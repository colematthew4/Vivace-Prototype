package cole.matthew.vivace;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

public abstract class VivacePermissionCodes
{
    public static final int RECORD_AUDIO = 73228346;    // "RECAUDIO" from phone keypad
    public static final int INTERNET = 46837638;        // "INTERNET" from phone keypad

    /**
     * Check if the given Context has been granted the given permission.
     * @param context The Context to check if the permission has been granted for.
     * @param permission The permission to check that has been granted to the Context.
     * @return true if the permission has been granted, false if not.
     */
    public static boolean HasPermission(Context context, String permission)
    {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
