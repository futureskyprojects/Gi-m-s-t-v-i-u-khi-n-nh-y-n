/*
 * Dùng để cấu hình khung nhìn và check các điều kiện cơ bản khi chọn thời gian
 */
package vn.vistark.dknncy.ui.dashboard_screen.timer_dialog

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatRadioButton
import es.dmoral.toasty.Toasty
import vn.vistark.dknncy.R
import vn.vistark.dknncy.utils.TimeUtils
import java.util.*

class TimerDialogBuilder(context: AppCompatActivity) : AlertDialog.Builder(context) {
    private val ivAddTimerIcon: ImageView
    private val tvAddTimerHeader: TextView
    var ivAddTimerClose: ImageView
    private val edtAddTimerLabelInput: EditText
    private val edtAddTimerTimeStartSelector: TextView
    private val edtAddTimerTimeEndSelector: TextView
    var btnAddTimerAddButton: Button
    private val acrbAddTimerPortARadioButton: AppCompatRadioButton
    private val acrbAddTimerPortBRadioButton: AppCompatRadioButton
    private val acrbAddTimerPortCRadioButton: AppCompatRadioButton
    private val acrbAddTimerPortDRadioButton: AppCompatRadioButton
    var selectedPort = "A"
    val defaultString = "HẸN GIỜ BẬT - TẮT | CỔNG "
    private fun initBaseEvents() {
        initEdtAddTimerTimeStartSelectorEvents()
        initEdtAddTimerTimeEndSelectorEvents()
        initAcrbAddTimerPortARadioButtonChangeEvent()
        initAcrbAddTimerPortBRadioButtonChangeEvent()
        initAcrbAddTimerPortCRadioButtonChangeEvent()
        initAcrbAddTimerPortDRadioButtonChangeEvent()
    }

    private fun removeAllChecked() {
        acrbAddTimerPortARadioButton.isChecked = false
        acrbAddTimerPortBRadioButton.isChecked = false
        acrbAddTimerPortCRadioButton.isChecked = false
        acrbAddTimerPortDRadioButton.isChecked = false
    }

    private fun initAcrbAddTimerPortDRadioButtonChangeEvent() {
        acrbAddTimerPortDRadioButton.setOnClickListener {
            removeAllChecked()
            acrbAddTimerPortDRadioButton.isChecked = true
            selectedPort = "D"
            if (label.isEmpty() || label.contains(defaultString)) {
                label = defaultString + selectedPort
            }
        }
    }

    private fun initAcrbAddTimerPortCRadioButtonChangeEvent() {
        acrbAddTimerPortCRadioButton.setOnClickListener {
            removeAllChecked()
            acrbAddTimerPortCRadioButton.isChecked = true
            selectedPort = "C"
            if (label.isEmpty() || label.contains(defaultString)) {
                label = defaultString + selectedPort
            }
        }
    }

    private fun initAcrbAddTimerPortBRadioButtonChangeEvent() {
        acrbAddTimerPortBRadioButton.setOnClickListener {
            removeAllChecked()
            acrbAddTimerPortBRadioButton.isChecked = true
            selectedPort = "B"
            if (label.isEmpty() || label.contains(defaultString)) {
                label = defaultString + selectedPort
            }
        }
    }

    private fun initAcrbAddTimerPortARadioButtonChangeEvent() {
        acrbAddTimerPortARadioButton.setOnClickListener {
            removeAllChecked()
            acrbAddTimerPortARadioButton.isChecked = true
            selectedPort = "A"
            if (label.isEmpty() || label.contains(defaultString)) {
                label = defaultString + selectedPort
            }
        }
    }

