package vn.vistark.giam_sat_nha_yen.ui.splash_screen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import vn.vistark.giam_sat_nha_yen.R;
import vn.vistark.giam_sat_nha_yen.services.CustomExceptionHandler;
import vn.vistark.giam_sat_nha_yen.ui.BaseAppCompatActivity;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.DashboardScreenActivity;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.video_transfer.VideoTransfer;
import vn.vistark.giam_sat_nha_yen.utils.PermissionUtils;
import vn.vistark.giam_sat_nha_yen.utils.ScreenUtils;

/**
 * Project ĐK Nhà Yến
 * Created by Nguyễn Trọng Nghĩa on 10/19/2019.
 * Organization: Vistark Team
 * Email: dev.vistark@gmail.com
 */

public class SplashScreenActivity extends AppCompatActivity implements BaseAppCompatActivity {
    private final static String TAG = SplashScreenActivity.class.getSimpleName();

    private TextView tvInitStateTextShow;

    private SplashScreenPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this));
        ScreenUtils.hideTitleBarAndTransparentStatusBar(this);
        initViews();
        initEvents();
        initPresenters();
    }

    @Override
    public void initViews() {
        tvInitStateTextShow = findViewById(R.id.initStateTextShow);
    }

    public void updateInitStateTextShowView(final String s) {
        tvInitStateTextShow.post(new Runnable() {
            @Override
            public void run() {
                tvInitStateTextShow.setText(s);
            }
        });
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
                SplashScreenActivity.this.finish();
            }
        });
    }

    // Hàm tiếp nhận kết quả kiểm tra quyền

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.isGrantedAll(requestCode, grantResults)) { // Nếu tất cả các quyền đã được cấp thành công
            // Tiến hành khởi động
            mPresenter.initStarting();
        } else {
            // Không thì yêu cầu cấp lại cho bằng được trước khi khởi động
            PermissionUtils.RequestAllPermission(this);

        }
    }
}
