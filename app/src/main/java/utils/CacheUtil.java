package utils;

import android.content.Context;

/**
 * Created by yh on 2016/12/15.
 */
/*我们缓存的是json，网络请求缓存的是json*/
/*网络缓存的工具类*/
public class CacheUtil {
    /**设置缓存保存在Sp中（调用了SpUtils工具类）
     * @param context
     * @param url
     * @param json
     */
    public static void setCache(Context context,String url,String json){
        SpUtil.putString(context,url,json);
    }

    /**根据url获取json缓存
     * @param context
     * @param url
     * @return
     */
    public static String getCache(Context context,String url){
      return   SpUtil.getString(context, url,null);
    }
}
