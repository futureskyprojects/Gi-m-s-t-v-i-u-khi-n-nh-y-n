package vn.vistark.dknncy.data.firebase

import android.annotation.SuppressLint
import com.google.firebase.database.FirebaseDatabase
import vn.vistark.dknncy.Config
import vn.vistark.dknncy.data.modal.CurrentDetail
import vn.vistark.dknncy.data.modal.TimerItem
import java.util.*

object FirebaseConfig {
    private val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference(Config.DEVEICE_NAME)
    val timerRef = myRef.child("Timer")
    private val infomationRef = myRef.child("Information")
    private val configRef = myRef.child("Config")
    val autoRef = myRef.child("Auto")

    @JvmField
    val wsRef = configRef.child("ws")
    const val KEY_PORT = "port"
    const val KEY_LABEL = "label"
    const val KEY_POWER = "power"
    const val KEY_STATE = "state"
    const val KEY_START = "start"
    const val KEY_END = "end"
    private const val KEY_HUMIDITY = "Humidity"
    private const val KEY_TEMPERATURE = "Temp"

    // cập nhật timer item
    fun updateData(timerItem: TimerItem) {
        val Id = timerRef.child(timerItem.id.toString() + "")
        Id.child(KEY_PORT).setValue(timerItem.port)
        Id.child(KEY_LABEL).setValue(timerItem.label)
        Id.child(KEY_POWER).setValue(timerItem.isPower.toString())
        Id.child(KEY_STATE).setValue(timerItem.isState.toString())
        Id.child(KEY_START).setValue(timerItem.start)
        Id.child(KEY_END).setValue(timerItem.end)
    }

    // Cập nhật nhiệt độ và độ ẩm
    fun updateTemperatureAndHumidity() {
        // lấy thời gian hiện tại
        val calendar = Calendar.getInstance()
        // Lấy chuỗi ngày tháng của hôm nay
        @SuppressLint("DefaultLocale") val keyDate = String.format(
                "%02d-%02d-%02d",
                calendar[Calendar.DAY_OF_MONTH],
                calendar[Calendar.MONTH] + 1,
                calendar[Calendar.YEAR])
        val keyTimeRef = infomationRef.child(keyDate)
        // Lấy key giờ
        val keyHour = calendar[Calendar.HOUR_OF_DAY].toString() + ""
        val keyHourRef = keyTimeRef.child(keyHour)
        keyHourRef.child(KEY_HUMIDITY).setValue(CurrentDetail.humidity)
        keyHourRef.child(KEY_TEMPERATURE).setValue(CurrentDetail.temperature)
    }
}