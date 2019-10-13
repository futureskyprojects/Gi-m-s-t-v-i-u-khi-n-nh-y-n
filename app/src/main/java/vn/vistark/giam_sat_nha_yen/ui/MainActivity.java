package vn.vistark.giam_sat_nha_yen.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import org.opencv.android.OpenCVLoader;

import java.io.IOException;
import java.util.List;

import vn.vistark.giam_sat_nha_yen.R;
import vn.vistark.giam_sat_nha_yen.data.arduino_community.ArduinoCommunity;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.DashboardScreenActivity;
import vn.vistark.giam_sat_nha_yen.ui.splash_screen.SplashScreenActivity;
import vn.vistark.giam_sat_nha_yen.utils.ScreenUtils;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScreenUtils.hideTitleBarAndTransparentStatusBar(this);
        startSplashScreen();
    }


    // Phương thức khởi chạy splash Screen
    void startSplashScreen() {
        Intent splashScreenIntent = new Intent(MainActivity.this, SplashScreenActivity.class);
        startActivity(splashScreenIntent);
        MainActivity.this.finish();
    }

    // Phương thức khởi chạy dashboard screen
    void startDashboardScreen() {
        Intent dashboardScreenIntent = new Intent(MainActivity.this, DashboardScreenActivity.class);
        startActivity(dashboardScreenIntent);
        MainActivity.this.finish();
    }
}
