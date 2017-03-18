package utils;

/**
 * Created by yh on 2016/10/26.
 */
public class ContantValue {

    // SERVER_URL更改域名比较方便，因为有时我们可能用线上或线下的域名不确定
    /**
     * 下面是一个线上地址
    yong真机测试的时候可以用
     */
//    public  static  final  String  SERVER_URL="http://zhihuibj.sinaapp.com/zhbj/categories.json";

    /**
     * 模拟器访问tomcatii
     */
    public static final String SERVER_URL="http://10.0.2.2:8080/zhbj";//服务器主域名
    public static final String CATEGORY_URL=SERVER_URL+"/categories.json";//分类信息接口


    /**
     * 是否是第一次打开应用，判断是进入新手引导页还是主界面
     */
    public static final String IS_FIRST_ENTER = "isfirstenter";

    /**
     * 标记已读
     */
    public static final String READ_IDS ="read_ids" ;
    /**
     * 组图资源连接
     */
    public static final String PHOTOS_URL =SERVER_URL+"/photos/photos_1.json";
}