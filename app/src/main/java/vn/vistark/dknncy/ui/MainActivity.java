
package vn.vistark.dknncy.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import vn.vistark.dknncy.R;
import vn.vistark.dknncy.services.CustomExceptionHandler;
import vn.vistark.dknncy.ui.splash_screen.SplashScreenActivity;
import vn.vistark.dknncy.utils.ScreenUtils;

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
