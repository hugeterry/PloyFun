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
    public static  void gallery(Context context) {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        ((Activity) context).startActivityForResult(intent, 2);
    }


}
