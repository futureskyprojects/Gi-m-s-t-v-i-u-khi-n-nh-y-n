package vn.vistark.giam_sat_nha_yen.data.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import vn.vistark.giam_sat_nha_yen.Config;
import vn.vistark.giam_sat_nha_yen.data.db.modal.TimerItem;

public class FirebaseConfig {
    private final static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public final static DatabaseReference myRef = database.getReference(Config.DEVEICE_NAME);
    public final static DatabaseReference timerRef = myRef.child("Timer");

    public final static String KEY_PORT = "port";
    public final static String KEY_LABEL = "label";
    public final static String KEY_POWER = "power";
    public final static String KEY_STATE = "state";
    public final static String KEY_START = "start";
    public final static String KEY_END = "end";
    public final static String KEY_DETAIL = "detail";

    // Cấu hình đường dẫn cho các thiết bị con
    public static void updateData(TimerItem timerItem) {
        DatabaseReference Id = timerRef.child(timerItem.getId() + "");
//        Id.child("id").setValue(timerItem.getId());
        Id.child(KEY_PORT).setValue(timerItem.getPort());
        Id.child(KEY_LABEL).setValue(timerItem.getLabel());
        Id.child(KEY_POWER).setValue(timerItem.isPower());
        Id.child(KEY_STATE).setValue(timerItem.isState());
        Id.child(KEY_START).setValue(timerItem.getStart());
        Id.child(KEY_END).setValue(timerItem.getEnd());
        Id.child(KEY_DETAIL).setValue(timerItem.getDetail());
    }
}
