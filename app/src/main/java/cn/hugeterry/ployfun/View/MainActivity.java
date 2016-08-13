package cn.hugeterry.ployfun.View;

import android.content.Intent;
import android.graphics.Bitmap;

import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import cn.hugeterry.ployfun.R;
import cn.hugeterry.ployfun.core.StartPolyFun;
import cn.hugeterry.ployfun.utils.GetPhoto;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/6/7 13:24
 */
public class MainActivity extends AppCompatActivity {
    private ImageView iv, seeit;
    private Button doit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.iv);
        seeit = (ImageView) findViewById(R.id.seeit);
        doit = (Button) findViewById(R.id.doit);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetPhoto.gallery(MainActivity.this);

            }
        });
        doit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new StartPolyFun(MainActivity.this, iv, seeit);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("uri", "111");
        switch (requestCode) {
            case 12:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Log.i("uri", uri + "");
                    // 取得返回的Uri,基本上选择照片的时候返回的是以Uri形式，但是在拍照中有得机子呢Uri是空的，所以要特别注意
                    if (uri != null) {
                        Bitmap image;
                        try {
                            // 这个方法是根据Uri获取Bitmap图片的静态方法
                            image = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            iv.setImageBitmap(image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            // 这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
                            Bitmap image = extras.getParcelable("data");
                            if (image != null) {
                                iv.setImageBitmap(image);
                            }
                        }
                    }
                }
                break;
        }
    }
}
