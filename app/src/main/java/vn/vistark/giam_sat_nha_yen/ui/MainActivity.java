
package vn.vistark.giam_sat_nha_yen.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import vn.vistark.giam_sat_nha_yen.R;
import vn.vistark.giam_sat_nha_yen.services.CustomExceptionHandler;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.DashboardScreenActivity;
import vn.vistark.giam_sat_nha_yen.ui.splash_screen.SplashScreenActivity;
import vn.vistark.giam_sat_nha_yen.utils.ScreenUtils;

/**
 * Project ĐK Nhà Yến
 * Created by Nguyễn Trọng Nghĩa on 10/19/2019.
 * Organization: Vistark Team
 * Email: dev.vistark@gmail.com
 */
public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this));
        ScreenUtils.hideTitleBarAndTransparentStatusBar(this);
        startSplashScreen();
    }


    // Phương thức khởi chạy splash Screen
    void startSplashScreen() {
        Intent splashScreenIntent = new Intent(MainActivity.this, SplashScreenActivity.class);
        startActivity(splashScreenIntent);
        MainActivity.this.finish();
    }
}
