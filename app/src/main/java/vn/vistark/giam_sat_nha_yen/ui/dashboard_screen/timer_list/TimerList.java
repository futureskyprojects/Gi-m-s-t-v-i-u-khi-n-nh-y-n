package vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.timer_list;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import vn.vistark.giam_sat_nha_yen.data.arduino_community.ArduinoCommunity;
import vn.vistark.giam_sat_nha_yen.data.modal.Auto;
import vn.vistark.giam_sat_nha_yen.data.modal.CurrentDetail;
import vn.vistark.giam_sat_nha_yen.data.modal.DefaultTimerItem;
import vn.vistark.giam_sat_nha_yen.data.modal.TimerItem;
import vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig;
import vn.vistark.giam_sat_nha_yen.utils.TimeUtils;

/**
 * Project ĐK Nhà Yến
 * Created by Nguyễn Trọng Nghĩa on 10/19/2019.
 * Organization: Vistark Team
 * Email: dev.vistark@gmail.com
 */

public class TimerList {
    public final static String TAG = TimerList.class.getSimpleName();

    private RecyclerView mTimerList;
    private TimerListAdapter mTimerListApdater;
    public static List<TimerItem> timerItems;

    public TimerList(RecyclerView timerList) {
        this.mTimerList = timerList;
        mTimerListApdater = new TimerListAdapter();

        // Thiết lập danh sách theo chiều dọc
        LinearLayoutManager layoutManager = new LinearLayoutManager(mTimerList.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        mTimerList.setLayoutManager(layoutManager);
        mTimerList.setAdapter(mTimerListApdater);

        // Gọi timer đê tự động check
        timerForChecking();
    }

    public void RefreshData(List<TimerItem> timerItems) {
        TimerList.timerItems.clear();
        TimerList.timerItems = timerItems;
    }

    public RecyclerView getmTimerList() {
        return mTimerList;
    }

    public void setmTimerList(RecyclerView mTimerList) {
        this.mTimerList = mTimerList;
    }

    public TimerListAdapter getmTimerListApdater() {
        return mTimerListApdater;
    }

    public void setmTimerListApdater(TimerListAdapter mTimerListApdater) {
        this.mTimerListApdater = mTimerListApdater;
    }

    public List<TimerItem> getTimerItems() {
        return timerItems;
    }

    public void setTimerItems(List<TimerItem> timerItems) {
        TimerList.timerItems = timerItems;
    }

    public void notifyDataChanged() {
        try {
            this.mTimerListApdater.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void timerForChecking() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                checkStateUpdateAndSendData();
            }
        }, 1000, 2500);
    }

    private void checkStateUpdateAndSendData() {
        if (TimerList.timerItems == null || mTimerListApdater == null)
            return;
        // Lặp trong danh sách các cổng
        for (int i = 0; i < TimerList.timerItems.size(); i++) {
            // Nếu timer này đang được bật nguồn
            if (TimerList.timerItems.get(i).isPower()) {
                if (TimerList.timerItems.get(i).getId() != DefaultTimerItem.timerPortA.getId() ||
                        TimerList.timerItems.get(i).getId() != DefaultTimerItem.timerPortB.getId() ||
                        TimerList.timerItems.get(i).getId() != DefaultTimerItem.timerPortC.getId()) {
                    // Nếu là các timer thông thường
                    // Tiến hành kiểm tra xem nó có còn trong khung giờ không
                    if (TimeUtils.isInTimer(TimerList.timerItems.get(i).getStart(), TimerList.timerItems.get(i).getEnd())) {
                        TimerList.timerItems.get(i).setState(true);
//                    Log.w(TAG, "checkStateUpdateAndSendData: Đang trong khung giờ.");
                    } else {
                        TimerList.timerItems.get(i).setState(false);
//                    Log.e(TAG, "checkStateUpdateAndSendData: Đang ngoài khung giờ.");
                    }
                } else {
                    try {
                        int currHumidity = Integer.parseInt(CurrentDetail.humidity);
                        int currTemperature = Integer.parseInt(CurrentDetail.temperature);

                        // Nếu là khung giờ mặc định của các cổng A,B,C
                        if (TimeUtils.isInTimer(TimerList.timerItems.get(i).getStart(), TimerList.timerItems.get(i).getEnd())) {
                            // Đối với hai máy bơm, cung cấp độ ẩm
                            if (TimerList.timerItems.get(i).getId() == DefaultTimerItem.timerPortA.getId() ||
                                    TimerList.timerItems.get(i).getId() == DefaultTimerItem.timerPortB.getId()) {
                                if (currHumidity < Auto.humidityStartValue) {
                                    TimerList.timerItems.get(i).setState(true);
                                }

                                if (currHumidity > Auto.humidityEndValue) {
                                    TimerList.timerItems.get(i).setState(false);
                                }
                            } else {
                                // Ngược lại đối với Port C là đèn, cung cấp nhiệt độ
                                if (currTemperature < Auto.tempStartValue) {
                                    TimerList.timerItems.get(i).setState(true);
                                }

                                if (currTemperature > Auto.tempEndValue) {
                                    TimerList.timerItems.get(i).setState(false);
                                }
                            }
                        } else {
                            TimerList.timerItems.get(i).setState(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                TimerList.timerItems.get(i).setState(false);
            }
            FirebaseConfig.updateData(TimerList.timerItems.get(i)); // -> Tự động sẽ được cập nhật
            // Tiến hành gửi thông tin xuống arduino
            ArduinoCommunity.sendCommand(TimerList.timerItems.get(i).getPort(), TimerList.timerItems.get(i).isState());
        }
    }

    public static void removeRemoveAndRefresh(long timerItemId) {
        List<TimerItem> timerItems = new ArrayList<>();
        if (TimerList.timerItems != null) {
            for (int i = 0; i < TimerList.timerItems.size(); i++) {
                if (TimerList.timerItems.get(i).getId() != timerItemId)
                    timerItems.add(TimerList.timerItems.get(i));
            }
            TimerList.timerItems.clear();
        }
        if (TimerList.timerItems != null)
            TimerList.timerItems.addAll(timerItems);
    }
}
