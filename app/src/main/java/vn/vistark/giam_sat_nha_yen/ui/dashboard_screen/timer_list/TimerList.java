package vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.timer_list;

import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
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
import vn.vistark.giam_sat_nha_yen.utils.TimerItemComparator;

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
        mTimerList.setHasFixedSize(true);

        mTimerList.setLayoutManager(layoutManager);
        mTimerList.setAdapter(mTimerListApdater);

        // Gọi timer đê tự động check
        timerForChecking();
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
            TimerItem previous = TimerList.timerItems.get(i);
            if (Auto.powerValue) {
                // Nếu chế độ tự động được bật, những timer thủ công sẽ được bỏ qua
                if (TimerList.timerItems.get(i).getId() != DefaultTimerItem.timerPortA.getId() &&
                        TimerList.timerItems.get(i).getId() != DefaultTimerItem.timerPortB.getId() &&
                        TimerList.timerItems.get(i).getId() != DefaultTimerItem.timerPortC.getId() &&
                        TimerList.timerItems.get(i).getId() != DefaultTimerItem.timerPortD.getId())
                    continue;
                else {
                    // Nếu không phải là những timer thủ công, tiến hành kiếm tra điều kiện và bật/tắt
                    try {
                        int currHumidity = Integer.parseInt(CurrentDetail.humidity);
                        int currTemperature = Integer.parseInt(CurrentDetail.temperature);

                        if (TimerList.timerItems.get(i).getId() == DefaultTimerItem.timerPortA.getId() ||
                                TimerList.timerItems.get(i).getId() == DefaultTimerItem.timerPortB.getId()) {
                            // Nếu là các cổng máy bơm
                            if (((currHumidity < Auto.humidityStartValue) || (currTemperature > Auto.tempEndValue)) && TimerList.timerItems.get(i).isPower()) {
                                TimerList.timerItems.get(i).setState(true);
                            } else {
                                TimerList.timerItems.get(i).setState(false);
                            }
                        } else if (TimerList.timerItems.get(i).getId() == DefaultTimerItem.timerPortC.getId()) {
                            // Ngược lại đối với Port C là đèn, cung cấp nhiệt độ
                            if (((currTemperature < Auto.tempStartValue) || (currHumidity > Auto.humidityEndValue)) && TimerList.timerItems.get(i).isPower()) {
                                TimerList.timerItems.get(i).setState(true);
                            } else {
                                TimerList.timerItems.get(i).setState(false);
                            }
                        } else {
                            if (TimerList.timerItems.get(i).isPower())
                                TimerList.timerItems.get(i).setState(true);
                            else
                                TimerList.timerItems.get(i).setState(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ArduinoCommunity.sendCommand(TimerList.timerItems.get(i).getPort(), TimerList.timerItems.get(i).isState());
                }
            } else {
                // Nếu chế độ tự động tắt, tiến hành tắt các timer tự động
                if (TimerList.timerItems.get(i).getId() == DefaultTimerItem.timerPortA.getId() ||
                        TimerList.timerItems.get(i).getId() == DefaultTimerItem.timerPortB.getId() ||
                        TimerList.timerItems.get(i).getId() == DefaultTimerItem.timerPortC.getId() ||
                        TimerList.timerItems.get(i).getId() == DefaultTimerItem.timerPortD.getId()) {
                    TimerList.timerItems.get(i).setState(false);
                    TimerList.timerItems.get(i).setPower(false);
                } else {
                    // Sau đó kiểm tra khung giờ bình thường
                    if (TimeUtils.isInTimer(TimerList.timerItems.get(i).getStart(), TimerList.timerItems.get(i).getEnd()) && TimerList.timerItems.get(i).isPower()) {
                        TimerList.timerItems.get(i).setState(true);
//                    Log.w(TAG, "checkStateUpdateAndSendData: Đang trong khung giờ.");
                    } else {
                        TimerList.timerItems.get(i).setState(false);
//                    Log.e(TAG, "checkStateUpdateAndSendData: Đang ngoài khung giờ.");
                    }
                    ArduinoCommunity.sendCommand(TimerList.timerItems.get(i).getPort(), TimerList.timerItems.get(i).isState());
                }
            }
            if (previous.compare(TimerList.timerItems.get(i))) {
                Log.d(TAG, "checkStateUpdateAndSendData: Đã cập nhập");
                FirebaseConfig.updateData(TimerList.timerItems.get(i)); // -> Tự động sẽ được cập nhật
            }
            // Tiến hành gửi thông tin xuống arduino
        }
    }

    static void removeAndRefresh(long timerItemId) {
        List<TimerItem> timerItems = new ArrayList<>();
        if (TimerList.timerItems != null) {
            for (int i = 0; i < TimerList.timerItems.size(); i++) {
                if (TimerList.timerItems.get(i).getId() != timerItemId)
                    timerItems.add(TimerList.timerItems.get(i));
            }
            TimerList.timerItems.clear();
        }
        if (TimerList.timerItems != null) {
            Collections.sort(TimerList.timerItems, new TimerItemComparator());
            TimerList.timerItems.addAll(timerItems);
        }
    }
}
