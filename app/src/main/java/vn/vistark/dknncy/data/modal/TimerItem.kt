package vn.vistark.dknncy.data.modal

import com.google.firebase.database.DataSnapshot
import vn.vistark.dknncy.data.firebase.FirebaseConfig
import vn.vistark.dknncy.utils.TimeUtils

class TimerItem(
        var id: Long = -1,
        var label: String = "",
        var port: String = "",
        var isPower: Boolean = false,
        var isState: Boolean = false,
        var start: String = "",
        var end: String = "",
        var detail: String = "") {

    fun isDefault(): Boolean {
        return id == DefaultTimerItem.timerPortA.id ||
                id == DefaultTimerItem.timerPortB.id ||
                id == DefaultTimerItem.timerPortC.id ||
                id == DefaultTimerItem.timerPortD.id
    }

    fun isPump(): Boolean {
        return id == DefaultTimerItem.timerPortA.id ||
                id == DefaultTimerItem.timerPortB.id
    }

    fun isLight(): Boolean {
        return id == DefaultTimerItem.timerPortC.id
    }

    fun parse(ds: DataSnapshot): TimerItem? {
        try {
            val timerItem = TimerItem()
            timerItem.id = ds.key?.toLong() ?: return null

            ds.children.forEach { ti ->
                when (ti.key) {
                    FirebaseConfig.KEY_PORT -> timerItem.port = ti.value.toString()
                    FirebaseConfig.KEY_LABEL -> timerItem.label = ti.value.toString()
                    FirebaseConfig.KEY_POWER -> timerItem.isPower = ti.value.toString().toBoolean()
                    FirebaseConfig.KEY_STATE -> timerItem.isState = ti.value.toString().toBoolean()
                    FirebaseConfig.KEY_START -> timerItem.start = ti.value.toString()
                    FirebaseConfig.KEY_END -> timerItem.end = ti.value.toString()
                }
            }
            return timerItem
        } catch (e: Exception) {
            e.printStackTrace()
            if (ds.key != null) {
                // Nếu xuất hiện giá trị bất thường thì xóa nó đi
                FirebaseConfig.timerRef.child(ds.key!!).removeValue()
            }
        }
        return null
    }

    fun update(timerItem: TimerItem) {
        id = timerItem.id
        label = timerItem.label
        port = timerItem.port
        isPower = timerItem.isPower
        isState = timerItem.isState
        start = timerItem.start
        end = timerItem.end
        detail = timerItem.detail
    }

    fun isDiff(timerItem: TimerItem): Boolean {
        return id != timerItem.id ||
                label != timerItem.label ||
                port != timerItem.port ||
                isPower != timerItem.isPower ||
                isState != timerItem.isState ||
                start != timerItem.start ||
                end != timerItem.end ||
                detail != timerItem.detail
    }

    init {
        this.start = TimeUtils.timerToStandard(start)
        this.end = TimeUtils.timerToStandard(end)
    }

    companion object {
        fun String.timerToStandard(): String {
            return TimeUtils.timerToStandard(this)
        }

    }

}