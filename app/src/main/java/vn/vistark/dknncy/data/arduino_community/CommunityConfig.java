package vn.vistark.dknncy.data.arduino_community;

import android.hardware.usb.UsbDeviceConnection;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;

public class CommunityConfig {
    public static UsbSerialDriver defaultUsbSerialDriver;
    public static UsbDeviceConnection defaultUsbDeviceConnection;
    public static UsbSerialPort defaultUsbSerialPort;
    public static int defaultBaudrate = 57600;
    public static int defaultDataBits = 8;
    public static char[] buffer = new char[]{'0', '2', '4', '6'};
    public static char[] bufferPrevious = new char[]{'0', '0', '0', '0'};

}
