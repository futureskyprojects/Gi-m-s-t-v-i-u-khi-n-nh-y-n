package vn.vistark.giam_sat_nha_yen.ui.dashboard_screen;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import vn.vistark.giam_sat_nha_yen.data.db.TimerItemDb.TimerItemHelper;
import vn.vistark.giam_sat_nha_yen.data.db.modal.TimerItem;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.arduino_sensor_reader.Reader;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.camera_displayer.CameraShow;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.timer_list.TimerList;
import vn.vistark.giam_sat_nha_yen.utils.TimeUtils;

import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.KEY_DETAIL;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.KEY_END;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.KEY_LABEL;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.KEY_PORT;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.KEY_POWER;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.KEY_START;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.KEY_STATE;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.timerRef;

public class DashboardScreenPresenter {
    private final static String TAG = DashboardScreenPresenter.class.getSimpleName();

    private DashboardScreenActivity mContext;
    private TimerList mTimerList;
    private List<TimerItem> timerItems;

    TimerItemHelper timerItemHelper;


    private long establishStartAt;

    DashboardScreenPresenter(DashboardScreenActivity mContext) {
        this.mContext = mContext;
        runningCurrentTime(); // Cập nhật thời gian thực theo giờ hiện tại
        establishTime();    // cập nhật số giờ đã chạy của ứng dụng
        initDatabase(); // Thiết lập kết nối đến firebase
        Reader.now(); // Tiến hành lắng nghe và đọc dữ liệu được gửi từ Arduino
    }

    private void camera2OnPause() {
    }

    private void camera2OnRemuse() {
    }

    private void initDatabase() {
        timerItemHelper = new TimerItemHelper(mContext);
    }

    private void runningCurrentTime() {
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Date date = new Date(System.currentTimeMillis());
                mContext.updateCurrentTimeView(simpleDateFormat.format(date));
            }
        }, 1000, 1000);
    }

    private void establishTime() {
        establishStartAt = System.currentTimeMillis();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                String establishedTime = TimeUtils.millisToCounter(System.currentTimeMillis() - establishStartAt);
                mContext.updateEstablishTimeView(establishedTime);
            }
        }, 1000, 1000);
    }

    void powerOffConfirm() {
        SweetAlertDialog exitConfirm = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);
        exitConfirm.setTitleText("ĐÓNG ỨNG DỤNG?");
        exitConfirm.setConfirmText("Đóng");
        exitConfirm.setCancelText("Hủy");
        // Bắt sự kiện khi người dùng nhấn đồng ý đóng
        exitConfirm.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                mContext.finish();
            }
        });
        // Bắt sự kiện khi người dùng hủy đóng
        exitConfirm.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
            }
        });
        exitConfirm.show();
    }

    void updateOrReplaceTimerItem(TimerItem timerItem) {
        if (timerItems == null) {
            timerItems = new ArrayList<>();
        } else {
            // nếu đã có thì xóa cái cũ
            for (TimerItem ti : timerItems) {
                if (ti.getId() == timerItem.getId()) {
                    timerItems.remove(ti);
                    break;
                }
            }
            // Tiến hành thêm cái mới
            timerItems.add(timerItem);
            mTimerList.notifyDataChanged();
        }
    }

    void loadTimerList(RecyclerView timerListView) {
        // Khởi tạo danh sách đã có
        timerItems = new ArrayList<>();
        // Lắng nghe thay đổi của danh sách từ firebase
        timerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                     if (ds.getKey() != null) {
                        TimerItem timerItem = new TimerItem();
                        timerItem.setId(Long.parseLong(ds.getKey()));
                        for (DataSnapshot ds2 : ds.getChildren()) {
                            if (ds2.getKey() != null && ds2.getKey().equals(KEY_PORT) && ds2.getValue() != null) {
                                timerItem.setPort(ds2.getValue().toString());
                            } else if (ds2.getKey() != null && ds2.getKey().equals(KEY_LABEL) && ds2.getValue() != null) {
                                timerItem.setLabel(ds2.getValue().toString());
                            } else if (ds2.getKey() != null && ds2.getKey().equals(KEY_POWER) && ds2.getValue() != null) {
                                timerItem.setPower(ds2.getValue().toString());
                            } else if (ds2.getKey() != null && ds2.getKey().equals(KEY_STATE) && ds2.getValue() != null) {
                                timerItem.setState(ds2.getValue().toString());
                            } else if (ds2.getKey() != null && ds2.getKey().equals(KEY_START) && ds2.getValue() != null) {
                                timerItem.setStart(ds2.getValue().toString());
                            } else if (ds2.getKey() != null && ds2.getKey().equals(KEY_END) && ds2.getValue() != null) {
                                timerItem.setEnd(ds2.getValue().toString());
                            } else if (ds2.getKey() != null && ds2.getKey().equals(KEY_DETAIL) && ds2.getValue() != null) {
                                timerItem.setDetail(ds2.getValue().toString());
                            }
                        }
                        // Cập nhật hoặc thêm mới vào đây
                        updateOrReplaceTimerItem(timerItem);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Lỗi khi đọc dữ liệu.", error.toException());
            }
        });
        // Đưa danh sách hẹn giờ vào khung nhìn
        mTimerList = new TimerList(timerListView, timerItems);
    }
}
