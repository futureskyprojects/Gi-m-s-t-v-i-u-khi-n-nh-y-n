package vn.vistark.dknncy.ui.splash_screen

import vn.vistark.dknncy.data.arduino_community.ArduinoInit
import vn.vistark.dknncy.utils.NetworkUtils
import vn.vistark.dknncy.utils.PermissionUtils
import java.util.*

internal class SplashScreenPresenter(private val mContext: SplashScreenActivity) {
    private fun checkPermissions() {
        mContext.updateInitStateTextShowView("Kiểm tra quyền của ứng dụng...")
        PermissionUtils.RequestAllPermission(mContext)
    }

    fun initStarting() { // Được gọi nếu sau khi kiểm tra tất cả các quyền đã được cấp hoàn tất bên Activity
        // Hiển thị tin nhắn khởi động
        mContext.updateInitStateTextShowView("Đang khởi động...")
        val timer = Timer()
        // Tiến hành khởi tạo cho bằng được kết nối internet trước khi sang bước tiếp theo
        timer.schedule(object : TimerTask() {
            override fun run() {
                // Khi đã chắc chắn có internet
                if (NetworkUtils.isNetworkConnected(mContext)) {
                    // Tiến hành kết nối đến máy chủ để chuẩn bị cho việc gửi nhận video sau này
//                    VideoTransfer.attach();
                    // Tiến hành khởi tạo giao tiếp phần cứng
                    initCommunity()
                    // Tắt timer để hoàn tất kết thúc việc kiểm tra mạng internet
                    timer.cancel()
                }
            }
        }, 1000, 1000)
    }

    private fun initCommunity() {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                // Cập nhật tin nhắn khởi động
                mContext.updateInitStateTextShowView("Đang tìm kiếm thiết bị ngoại vi...")
                // Nếu khởi tạo giao tiếp với phần cứng bên dưới thành công
                if (ArduinoInit.initializer(mContext)) {
                    // Đóng timer để ngưng việc khởi tạo giao tiếp
                    timer.cancel()

                    // Tiến hành chuyển sang mang hình bảng điều khiển
                    delayAndStartDashboardActivity(1000)
                    //
                }
            }
        }, 1000, 1000)
    }

    // Khởi chạy màn hình điều khiển sau x mili giây (1 giây = 1000 mili giây)
    private fun delayAndStartDashboardActivity(miliseconds: Int) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                // Cập nhật tin nhắn khởi động
                mContext.updateInitStateTextShowView("Khởi động bảng điều khiển...")
                // Tiến hành khởi động bảng điều khiển
                mContext.startDashboardActivity()
            }
        }, miliseconds.toLong())
    }

    companion object {
        private val TAG = SplashScreenPresenter::class.java.simpleName
    }

    init {
        // Tiến hành kiểm tra quyền
        checkPermissions()
    }
}