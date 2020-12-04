package vn.vistark.dknncy.data.modal

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import vn.vistark.dknncy.data.firebase.FirebaseConfig
import vn.vistark.dknncy.ui.dashboard_screen.DashboardScreenActivity

class Auto {
    fun powerSync(context: DashboardScreenActivity) {
        FirebaseConfig.autoRef.child(powerKey).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    if (dataSnapshot.value != null) {
                        powerValue = dataSnapshot.value.toString().toBoolean()
                        if (powerValue != context.stateAutoPower) {
                            // Nếu nút nguồn cho chế độ tự động có sự thay đổi
                            context.updateTimerAutoPower(powerValue)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "Lỗi khi đọc dữ liệu.", databaseError.toException())
            }
        })
    }

    fun sync(rootRef: DatabaseReference?, context: DashboardScreenActivity) {
        rootRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.hasChild(crrRootKey)) {
                    updateAuto()
                }
                addAutoRefListener(context)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun addAutoRefListener(context: DashboardScreenActivity) {
        FirebaseConfig.autoRef.child(humidityKey).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    try {
                        if (ds.key == null)
                            return

                        // Nếu là key start trong Auto Humidity
                        if (ds.key == humidityStartKey) {
                            println("Đã nhận độ ẩm S: ${ds.key}")
                            if (ds.value != null) {
                                humidityStartValue = ds.value.toString().toIntOrNull() ?: return
                            } else {
                                FirebaseConfig.autoRef.child(humidityKey).child(humidityStartKey).setValue(humidityStartValue)
                            }
                        }

                        // Nếu là key end trong Auto Humidity
                        if (ds.key == humidityEndKey) {
                            println("Đã nhận độ ẩm E: ${ds.key}")
                            if (ds.value != null) {
                                humidityEndValue = ds.value.toString().toIntOrNull() ?: return
                            } else {
                                FirebaseConfig.autoRef.child(humidityKey).child(humidityEndKey).setValue(humidityEndValue)
                            }
                        }
                        context.updateAutoHumidity(humidityStartValue.toString() + "", humidityEndValue.toString() + "")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "Lỗi khi đọc dữ liệu.", databaseError.toException())
            }
        })
        FirebaseConfig.autoRef.child(tempKey).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    try {
                        if (ds.key == null) return
                        // Nếu là key start trong Auto Humidity
                        if (ds.key == tempStartKey) {
                            println("Đã nhận nhiệt độ S: ${ds.key}")
                            if (ds.value != null) {
                                tempStartValue = ds.value.toString().toInt()
                            } else {
                                FirebaseConfig.autoRef.child(tempKey).child(tempStartKey).setValue(tempStartValue)
                            }
                        }

                        // Nếu là key end trong Auto Humidity
                        if (ds.key == tempEndKey) {
                            println("Đã nhận nhiệt độ E: ${ds.key}")
                            if (ds.value != null) {
                                tempEndValue = ds.value.toString().toInt()
                            } else {
                                FirebaseConfig.autoRef.child(tempKey).child(tempEndKey).setValue(tempEndValue)
                            }
                        }
                        context.updateAutoTemp(tempStartValue.toString() + "", tempEndValue.toString() + "")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "Lỗi khi đọc dữ liệu.", databaseError.toException())
            }
        })
    }

    companion object {
        private val TAG = Auto::class.java.simpleName
        private const val crrRootKey = "Auto"
        private const val humidityKey = "humidity"
        private const val humidityStartKey = "start"
        private const val humidityEndKey = "end"
        private const val tempKey = "temp"
        private const val tempStartKey = "start"
        private const val tempEndKey = "end"
        const val powerKey = "power"
        var humidityStartValue = 70
        var humidityEndValue = 90
        var tempStartValue = 28
        var tempEndValue = 100

        @JvmField
        var powerValue = false
        fun updateAuto() {
            val humidityRef = FirebaseConfig.autoRef.child(humidityKey)
            val tempRef = FirebaseConfig.autoRef.child(tempKey)
            humidityRef.child(humidityStartKey).setValue(humidityStartValue)
            humidityRef.child(humidityEndKey).setValue(humidityEndValue)
            tempRef.child(tempStartKey).setValue(tempStartValue)
            tempRef.child(tempEndKey).setValue(tempEndValue)
            FirebaseConfig.autoRef.child(powerKey).setValue(powerValue)
        }
    }
}