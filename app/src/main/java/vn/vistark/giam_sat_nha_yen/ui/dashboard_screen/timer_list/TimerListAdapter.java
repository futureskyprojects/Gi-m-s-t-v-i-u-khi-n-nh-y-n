package vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.timer_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import vn.vistark.giam_sat_nha_yen.R;
import vn.vistark.giam_sat_nha_yen.data.db.modal.TimerItem;
import vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.timer_dialog.TimerDialog;

import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.myRef;
import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.timerRef;

public class TimerListAdapter extends RecyclerView.Adapter<TimerListViewHolder> {
    private List<TimerItem> timerItems;

    TimerListAdapter(List<TimerItem> timerItems) {
        this.timerItems = timerItems;
    }

    @NonNull
    @Override
    public TimerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.timer_item_layout, parent, false);
        return new TimerListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final TimerListViewHolder holder, final int position) {
        TimerItem currentTimerItem = timerItems.get(position);
        holder.updateTimerItemIdView(currentTimerItem.getId() + "");
        holder.updateTimerItemLabelView(currentTimerItem.getLabel());
        holder.updateTimerItemStartView(currentTimerItem.getStart());
        holder.updateTimerItemEndView(currentTimerItem.getEnd());
        holder.updateTimerSwitchCompatView(currentTimerItem.isPower());
        holder.updateTimerStateView(currentTimerItem.isState());
        holder.holderUpdateEmptyNotify(timerItems.size());

        // Sự kiện khi đổi trạng thái bật nguồn hoặc tắt nguồn thê thục hiện hoặc bỏ qua timer
        holder.scTimerSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                timerItems.get(position).setPower(b);
                try {
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FirebaseConfig.updateData(timerItems.get(position));
            }
        });

        // Sự kiện khi nhấn giữ vào item (Xóa)
        holder.rlTimerItemRoot.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                askForRemoveTimerItem(holder.rlTimerItemRoot.getContext(), position);
                return true;
            }
        });

        // Sự kiện khi nhấn vào item (Sửa)
        holder.rlTimerItemRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimerDialog.showEdit((AppCompatActivity) holder.rlTimerItemRoot.getContext(), timerItems.get(position), TimerListAdapter.this);
            }
        });
    }

    @Override
    public int getItemCount() {
        return timerItems.size();
    }

    private void askForRemoveTimerItem(final Context context, final int position) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setTitleText("XÁC NHẬN XÓA HẸN GIỜ?").show();
        sweetAlertDialog.setConfirmText("Xóa");
        sweetAlertDialog.setCancelText("Đóng");

        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                TimerItem timerItem = timerItems.get(position);
                timerRef.child(timerItem.getId() + "").removeValue();
                timerItems.remove(timerItem);
                notifyDataSetChanged();
                if (timerItems.indexOf(timerItem) < 0) {
                    Toasty.success(context, "Đã xóa").show();
                }
                sweetAlertDialog.dismiss();
            }
        });

        sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
            }
        });

        sweetAlertDialog.show();
    }
}
