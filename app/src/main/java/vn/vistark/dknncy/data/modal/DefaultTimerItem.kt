package vn.vistark.dknncy.data.modal

import android.util.Log
import com.google.firebase.database.DataSnapshot
import vn.vistark.dknncy.Config
import vn.vistark.dknncy.data.firebase.FirebaseConfig

object DefaultTimerItem {
    val TAG = DefaultTimerItem::class.java.simpleName
    @JvmField
    val timerPortA = TimerItem(
            0,
            Config.PORT_A_NAME,
            "A",
            false,
            false,
            "00:00",
            "23:59",
            "Timer mặc định để điều khiển port A hoạt động 24/24"
    )
    @JvmField
    val timerPortB = TimerItem(
            1,
            Config.PORT_B_NAME,
            "B",
            false,
            false,
            "00:00",
            "23:59",
            "Timer mặc định để điều khiển port B hoạt động 24/24"
    )
    @JvmField
    val timerPortC = TimerItem(
            2,
            Config.PORT_C_NAME,
            "C",
            false,
            false,
            "00:00",
            "23:59",
            "Timer mặc định để điều khiển port C hoạt động 24/24"
    )
    @JvmField
    val timerPortD = TimerItem(
            3,
            Config.PORT_D_NAME,
            "D",
            true,
            true,
            "00:00",
            "23:59",
            "Timer mặc định để điều khiển port D hoạt động 24/24"
    )

    @JvmStatic
    fun check(ds: DataSnapshot) {
        if (!ds.hasChild(timerPortA.id.toString() + "")) {
            Log.e(TAG, "check: Không có A")
            FirebaseConfig.updateData(timerPortA)
        }
        if (!ds.hasChild(timerPortB.id.toString() + "")) {
            Log.e(TAG, "check: Không có B")
            FirebaseConfig.updateData(timerPortB)
        }
        if (!ds.hasChild(timerPortC.id.toString() + "")) {
            Log.e(TAG, "check: Không có C")
            FirebaseConfig.updateData(timerPortC)
        }
        if (!ds.hasChild(timerPortD.id.toString() + "")) {
            Log.e(TAG, "check: Không có D")
            FirebaseConfig.updateData(timerPortD)
        }
    }
}