package vn.vistark.giam_sat_nha_yen.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;

public final class IOUtils {
    public static File getRootFile(Context mContext) {
        return mContext.getExternalFilesDir(null).getParentFile().getParentFile().getParentFile().getParentFile();
    }
    public static String saveFileInDirectory(Context mContext, String directoryName, String fileName, byte[] data) {
        String PathX = getRootFile(mContext) + directoryName;
        File direct = new File(PathX);

        if (!direct.exists()) {
            File wallpaperDirectory = new File(PathX);
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File(PathX), fileName.replace("-","_"));
        file.deleteOnExit();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
            return file.getPath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
