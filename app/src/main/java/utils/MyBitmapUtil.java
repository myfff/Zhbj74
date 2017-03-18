package utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/3/3.
 * 自定义三级缓存图片加载工具
 * 内存，本地（sd），网络
 * 其实我们还是要先请求网络，然后才有可能用到缓存
 */

public class MyBitmapUtil {
    private  NetCachUtil netCachUtil;
    private  LocalCacheUtils localCacheUtils;
    private   MemoryCacheUtils memoryCacheUtils;
    public  MyBitmapUtil(){
        localCacheUtils=new LocalCacheUtils();
        netCachUtil=new NetCachUtil();
        memoryCacheUtils=new MemoryCacheUtils();

    }
     public  void display(ImageView imageView,String url){
         //【1】线内存缓存
         Bitmap bitmap1=memoryCacheUtils.getMemoryCache(url);
         if(bitmap1!=null){
             imageView.setImageBitmap(bitmap1);
             System.out.print("从内存中加载了图片");
             return;
         }

         //【2】再本地缓存
         Bitmap bitmap=localCacheUtils.getLocalCache(url);
         if(bitmap!=null){
             imageView.setImageBitmap(bitmap);
             System.out.print("从本地加载图片了");

             //【3】写入内存缓存
             memoryCacheUtils.setMemoryCache(url,bitmap);
             return;
         }


         //【3】再网络缓存
         //进b来我们就进行默认缓存
         //网络加载图片
         imageView.setImageResource(android.R.drawable.btn_default);//后面是id
         netCachUtil.getBitMapFromNet(imageView, url);
     }
}
