package vn.vistark.giam_sat_nha_yen.ui.splash_screen;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import vn.vistark.giam_sat_nha_yen.R;
import vn.vistark.giam_sat_nha_yen.ui.BaseAppCompatActivity;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.DashboardScreenActivity;
import vn.vistark.giam_sat_nha_yen.utils.ScreenUtils;

public class SplashScreenActivity extends AppCompatActivity implements BaseAppCompatActivity {
    private final static String TAG = SplashScreenActivity.class.getSimpleName();

    private SplashScreenPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ScreenUtils.hideTitleBarAndTransparentStatusBar(this);
        initViews();
        initEvents();
        initPresenters();
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initEvents() {

    }

    @Override
    public void initPresenters() {
        mPresenter = new SplashScreenPresenter(this);
    }

    // Khởi chạy giao diện màn hình điều khiển
    void startDashboardActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent dashboardScreenIntent = new Intent(SplashScreenActivity.this, DashboardScreenActivity.class);
                startActivity(dashboardScreenIntent);
            }
        });
    }
}
