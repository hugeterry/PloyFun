package cn.hugeterry.ployfun.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import java.io.File;


/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/8/7 19:27
 */
public class GetPhoto {
    /*
    * 从相册获取
        */
    public static void gallery(Context context) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((Activity)context).startActivityForResult(intent, 12);
    }

}
