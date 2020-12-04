package vn.vistark.dknncy.utils;

import androidx.appcompat.app.AppCompatActivity;

import es.dmoral.toasty.Toasty;

public class ExitUtils {
    private final static long MAX_TIME_BETTWEN = 500;

    private static int pressedCount = 0;
    private static long pressedMillis = -1;

    public static void Process(AppCompatActivity mContext) {
        if (pressedCount < 1) {
            pressedCount = 1;
            Toasty.info(mContext, "Nhấn thêm lần nữa để thoát").show();
        } else if (System.currentTimeMillis() - pressedMillis > MAX_TIME_BETTWEN) {
            pressedCount = 1;
            pressedMillis = System.currentTimeMillis();
            Toasty.info(mContext, "Nhấn thêm lần nữa để thoát").show();
        } else if (pressedCount > 0 && System.currentTimeMillis() - pressedMillis < MAX_TIME_BETTWEN) {
            mContext.finish();
        }
    }
}
