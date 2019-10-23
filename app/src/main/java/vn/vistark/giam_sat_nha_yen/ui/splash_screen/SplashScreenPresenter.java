package vn.vistark.giam_sat_nha_yen.ui.splash_screen;

import java.util.Timer;
import java.util.TimerTask;

import vn.vistark.giam_sat_nha_yen.data.arduino_community.ArduinoCommunity;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.video_transfer.VideoTransfer;
import vn.vistark.giam_sat_nha_yen.utils.NetworkUtils;
import vn.vistark.giam_sat_nha_yen.utils.PermissionUtils;

/**
 * Project ĐK Nhà Yến
 * Created by Nguyễn Trọng Nghĩa on 10/19/2019.
 * Organization: Vistark Team
 * Email: dev.vistark@gmail.com
 */

class SplashScreenPresenter {
    private final static String TAG = SplashScreenPresenter.class.getSimpleName();

    private SplashScreenActivity mContext;

    SplashScreenPresenter(SplashScreenActivity mContext) {
        this.mContext = mContext;
        // Tiến hành kiểm tra quyền
        checkPermissions();
    }

    private void checkPermissions() {
        mContext.updateInitStateTextShowView("Kiểm tra quyền của ứng dụng...");
        PermissionUtils.RequestAllPermission(mContext);
    }

    public void initStarting() { // Được gọi nếu sau khi kiểm tra tất cả các quyền đã được cấp hoàn tất bên Activity
        // Hiển thị tin nhắn khởi động
        mContext.updateInitStateTextShowView("Đang khởi động...");
        final Timer timer = new Timer();
        // Tiến hành khởi tạo cho bằng được kết nối internet trước khi sang bước tiếp theo
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Khi đã chắc chắn có internet
                if (NetworkUtils.isNetworkConnected(mContext)) {
                    // Tiến hành kết nối đến máy chủ để chuẩn bị cho việc gửi nhận video sau này
                    VideoTransfer.attach();
                    // Tiến hành khởi tạo giao tiếp phần cứng
                    initCommunity();
                    // Tắt timer để hoàn tất kết thúc việc kiểm tra mạng internet
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
                // Cập nhật tin nhắn khởi động
                mContext.updateInitStateTextShowView("Đang tìm kiếm thiết bị ngoại vi...");
                // Nếu khởi tạo giao tiếp với phần cứng bên dưới thành công
                if (ArduinoCommunity.init(mContext)) {
                    // Đóng timer để ngưng việc khởi tạo giao tiếp
                    timer.cancel();
                    // Tiến hành chuyển sang mang hình bảng điều khiển
                    delayAndStartDashboardActivity(1000);
                }
            }
        }, 1000, 1000);
    }


    // Khởi chạy màn hình điều khiển sau x mili giây (1 giây = 1000 mili giây)
    private void delayAndStartDashboardActivity(int miliseconds) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // Cập nhật tin nhắn khởi động
                mContext.updateInitStateTextShowView("Khởi động bảng điều khiển...");
                // Tiến hành khởi động bảng điều khiển
                mContext.startDashboardActivity();
            }
        }, miliseconds);
    }
}
