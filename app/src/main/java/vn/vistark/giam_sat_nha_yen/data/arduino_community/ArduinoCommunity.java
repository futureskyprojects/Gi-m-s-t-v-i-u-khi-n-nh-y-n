package vn.vistark.giam_sat_nha_yen.data.arduino_community;

import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig;
import vn.vistark.giam_sat_nha_yen.data.modal.CurrentDetail;
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.DashboardScreenActivity;
import vn.vistark.giam_sat_nha_yen.utils.CommonUtils;

public class ArduinoCommunity {
    public final static String TAG = ArduinoCommunity.class.getSimpleName();

    public static UsbManager manager;
    public static List<UsbSerialDriver> availableDrivers;
    public static Thread communityThread;

    public static boolean init(AppCompatActivity mContext) {
        manager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            return false;
        } else {
            // Nếu mọi thứ đều ổn, lấy kết nối đầu tiên
            CommunityConfig.defaultUsbSerialDriver = availableDrivers.get(0);
            return openConnection(mContext);
        }
    }

    public static boolean openConnection(final AppCompatActivity mContext) {
        try {
            CommunityConfig.defaultUsbDeviceConnection = manager.openDevice(CommunityConfig.defaultUsbSerialDriver.getDevice());
        } catch (Exception e) {
            e.printStackTrace();
            return init(mContext);
        }
        if (CommunityConfig.defaultUsbDeviceConnection == null) {
            Log.e(TAG, "Không thể mở kết nối đến thiết bị ngoại vi.");
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toasty.warning(mContext, "Vui lòng ngắt kết nối với thiết bị ngoại vi sau đó kết nối lại.", Toasty.LENGTH_SHORT, false).show();
                }
            });
            return false;
        } else {
            return openCommunity();
        }
    }

    public static void closeCommunity() {
        try {
            if (communityThread != null) {
                if (communityThread.isAlive() && !communityThread.isInterrupted()) {
                    communityThread.interrupt();
                }
                communityThread = null;
            }
            if (CommunityConfig.defaultUsbSerialPort != null)
                CommunityConfig.defaultUsbSerialPort.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean openCommunity() {
        closeCommunity();
        CommunityConfig.defaultUsbSerialPort = CommunityConfig.defaultUsbSerialDriver.getPorts().get(0);
        try {
            CommunityConfig.defaultUsbSerialPort.open(CommunityConfig.defaultUsbDeviceConnection);
            CommunityConfig.defaultUsbSerialPort.setParameters(
                    CommunityConfig.defaultBaudrate,
                    CommunityConfig.defaultDataBits,
                    UsbSerialPort.STOPBITS_1,
                    UsbSerialPort.PARITY_NONE
            );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Mở giao tiếp không thành công!");
            return false;
        }
    }


    public static void openReadOrWrite(final DashboardScreenActivity mContext) {
        try {
            while (true) {
                if (CurrentDetail.humidity == "" && CurrentDetail.temperature == "") {
                    byte[] bs = "a".getBytes();
                    int numBytesWrite = CommunityConfig.defaultUsbSerialPort.write(bs, 500);
                    if (numBytesWrite >= bs.length) {
                        Log.i(TAG, "openReadOrWrite: Đã ghi thành công! (" + numBytesWrite + "/" + bs.length + ")");
                    } else {
                        Log.i(TAG, "openReadOrWrite: Ghi không thành công (" + numBytesWrite + "/" + bs.length + ")");
                    }
                }
//                renew();
                // Đọc
                byte[] buffer = new byte[16];
                int numBytesRead = 0;
                try {
                    numBytesRead = CommunityConfig.defaultUsbSerialPort.read(buffer, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    //init(mContext);
                }
                String s = new String(buffer);
                s = s.substring(0, numBytesRead);
//                Log.e(TAG, "openReadOrWrite: " + s);
                extractInfo(s, mContext);

                ///

                // Ghi
//                if (CommunityConfig.buffer != CommunityConfig.bufferPrevious) {
//                    CommunityConfig.bufferPrevious = CommunityConfig.buffer;
//
////                    renew();
//
//                }
                if (CurrentDetail.humidity != "" && CurrentDetail.temperature != "") {
                    String cmd = new String(CommunityConfig.buffer);
                    cmd += "\r\n";
                    if (!cmd.isEmpty()) {
                        Log.i(TAG, "Đã gửi: " + cmd);
                        byte[] bytes = cmd.getBytes();
                        int numBytesWrite = CommunityConfig.defaultUsbSerialPort.write(bytes, 1000);
                        if (numBytesWrite >= bytes.length) {
                            Log.i(TAG, "openReadOrWrite: Đã ghi thành công! (" + numBytesWrite + "/" + bytes.length + ")");
                        } else {
                            Log.i(TAG, "openReadOrWrite: Ghi không thành công (" + numBytesWrite + "/" + bytes.length + ")");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toasty.warning(mContext, "Mất kết nối đến thiết bị ngoại vi. Tiến hành dừng chương trình.", Toasty.LENGTH_SHORT, false).show();
                    mContext.finish();
                }
            });
        }
    }

    private static void renew() {
        if (CommunityConfig.defaultUsbSerialPort != null) {
            try {
                CommunityConfig.defaultUsbSerialPort.close();
                SystemClock.sleep(100);
                CommunityConfig.defaultUsbSerialPort.open(CommunityConfig.defaultUsbDeviceConnection);
                CommunityConfig.defaultUsbSerialPort.setParameters(
                        CommunityConfig.defaultBaudrate,
                        CommunityConfig.defaultDataBits,
                        UsbSerialPort.DATABITS_8,
                        UsbSerialPort.PARITY_NONE
                );
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Mở giao tiếp không thành công!");
            }
        }
    }

    private static void extractInfo(String s, DashboardScreenActivity mContext) {
        final Pattern pattern = Pattern.compile("s[0-9]{4}e", Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            String gotted = matcher.group(0);
            if (gotted != null) {
                CurrentDetail.temperature = gotted.substring(1, 3);
                CurrentDetail.humidity = gotted.substring(3, 5);
//                Log.d(TAG, "Nhiệt độ " + CurrentDetail.temperature + " - Độ ẩm: " + CurrentDetail.humidity);
                mContext.updateTemperature(CurrentDetail.temperature);
                mContext.updateHumidityView(CurrentDetail.humidity);
                FirebaseConfig.updateTemperatureAndHumidity();
            }
        }
    }

    public static void asyncCommunity(final DashboardScreenActivity mContext) {
        if (communityThread == null) {
            communityThread = new Thread() {
                @Override
                public void run() {
                    openReadOrWrite(mContext);
                }
            };
            communityThread.start();
        }
    }

    public static void sendCommand(String port, boolean state) {
//        if (CommunityConfig.buffer.length() > 512)
//            CommunityConfig.buffer = "";
        if (port.matches("A")) {
            if (state)
                CommunityConfig.buffer[0] = '1';
            else
                CommunityConfig.buffer[0] = '0';
        }
        if (port.matches("B")) {
            if (state)
                CommunityConfig.buffer[1] = '3';
            else
                CommunityConfig.buffer[1] = '2';
        }
        if (port.matches("C")) {
            if (state)
                CommunityConfig.buffer[2] = '5';
            else
                CommunityConfig.buffer[2] = '4';
        }
        if (port.matches("D")) {
            if (state)
                CommunityConfig.buffer[3] = '7';
            else
                CommunityConfig.buffer[3] = '6';
        }

    }
}
