package vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.timer_list;

import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import vn.vistark.giam_sat_nha_yen.data.arduino_community.ArduinoCommunity;
import vn.vistark.giam_sat_nha_yen.data.db.modal.TimerItem;
import vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig;
import vn.vistark.giam_sat_nha_yen.utils.TimeUtils;

public class TimerList {
    private RecyclerView mTimerList;
    private TimerListAdapter mTimerListApdater;
    private List<TimerItem> timerItems;

    public TimerList(RecyclerView timerList, List<TimerItem> timerItems) {
        this.mTimerList = timerList;
        this.timerItems = timerItems;
        mTimerListApdater = new TimerListAdapter(timerItems);

        // Thiết lập danh sách theo chiều dọc
        LinearLayoutManager layoutManager = new LinearLayoutManager(mTimerList.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        mTimerList.setLayoutManager(layoutManager);
        mTimerList.setAdapter(mTimerListApdater);

        // Gọi timer đê tự động check
        timerForChecking();
    }

    public void RefreshData(List<TimerItem> timerItems) {
        this.timerItems.clear();
        this.timerItems = timerItems;
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
        this.timerItems = timerItems;
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
        }, 1000, 1000);
    }

    private void checkStateUpdateAndSendData() {
        if (timerItems == null || mTimerListApdater == null)
            return;
        // Lặp trong danh sách các cổng
        for (int i = 0; i < timerItems.size(); i++) {
            // Nếu timer này đang được bật nguồn
            if (timerItems.get(i).isPower()) {
                // Tiến hành kiểm tra xem nó có còn trong khung giờ không
                timerItems.get(i).setState(
                        TimeUtils.isInTimer(timerItems.get(i).getStart(), timerItems.get(i).getEnd())
                );
                notifyDataChanged();
                FirebaseConfig.updateData(timerItems.get(i)); // -> Tự động sẽ được cập nhật
                // Tiến hành gửi thông tin xuống arduino
                ArduinoCommunity.sendCommand(timerItems.get(i).getPort(), timerItems.get(i).isState());
            }
        }
    }

    public void removeTimerItemById(String id) {
        for (TimerItem timerItem : timerItems) {
            if ((timerItem.getId() + "").equals(id)) {
                timerItems.remove(timerItem);
                notifyDataChanged();
            }
        }
    }
}
