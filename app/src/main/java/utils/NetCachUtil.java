package utils;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络缓存，缓存之后并把图片给imageView设置进去
 * Created by Administrator on 2017/3/3.
 */

public class NetCachUtil {
    private ImageView imageView;
    private LocalCacheUtils localCacheUtils;
    private MemoryCacheUtils memoryCacheUtils;

    public NetCachUtil() {
        localCacheUtils = new LocalCacheUtils();
        memoryCacheUtils = new MemoryCacheUtils();
    }

    public void getBitMapFromNet(ImageView imageView, String url) {
        //AsyncTask 异步封装工具，可以实现异步请求和主页面的更新(实现了对子线程和handler的封装)
        new newTAsk().execute(imageView, url);
    }

    /*三个泛型意义：
    * 1 代表doInBackground参数类型，省略号代表可以多个参数，这些参数是由 new newTAsk().execute(imageView,url);中传进过来的
    * 2 onProgressUpdate(Void... values)的参数数据类型
    * 3 onPostExecute的参数类型和 doInBackground的返回类型*/
    class newTAsk extends AsyncTask<Object, Integer, Bitmap> {

        private String url;

        /**
         * 【1】 预加载，运行在主线程
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * 【2】核心方法，运行在子线程，可直接异步请求
         *
         * @param params
         * @return
         */
        @Override
        protected Bitmap doInBackground(Object... params) {
            imageView = (ImageView) params[0];
            url = (String) params[1];
            //打标记
            //   imageView.setTag(url);
            //
            // imageView.setTag(1,url);
            //打标记，将当前imageView与url绑定带一起
            Bitmap bitmap = dwonLoad(url);

            //publishProgress();
            return bitmap;
        }


        /**
         * 【3】运行在主线程，可以更新UI
         * 想用此方法需要在doInBackground使用publishProgress();才会回调此方法
         *
         * @param values
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        /**
         * 【4】运行在主线程，核心线程，更新UI
         * 最终的网络的请求结果会通过
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                //将返回的图片设置给ImageView
                //由于listView的重用机制可能使一个bitmap应用到多个item
             /*   String url= (String) imageView.getTag();
                if(url.equals(this.url)){*/
                //如果是我们标记的，则正常加载
                imageView.setImageBitmap(result);//后面是bitmap
                System.out.print("从网络加载图片了");

                //【2】写入本地缓存
             localCacheUtils.setLocalCache(url, result);

                //【3】写入内存缓存
                memoryCacheUtils.setMemoryCache(url, result);
             /*  }*/

            }
            super.onPostExecute(result);
        }
    }

    /**
     * 根据URL请求图片资源
     *
     * @param url
     */
    private Bitmap dwonLoad(String url) {

        HttpURLConnection coon = null;
        try {
            coon = (HttpURLConnection) new URL(url).openConnection();
            coon.setRequestMethod("GET");
            coon.setConnectTimeout(8000);
            coon.setReadTimeout(8000);
            coon.connect();
            if (coon.getResponseCode() == 200) {
                InputStream is = coon.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                return bitmap;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (coon != null) {
            coon.disconnect();
        }
        return null;
    }
}
