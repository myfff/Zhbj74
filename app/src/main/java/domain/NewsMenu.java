package domain;

import android.content.Intent;

import java.util.ArrayList;

/**
 * Created by yh on 2016/12/15.
 */
/*分类信息封装,侧滑栏的数据*/
    /*使用Gson解析，对象书写技巧
    * 1，封{}创建对象，封[]创建集合（ArrayList）
    * 2，所有字段要和json里面的高度一致*/
public class NewsMenu {
    public  int retcode;
    public ArrayList<Integer> extend;
    public  ArrayList<NewsMenuData> data;

    //侧边栏对象
    public  class  NewsMenuData{
        public  int id;
        public  String title;
        public  int type;
        public  ArrayList<NewsTabData> children;

        @Override
        public String toString() {
            return "NewsMenuData{" +
                    "childer=" + children +

                    ", title='" + title + '\'' +

                    '}';
        }
    }

    //页签的对象
    public  class NewsTabData {
        public  int id;
        public  String title;
        public  int type;
        public  String url;

        @Override
        public String toString() {
            return "NewsTabData{" +
                    ", title='" + title + '\'' +

                    '}';
        }
    }


    @Override
    public String toString() {
        return "NewsMenu{" +
                "data=" + data +
                ", rotcode=" + retcode +
                ", extend=" + extend +
                '}';
    }
}
