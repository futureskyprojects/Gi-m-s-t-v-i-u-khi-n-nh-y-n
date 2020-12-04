package vn.vistark.dknncy.data.arduino_community

import android.content.Context
import android.hardware.usb.UsbManager
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import es.dmoral.toasty.Toasty
import vn.vistark.dknncy.data.firebase.FirebaseConfig
import vn.vistark.dknncy.data.modal.CurrentDetail
import vn.vistark.dknncy.ui.dashboard_screen.DashboardScreenActivity
import java.util.regex.Pattern

class ArduinoInit {
    companion object {
        fun initializer(mContext: AppCompatActivity): Boolean {
            manager = mContext.getSystemService(Context.USB_SERVICE) as UsbManager
            availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
            return if (availableDrivers.isEmpty()) {
                false
            } else {
                // Nếu mọi thứ đều ổn, lấy kết nối đầu tiên
                CommunityConfig.defaultUsbSerialDriver = availableDrivers.get(0)
                openConnection(mContext)
            }
        }

        val TAG = ArduinoInit::class.java.simpleName
        var manager: UsbManager? = null
        lateinit var availableDrivers: List<UsbSerialDriver>
        var communityThread: Thread? = null

        @JvmStatic


        fun openConnection(mContext: AppCompatActivity): Boolean {
            try {
                CommunityConfig.defaultUsbDeviceConnection = manager!!.openDevice(CommunityConfig.defaultUsbSerialDriver.device)
            } catch (e: Exception) {
                e.printStackTrace()
                return initializer(mContext)
            }
            return if (CommunityConfig.defaultUsbDeviceConnection == null) {
                Log.e(TAG, "Không thể mở kết nối đến thiết bị ngoại vi.")
                mContext.runOnUiThread { Toasty.warning(mContext, "Vui lòng ngắt kết nối với thiết bị ngoại vi sau đó kết nối lại.", Toasty.LENGTH_SHORT, false).show() }
                false
            } else {
                openCommunity()
            }
        }

        fun closeCommunity() {
            try {
                if (communityThread != null) {
                    if (communityThread!!.isAlive && !communityThread!!.isInterrupted) {
                        communityThread!!.interrupt()
                    }
                    communityThread = null
                }
                if (CommunityConfig.defaultUsbSerialPort != null) CommunityConfig.defaultUsbSerialPort.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun openCommunity(): Boolean {
            closeCommunity()
            CommunityConfig.defaultUsbSerialPort = CommunityConfig.defaultUsbSerialDriver.ports[0]
            return try {
                CommunityConfig.defaultUsbSerialPort.open(CommunityConfig.defaultUsbDeviceConnection)
                CommunityConfig.defaultUsbSerialPort.setParameters(
                        CommunityConfig.defaultBaudrate,
                        CommunityConfig.defaultDataBits,
                        UsbSerialPort.STOPBITS_1,
                        UsbSerialPort.PARITY_NONE
                )
                true
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "Mở giao tiếp không thành công!")
                false
            }
        }

        private fun renew() {
            if (CommunityConfig.defaultUsbSerialPort != null) {
                try {
                    CommunityConfig.defaultUsbSerialPort.close()
                    SystemClock.sleep(100)
                    CommunityConfig.defaultUsbSerialPort.open(CommunityConfig.defaultUsbDeviceConnection)
                    CommunityConfig.defaultUsbSerialPort.setParameters(
                            CommunityConfig.defaultBaudrate,
                            CommunityConfig.defaultDataBits,
                            UsbSerialPort.DATABITS_8,
                            UsbSerialPort.PARITY_NONE
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, "Mở giao tiếp không thành công!")
                }
            }
        }

        fun extractInfo(_s: String, mContext: DashboardScreenActivity) {
            val s = _s.trim()
//            println(">> Nhận: [$s]")
            val pattern = Pattern.compile("s[0-9]{4}e", Pattern.MULTILINE)
            val matcher = pattern.matcher(s)
            while (matcher.find()) {
                val gotted = matcher.group(0)
                if (gotted != null) {
                    CurrentDetail.temperature = gotted.substring(1, 3)
                    CurrentDetail.humidity = gotted.substring(3, 5)
//                    Log.d(TAG, "Nhiệt độ " + CurrentDetail.temperature + " - Độ ẩm: " + CurrentDetail.humidity);
                    mContext.updateTemperature(CurrentDetail.temperature)
                    mContext.updateHumidityView(CurrentDetail.humidity)
//                    FirebaseConfig.updateTemperatureAndHumidity()
                }
            }
        }

        fun asyncCommunity(mContext: DashboardScreenActivity) {
            ArduinoCommunity(mContext).execute()
        }

        fun sendCommand(port: String, state: Boolean) {
//        if (CommunityConfig.buffer.length() > 512)
//            CommunityConfig.buffer = "";
            if (port == "A") {
                if (state) CommunityConfig.buffer[0] = '1' else CommunityConfig.buffer[0] = '0'
            }
            if (port == "B") {
                if (state) CommunityConfig.buffer[1] = '3' else CommunityConfig.buffer[1] = '2'
            }
            if (port == "C") {
                if (state) CommunityConfig.buffer[2] = '5' else CommunityConfig.buffer[2] = '4'
            }
            if (port == "D") {
                if (state) CommunityConfig.buffer[3] = '7' else CommunityConfig.buffer[3] = '6'
            }
        }
    }

}