package map.cheena.kz.mapdirection.utils;

import android.content.Context;
import android.content.pm.PackageManager;

public class Utility {
    public static boolean checkGPSPermission(Context context) {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}