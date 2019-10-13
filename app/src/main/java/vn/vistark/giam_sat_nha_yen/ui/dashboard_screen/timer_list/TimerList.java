package vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.timer_list;

import android.util.Log;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import vn.vistark.giam_sat_nha_yen.data.arduino_community.ArduinoCommunity;
import vn.vistark.giam_sat_nha_yen.data.db.modal.TimerItem;
import vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig;
import vn.vistark.giam_sat_nha_yen.utils.TimeUtils;

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
        }, 5000, 5000);
    }

    private void checkStateUpdateAndSendData() {
        if (TimerList.timerItems == null || mTimerListApdater == null)
            return;
        // Lặp trong danh sách các cổng
        for (int i = 0; i < TimerList.timerItems.size(); i++) {
            // Nếu timer này đang được bật nguồn
            if (TimerList.timerItems.get(i).isPower()) {
                // Tiến hành kiểm tra xem nó có còn trong khung giờ không
                if (TimeUtils.isInTimer(TimerList.timerItems.get(i).getStart(), TimerList.timerItems.get(i).getEnd())) {
                    TimerList.timerItems.get(i).setState(true);
                    Log.d(TAG, "checkStateUpdateAndSendData: Đang trong khung giờ.");
                } else {
                    TimerList.timerItems.get(i).setState(false);
                    Log.d(TAG, "checkStateUpdateAndSendData: Đang ngoài khung giờ.");
                }
                this.notifyDataChanged();
                FirebaseConfig.updateData(TimerList.timerItems.get(i)); // -> Tự động sẽ được cập nhật
                // Tiến hành gửi thông tin xuống arduino
                ArduinoCommunity.sendCommand(TimerList.timerItems.get(i).getPort(), TimerList.timerItems.get(i).isState());
            } else {
                TimerList.timerItems.get(i).setState(false);
            }
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
        TimerList.timerItems.addAll(timerItems);
    }
}
