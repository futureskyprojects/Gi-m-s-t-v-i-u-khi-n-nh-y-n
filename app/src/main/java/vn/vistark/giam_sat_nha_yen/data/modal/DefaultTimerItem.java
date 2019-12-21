package vn.vistark.giam_sat_nha_yen.data.modal;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import vn.vistark.giam_sat_nha_yen.Config;
import vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig;

/**
 * Project ĐK Nhà Yến
 * Packagename: vn.vistark.giam_sat_nha_yen.data.modal
 * Created by Nguyễn Trọng Nghĩa on 10/28/2019.
 * Organization: Vistark Team
 * Email: dev.vistark@gmail.com
 */
public class DefaultTimerItem {
    public final static String TAG = DefaultTimerItem.class.getSimpleName();
    public final static TimerItem timerPortA = new TimerItem(
            0,
            Config.PORT_A_NAME,
            "A",
            false,
            false,
            "00:00",
            "23:59",
            "Timer mặc định để điều khiển port A hoạt động 24/24"
    );
    public final static TimerItem timerPortB = new TimerItem(
            1,
            Config.PORT_B_NAME,
            "B",
            false,
            false,
            "00:00",
            "23:59",
            "Timer mặc định để điều khiển port B hoạt động 24/24"
    );
    public final static TimerItem timerPortC = new TimerItem(
            2,
            Config.PORT_C_NAME,
            "C",
            false,
            false,
            "00:00",
            "23:59",
            "Timer mặc định để điều khiển port C hoạt động 24/24"
    );
    public final static TimerItem timerPortD = new TimerItem(
            3,
            Config.PORT_D_NAME,
            "D",
            true,
            true,
            "00:00",
            "23:59",
            "Timer mặc định để điều khiển port D hoạt động 24/24"
    );

    public static void check(DataSnapshot ds) {
        if (!ds.hasChild(timerPortA.getId() + "")) {
            Log.e(TAG, "check: Không có A");
            FirebaseConfig.updateData(timerPortA);
        }
        if (!ds.hasChild(timerPortB.getId() + "")) {
            Log.e(TAG, "check: Không có B");
            FirebaseConfig.updateData(timerPortB);
        }
        if (!ds.hasChild(timerPortC.getId() + "")) {
            Log.e(TAG, "check: Không có C");
            FirebaseConfig.updateData(timerPortC);
        }
        if (!ds.hasChild(timerPortD.getId() + "")) {
            Log.e(TAG, "check: Không có D");
            FirebaseConfig.updateData(timerPortD);
        }
    }
}
