package vn.vistark.giam_sat_nha_yen.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Project ĐK Nhà Yến
 * Packagename: vn.vistark.giam_sat_nha_yen.utils
 * Created by Nguyễn Trọng Nghĩa on 10/19/2019.
 * Organization: Vistark Team
 * Email: dev.vistark@gmail.com
 */

public class PermissionUtils {
    private final static String TAG = PermissionUtils.class.getSimpleName();

    public static void RequestAllPermission(AppCompatActivity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.requestPermissions(AppPermission.getStringsPermission(), AppPermission.TOTAL_PERMISSION_REQUEST_CODE);
        }
    }

    public static boolean isGrantedAll(int requestCode, int[] grantResults) {
        if (requestCode == AppPermission.TOTAL_PERMISSION_REQUEST_CODE) {
            int accepted = 0;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    accepted++;
                }
            }
            return accepted >= AppPermission.getListInstances().size();
        }
        return false;
    }

    private static class AppPermission {
        final static int TOTAL_PERMISSION_REQUEST_CODE = 140398;

        static String[] getStringsPermission() {
            List<AppPermission> appPermissions = getListInstances();
            String[] permissions = new String[appPermissions.size()];
            for (int i = 0; i < appPermissions.size(); i++) {
                permissions[i] = appPermissions.get(i).getKey();
            }
            return permissions;
        }

        static List<AppPermission> getListInstances() {
            List<AppPermission> appPermissions = new ArrayList<>();
            int permissionCode = 1000;
            appPermissions.add(new AppPermission(permissionCode++, Manifest.permission.ACCESS_NETWORK_STATE, ""));
            appPermissions.add(new AppPermission(permissionCode++, Manifest.permission.INTERNET, ""));
            appPermissions.add(new AppPermission(permissionCode++, Manifest.permission.READ_EXTERNAL_STORAGE, ""));
            appPermissions.add(new AppPermission(permissionCode++, Manifest.permission.WRITE_EXTERNAL_STORAGE, ""));
            appPermissions.add(new AppPermission(permissionCode++, Manifest.permission.CAMERA, ""));
            appPermissions.add(new AppPermission(permissionCode++, Manifest.permission.VIBRATE, ""));
            appPermissions.add(new AppPermission(permissionCode, Manifest.permission.WAKE_LOCK, ""));

            return appPermissions;
        }

        private int code = -1;
        private String key = "";
        private String description = "";

        AppPermission(int code, String key, String description) {
            this.code = code;
            this.key = key;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
