package cn.hugeterry.ployfun.View;

import android.content.Intent;
import android.graphics.Bitmap;

import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import cn.hugeterry.ployfun.PolyfunKey;
import cn.hugeterry.ployfun.R;
import cn.hugeterry.ployfun.core.StartPolyFun;
import cn.hugeterry.ployfun.utils.GetPhoto;
import cn.hugeterry.ployfun.utils.SavePhoto;
import cn.hugeterry.ployfun.utils.ShareUtils;
import cn.hugeterry.updatefun.UpdateFunGO;
import cn.hugeterry.updatefun.config.UpdateKey;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/6/7 13:24
 */
public class MainActivity extends AppCompatActivity {
    private Uri uri;
    private String path;
    private LinearLayout ll_choose, ll_result;
    private ImageView iv;
    private Toolbar toolbar;
    private Button bt_save, bt_share;
    private SeekBar seekbar;
    private TextView seekbar_count;
    private LinearLayout progressbar;
    private static boolean isDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUpdate();
        initToolbar();
        initView();
    }

    private void setupUpdate() {
        UpdateKey.API_TOKEN = "";
        UpdateKey.APP_ID = "57b14ee3ca87a87c1b0009e6";
        //UpdateKey.DialogOrNotification=UpdateKey.WITH_DIALOG;通过Dialog来进行下载
        UpdateFunGO.init(this);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initView() {
        iv = (ImageView) findViewById(R.id.iv);
        ll_choose = (LinearLayout) findViewById(R.id.ll_choose);
        ll_result = (LinearLayout) findViewById(R.id.ll_result);
        bt_save = (Button) findViewById(R.id.bt_save);
        bt_share = (Button) findViewById(R.id.bt_share);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        seekbar_count = (TextView) findViewById(R.id.seekbar_count);
        progressbar = (LinearLayout) findViewById(R.id.progressbar);

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path == null || path.length() == 0) {
                    path = SavePhoto.writePhoto(iv.getDrawingCache());
                    SavePhoto.scanPhotos(path, MainActivity.this);
                    Snackbar.make(iv, "已保存到" + path, Snackbar.LENGTH_LONG).show();
                } else Snackbar.make(bt_save, "该图片已保存", Snackbar.LENGTH_SHORT).show();
            }


        });
        bt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path == null || path.length() == 0) {
                    path = SavePhoto.writePhoto(iv.getDrawingCache());
                    SavePhoto.scanPhotos(path, MainActivity.this);
                    Snackbar.make(iv, "已保存到" + path, Snackbar.LENGTH_LONG).show();
                }
                ShareUtils.shareImage(MainActivity.this, path);
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbar_count.setText(progress + 600 + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PolyfunKey.pc = seekBar.getProgress() + 600;
                Log.i("PolyFun pc", PolyfunKey.pc + "");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_choose:
                path = null;
                isDone = false;
                ll_result.setVisibility(View.INVISIBLE);
                ll_choose.setVisibility(View.VISIBLE);
                GetPhoto.gallery(MainActivity.this);
                break;
            case R.id.action_done:
                if (uri != null) {
                    if (!isDone) {
                        new StartPolyFun(this, iv, progressbar, ll_choose, ll_result).start();
                    } else Snackbar.make(iv, "已经制作完了，请重新选择图片", Snackbar.LENGTH_LONG).show();
                } else Snackbar.make(iv, "请先选择图片再制作", Snackbar.LENGTH_LONG).show();
                isDone = true;
                break;
            case R.id.action_about_me:
                ShareUtils.shareUri(this, "http://www.hugeterry.cn/about");
                break;
            case R.id.action_about:
                Intent intent_about = new Intent(this, AboutActivity.class);
                startActivity(intent_about);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 12:
                if (resultCode == RESULT_OK) {
                    uri = data.getData();
                    Log.i("uri", uri + "");
                    Bitmap image;
                    try {
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        iv.setImageBitmap(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateFunGO.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UpdateFunGO.onStop(this);
    }
}
