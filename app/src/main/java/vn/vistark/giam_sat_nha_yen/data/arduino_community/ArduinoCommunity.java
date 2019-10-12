package vn.vistark.giam_sat_nha_yen.data.arduino_community;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.physicaloid.lib.Physicaloid;

import es.dmoral.toasty.Toasty;

public class ArduinoCommunity {
    public final static String TAG = ArduinoCommunity.class.getSimpleName();

    public static Physicaloid mPhysicaloid;

    public static void init(AppCompatActivity mContext) {
        mPhysicaloid = new Physicaloid(mContext);
        Log.w(TAG, "init: Tiến hành khởi động kết nối");
//        if (!mPhysicaloid.isOpened()) {
//            Log.w(TAG, "init: Đã mở!");
//            mPhysicaloid.clearReadListener();
//            mPhysicaloid.close();
//        }
        if (mPhysicaloid.open()) {
            Log.w(TAG, "init: Tiến hành mở");
            if (mPhysicaloid.isOpened()) {
                Log.w(TAG, "init: Mở thành công!");
                mPhysicaloid.clearReadListener();
                mPhysicaloid.setBaudrate(9600);
            }
        } else {
            Log.w(TAG, "init: Không mở thành công!");
            init(mContext);
//            Toasty.error(mContext, "Không thể kết nối đến mạch điều khiển").show();
        }
    }

    public static void sendCommand(String port, boolean state) {
        if (port.equals("A") && state)
            send("1");
        else
            send("0");
        if (port.equals("B") && state)
            send("3");
        else
            send("2");
        if (port.equals("C") && state)
            send("5");
        else
            send("4");
        if (port.equals("D") && state)
            send("7");
        else
            send("6");
    }


    // Gửi dữ liệu xuống arduino
    private static void send(String str) {
//        if (str.length() > 0) {
//            byte[] buf = str.getBytes();
//            if (mPhysicaloid.isOpened()) {
//                mPhysicaloid.write(buf, buf.length);
//            } else {
//                mPhysicaloid.open();
//            }
//        }
//        Log.d(TAG, "send: " + str);
    }

}
