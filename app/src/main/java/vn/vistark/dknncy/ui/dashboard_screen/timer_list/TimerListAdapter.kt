package vn.vistark.dknncy.ui.dashboard_screen.timer_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import vn.vistark.dknncy.R
import vn.vistark.dknncy.data.modal.TimerItem

class TimerListAdapter(private val timerItems: ArrayList<TimerItem>) : RecyclerView.Adapter<TimerListViewHolder>() {

    var onLongClick: ((TimerItem) -> Unit)? = null
    var onClick: ((TimerItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerListViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.timer_item_layout, parent, false)
        return TimerListViewHolder(v)
    }

    override fun onBindViewHolder(holder: TimerListViewHolder, position: Int) {
        val currentTimerItem = timerItems[position]
        holder.bind(currentTimerItem)


        // Sự kiện khi nhấn giữ vào item (Xóa)
        holder.rlTimerItemRoot.setOnLongClickListener {
            onLongClick?.invoke(currentTimerItem)
            true
        }

        // Sự kiện khi nhấn vào item (Sửa)
        holder.rlTimerItemRoot.setOnClickListener {
            onClick?.invoke(currentTimerItem)
        }
    }

    override fun getItemCount(): Int {
        return timerItems.size
    }
}