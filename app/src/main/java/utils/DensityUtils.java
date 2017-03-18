package utils;

import android.content.Context;

/**
 * Created by Administrator on 2017/3/3.
 * dp转px   dp=px%desnsity
 */

public class DensityUtils {
    //将dp为float型转化为int，因为在代码中要求用int（代码中px（int），但我们为了适配屏幕，所以想用dp（float））
    public  static int dip2px(Float dip, Context context){
        float density=context.getResources().getDisplayMetrics().density;//获得屏幕密度
        int px= (int) (dip*density+0.5f);//加0.5f进行四舍五入
        return px;
    }
    public  static  float px2dip(int px,Context context){
        float desiny=context.getResources().getDisplayMetrics().density;
        float dip=px/desiny;
        return dip;
    }
}
