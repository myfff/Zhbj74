package utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * 1 ，Java回收机制回收强引用的比较慢
 * 2 ， 比较容易回收软引用软引用（SoftReference）
 * 3 ，Api9 更倾向回收软引用 改用lruCauche
 * Created by Administrator on 2017/3/3.
 * 内存
 * 我们用软引用改造内存缓存回收更倾向于回收软引用（SoftReference）和弱引用，GooGle官方推荐使用 LruCache来避免内存溢出
 * （ LruCache还可以自己进行内存管理）
 * 2,3之后，垃圾
 */

public class MemoryCacheUtils {

    //【1强引用】 private  HashMap<String,Bitmap> mMemoryCache=new HashMap<String,Bitmap>();
   /* 【2软引用】//创建了啊一个软引用的hashMap，
    private  HashMap<String,SoftReference<Bitmap>> mMemoryCache=new HashMap<String,SoftReference<Bitmap>>();
  */

    //【3】最近最少使用
    private LruCache<String,Bitmap> mMemoryCache;

    public  MemoryCacheUtils(){
        // least recentlly used 最近最少使用
        //分给每个应用程序的大小（16M左右）
        long MaxMemory=Runtime.getRuntime().maxMemory();
        mMemoryCache=new LruCache<String,Bitmap>((int) (MaxMemory/8)){//图片缓存我们给其的八分之一
            //获取每个对象的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {

                return  value.getByteCount();
            }
        };
    }

    public  void setMemoryCache(String url,Bitmap bitmap){
        mMemoryCache.put(url,bitmap);
        //【1强引用】 mMemoryCache.put(url,bitmap);
      /* 【2软引用】 //将bitmap封装
        SoftReference<Bitmap> softReference=new SoftReference<Bitmap>(bitmap);//使用软引用将bitmap包装起来
        //缓存(根据url缓存)
        mMemoryCache.put(url,softReference);*/
    }
    public   Bitmap getMemoryCache(String url){
        mMemoryCache.get(url);
       //【1强引用】 mMemoryCache.get(url);
      /*  2软引用】SoftReference<Bitmap> softReference=mMemoryCache.get(url);//根据url获取
        if(softReference!=null){//因为可能被回收
            Bitmap bitmap=softReference.get();
            return  bitmap;
        }*/
        return null;
    }
}
