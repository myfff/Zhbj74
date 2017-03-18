package utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**本地缓存
 * Created by Administrator on 2017/3/3.
 */

public class LocalCacheUtils {

    private  static  String LOCAL_CACHE_PATH= Environment.getDownloadCacheDirectory().getPath()
            + "/zhbj_74";

    /**写本地缓存
     * @param url
     * @param bitmap
     */
    public  static  void setLocalCache(String url, Bitmap bitmap){//文件中存储的是一个文件
        File dir=new File(LOCAL_CACHE_PATH);
        if(!dir.exists()||dir.isDirectory()){//判断是否是一个文件夹
            dir.mkdirs();//创建文件夹
        }
        File fileCache=new File(dir,url);//文件已传进来的命名
        try {
            //将当前文件压缩到本地
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(fileCache));//将图片写入sd卡缓存
            //参数1：图片格式，参数2：压缩比例 参数3 输出流
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**读本地缓存
     * @param url
     * @return
     */
    public  static Bitmap getLocalCache(String url){
        File fileCache=new File(LOCAL_CACHE_PATH,url);
        if (fileCache.exists()){
            try {

                Bitmap bitmap= BitmapFactory.decodeStream(new FileInputStream(fileCache));
                return bitmap;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
}
