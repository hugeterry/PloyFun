package cn.hugeterry.ployfun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
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

public class MainActivity extends AppCompatActivity {
    static int graMax = 30;//边缘检测的阀值控制，值越大，出来的点越少
    static int pc = 400;//最后绘制的点数量控制
    static int initialSize = 10000;//包围三角形的大小
    static Triangulation dt;
    static List<MyPoint> pnts = new ArrayList<MyPoint>();
    static long time = System.currentTimeMillis();

    ImageView iv;
    Canvas canvas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.iv);
        iv.setDrawingCacheEnabled(true);

        iv.setImageResource(R.mipmap.ic_launcher);
        iv.buildDrawingCache();

        setupTriangle();
    }

    private void setupTriangle() {
        //这是包围三角形的诞生地...
        Triangle initialTriangle = new Triangle(
                new Pnt(-initialSize, -initialSize),
                new Pnt(initialSize, -initialSize),
                new Pnt(0, initialSize));
        dt = new Triangulation(initialTriangle);
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
        System.out.println(width + "=====" + height);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.me)
                .copy(Bitmap.Config.ARGB_8888, true);
        Bitmap resultBitmap = convertGreyImg(bmp);
        if (canvas == null) {
            canvas = new Canvas(bmp);
        }

        for (int y = 1; y < height - 1; ++y) {
            for (int x = 1; x < width - 1; ++x) {
                int rgb = resultBitmap.getPixel(x, y);
                rgb = (rgb & 0xff0000) >> 16;//留灰度
                if (rgb > graMax) {
                    //dt.delaunayPlace(new Pnt(x,y));//加入三角点
                    pnts.add(new MyPoint(x, y));
//					outBinary.setRGB(x, y, 0xffffffff);
                }
            }
        }
        System.out.println("未过滤点集有" + pnts.size() + "个，正在随机排序");
        Collections.shuffle(pnts);
        int count = Math.min(pnts.size(), pc);
        System.out.println("正在加入点并剖分三角，请耐心等待。。。");
        for (int i = 0; i < count; i++) {
            MyPoint p = pnts.get(i);
            resultBitmap.setPixel(p.x, p.y, 0xffffffff);
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
//                System.out.println(cx+"--"+cy);
                    int rgb = resultBitmap.getPixel(cx, cy);//三角形填充色
                    //绘画图形
                    drawSanJiao(vertices, rgb, canvas);
                }
            }
            iv.setImageBitmap(resultBitmap);
            System.out.println("输出图片完成！耗时" + (System.currentTimeMillis() - time) + "ms");
        }

    }

    public static void drawSanJiao(Pnt[] polygon, int fillColor, Canvas canvas) {
        int[] x = new int[polygon.length];
        int[] y = new int[polygon.length];
        for (int i = 0; i < polygon.length; i++) {
            x[i] = (int) polygon[i].coord(0);
            y[i] = (int) polygon[i].coord(1);
        }
        Paint p = new Paint();
        Path path = new Path();
        p.setColor(fillColor);
        path.lineTo(x[0], y[0]);
        path.moveTo(x[1], y[1]);
        path.lineTo(x[2], y[2]);
        path.close();
        canvas.drawPath(path, p);

    }

    /**
     * 将彩色图转换为灰度图
     *
     * @param img 位图
     * @return 返回转换好的位图
     */
    public Bitmap convertGreyImg(Bitmap img) {
        int width = img.getWidth();         //获取位图的宽
        int height = img.getHeight();       //获取位图的高

        int[] pixels = new int[width * height]; //通过位图的大小创建像素点数组

        img.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }
}
