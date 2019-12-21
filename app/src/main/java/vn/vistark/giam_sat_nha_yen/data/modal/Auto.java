package vn.vistark.giam_sat_nha_yen.data.modal;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.DashboardScreenActivity;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.timer_list.TimerList;

import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.KEY_END;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.KEY_LABEL;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.KEY_PORT;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.KEY_POWER;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.KEY_START;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.KEY_STATE;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.autoRef;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.timerRef;

/**
 * Project ĐK Nhà Yến
 * Packagename: vn.vistark.giam_sat_nha_yen.data.modal
 * Created by Nguyễn Trọng Nghĩa on 10/28/2019.
 * Organization: Vistark Team
 * Email: dev.vistark@gmail.com
 */
public class Auto {
    private final static String TAG = Auto.class.getSimpleName();

    private final static String crrRootKey = "Auto";

    private final static String humidityKey = "humidity";
    private final static String humidityStartKey = "start";
    private final static String humidityEndKey = "end";
    private final static String tempKey = "temp";
    private final static String tempStartKey = "start";
    private final static String tempEndKey = "end";
    public final static String powerKey = "power";

    public static int humidityStartValue = 70;
    public static int humidityEndValue = 90;
    public static int tempStartValue = 28;
    public static int tempEndValue = 100;

    public static boolean powerValue = false;

    public void sync(DatabaseReference rootRef, final DashboardScreenActivity context) {
        if (rootRef != null) {
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(crrRootKey)) {
                        updateAuto();
                    }
                    addAutoRefListener(context);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void addAutoRefListener(final DashboardScreenActivity context) {
        autoRef.child(humidityKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    try {
                        if (ds.getKey() != null) {
                            // Nếu là key start trong Auto Humidity
                            if (ds.getKey().equals(humidityStartKey)) {
                                if (ds.getValue() != null) {
                                    humidityStartValue = Integer.parseInt(ds.getValue().toString());
                                } else {
                                    autoRef.child(humidityKey).child(humidityStartKey).setValue(humidityStartValue);
                                }
                            }

                            // Nếu là key end trong Auto Humidity
                            if (ds.getKey().equals(humidityEndKey)) {
                                if (ds.getValue() != null) {
                                    humidityEndValue = Integer.parseInt(ds.getValue().toString());
                                } else {
                                    autoRef.child(humidityKey).child(humidityEndKey).setValue(humidityEndValue);
                                }
                            }
                            context.updateAutoHumidity(humidityStartValue + "", humidityEndValue + "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Lỗi khi đọc dữ liệu.", databaseError.toException());
            }
        });
        autoRef.child(tempKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    try {
                        if (ds.getKey() != null) {
                            // Nếu là key start trong Auto Humidity
                            if (ds.getKey().equals(tempStartKey)) {
                                if (ds.getValue() != null) {
                                    tempStartValue = Integer.parseInt(ds.getValue().toString());
                                } else {
                                    autoRef.child(tempKey).child(tempStartKey).setValue(tempStartValue);
                                }
                            }

                            // Nếu là key end trong Auto Humidity
                            if (ds.getKey().equals(tempEndKey)) {
                                if (ds.getValue() != null) {
                                    tempEndValue = Integer.parseInt(ds.getValue().toString());
                                } else {
                                    autoRef.child(tempKey).child(tempEndKey).setValue(tempEndValue);
                                }
                            }
                            context.updateAutoTemp(tempStartValue + "", tempEndValue + "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Lỗi khi đọc dữ liệu.", databaseError.toException());
            }
        });
    }

    public static void updateAuto() {
        DatabaseReference humidityRef = FirebaseConfig.autoRef.child(humidityKey);
        DatabaseReference tempRef = FirebaseConfig.autoRef.child(tempKey);

        humidityRef.child(humidityStartKey).setValue(humidityStartValue);
        humidityRef.child(humidityEndKey).setValue(humidityEndValue);

        tempRef.child(tempStartKey).setValue(tempStartValue);
        tempRef.child(tempEndKey).setValue(tempEndValue);

        FirebaseConfig.autoRef.child(powerKey).setValue(powerValue);
    }
}
