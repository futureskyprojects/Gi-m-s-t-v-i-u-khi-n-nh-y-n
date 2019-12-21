package vn.vistark.giam_sat_nha_yen.utils;

import java.util.Comparator;

import vn.vistark.giam_sat_nha_yen.data.modal.TimerItem;

public class TimerItemComparator implements Comparator<TimerItem> {
    public int compare(TimerItem left, TimerItem right) {
        return (int) (left.getId() - right.getId());
    }
}
