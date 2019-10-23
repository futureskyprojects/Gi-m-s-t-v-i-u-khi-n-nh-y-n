package vn.vistark.giam_sat_nha_yen.data.arduino_community;

import android.hardware.usb.UsbDeviceConnection;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;

/**
 * Project ĐK Nhà Yến
 * Created by Nguyễn Trọng Nghĩa on 10/19/2019.
 * Organization: Vistark Team
 * Email: dev.vistark@gmail.com
 */

public class CommunityConfig {
    public static UsbSerialDriver defaultUsbSerialDriver;
    public static UsbDeviceConnection defaultUsbDeviceConnection;
    public static UsbSerialPort defaultUsbSerialPort;
    public static int defaultBaudrate = 9600;
    public static int defaultDataBits = 8;
    public static String buffer = "";

}
