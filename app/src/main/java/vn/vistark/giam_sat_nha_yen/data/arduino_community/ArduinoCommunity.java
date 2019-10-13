package vn.vistark.giam_sat_nha_yen.data.arduino_community;

import android.annotation.SuppressLint;
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
import vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.DashboardScreenActivity;
import vn.vistark.giam_sat_nha_yen.utils.CommonUtils;

public class ArduinoCommunity {
    public final static String TAG = ArduinoCommunity.class.getSimpleName();

    public static UsbManager manager;
    public static List<UsbSerialDriver> availableDrivers;
    public static Thread communityThread;

    private static ProgressDialog noUsbFound;

    public static boolean init(AppCompatActivity mContext) {
        manager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            Log.e(TAG, "Không tìm thấy bất cứ thiết bị nào được kết nối!");
            Toasty.error(mContext, "Không tìm được thiết bị ngoại vi nào.").show();
            return false;
        } else {
            // Nếu mọi thứ đều ổn, lấy kết nối đầu tiên
            CommunityConfig.defaultUsbSerialDriver = availableDrivers.get(0);
            return openConnection(mContext);
        }
    }

    public static boolean openConnection(Context mContext) {
        CommunityConfig.defaultUsbDeviceConnection = manager.openDevice(CommunityConfig.defaultUsbSerialDriver.getDevice());
        if (CommunityConfig.defaultUsbDeviceConnection == null) {
            Log.e(TAG, "Không thể mở kết nối đến thiết bị ngoại vi.");
            Toasty.error(mContext, "Không thể thiết lập kết nối đến thiết bị ngoại vi.").show();
            return false;
        } else {
            return openCommunity();
        }
    }

    public static boolean openCommunity() {
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

    public static void close() {
        try {
            CommunityConfig.defaultUsbSerialPort.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openReadOrWrite(final DashboardScreenActivity mContext) {
        try {
            // Đọc
            byte[] buffer = new byte[16];
            int numBytesRead = 0;
            try {
                numBytesRead = CommunityConfig.defaultUsbSerialPort.read(buffer, 1000);
            } catch (Exception e) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (noUsbFound == null) {
                            noUsbFound = CommonUtils.showNoUsbDialog(mContext);
                        }
                        if (!noUsbFound.isShowing()) {
                            noUsbFound.show();
                        }
                    }
                });
                init(mContext);
            }
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (noUsbFound != null && noUsbFound.isShowing()) {
                        noUsbFound.dismiss();
                    }
                }
            });
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void extractInfo(String s, DashboardScreenActivity mContext) {
        final Pattern pattern = Pattern.compile("s[0-9]{4}e", Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            String gotted = matcher.group(0);
            if (gotted != null) {
                String temperature = gotted.substring(1, 3);
                String humidity = gotted.substring(3, 5);
                mContext.updateTemperature(temperature);
                mContext.updateHumidityView(humidity);
                FirebaseConfig.updateTemperatureAndHumidity(temperature, humidity);
            }
        }
    }

    public static void asyncCommunity(final DashboardScreenActivity mContext) {
        if (communityThread != null) {
            communityThread = null;
        }
        communityThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    openReadOrWrite(mContext);
                }
            }
        };
        communityThread.start();
    }

    public static void sendCommand(String port, boolean state) {
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
