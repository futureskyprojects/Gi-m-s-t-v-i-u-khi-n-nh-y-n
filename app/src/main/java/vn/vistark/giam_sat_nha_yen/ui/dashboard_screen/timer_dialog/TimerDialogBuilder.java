/*
 * Dùng để cấu hình khung nhìn và check các điều kiện cơ bản khi chọn thời gian
 */

package vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.timer_dialog;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;
import vn.vistark.giam_sat_nha_yen.R;
import vn.vistark.giam_sat_nha_yen.utils.TimeUtils;

class TimerDialogBuilder extends AlertDialog.Builder {
    private ImageView ivAddTimerIcon;
    private TextView tvAddTimerHeader;
    ImageView ivAddTimerClose;
    private EditText edtAddTimerLabelInput;
    private TextView edtAddTimerTimeStartSelector;
    private TextView edtAddTimerTimeEndSelector;
    Button btnAddTimerAddButton;
    private AppCompatRadioButton acrbAddTimerPortARadioButton;
    private AppCompatRadioButton acrbAddTimerPortBRadioButton;
    private AppCompatRadioButton acrbAddTimerPortCRadioButton;
    private AppCompatRadioButton acrbAddTimerPortDRadioButton;


    private String selectedPort = "A";
    final String defaultString = "HẸN GIỜ BẬT - TẮT | CỔNG ";


    TimerDialogBuilder(AppCompatActivity context) {
        super(context);
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.add_timer_dialog_layout, null);
        ivAddTimerIcon = v.findViewById(R.id.addTimerIcon);
        tvAddTimerHeader = v.findViewById(R.id.addTimerHeader);
        ivAddTimerClose = v.findViewById(R.id.addTimerClose);
        edtAddTimerLabelInput = v.findViewById(R.id.addTimerLabelInput);
        edtAddTimerTimeStartSelector = v.findViewById(R.id.addTimerTimeStartSelector);
        edtAddTimerTimeEndSelector = v.findViewById(R.id.addTimerTimeEndSelector);
        btnAddTimerAddButton = v.findViewById(R.id.addTimerAddButton);
        acrbAddTimerPortARadioButton = v.findViewById(R.id.addTimerPortARadioButton);
        acrbAddTimerPortBRadioButton = v.findViewById(R.id.addTimerPortBRadioButton);
        acrbAddTimerPortCRadioButton = v.findViewById(R.id.addTimerPortCRadioButton);
        acrbAddTimerPortDRadioButton = v.findViewById(R.id.addTimerPortDRadioButton);
        initBaseEvents();

