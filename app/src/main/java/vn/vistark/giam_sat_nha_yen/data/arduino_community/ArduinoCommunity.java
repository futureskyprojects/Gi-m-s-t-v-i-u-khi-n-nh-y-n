package vn.vistark.giam_sat_nha_yen.data.arduino_community;

import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.usb.UsbManager;
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

/**
 * Project ĐK Nhà Yến
 * Created by Nguyễn Trọng Nghĩa on 10/19/2019.
 * Organization: Vistark Team
 * Email: dev.vistark@gmail.com
 */

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
        CommunityConfig.defaultUsbDeviceConnection = manager.openDevice(CommunityConfig.defaultUsbSerialDriver.getDevice());
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
        } catch (IOException e) {
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


    private static void openReadOrWrite(final DashboardScreenActivity mContext) {
        try {
            while (true) {
                // Đọc
                byte[] buffer = new byte[16];
                int numBytesRead = 0;
                try {
                    ////////////////// LỖI Ở ĐÂY
                    numBytesRead = CommunityConfig.defaultUsbSerialPort.read(buffer, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    //init(mContext);
                }
                String s = new String(buffer);
                s = s.substring(0, numBytesRead);
                extractInfo(s, mContext);
                // Ghi
                if (!CommunityConfig.buffer.isEmpty()) {
                    Log.i(TAG, "Đã gửi: " + CommunityConfig.buffer);
                    byte[] bytes = CommunityConfig.buffer.getBytes();
                    int numBytesWrite = CommunityConfig.defaultUsbSerialPort.write(bytes, 1000);
                    if (numBytesWrite >= bytes.length) {
                        Log.i(TAG, "openReadOrWrite: Đã ghi thành công! (" + numBytesWrite + "/" + bytes.length + ")");
                    } else {
                        Log.i(TAG, "openReadOrWrite: Ghi không thành công (" + numBytesWrite + "/" + bytes.length + ")");
                    }
                    CommunityConfig.buffer = "";
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

    private static void extractInfo(String s, DashboardScreenActivity mContext) {
        final Pattern pattern = Pattern.compile("s[0-9]{4}e", Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            String gotted = matcher.group(0);
            if (gotted != null) {
                CurrentDetail.temperature = gotted.substring(1, 3);
                CurrentDetail.humidity = gotted.substring(3, 5);
                Log.d(TAG, "Nhiệt độ " + CurrentDetail.temperature + " - Độ ẩm: " + CurrentDetail.humidity);
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
        if (CommunityConfig.buffer.length() > 512)
            CommunityConfig.buffer = "";
        if (port.equals("A") && state)
            CommunityConfig.buffer += "1";
        else
            CommunityConfig.buffer += "0";
        if (port.equals("B") && state)
            CommunityConfig.buffer += "3";
        else
            CommunityConfig.buffer += "2";
        if (port.equals("C") && state)
            CommunityConfig.buffer += "5";
        else
            CommunityConfig.buffer += "4";
        if (port.equals("D") && state)
            CommunityConfig.buffer += "7";
        else
            CommunityConfig.buffer += "6";
    }
}
