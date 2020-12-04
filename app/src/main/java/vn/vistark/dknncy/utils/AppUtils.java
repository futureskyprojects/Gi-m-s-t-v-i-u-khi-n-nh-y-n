package vn.vistark.dknncy.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public final class AppUtils {
    private AppUtils() {
        // Không công khai class này
    }

    public static void openPlayStoreForApp(Context context) {
        final String appPackageName = context.getPackageName();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName)));
        }
    }
}
