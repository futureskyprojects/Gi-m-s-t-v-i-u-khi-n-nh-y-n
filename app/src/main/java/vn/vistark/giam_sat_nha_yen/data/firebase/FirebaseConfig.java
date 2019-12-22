package vn.vistark.giam_sat_nha_yen.data.firebase;

import android.annotation.SuppressLint;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import vn.vistark.giam_sat_nha_yen.Config;
import vn.vistark.giam_sat_nha_yen.data.modal.Auto;
import vn.vistark.giam_sat_nha_yen.data.modal.CurrentDetail;
import vn.vistark.giam_sat_nha_yen.data.modal.TimerItem;

public class FirebaseConfig {
    private final static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public final static DatabaseReference myRef = database.getReference(Config.DEVEICE_NAME);
    public final static DatabaseReference timerRef = myRef.child("Timer");
    private final static DatabaseReference infomationRef = myRef.child("Information");
    private final static DatabaseReference configRef = myRef.child("Config");
    public final static DatabaseReference autoRef = myRef.child("Auto");
    public final static DatabaseReference wsRef = configRef.child("ws");

    public final static String KEY_PORT = "port";
    public final static String KEY_LABEL = "label";
    public final static String KEY_POWER = "power";
    public final static String KEY_STATE = "state";
    public final static String KEY_START = "start";
    public final static String KEY_END = "end";

    private final static String KEY_HUMIDITY = "Humidity";
    private final static String KEY_TEMPERATURE = "Temp";


    // cập nhật timer item
    public static void updateData(TimerItem timerItem) {
        DatabaseReference Id = timerRef.child(timerItem.getId() + "");
        Id.child(KEY_PORT).setValue(timerItem.getPort());
        Id.child(KEY_LABEL).setValue(timerItem.getLabel());
        Id.child(KEY_POWER).setValue(timerItem.isPower());
        Id.child(KEY_STATE).setValue(timerItem.isState());
        Id.child(KEY_START).setValue(timerItem.getStart());
        Id.child(KEY_END).setValue(timerItem.getEnd());
    }

    // Cập nhật nhiệt độ và độ ẩm
    public static void updateTemperatureAndHumidity() {
        // lấy thời gian hiện tại
        Calendar calendar = Calendar.getInstance();
        // Lấy chuỗi ngày tháng của hôm nay
        @SuppressLint("DefaultLocale") String keyDate = String.format(
                "%02d-%02d-%02d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR));
        DatabaseReference keyTimeRef = infomationRef.child(keyDate);
        // Lấy key giờ
        String keyHour = calendar.get(Calendar.HOUR_OF_DAY) + "";
        DatabaseReference keyHourRef = keyTimeRef.child(keyHour);
        keyHourRef.child(KEY_HUMIDITY).setValue(CurrentDetail.humidity);
        keyHourRef.child(KEY_TEMPERATURE).setValue(CurrentDetail.temperature);

    }
}
