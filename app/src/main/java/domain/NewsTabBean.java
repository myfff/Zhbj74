package domain;

import java.util.ArrayList;

/**
 * 页面详情页签对象，
 * Created by yh on 2016/12/16.
 */

public class NewsTabBean {

    //现在我们已经走到了页签内对象，即通过一个新的Url进入了list_1.json里面，也就是我们的第一个页签内
    //通过对json字段的分析和原则，我们做如下的字段定义
    public NewsTab data;//新闻页签
    public  class NewsTab{
        public String more;
        public ArrayList<NewsData> news;//新闻list
        public ArrayList<TopNews> topnews;//新闻头条
    }

    //新闻列表对象
    public  class NewsData{
        public  int id;
        public  String listimage;
        public  String pubdate;
        public  String title;
        public  String type;
        public  String url;//每一个tab中listView条目点击后的连接

    }
    //头条新闻
    public  class TopNews{
        public  int id;
        public  String topimage;
        public  String pubdate;
        public  String title;
        public  String type;
        public  String url;

    }

}
