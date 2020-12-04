package vn.vistark.dknncy.ui.dashboard_screen.timer_list

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import vn.vistark.dknncy.R
import vn.vistark.dknncy.data.firebase.FirebaseConfig
import vn.vistark.dknncy.data.modal.TimerItem
import vn.vistark.dknncy.ui.dashboard_screen.DashboardScreenActivity

class TimerListViewHolder(v: View) : ViewHolder(v) {
    private var tvTimerItemId: TextView = v.findViewById(R.id.timerItemId)
    private var tvTimerItemLabel: TextView = v.findViewById(R.id.timerItemLabel)
    private var tvTimerItemStart: TextView = v.findViewById(R.id.timerItemStart)
    private var tvTimerItemEnd: TextView = v.findViewById(R.id.timerItemEnd)
    var scTimerSwitchCompat: SwitchCompat = v.findViewById(R.id.timerSwitchCompat)
    var rlTimerItemRoot: RelativeLayout = v.findViewById(R.id.timerItemRoot)

    var context = tvTimerItemId.context as DashboardScreenActivity

    fun bind(ti: TimerItem) {
        tvTimerItemId.text = ti.id.toString()
        tvTimerItemId.isSelected = true
        tvTimerItemLabel.text = ti.label
        tvTimerItemLabel.isSelected = true
        tvTimerItemStart.text = ti.start
        tvTimerItemEnd.text = ti.end
        scTimerSwitchCompat.isChecked = ti.isPower
        tvTimerItemId.setBackgroundResource(if (ti.isPower && ti.isState) R.drawable.quepal else R.drawable.burning_orange)

        onSweetChange(ti)
    }

    private fun onSweetChange(ti: TimerItem) {
        // Sự kiện khi đổi trạng thái bật nguồn hoặc tắt nguồn thê thục hiện hoặc bỏ qua timer
        scTimerSwitchCompat.setOnClickListener {
            ti.isPower = !ti.isPower
            println(">>>>>>> Đã gạt thành [${ti.isPower}] cho ID: ${ti.id}")
            FirebaseConfig.updateData(ti)
        }
//        scTimerSwitchCompat.setOnCheckedChangeListener { compoundButton, b ->
//            ti.isPower = b
//            println(">>>>>>> Đã gạt thành [$b] cho ID: ${ti.id}")
//            FirebaseConfig.updateData(ti)
//        }
    }
}