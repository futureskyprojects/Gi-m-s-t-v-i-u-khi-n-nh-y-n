package vn.vistark.giam_sat_nha_yen.data.arduino_community;

import android.hardware.usb.UsbDeviceConnection;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;

public class CommunityConfig {
    public static UsbSerialDriver defaultUsbSerialDriver;
    public static UsbDeviceConnection defaultUsbDeviceConnection;
    public static UsbSerialPort defaultUsbSerialPort;
    public static int defaultBaudrate = 9600;
    public static int defaultDataBits = 8;
    public static String buffer = "";

}
