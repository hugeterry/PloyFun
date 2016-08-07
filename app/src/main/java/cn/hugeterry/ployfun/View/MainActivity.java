package cn.hugeterry.ployfun.View;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cn.hugeterry.ployfun.Bean.MyPoint;
import cn.hugeterry.ployfun.DelaunayThread;
import cn.hugeterry.ployfun.PolyfunKey;
import cn.hugeterry.ployfun.R;
import cn.hugeterry.ployfun.core.Pnt;
import cn.hugeterry.ployfun.core.Triangulation;
import cn.hugeterry.ployfun.core.Triangle;
import cn.hugeterry.ployfun.utils.ConvertGreyImg;
import cn.hugeterry.ployfun.utils.DrawTriangle;

public class MainActivity extends AppCompatActivity {

    private ExecutorService executor = Executors.newCachedThreadPool();

    public static Triangulation dt;
    public static List<MyPoint> pnts = new ArrayList<MyPoint>();
    public static long time = System.currentTimeMillis();

    private ImageView iv, seeit;
    private Button doit;
    private Canvas canvas;
    private Paint p;

    private int xd, yd;
    private int x, y;
    private int cx, cy, rgb;
    private int in = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.iv);
        seeit = (ImageView) findViewById(R.id.seeit);
        doit = (Button) findViewById(R.id.doit);
        iv.setDrawingCacheEnabled(true);

        doit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupLowPoly();
            }
        });

    }

    private void setupLowPoly() {
        //这是包围三角形的诞生地...
        Triangle initialTriangle = new Triangle(
                new Pnt(-PolyfunKey.initialSize, -PolyfunKey.initialSize),
                new Pnt(PolyfunKey.initialSize, -PolyfunKey.initialSize),
                new Pnt(0, PolyfunKey.initialSize));
        dt = new Triangulation(initialTriangle);
        //**************读取图片所在位置******************
        Bitmap bmp = iv.getDrawingCache();
        int height = bmp.getHeight();
        int width = bmp.getWidth();
        Log.i("PolyFun WH", "11111height:" + height + ",width:" + width);
        //加入四个端点
        dt.delaunayPlace(new Pnt(1, 1));
        dt.delaunayPlace(new Pnt(1, height - 1));
        dt.delaunayPlace(new Pnt(width - 1, 1));
        dt.delaunayPlace(new Pnt(width - 1, height - 1));
        //随机加入一些点
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            dt.delaunayPlace(new Pnt(x, y));
        }

        //***************************
//        System.out.println(width + "=====" + height);

        Bitmap resultBitmap = ConvertGreyImg.convertGreyImg(bmp);
        if (canvas == null) {
            canvas = new Canvas(bmp);
        }
        for (int y = 1; y < height - 1; ++y) {
            for (int x = 1; x < width - 1; ++x) {
                int rgb = resultBitmap.getPixel(x, y);
                rgb = (rgb & 0xff0000) >> 16;//留灰度
                if (rgb > PolyfunKey.graMax) {
                    pnts.add(new MyPoint(x, y));
                }
            }
        }
        Log.i("TAG", "DODOEEEEEEE");
        System.out.println("未过滤点集有" + pnts.size() + "个，正在随机排序");
        Collections.shuffle(pnts);
        int count = Math.min(pnts.size(), PolyfunKey.pc);
        System.out.println("正在加入点并剖分三角，请耐心等待。。。");
        Log.i("TAG", "DODOGGGGGGG");
        for (int i = 0; i < count; i++) {
            MyPoint p = pnts.get(i);
        //  bmp.setPixel(p.x, p.y, 0xffffffff);用来观测加入的三角点
        //  三角剖分
            dt.delaunayPlace(new Pnt(p.x, p.y));//加入三角点
        }
//        Thread t1 = new Thread(new DelaunayThread(dt, pnts, count));
//        Thread t2 = new Thread(new DelaunayThread(dt, pnts, count));
//        Thread t3 = new Thread(new DelaunayThread(dt, pnts, count));
//        Thread t4 = new Thread(new DelaunayThread(dt, pnts, count));
//
//        executor.execute(t1);
//        executor.execute(t2);
//        executor.execute(t3);
//        executor.execute(t4);
//        executor.shutdown();

        Log.i("TAG", "DODOOOOOO");
        /**
         * 开始绘制最终结果
         */
//        if (executor.isTerminated()) {
            for (Triangle triangle : dt) {//取出所有三角形
                xd = 0;
                yd = 0;
                Pnt[] vertices = triangle.toArray(new Pnt[0]);//取出三个点

                in = 3;
                for (Pnt pnt : vertices) {//判断三个点都在图片内
                    x = (int) pnt.coord(0);
                    y = (int) pnt.coord(1);
                    xd += x;
                    yd += y;
                    if (x < 0 || x > width || y < 0 || y > height) {
                        in -= 1;
                    }
                }
                if (in == 3) {//三个点都在图内,才画三角形
                    //取中点颜色
                    cx = xd / 3;
                    cy = yd / 3;
                    rgb = bmp.getPixel(cx, cy);//三角形填充色
                    //绘画图形
                    p = DrawTriangle.drawTriangle(vertices, rgb, canvas, getResources());
                }

            }

//        canvas.drawBitmap(bmp, width, height, p);
            seeit.setImageBitmap(bmp);
            System.out.println("输出图片完成！耗时" + (System.currentTimeMillis() - time) + "ms");
        }
//    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            executor.shutdownNow();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // Too bad
        } finally {
            Process.killProcess(Process.myPid());
        }
    }
}
