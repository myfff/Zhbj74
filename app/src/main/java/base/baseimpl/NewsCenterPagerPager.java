package base.baseimpl;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;


import java.util.ArrayList;

import aygxy.zhbj74.MainActivity;
import base.BaseMenuDetailPager;
import base.BasePager;

import base.baseimpl.menunewscenter.InteractMenuDetailPager;
import base.baseimpl.menunewscenter.NewsMenuDetailPager;
import base.baseimpl.menunewscenter.PhotosMenuDetailPager;
import base.baseimpl.menunewscenter.TopicMenuDetailPager;
import domain.NewsMenu;
import fragment.LeftMenuFragment;
import utils.CacheUtil;
import utils.ContantValue;

/*菜单数据(json的data字段    leftMenuFragment.setMenuData(json.data);//菜单数据(json的data字段)

 mMenuDetailPagers.add(new NewsMenuDetailPager((MainActivity) mActivity, json.data.get(0).children));//菜单第一项数组
* */
/*我们此时需要给此整体布局的的framelayout（也就是主要处理initdata（））动态的添加布局（），加了四个（是根据此此里面获取的数据传递给侧边栏，然后点击侧边栏时想对应的）

后 第一次访问数据是在新闻中心，得到的数据是要给侧边栏用，来适配listView
* 只需通过一步即可
*
*
* 另外得到的数据还要给根据侧边栏点击时我们要做详细的新闻详情页，
* 再此处我们是在初始化新闻详情页的时候将数据闯过去的*/

/**
 * Created by yh on 2016/12/14.
 */
/*新闻界面，要做对主界面的操作和侧边栏的操作*/
    /*此界面是是新闻中心，在此界面我们需要做如下操作
    * 1，首先用XUtils，和Gson从网络上获取我们此模块要用到的json数据放到本地
    * 2，先给侧边栏填充数据，将此新闻中心通过MActivity(媒介)和leftFragment相关联，
    * 在此新闻中心调用leftFragment中的方法将json数据中的data字段传递给侧边栏让其自己进行设置即可*/

public class NewsCenterPagerPager extends BasePager {
    private ArrayList<BaseMenuDetailPager> mMenuDetailPagers;//放四个菜单详情页的list
    private NewsMenu json;

    public NewsCenterPagerPager(Activity activity) {
        super(activity);
    }

    /**
     * 给帧布局填充布局对象
     */
    @Override
    public void initData() {
        super.initData();
        mTvTitle.setText("新闻中心");

        TextView textView = new TextView(mActivity);
        textView.setText("新闻");
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        //将textView加入帧布局
        mFlContent.addView(textView);
        //显示菜单按钮
        mImgMenu.setVisibility(View.VISIBLE);


        //判断是否有缓存，如果有进解析
        String json = CacheUtil.getCache(mActivity, ContantValue.CATEGORY_URL);
        if (!TextUtils.isEmpty(json)) {
            System.out.println("使用缓存了");
            //解析json数据
            processData(json);
        }
        //原本如果为空的话我们需要进行网络请求，但为了用户的体验，我们不论是否为，都进行网络请求，
        //因为本地缓存一直在，如果此时我们一直不网络请求，用到的将会一直是同一数据，数据更新时我们加载不到，
        //所以空时一定网络请求，不空时我们可以先让显示一些旧数据同时，去加载新数据，加载出来以后就可以用，写成一下逻辑


        //做新闻界面的处理，当选中新闻业时，我们进行新闻页面的初始化，我们在此进行处理
        //请求服务器，回去数据，开源框架，XUtils进行处理
        getDataFromServer();//有缓存我用一下，但是我还是回去请求新的

    }

