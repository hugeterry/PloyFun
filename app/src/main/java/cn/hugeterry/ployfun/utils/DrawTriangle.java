package cn.hugeterry.ployfun.utils;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import cn.hugeterry.ployfun.core.DelaunayTriangulation.Pnt;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/5/1 16:07
 */
public class DrawTriangle {
    private static int[] x, y;
    private static Paint p;
    private static Path path;


    public static Paint drawTriangle(Pnt[] polygon, int fillColor, Canvas canvas,  Resources res) {
        x = new int[polygon.length];
        y = new int[polygon.length];
        for (int i = 0; i < polygon.length; i++) {
            x[i] = (int) polygon[i].coord(0);
            y[i] = (int) polygon[i].coord(1);
        }
        p = new Paint();
        path = new Path();
        p.setColor(fillColor);
        System.out.println("fillColorrrrrrrrrrrrrrrrrrrrrr=====" + fillColor);
        System.out.println(x[0] + "========" + y[0]);
        System.out.println(x[1] + "========" + y[1]);
        System.out.println(x[2] + "========" + y[2]);
        path.moveTo(x[0], y[0]);
        path.lineTo(x[1], y[1]);
        path.lineTo(x[2], y[2]);
        path.close();
        canvas.drawPath(path, p);
        return p;
    }
}
