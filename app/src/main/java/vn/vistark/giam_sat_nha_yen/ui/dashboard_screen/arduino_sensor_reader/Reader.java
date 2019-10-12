package vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.arduino_sensor_reader;

import android.util.Log;

import com.physicaloid.lib.usb.driver.uart.ReadLisener;

import vn.vistark.giam_sat_nha_yen.data.arduino_community.ArduinoCommunity;

public class Reader {
    public final static String TAG = Reader.class.getSimpleName();

    public static void now() {
        if (ArduinoCommunity.mPhysicaloid.isOpened()) {
            Log.w(TAG, "now: Đã mở");
            ArduinoCommunity.mPhysicaloid.addReadListener(new ReadLisener() {
                @Override
                public void onRead(int size) {
                    try {
                        byte[] buf = new byte[size];
                        ArduinoCommunity.mPhysicaloid.read(buf, size);
                        String s = new String(buf);

                        if (s.matches("s[0-9]{4}e")) {
                            int temperature = Integer.parseInt(s.substring(1, 2));
                            int humidity = Integer.parseInt(s.substring(2, 4));
                            Log.d(TAG, ">>>> Nhiệt độ: " + temperature);
                            Log.d(TAG, ">>>> Độ ẩm: " + humidity);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Log.w(TAG, "now: Chưa mở");
        }
    }
}