        // Thiết lập giao diện
        this.setView(v);
    }

    private void initBaseEvents() {
        initEdtAddTimerTimeStartSelectorEvents();
        initEdtAddTimerTimeEndSelectorEvents();
        initAcrbAddTimerPortARadioButtonChangeEvent();
        initAcrbAddTimerPortBRadioButtonChangeEvent();
        initAcrbAddTimerPortCRadioButtonChangeEvent();
        initAcrbAddTimerPortDRadioButtonChangeEvent();
    }

    private void removeAllChecked() {
        acrbAddTimerPortARadioButton.setChecked(false);
        acrbAddTimerPortBRadioButton.setChecked(false);
        acrbAddTimerPortCRadioButton.setChecked(false);
        acrbAddTimerPortDRadioButton.setChecked(false);
    }

    private void initAcrbAddTimerPortDRadioButtonChangeEvent() {
        acrbAddTimerPortDRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllChecked();
                acrbAddTimerPortDRadioButton.setChecked(true);
                selectedPort = "D";
                if (getLabel().isEmpty() || getLabel().contains(defaultString)) {
                    setLabel(defaultString + selectedPort);
                }
            }
        });
    }

    private void initAcrbAddTimerPortCRadioButtonChangeEvent() {
        acrbAddTimerPortCRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllChecked();
                acrbAddTimerPortCRadioButton.setChecked(true);
                selectedPort = "C";
                if (getLabel().isEmpty() || getLabel().contains(defaultString)) {
                    setLabel(defaultString + selectedPort);
                }
            }
        });
    }

    private void initAcrbAddTimerPortBRadioButtonChangeEvent() {
        acrbAddTimerPortBRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllChecked();
                acrbAddTimerPortBRadioButton.setChecked(true);
                selectedPort = "B";
                if (getLabel().isEmpty() || getLabel().contains(defaultString)) {
                    setLabel(defaultString + selectedPort);
                }
            }
        });
    }


    private void initAcrbAddTimerPortARadioButtonChangeEvent() {
        acrbAddTimerPortARadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllChecked();
                acrbAddTimerPortARadioButton.setChecked(true);
                selectedPort = "A";
                if (getLabel().isEmpty() || getLabel().contains(defaultString)) {
                    setLabel(defaultString + selectedPort);
                }
            }
        });
    }

    private void initEdtAddTimerTimeEndSelectorEvents() {
        edtAddTimerTimeEndSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(edtAddTimerTimeStartSelector.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        setEndTimer(selectedHour + ":" + selectedMinute);
                        checkEndTimeWithStartTime();
                    }
                }, hour, minute, true); // Chọn định dạng là 24 giờ
                mTimePicker.setTitle("Chọn thời điểm bắt đầu");
                mTimePicker.show();
            }
        });
    }

    private void initEdtAddTimerTimeStartSelectorEvents() {
        edtAddTimerTimeStartSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(edtAddTimerTimeStartSelector.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        setStartTimer(selectedHour + ":" + selectedMinute);
                        checkEndTimeWithStartTime();
                    }
                }, hour, minute, true); // Chọn định dạng là 24 giờ
                mTimePicker.setTitle("Chọn thời điểm bắt đầu");
                mTimePicker.show();
            }
        });
    }

    // Thời gian bắt đầu không được bé hơn thời gian kết thúc
    public boolean checkEndTimeWithStartTime() {
        if (getStartTimer().contains(":") && getEndTimer().contains(":")) {
            // Tách lấy thời gian bắt đầu
            int startHours = Integer.parseInt(
                    getStartTimer().split(":")[0]
            );

            int startMinutes = Integer.parseInt(
                    getStartTimer().split(":")[1]
            );

            // Tách lấy thời gian két thúc
            int endHours = Integer.parseInt(
                    getEndTimer().split(":")[0]
            );

            int endMinutes = Integer.parseInt(
                    getEndTimer().split(":")[1]
            );

            // Tiến hành so sánh
            if ((endHours < startHours) || (endHours == startHours && endMinutes < startMinutes)) {
                Toasty.warning(ivAddTimerClose.getContext(), "Thời gian kết thúc cần lớn hơn thời gian bắt đầu").show();
                return false;
            }
        }
        return true;
    }


    void changeToAddLayout() {
        ivAddTimerIcon.setImageResource(R.drawable.stopwatch);
        tvAddTimerHeader.setText(R.string.addTimerOnOff);
        btnAddTimerAddButton.setText(R.string.add);
    }

    void changeToEditLayout() {
        ivAddTimerIcon.setImageResource(R.drawable.stopwatch_edit);
        tvAddTimerHeader.setText(R.string.editTimerOnOff);
        btnAddTimerAddButton.setText(R.string.edit);
    }

    void setSelectedPort(String port) {
        selectedPort = port;
        switch (selectedPort) {
            case "A":
                acrbAddTimerPortARadioButton.setChecked(true);
                break;
            case "B":
                acrbAddTimerPortBRadioButton.setChecked(true);
                break;
            case "C":
                acrbAddTimerPortCRadioButton.setChecked(true);
                break;
            case "D":
                acrbAddTimerPortDRadioButton.setChecked(true);
                break;
            default:
                acrbAddTimerPortARadioButton.setChecked(true);
                break;
        }
    }

    String getSelectedPort() {
        return selectedPort;
    }

    void setLabel(String label) {
        edtAddTimerLabelInput.setText(label);
    }

    String getLabel() {
        return edtAddTimerLabelInput.getText().toString();
    }

    void setStartTimer(String start) {
        start = TimeUtils.timerToStandard(start);
        edtAddTimerTimeStartSelector.setText(start);
    }

    String getStartTimer() {
        return edtAddTimerTimeStartSelector.getText().toString();
    }

    void setEndTimer(String end) {
        end = TimeUtils.timerToStandard(end);
        edtAddTimerTimeEndSelector.setText(end);
    }

    String getEndTimer() {
        return edtAddTimerTimeEndSelector.getText().toString();
    }
}
