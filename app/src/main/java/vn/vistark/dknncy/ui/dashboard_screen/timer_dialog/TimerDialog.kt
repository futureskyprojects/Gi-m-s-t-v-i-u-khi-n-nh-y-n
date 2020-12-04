package vn.vistark.dknncy.ui.dashboard_screen.timer_dialog

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import es.dmoral.toasty.Toasty
import vn.vistark.dknncy.data.firebase.FirebaseConfig
import vn.vistark.dknncy.data.modal.TimerItem

object TimerDialog {
    private var alertDialog: AlertDialog? = null

    fun initMe(mContext: AppCompatActivity): TimerDialogBuilder {
        if (alertDialog != null && alertDialog!!.isShowing) alertDialog!!.dismiss()

        val builder = TimerDialogBuilder(mContext)
        builder.changeToAddLayout() // Chuyển thành layout thêm
        builder.selectedPort = "A"
        initAddEvents(builder)
        return builder
    }

    fun showAdd(context: AppCompatActivity) {
        initMe(context).apply {
            label = defaultString + selectedPort
            initSubmitAddButton(this) // Xử lý khi nhấn nút thêm
            alertDialog = create()
            alertDialog?.show()
        }
    }

    fun showEdit(context: AppCompatActivity, timerItem: TimerItem) {
        initMe(context).apply {
            // Điền các thông tin đã có, kế thừa từ timer cần sửa
            label = timerItem.label
            selectedPort = timerItem.port
            startTimer = timerItem.start
            endTimer = timerItem.end
            initSubmitEditButton(this, timerItem) // Xử lý khi nhấn nút thêm
            alertDialog = create()
            alertDialog?.show()
        }
    }

    private fun initSubmitAddButton(builder: TimerDialogBuilder) {
        builder.btnAddTimerAddButton.setOnClickListener {
            if (builder.label.isEmpty()) {
                Toasty.warning(builder.context, "Vui lòng nhập nhãn cho mục.").show()
            } else if (!builder.startTimer.contains(":")) {
                Toasty.warning(builder.context, "Vui lòng chọn thời gian bắt đầu.").show()
            } else if (!builder.endTimer.contains(":")) {
                Toasty.warning(builder.context, "Vui lòng chọn thời gian kết thúc.").show()
            } else if (builder.checkEndTimeWithStartTime()) {
                // Khởi tạo đổi tượng TimerItem mới để chứa dữ liệu
                val tsLong = System.currentTimeMillis() / 1000
                val timerItem = TimerItem(
                        tsLong,
                        builder.label,
                        builder.selectedPort,
                        true,
                        false,
                        builder.startTimer,
                        builder.endTimer,
                        ""
                )
                FirebaseConfig.updateData(timerItem)
                alertDialog!!.dismiss()
            }
        }
    }

    private fun initSubmitEditButton(builder: TimerDialogBuilder, timerItem: TimerItem) {
        builder.btnAddTimerAddButton.setOnClickListener {
            if (builder.label.isEmpty()) {
                Toasty.warning(builder.context, "Vui lòng nhập nhãn cho mục.").show()
            } else if (!builder.startTimer.contains(":")) {
                Toasty.warning(builder.context, "Vui lòng chọn thời gian bắt đầu.").show()
            } else if (!builder.endTimer.contains(":")) {
                Toasty.warning(builder.context, "Vui lòng chọn thời gian kết thúc.").show()
            } else if (builder.checkEndTimeWithStartTime()) {
                // Cập nhật đối tượng mới
                timerItem.label = builder.label
                timerItem.port = builder.selectedPort
                timerItem.start = builder.startTimer
                timerItem.end = builder.endTimer
//                adapter.notifyDataSetChanged()

                // Update lên firebase
                FirebaseConfig.updateData(timerItem)
                alertDialog!!.dismiss()
            }
        }
    }

    private fun initAddEvents(builder: TimerDialogBuilder) {
        builder.ivAddTimerClose.setOnClickListener { alertDialog!!.dismiss() }
    }

}