package cn.hugeterry.ployfun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import cn.hugeterry.ployfun.core.Pnt;
import cn.hugeterry.ployfun.core.Triangulation;
import cn.hugeterry.ployfun.core.Triangle;
import cn.hugeterry.ployfun.utils.ConvertGreyImg;
import cn.hugeterry.ployfun.utils.DrawTriangle;

public class MainActivity extends AppCompatActivity {

    public static Triangulation dt;
    public static List<MyPoint> pnts = new ArrayList<MyPoint>();
    public static long time = System.currentTimeMillis();

    private ImageView iv;
    private Canvas canvas;
    private Paint p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.iv);

        setupLowPoly();
    }

    private void setupLowPoly() {
        //这是包围三角形的诞生地...
        Triangle initialTriangle = new Triangle(
                new Pnt(-PolyfunKey.initialSize, -PolyfunKey.initialSize),
                new Pnt(PolyfunKey.initialSize, -PolyfunKey.initialSize),
                new Pnt(0, PolyfunKey.initialSize));
        dt = new Triangulation(initialTriangle);
        //**************读取图片所在位置******************
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        iv.measure(w, h);
        int height = iv.getMeasuredHeight();
        int width = iv.getMeasuredWidth();
        //加入四个端点
        dt.delaunayPlace(new Pnt(1, 1));
        dt.delaunayPlace(new Pnt(1, height - 1));
        dt.delaunayPlace(new Pnt(width - 1, 1));
        dt.delaunayPlace(new Pnt(width - 1, height - 1));
        //随机加入一些点
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 100; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            dt.delaunayPlace(new Pnt(x, y));
        }

        //***************************
//        System.out.println(width + "=====" + height);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.me)
                .copy(Bitmap.Config.ARGB_8888, true);
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
        System.out.println("未过滤点集有" + pnts.size() + "个，正在随机排序");
        Collections.shuffle(pnts);
        int count = Math.min(pnts.size(), PolyfunKey.pc);
        System.out.println("正在加入点并剖分三角，请耐心等待。。。");
        for (int i = 0; i < count; i++) {
            MyPoint p = pnts.get(i);
            bmp.setPixel(p.x, p.y, 0xffffffff);
            dt.delaunayPlace(new Pnt(p.x, p.y));//加入三角点
        }

        /**
         * 开始绘制最终结果
         */
        for (Triangle triangle : dt) {//取出所有三角形
            int xd = 0;
            int yd = 0;
            Pnt[] vertices = triangle.toArray(new Pnt[0]);//取出三个点

            boolean in = true;
            for (Pnt pnt : vertices) {//判断三个点都在图片内
                int x = (int) pnt.coord(0);
                int y = (int) pnt.coord(1);
                xd += x;
                yd += y;
                if (x < 0 || x > width || y < 0 || y > height) {
                    in = false;
                }
                if (in) {//三个点都在图内,才画三角形
                    //取中点颜色
                    int cx = xd / 3;
                    int cy = yd / 3;
                    System.out.println(cx + "--" + cy);
                    int rgb = bmp.getPixel(cx, cy);//三角形填充色
                    //绘画图形
                    p = DrawTriangle.drawTriangle(vertices, rgb, canvas, height, width, getResources());
                }
            }
        }

        canvas.drawBitmap(bmp, width, height, p);
        iv.setImageBitmap(bmp);
        System.out.println("输出图片完成！耗时" + (System.currentTimeMillis() - time) + "ms");

    }

}
