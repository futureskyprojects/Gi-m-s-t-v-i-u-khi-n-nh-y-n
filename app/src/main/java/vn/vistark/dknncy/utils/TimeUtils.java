package vn.vistark.dknncy.utils;

import android.annotation.SuppressLint;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public final static String TAG = TimeUtils.class.getSimpleName();

    @SuppressLint("DefaultLocale")
    public static String millisToCounter(long millis) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    // Chuẩn hóa lại chuỗi thời gian
    public static String timerToStandard(String timer) {
        if (timer.contains(":")) {
            String[] split = timer.split(":");
            if (split.length == 2) {
                for (int i = 0; i < split.length; i++) {
                    if (split[i].length() < 2) {
                        split[i] = "0" + split[i];
                    }
                }
                return split[0] + ":" + split[1];
            } else return timer;
        }
        return timer;
    }

    // Kiểm tra hiện tại có nằm trong khung giờ được nhập không
    public static boolean isInTimer(String startTimer, String endTimer) {
        if (startTimer.contains(":") && endTimer.contains(":")) {
            // Tách lấy thời gian bắt đầu
            int startHours = Integer.parseInt(
                    startTimer.split(":")[0]
            );

            int startMinutes = Integer.parseInt(
                    startTimer.split(":")[1]
            );

            // Tách lấy thời gian két thúc
            int endHours = Integer.parseInt(
                    endTimer.split(":")[0]
            );

            int endMinutes = Integer.parseInt(
                    endTimer.split(":")[1]
            );

            // Lấy thời điểm hiện tại
            Calendar calendar = Calendar.getInstance();
            int currentHours = calendar.get(Calendar.HOUR_OF_DAY);
            int currentMinutes = calendar.get(Calendar.MINUTE);

            // Tiến hành so sánh
            return !(
                    (currentHours < startHours) ||
                            ((currentHours == startHours) && (currentMinutes < startMinutes)) ||
                            (currentHours > endHours) ||
                            ((currentHours == endHours) && (currentMinutes > endMinutes))
            );
        }
        return false;
    }
}
