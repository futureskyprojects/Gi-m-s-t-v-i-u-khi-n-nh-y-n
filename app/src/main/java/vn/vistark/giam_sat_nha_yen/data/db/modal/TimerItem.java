package vn.vistark.giam_sat_nha_yen.data.db.modal;

import java.util.ArrayList;
import java.util.List;

public class TimerItem {
    private long id = -1;
    private String label = "";
    private String port = "";
    private boolean power = false;
    private boolean state = false;
    private String start = "";
    private String end = "";
    private String detail = "";

    public static List<TimerItem> getInstance() {
        List<TimerItem> timerItems = new ArrayList<>();
        timerItems.add(new TimerItem(1, "aaaa", "A", true, false, "8:00", "12:00", ""));
        timerItems.add(new TimerItem(2, "bbbb", "B", false, true, "8:00", "12:00", ""));
        timerItems.add(new TimerItem(1, "aaaa", "A", true, false, "8:00", "12:00", ""));
        timerItems.add(new TimerItem(2, "bbbb", "B", false, true, "8:00", "12:00", ""));
        timerItems.add(new TimerItem(1, "aaaa", "A", true, false, "8:00", "12:00", ""));
        timerItems.add(new TimerItem(2, "bbbb", "B", false, true, "8:00", "12:00", ""));
        return timerItems;
    }

    public TimerItem() {

    }


    public TimerItem(long id, String label, String port, boolean power, boolean state, String start, String end, String detail) {
        this.id = id;
        this.label = label;
        this.port = port;
        this.power = power;
        this.state = state;
        this.start = start;
        this.end = end;
        this.detail = detail;
    }

    public TimerItem(String id, String label, String port, String power, String state, String start, String end, String detail) {
        this.id = Integer.parseInt(id);
        this.label = label;
        this.port = port;
        this.power = Boolean.parseBoolean(power);
        this.state = Boolean.parseBoolean(state);
        this.start = start;
        this.end = end;
        this.detail = detail;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean isPower() {
        return power;
    }

    public void setPower(boolean power) {
        this.power = power;
    }

    public void setPower(String power) {
        this.power = Boolean.parseBoolean(power);
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setState(String state) {
        this.state = Boolean.parseBoolean(state);
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
