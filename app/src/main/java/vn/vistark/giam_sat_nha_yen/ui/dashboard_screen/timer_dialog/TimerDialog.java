package vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.timer_dialog;

import android.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

import es.dmoral.toasty.Toasty;
import vn.vistark.giam_sat_nha_yen.data.db.modal.TimerItem;
import vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.DashboardScreenPresenter;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.timer_list.TimerListAdapter;

public class TimerDialog {
    private static AlertDialog alertDialog;

    public static void showAdd(AppCompatActivity mContext) {
        if (alertDialog != null && alertDialog.isShowing())
            alertDialog.dismiss();
        TimerDialogBuilder builder = new TimerDialogBuilder(mContext);
        builder.changeToAddLayout(); // Chuyển thành layout thêm
        builder.setSelectedPort("A");
        initAddEvents(builder);
        builder.setLabel(builder.defaultString + builder.getSelectedPort());
        initSubmitAddButton(builder); // Xử lý khi nhấn nút thêm
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showEdit(AppCompatActivity mContext, TimerItem timerItem, TimerListAdapter adapter) {
        if (alertDialog != null && alertDialog.isShowing())
            alertDialog.dismiss();
        TimerDialogBuilder builder = new TimerDialogBuilder(mContext);
        builder.changeToEditLayout(); // Chuyển thành layout thêm
        initAddEvents(builder);
        // Điền các thông tin đã có, kế thừa từ timer cần sửa
        builder.setLabel(timerItem.getLabel());
        builder.setSelectedPort(timerItem.getPort());
        builder.setStartTimer(timerItem.getStart());
        builder.setEndTimer(timerItem.getEnd());

        initSubmitEditButton(builder, timerItem, adapter); // Xử lý khi nhấn nút thêm
        alertDialog = builder.create();
        alertDialog.show();
    }

    private static void initSubmitAddButton(final TimerDialogBuilder builder) {
        builder.btnAddTimerAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (builder.getLabel().isEmpty()) {
                    Toasty.warning(builder.getContext(), "Vui lòng nhập nhãn cho mục.").show();
                } else if (!builder.getStartTimer().contains(":")) {
                    Toasty.warning(builder.getContext(), "Vui lòng chọn thời gian bắt đầu.").show();
                } else if (!builder.getEndTimer().contains(":")) {
                    Toasty.warning(builder.getContext(), "Vui lòng chọn thời gian kết thúc.").show();
                } else if (builder.checkEndTimeWithStartTime()) {
                    // Khởi tạo đổi tượng TimerItem mới để chứa dữ liệu
                    long tsLong = System.currentTimeMillis() / 1000;
                    TimerItem timerItem = new TimerItem(
                            tsLong,
                            builder.getLabel(),
                            builder.getSelectedPort(),
                            true,
                            false,
                            builder.getStartTimer(),
                            builder.getEndTimer(),
                            ""
                    );
                    FirebaseConfig.updateData(timerItem);
                    alertDialog.dismiss();
                }
            }
        });
    }

    private static void initSubmitEditButton(final TimerDialogBuilder builder, final TimerItem timerItem, final TimerListAdapter adapter) {
        builder.btnAddTimerAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (builder.getLabel().isEmpty()) {
                    Toasty.warning(builder.getContext(), "Vui lòng nhập nhãn cho mục.").show();
                } else if (!builder.getStartTimer().contains(":")) {
                    Toasty.warning(builder.getContext(), "Vui lòng chọn thời gian bắt đầu.").show();
                } else if (!builder.getEndTimer().contains(":")) {
                    Toasty.warning(builder.getContext(), "Vui lòng chọn thời gian kết thúc.").show();
                } else if (builder.checkEndTimeWithStartTime()) {
                    // Cập nhật đối tượng mới
                    timerItem.setLabel(builder.getLabel());
                    timerItem.setPort(builder.getSelectedPort());
                    timerItem.setStart(builder.getStartTimer());
                    timerItem.setEnd(builder.getEndTimer());

                    adapter.notifyDataSetChanged();

                    // Update lên firebase
                    FirebaseConfig.updateData(timerItem);
                    alertDialog.dismiss();
                }
            }
        });
    }

    private static void initAddEvents(TimerDialogBuilder builder) {
        closeButonEvent(builder);
    }

    private static void closeButonEvent(TimerDialogBuilder builder) {
        builder.ivAddTimerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }
}
