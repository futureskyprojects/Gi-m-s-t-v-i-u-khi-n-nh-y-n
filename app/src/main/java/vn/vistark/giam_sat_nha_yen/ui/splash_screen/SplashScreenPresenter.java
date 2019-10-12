package vn.vistark.giam_sat_nha_yen.ui.splash_screen;

import java.util.Timer;
import java.util.TimerTask;

class SplashScreenPresenter {
    private final static String TAG = SplashScreenPresenter.class.getSimpleName();

    private SplashScreenActivity mContext;

    SplashScreenPresenter(SplashScreenActivity mContext) {
        this.mContext = mContext;
        delayAndStartDashboardActivity(1000);
    }




    // Khởi chạy màn hình điều khiển sau x mili giây (1 giây = 1000 mili giây)
    private void delayAndStartDashboardActivity(int miliseconds) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mContext.startDashboardActivity();
            }
        }, miliseconds);
    }
}
