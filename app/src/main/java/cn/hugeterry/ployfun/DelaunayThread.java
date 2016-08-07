package cn.hugeterry.ployfun;

import java.util.List;

import cn.hugeterry.ployfun.Bean.MyPoint;
import cn.hugeterry.ployfun.core.Pnt;
import cn.hugeterry.ployfun.core.Triangulation;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/8/7 09:27
 */
public class DelaunayThread implements Runnable {
    private Triangulation dt;
    private List<MyPoint> pnts;
    private int count;

    public DelaunayThread(Triangulation dt, List<MyPoint> pnts, int count) {
        this.dt = dt;
        this.pnts = pnts;
        this.count = count;
    }

    @Override
    public void run() {
        for (int i = 0; i < count; i++) {
            synchronized (pnts) {
                MyPoint p = pnts.get(i);
                //  三角剖分
                dt.delaunayPlace(new Pnt(p.x, p.y));//加入三角点
            }
        }
        PolyfunKey.started++;
    }
}
