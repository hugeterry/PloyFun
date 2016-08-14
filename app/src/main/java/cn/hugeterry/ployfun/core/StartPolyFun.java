package cn.hugeterry.ployfun.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.hugeterry.ployfun.Bean.MyPoint;
import cn.hugeterry.ployfun.PolyfunKey;
import cn.hugeterry.ployfun.core.DelaunayTriangulation.Pnt;
import cn.hugeterry.ployfun.core.DelaunayTriangulation.Triangle;
import cn.hugeterry.ployfun.core.DelaunayTriangulation.Triangulation;
import cn.hugeterry.ployfun.utils.ConvertGreyImg;
import cn.hugeterry.ployfun.utils.DrawTriangle;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/8/13 11:34
 */
public class StartPolyFun {

    public static Triangulation dt;
    public static List<MyPoint> pnts = new ArrayList<MyPoint>();
    public static long time = System.currentTimeMillis();

    private ExecutorService executor = Executors.newCachedThreadPool();

    private Canvas canvas;
    private Paint p;
    private Bitmap bmp;
    private String picPath = null;

    private int xd, yd;
    private int x, y;
    private int cx, cy, rgb;
    private int in = 0;

    public StartPolyFun(Context context, ImageView iv, ImageView seeit) {
        if (iv.isDrawingCacheEnabled()) {
            iv.destroyDrawingCache();
            Log.i("PolyFun TAG", "destory drawing cache");
        }
        iv.setDrawingCacheEnabled(true);
        //这是包围三角形的诞生地...
        Triangle initialTriangle = new Triangle(
                new Pnt(-PolyfunKey.initialSize, -PolyfunKey.initialSize),
                new Pnt(PolyfunKey.initialSize, -PolyfunKey.initialSize),
                new Pnt(0, PolyfunKey.initialSize));
        dt = new Triangulation(initialTriangle);
        //**************读取图片所在位置******************
        bmp = iv.getDrawingCache();
        int height = bmp.getHeight();
        int width = bmp.getWidth();
        Log.i("PolyFun TAG", "height:" + height + ",width:" + width);
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

        Bitmap resultBitmap = ConvertGreyImg.convertGreyImg(bmp);
        canvas = new Canvas(bmp);
        for (int y = 1; y < height - 1; ++y) {
            for (int x = 1; x < width - 1; ++x) {
                int rgb = resultBitmap.getPixel(x, y);
                rgb = (rgb & 0xff0000) >> 16;//留灰度
                if (rgb > PolyfunKey.graMax) {
                    pnts.add(new MyPoint(x, y));
                }
            }
        }
        Log.i("PolyFun TAG", "未过滤点集有" + pnts.size() + "个，正在随机排序");
        Collections.shuffle(pnts);
        int count = Math.min(pnts.size(), PolyfunKey.pc);
        Log.i("PolyFun TAG", "正在加入点并剖分三角，请耐心等待");
        for (int i = 0; i < count; i++) {
            MyPoint p = pnts.get(i);
            //  bmp.setPixel(p.x, p.y, 0xffffffff);用来观测加入的三角点
            dt.delaunayPlace(new Pnt(p.x, p.y));
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

        Log.i("PolyFun TAG", "开始绘制最终结果");
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
                p = DrawTriangle.drawTriangle(vertices, rgb, canvas, context.getResources());
            }

        }

//        canvas.drawBitmap(bmp, width, height, p);
        seeit.setImageBitmap(bmp);

        Log.i("PolyFun TAG", "输出图片完成！耗时" + (System.currentTimeMillis() - time) + "ms");
    }

    private static volatile StartPolyFun sInst = null;

    public static StartPolyFun getInstance(Context context, ImageView iv, ImageView seeit) {
        StartPolyFun inst = sInst;  // <<< 在这里创建临时变量
        if (inst == null) {
            synchronized (StartPolyFun.class) {
                inst = sInst;
                if (inst == null) {
                    inst = new StartPolyFun(context, iv, seeit);
                    sInst = inst;
                }
            }
        }
        return inst;  // <<< 注意这里返回的是临时变量
    }
}