    private fun initEdtAddTimerTimeEndSelectorEvents() {
        edtAddTimerTimeEndSelector.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(edtAddTimerTimeStartSelector.context, TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                endTimer = "$selectedHour:$selectedMinute"
                checkEndTimeWithStartTime()
            }, hour, minute, true) // Chọn định dạng là 24 giờ
            mTimePicker.setTitle("Chọn thời điểm bắt đầu")
            mTimePicker.show()
        }
    }

    private fun initEdtAddTimerTimeStartSelectorEvents() {
        edtAddTimerTimeStartSelector.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(edtAddTimerTimeStartSelector.context, TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                startTimer = "$selectedHour:$selectedMinute"
                checkEndTimeWithStartTime()
            }, hour, minute, true) // Chọn định dạng là 24 giờ
            mTimePicker.setTitle("Chọn thời điểm bắt đầu")
            mTimePicker.show()
        }
    }

    // Thời gian bắt đầu không được bé hơn thời gian kết thúc
    fun checkEndTimeWithStartTime(): Boolean {
        if (startTimer.contains(":") && endTimer.contains(":")) {
            // Tách lấy thời gian bắt đầu
            val startHours =
                    startTimer.split(":".toRegex()).toTypedArray()[0]
                            .toInt()
            val startMinutes =
                    startTimer.split(":".toRegex()).toTypedArray()[1]
                            .toInt()

            // Tách lấy thời gian két thúc
            val endHours =
                    endTimer.split(":".toRegex()).toTypedArray()[0]
                            .toInt()
            val endMinutes =
                    endTimer.split(":".toRegex()).toTypedArray()[1]
                            .toInt()

            // Tiến hành so sánh
            if (endHours < startHours || endHours == startHours && endMinutes < startMinutes) {
                Toasty.warning(ivAddTimerClose.context, "Thời gian kết thúc cần lớn hơn thời gian bắt đầu").show()
                return false
            }
        }
        return true
    }

    fun changeToAddLayout() {
        ivAddTimerIcon.setImageResource(R.drawable.stopwatch)
        tvAddTimerHeader.setText(R.string.addTimerOnOff)
        btnAddTimerAddButton.setText(R.string.add)
    }

    fun changeToEditLayout() {
        ivAddTimerIcon.setImageResource(R.drawable.stopwatch_edit)
        tvAddTimerHeader.setText(R.string.editTimerOnOff)
        btnAddTimerAddButton.setText(R.string.edit)
    }

    fun updateSelectedPort(port: String) {
        selectedPort = port
        when (selectedPort) {
            "A" -> acrbAddTimerPortARadioButton.isChecked = true
            "B" -> acrbAddTimerPortBRadioButton.isChecked = true
            "C" -> acrbAddTimerPortCRadioButton.isChecked = true
            "D" -> acrbAddTimerPortDRadioButton.isChecked = true
            else -> acrbAddTimerPortARadioButton.isChecked = true
        }
    }

    var label: String
        get() = edtAddTimerLabelInput.text.toString()
        set(label) {
            edtAddTimerLabelInput.setText(label)
        }

    var startTimer: String = ""
        get() = edtAddTimerTimeStartSelector.text.toString()
        set(start) {
            field = TimeUtils.timerToStandard(start)
            edtAddTimerTimeStartSelector.text = start
        }

    var endTimer: String = ""
        get() = edtAddTimerTimeEndSelector.text.toString()
        set(end) {
            field = TimeUtils.timerToStandard(end)
            edtAddTimerTimeEndSelector.text = end
        }

    init {
        val v = context.layoutInflater.inflate(R.layout.add_timer_dialog_layout, null)
        ivAddTimerIcon = v.findViewById(R.id.addTimerIcon)
        tvAddTimerHeader = v.findViewById(R.id.addTimerHeader)
        ivAddTimerClose = v.findViewById(R.id.addTimerClose)
        edtAddTimerLabelInput = v.findViewById(R.id.addTimerLabelInput)
        edtAddTimerTimeStartSelector = v.findViewById(R.id.addTimerTimeStartSelector)
        edtAddTimerTimeEndSelector = v.findViewById(R.id.addTimerTimeEndSelector)
        btnAddTimerAddButton = v.findViewById(R.id.addTimerAddButton)
        acrbAddTimerPortARadioButton = v.findViewById(R.id.addTimerPortARadioButton)
        acrbAddTimerPortBRadioButton = v.findViewById(R.id.addTimerPortBRadioButton)
        acrbAddTimerPortCRadioButton = v.findViewById(R.id.addTimerPortCRadioButton)
        acrbAddTimerPortDRadioButton = v.findViewById(R.id.addTimerPortDRadioButton)
        initBaseEvents()

        // Thiết lập giao diện
        this.setView(v)
    }
}