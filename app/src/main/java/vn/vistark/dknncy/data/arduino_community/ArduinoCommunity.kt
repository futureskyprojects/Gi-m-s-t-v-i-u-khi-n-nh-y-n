package vn.vistark.dknncy.data.arduino_community

import android.os.AsyncTask
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import es.dmoral.toasty.Toasty
import vn.vistark.dknncy.data.modal.CurrentDetail
import vn.vistark.dknncy.ui.dashboard_screen.DashboardScreenActivity

class ArduinoCommunity(val mContext: DashboardScreenActivity) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg p0: Void?): Void? {
        try {
            while (true) {
                if (CurrentDetail.humidity === "" && CurrentDetail.temperature === "") {
                    val bs = "a".toByteArray()
                    val numBytesWrite = CommunityConfig.defaultUsbSerialPort.write(bs, 500)
                    if (numBytesWrite >= bs.size) {
                        Log.i(ArduinoInit.TAG, "openReadOrWrite: Đã ghi thành công! (" + numBytesWrite + "/" + bs.size + ")")
                    } else {
                        Log.i(ArduinoInit.TAG, "openReadOrWrite: Ghi không thành công (" + numBytesWrite + "/" + bs.size + ")")
                    }
                }
                //                renew();
                // Đọc
                val buffer = ByteArray(16)
                var numBytesRead = 0
                try {
                    numBytesRead = CommunityConfig.defaultUsbSerialPort.read(buffer, 1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                    //init(mContext);
                }
                var s = String(buffer)
                s = s.substring(0, numBytesRead)
                //                Log.e(TAG, "openReadOrWrite: " + s);
                ArduinoInit.extractInfo(s, mContext)

                ///

                // Ghi
//                if (CommunityConfig.buffer != CommunityConfig.bufferPrevious) {
//                    CommunityConfig.bufferPrevious = CommunityConfig.buffer;
//
////                    renew();
//
//                }
                if (CurrentDetail.humidity !== "" && CurrentDetail.temperature !== "") {
                    var cmd = String(CommunityConfig.buffer)
                    cmd += "\r\n"
                    if (!cmd.isEmpty()) {
//                        Log.i(ArduinoInit.TAG, "Đã gửi: $cmd")
                        val bytes = cmd.toByteArray()
                        val numBytesWrite = CommunityConfig.defaultUsbSerialPort.write(bytes, 1000)
//                        if (numBytesWrite >= bytes.size) {
//                            Log.i(ArduinoInit.TAG, "openReadOrWrite: Đã ghi thành công! (" + numBytesWrite + "/" + bytes.size + ")")
//                        } else {
//                            Log.i(ArduinoInit.TAG, "openReadOrWrite: Ghi không thành công (" + numBytesWrite + "/" + bytes.size + ")")
//                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mContext.runOnUiThread {
                Toasty.warning(mContext, "Mất kết nối đến thiết bị ngoại vi. Tiến hành dừng chương trình.", Toasty.LENGTH_SHORT, false).show()
                mContext.finish()
            }
        }
        return null
    }

}