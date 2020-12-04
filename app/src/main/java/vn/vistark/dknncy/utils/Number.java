package vn.vistark.dknncy.utils;

public final class Number {
    public static int INT(double d) {
        return (int)Math.floor(d);
    }

    public static int MOD(int x, int y) {
        int z = x - (int)(y * Math.floor(((double)x / y)));
        if (z == 0) {
            z = y;
        }
        return z;
    }
}
