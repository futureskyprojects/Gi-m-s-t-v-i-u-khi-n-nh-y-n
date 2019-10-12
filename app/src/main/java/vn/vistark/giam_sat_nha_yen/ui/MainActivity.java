package vn.vistark.giam_sat_nha_yen.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.physicaloid.lib.Physicaloid;

import org.opencv.android.OpenCVLoader;

import vn.vistark.giam_sat_nha_yen.R;
import vn.vistark.giam_sat_nha_yen.data.arduino_community.ArduinoCommunity;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.DashboardScreenActivity;
import vn.vistark.giam_sat_nha_yen.ui.splash_screen.SplashScreenActivity;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initOpenCV();
        ArduinoCommunity.init(this);
        //startSplashScreen();
        startDashboardScreen();
    }

    private void initOpenCV() {
//        if (!OpenCVLoader.initDebug()) {
//            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), không hoạt động.");
//        } else {
//            Log.i(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), đã hoạt động.");
//        }
    }

    // Phương thức khởi chạy splash Screen
    void startSplashScreen() {
        Intent splashScreenIntent = new Intent(MainActivity.this, SplashScreenActivity.class);
        startActivity(splashScreenIntent);
    }

    // Phương thức khởi chạy dashboard screen
    void startDashboardScreen() {
        Intent dashboardScreenIntent = new Intent(MainActivity.this, DashboardScreenActivity.class);
        startActivity(dashboardScreenIntent);
    }
}