    /**
     * 从服务器上获取数据
     */
    private void getDataFromServer() {
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET,
                ContantValue.CATEGORY_URL,
                new RequestCallBack<String>() {
                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;//
                        //返回来的是json数据，我们需要将其解析
                        processData(result);
                        //我们需要将每一次成功从网络下载的东西作为缓存保存下来（通过他的url来唯一标示）
                        CacheUtil.setCache(mActivity, ContantValue.CATEGORY_URL, result);
                    }

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        error.printStackTrace();//
                        Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * 解析json数据，用一个工具类Gson
     * 开源库 Gson
     * 新闻中心里面有4个页签，在此初始化，设置菜单详情页
     * <p>
     * 解析传给侧边，并根据侧边点击回传的position初始化对应菜单详情页（菜单详情页的内容初始化时就传进去）
     */
    private void processData(String result) {

        //Gson:Google Json
        Gson gson = new Gson();
        json = gson.fromJson(result, NewsMenu.class);
        //将解析到的数据创给侧边栏，首先要通过mActivity拿到侧边栏对象，在调用侧边栏里面的方法将json中的打他字段创出去
        MainActivity mainUI = (MainActivity) mActivity;
        LeftMenuFragment leftMenuFragment = mainUI.getLeftMenuFragment();
        //将数据进行回传
        leftMenuFragment.setMenuData(json.data);//菜单数据(json的data字段)

        //因为数据在此处，所以我我们设置侧边栏是从此处调用创给它参数，
        //当从侧边栏到各个新闻中心的不同内容时，我们从侧边栏传过来，在此处做方法 ，设置新闻中心标签内frameLayout中的内容
        //在新闻中心frameLayout布局中设置4个菜单详情页

        //初始化4个菜单详情页
        mMenuDetailPagers = new ArrayList<BaseMenuDetailPager>();
        mMenuDetailPagers.add(new NewsMenuDetailPager((MainActivity) mActivity, json.data.get(0).children));//菜单第一项数组
        mMenuDetailPagers.add(new TopicMenuDetailPager((MainActivity) mActivity));
        mMenuDetailPagers.add(new PhotosMenuDetailPager((MainActivity) mActivity, btnphoto));
        mMenuDetailPagers.add(new InteractMenuDetailPager((MainActivity) mActivity));

        //将新闻中心菜单页设为默认页，一开始就在菜单详情也新闻这，这样一来我们上面设置的5个标签页的frameLayout中的内容也就没什么用了
        setCurrentDetailPager(0);

    }

    /**
     * 根据传进来的不同位置，设置菜单详情页(上面已经设置好)，添加进frameLayout
     *
     * @param position 我们现在挑选一个新闻菜单详情页做（即新闻中心frameLayout里面做加载详情页的跟布局，此时我们需要跑到新闻菜单详情页中做布局的处理）
     *                 此方法再侧边栏调用传position
     */
    public void setCurrentDetailPager(int position) {
        //重新给FrameLayout   mFlContent添加内容
        BaseMenuDetailPager pager = mMenuDetailPagers.get(position);//获取当前应该显示的侧滑详情页页面
        View view = pager.mRootView;//当前侧滑详情的跟布局（其根本布局将在其具体里面实现，此处我们没有实现）

        //因为帧布局是一个比较纯洁的页面，我们只能加入一个跟布局，多个化会重叠，所以在加入之前我们需要将原本有的布局清除掉
        mFlContent.removeAllViews();//

        //此时在NewsCenterOagerPager里面，我们可以拿到帧布局将当前侧滑详情的跟布局添加进去
        mFlContent.addView(view);//给帧布局添加侧滑详情的跟布局

        //初始化(侧边栏详情页的数据)页面数据
        pager.initData();

        //新闻（标签）中心的侧边栏的新闻详情页设置完毕后，我们还需要将此时新闻中心的title改变一下
        mTvTitle.setText(json.data.get(position).title);//更新标签(jiang我们解析的json的data中的title拿出来)


        //如果还组图界面，需要切换按钮
        if (pager instanceof PhotosMenuDetailPager) {
            btnphoto.setVisibility(View.VISIBLE);//设置右边可见（我们后面实现了组图）

        } else {
            //否则不显示左边按钮
            btnphoto.setVisibility(View.INVISIBLE);
        }
    }
}
