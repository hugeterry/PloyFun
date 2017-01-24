package cn.hugeterry.ployfun.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/8/14 17:21
 */
public class SavePhoto {
    public static String writePhoto(Bitmap bitmap) {
        File sdCardDir = Environment.getExternalStorageDirectory();
        String strPath = "/PolyFun/polyfun" + System.currentTimeMillis()
                + ".png";
        File file = new File(sdCardDir, strPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            FileOutputStream os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sdCardDir + strPath;
    }

    public static void scanPhotos(String filePath, Context context) {
        Intent intent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }
}
