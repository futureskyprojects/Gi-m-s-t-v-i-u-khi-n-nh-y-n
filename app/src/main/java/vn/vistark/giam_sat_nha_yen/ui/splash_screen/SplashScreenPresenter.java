package vn.vistark.giam_sat_nha_yen.ui.splash_screen;

import java.util.Timer;
import java.util.TimerTask;

import vn.vistark.giam_sat_nha_yen.data.arduino_community.ArduinoCommunity;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.video_transfer.VideoTransfer;
import vn.vistark.giam_sat_nha_yen.utils.NetworkUtils;

class SplashScreenPresenter {
    private final static String TAG = SplashScreenPresenter.class.getSimpleName();

    private SplashScreenActivity mContext;

    SplashScreenPresenter(SplashScreenActivity mContext) {
        this.mContext = mContext;
        initStarting();
        //delayAndStartDashboardActivity(1000);
    }

    private void initStarting() {
        mContext.updateInitStateTextShowView("Đang khởi động...");
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (NetworkUtils.isNetworkConnected(mContext)) {
                    VideoTransfer.attach();
                    initCommunity();
                    timer.cancel();
                }
            }
        }, 1000, 1000);
    }

    private void initCommunity() {

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mContext.updateInitStateTextShowView("Đang tìm kiếm thiết bị ngoại vi...");
                if (ArduinoCommunity.init(mContext)) {
                    timer.cancel();
                    delayAndStartDashboardActivity(1000);
                } else {
                    //
                }
            }
        }, 1000, 1000);
    }


    // Khởi chạy màn hình điều khiển sau x mili giây (1 giây = 1000 mili giây)
    private void delayAndStartDashboardActivity(int miliseconds) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mContext.updateInitStateTextShowView("Khởi động bảng điều khiển...");
                mContext.startDashboardActivity();
            }
        }, miliseconds);
    }
}
