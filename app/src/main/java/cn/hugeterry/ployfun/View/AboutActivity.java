package cn.hugeterry.ployfun.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import cn.hugeterry.ployfun.BuildConfig;
import cn.hugeterry.ployfun.R;
import cn.hugeterry.ployfun.utils.ShareUtils;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/2/24 02:24
 */
public class AboutActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initToolbar();
        initVersionTextView();
    }

    private void initVersionTextView() {
        tv_version = (TextView) findViewById(R.id.about_version);
        tv_version.setText("Version:" + BuildConfig.VERSION_NAME);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        getSupportActionBar().setTitle("关于");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_share:
                ShareUtils.shareText(this, "「PolyFun」为你喜欢的图片添上低平面风 下载地址：http://fir.im/polyfun");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
