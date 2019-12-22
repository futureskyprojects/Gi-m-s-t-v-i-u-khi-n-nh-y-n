package vn.vistark.giam_sat_nha_yen.ui.dashboard_screen;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import vn.vistark.giam_sat_nha_yen.data.arduino_community.ArduinoCommunity;
import vn.vistark.giam_sat_nha_yen.data.arduino_community.CommunityConfig;
import vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig;
import vn.vistark.giam_sat_nha_yen.data.modal.Auto;
import vn.vistark.giam_sat_nha_yen.data.modal.DefaultTimerItem;
import vn.vistark.giam_sat_nha_yen.data.modal.TimerItem;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.timer_list.TimerList;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.video_transfer.VideoTransfer;
import vn.vistark.giam_sat_nha_yen.utils.CommonUtils;
import vn.vistark.giam_sat_nha_yen.utils.NetworkUtils;
import vn.vistark.giam_sat_nha_yen.utils.TimeUtils;

import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.KEY_END;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.KEY_LABEL;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.KEY_PORT;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.KEY_POWER;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.KEY_START;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.KEY_STATE;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.autoRef;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.timerRef;

public class DashboardScreenPresenter {
    private final static String TAG = DashboardScreenPresenter.class.getSimpleName();

    private DashboardScreenActivity mContext;
    private TimerList mTimerList;


    private ProgressDialog noInternetDialog;


    private long establishStartAt;

    DashboardScreenPresenter(DashboardScreenActivity mContext) {
        this.mContext = mContext;
        runningCurrentTime(); // Cập nhật thời gian thực theo giờ hiện tại
        establishTime();    // cập nhật số giờ đã chạy của ứng dụng
        noInternetDialog = CommonUtils.showNoInternetDialog(mContext);
    }


    // Cập nhật và hiển thị giờ hiện tại
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

    // Cập nhật và hiển thị giờ đã khởi chạy
    private void establishTime() {
        establishStartAt = System.currentTimeMillis();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                String establishedTime = TimeUtils.millisToCounter(System.currentTimeMillis() - establishStartAt);
                mContext.updateEstablishTimeView(establishedTime);

                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (NetworkUtils.isNetworkConnected(mContext)) {
                            if (noInternetDialog != null && noInternetDialog.isShowing()) {
                                noInternetDialog.dismiss();
                                if (VideoTransfer.init()) {
                                    //
                                } else {
                                    Toasty.error(mContext, "Kết nối đến máy chủ hình ảnh thất bại, hãy khởi động lại", Toasty.LENGTH_SHORT, false).show();
                                }
                            }
                        } else {
                            if (noInternetDialog != null && noInternetDialog.isShowing()) {
                                noInternetDialog.show();
                            }
                        }
                    }
                });
            }
        }, 1000, 1000);
    }

    // Hiển thị hộp thoại xác nhận thoát hay không
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
        //
        try {
            exitConfirm.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void changeListByStateOfAutoPower(Boolean state) {
        autoRef.child(Auto.powerKey).setValue(state);
        if (state) {
            FirebaseConfig.updateData(DefaultTimerItem.timerPortA.setPower(true));
            FirebaseConfig.updateData(DefaultTimerItem.timerPortB.setPower(true));
            FirebaseConfig.updateData(DefaultTimerItem.timerPortC.setPower(true));
            FirebaseConfig.updateData(DefaultTimerItem.timerPortD.setPower(true));
        }
    }

    private void addOrUpdateTimerItem(TimerItem timerItem) {
        if (TimerList.timerItems == null) {
            TimerList.timerItems = new ArrayList<>();
        } else {
            // Tiến hành thêm cái mới
            boolean isHave = false;
            for (int i = 0; i < TimerList.timerItems.size(); i++) {
                if (TimerList.timerItems.get(i).getId() == timerItem.getId()) {
                    TimerList.timerItems.get(i).update(timerItem);
                    isHave = true;
                    mTimerList.notifyDataChanged();
                    break;
                }
            }
//            TimerList.removeAndRefresh(timerItem.getId());
            if (TimerList.timerItems.isEmpty() || !isHave) {
                TimerList.timerItems.add(timerItem);
                mTimerList.notifyDataChanged();
            }
//            Collections.sort(TimerList.timerItems, new TimerItemComparator());
        }
    }

    void loadTimerList(RecyclerView timerListView) {
        // Khởi tạo danh sách đã có
        TimerList.timerItems = new ArrayList<>();

        // But nguồn auto
        autoRef.child(Auto.powerKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        Auto.powerValue = Boolean.parseBoolean(dataSnapshot.getValue().toString());
                        if (Auto.powerValue != mContext.getStateAutoPower()) {
                            // Nếu nút nguồn cho chế độ tự động có sự thay đổi
                            mContext.updateTimerAutoPower(Auto.powerValue);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Lỗi khi đọc dữ liệu.", databaseError.toException());
            }
        });
        // Lắng nghe thay đổi của danh sách từ firebase
        timerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                DefaultTimerItem.check(dataSnapshot);
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    try {
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
                                }
                            }
                            // Cập nhật hoặc thêm mới vào đây
                            addOrUpdateTimerItem(timerItem);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (ds.getKey() != null) {
                            // Nếu xuất hiện giá trị bất thường thì xóa nó đi
                            timerRef.child(ds.getKey()).removeValue();
                        }
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
        mTimerList = new TimerList(timerListView);
    }
}
