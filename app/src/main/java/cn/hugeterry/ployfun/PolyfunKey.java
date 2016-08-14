package cn.hugeterry.ployfun;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/5/1 16:28
 */
public class PolyfunKey {
    public static final int graMax = 30;//边缘检测的阀值控制，值越大，出来的点越少
    public static final int pc = 800;//最后绘制的点数量控制
    public static final int initialSize = 4000;//包围三角形的大小
    public static int started = 0;
    public static boolean FIRST = true;
}
