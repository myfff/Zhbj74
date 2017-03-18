package base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import aygxy.zhbj74.MainActivity;
import aygxy.zhbj74.NewsDetailActivity;
import aygxy.zhbj74.R;
import domain.NewsTabBean;
import domain.NewsMenu;
import utils.CacheUtil;
import utils.ContantValue;
import utils.SpUtil;
import view.PullToRefreshListview;
//每一页的图片缓存我们只缓存了第一页的
//mMoreUrl是在判断在第一页传来的数据是否有下一页
// 注意ismore是判断是否加载的更多，如果是就再原来集合追加

/**在此里面我们只是根据上层的指示器，做最后的给Viewpager适配不同的数据（实例化各个页签），这些数据我们需要重新请求
 * Created by yh on 2016/12/15.
 * 新闻详情页中的各个标签
 * 根据标签的不同加载不同的布局，布局是一个自定义的listView
 *
 *
 */

public class BaseTabDetailPager {
    @ViewInject(R.id.vp_top_news)
    private ViewPager mViewPAger;//本布局的viewpager头条新闻
    @ViewInject(R.id.tv_title)
    private TextView mTvTitle;//本布局中的title（在viewpager上面）
    @ViewInject(R.id.indicator)
    private CirclePageIndicator indicator;//圆点指示器（在viewpager的上面）


    @ViewInject(R.id.lv_listView)//本布局的listView  列表新闻
    private PullToRefreshListview listView;

    public MainActivity mActivity;
    public View mRootView;// 页签详情内容的跟布局（就是那个）
    private NewsMenu.NewsTabData mTabData;//单个页签的网络数据（javabean）
    private TextView textView;//viewpager上的
    private final String mUrl;//（第一次的url）访问服务器的地址（此地址用来访问每个页签的数据，就是传递过来的北京.url字段// ）
    private ArrayList<NewsTabBean.TopNews> mTopNewses;//头条新闻的集合（javabean里面的一个字段，此字段包含TopNews对象）
    private ArrayList<NewsTabBean.NewsData> mNewsDatas;//listView的集合（javabean里面的一个字段，此字段包含NewsData对象）
    private NewsAdater mNewsAdater;//listView d的适配器
    //（当第一次解析完发现字段more部位空，将下一页的地址重写赋值进行下一页的在一次请求）加载下一页的Url
    //下一页刷新的时候我们还要判断是不是第一页，如果不是加载更多就不用再原来数据上进行追加，否则要追加
    private String mMoreUrl;
    private NewsTabBean newsTabBean;//将从网络解析的Tab的打他给Javabean对象
    private TopNewsAdater mTopNewsAdater;//头条的数据适配
    private Handler mHandler;

    public BaseTabDetailPager(MainActivity mainActivity, NewsMenu.NewsTabData newsTabData) {
        this.mActivity = mainActivity;
        mRootView = initView();
        mTabData = newsTabData;
        //tab页签部分我们需要重新访问网络拿到图片和数据设置，用XUtils，
        //首先给出一个URl
        mUrl = ContantValue.SERVER_URL + mTabData.url;//每一个具体页签的url
    }

    /**
     * 初始化布局，
     *
     * @return
     */
    public View initView() {
        //此处就不再是只添加一个TextView了，而是要在NewsMenuDetailPager的viewpager中再添加一个跟布局（此跟布局页签，此页签又含有Viewpager和下拉列表）
        View view = View.inflate(mActivity, R.layout.pager_tab_detail, null);
        com.lidroid.xutils.ViewUtils.inject(this, view);//好找子类控件

        //发现listView和上面的Viewpager不能一起滑动，所以此处我们将Viewpager增加为listview的头布局
        //将上面的布局拿出来专门做一个头布局
        final View mHerderView = View.inflate(mActivity, R.layout.list_item_herder, null);
        ViewUtils.inject(this, mHerderView);//注解找到id（必须注解此布局才有可能找到他里面的控件）
        listView.addHeaderView(mHerderView);//增加头布局，


        //listView的回调，根据mMoreUrl，进行是否进行更多数据的请求，还是第一次数据请求
        //listView的下拉上拉及恢复状态我们都已经在自定义listview处理了
        listView.setORefreshListener(new PullToRefreshListview.OnRefreshListener() {
            @Override
            public void onLoadMore() {
                if (mMoreUrl != null) {
                    //有下一页数据
                    getMoreDataFromServer();//监听listview，mMoreUrl不为空进行更多加载
                } else {
                    //没有下一页数据
                    Toast.makeText(mActivity, "没有更多数据了", Toast.LENGTH_LONG).show();
                    //也需要让进度条隐藏掉
                    listView.onRefreshComplete(false);//无所谓传那个值，（因为这里并不是下拉无需更新下拉的事时间）
                }
            }
            @Override
            public void onRefresh() {
                getDataFromServer();
                listView.onRefreshComplete(true);//但这里是要传true的，否则的话下拉不会更新时间
            }
        });


        //初始化布局，做已读未读标记   和listview的适配要联系到一起
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int headerViewsCount = listView.getHeaderViewsCount();//得到listview的头布局
                position = position - headerViewsCount;
                NewsTabBean.NewsData news = mNewsDatas.get(position);//找到此刻那个数据对象被点击

                //我们要得到我们点击了那个条目，通过条目的id来区分，此处并未唯一，但真正服务器给我们提供的时唯一的

                //将此id保存至sp   此id的保存方式是一字符串形式保存的，没点击一次，我们先取出id字符串，如果里面不包括就放进去，并将条目变成灰色
                String readIds = SpUtil.getString(mActivity, ContantValue.READ_IDS, "");
                //如果sp中未保存，就进行保存
                if (!ContantValue.READ_IDS.contains(news.id + "")) {
                    //再原来的基础上今次那个追加，一个字段保存多个值
                    readIds = readIds + news.id + ",";//加""将int转换为字符串
                    SpUtil.putString(mActivity, ContantValue.READ_IDS, readIds);
                }
                //将条目中的文字变灰色，view即所点条目
                TextView textView = (TextView) view.findViewById(R.id.tv_title_news);
                textView.setTextColor(Color.GRAY);//局部刷新，新能较好

                /*    mNewsAdater.notifyDataSetChanged();//全局刷新，性能较低*/
                //当某一页被点击之后，就需要跳转得到Activity
                Intent intent = new Intent(mActivity, NewsDetailActivity.class);
                intent.putExtra("url", news.url);
                mActivity.startActivity(intent);
            }
        });
        return view;
    }


    /**
     * 初始化数
     * 初始化数据,当我们在上一层加入各个页签的时候就已经实例化对象时调用了对象的此方法，
     * 而加载布局是在构造方法中调用的，也就是我们已经在调用initData之前调用过了initView
     *
     * 这个才会第一次走
     */
    public void initData() {
        //网络请求数据
        String cache = CacheUtil.getCache(mActivity, mUrl);
        if (!TextUtils.isEmpty(cache)) {
            processData(cache, false);
        }
        getDataFromServer();//第一次请求
    }

    /**
     * 请求网络，加载第一页数据
     */
    private void getDataFromServer() {
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, mUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String jsonTab = responseInfo.result;//解析到的数据 给我们写的javabean对象
                processData(jsonTab, false);//无需追加

                //每次我们解析完成后，我们必须要定性缓存的保存
                //其实我们这里缓存的只是json数据，我们并没有缓存图片，图片是BitmapXUtils帮我们做的，将图片保存在sd卡上，
                //我们必须将url也就是此时的json数据，url通过MD5加密，
                // 每一个url对应唯一图片。即使我们关掉服务器，由于图片和url都已经缓存，我们既可以通过url找到唯一标示的图片
                CacheUtil.setCache(mActivity, mUrl, jsonTab);


                //****** 不能在主线程更新ui，但是我们XUtils 的底层不仅帮我们开了子线程，还有帮我们封装了handler机制，使得onSuccess，onFailure运行在主线程
                //当网络请求结束，收起下拉刷新的界面
                //恢复界面
                listView.onRefreshComplete(true);//可以传递给控件参数
            }

            @Override
            public void onFailure(HttpException e, String s) {
                e.printStackTrace();//
                Toast.makeText(mActivity, s, Toast.LENGTH_LONG).show();
                listView.onRefreshComplete(false);//
            }
        });
    }

    /**第一次解析判断是否给MoreUrl

     * 解析jsonTab数据（第一页加载，后下一页加载要进行追加），根据上一次下载的json中是否有mMoreUrl数据，当不空加载下一页，否则原来的
     *
     * @param jsonTab
     * mMoreUrl是否加载更多,在这里进行初始化，
     */
    private void processData(String jsonTab, boolean isMore) {
        Gson gson = new Gson();
        //将解析到的数据放入到一个javabean对象的相应字段中，方便我们后来的应用
        newsTabBean = gson.fromJson(jsonTab, NewsTabBean.class);

        //获取更多的下一页的url，根据此mMoreUrl我们又去下载，然后再传回来解析
        String moreUrl = newsTabBean.data.more;
        if (!TextUtils.isEmpty(moreUrl)) {
            //url加上域名,下一页数据连接,根据此判断是否有下一页数据，有的话需要在一次请求数据
            mMoreUrl = ContantValue.SERVER_URL + moreUrl;
        } else {
            mMoreUrl = null;
        }

        if (!isMore) {//加载第一页时，正常给Viewpager，listView正常适配数据
            //加载首页数据
            //Viewpager中的数据头条集合
            mTopNewses = newsTabBean.data.topnews;
            //给头条新闻填充数据
            mTopNewsAdater = new TopNewsAdater();
            if (mTopNewses != null) {
                mViewPAger.setAdapter(mTopNewsAdater);
                //页面同步指示器
                indicator.setViewPager(mViewPAger);
                indicator.setSnap(true);//快照

                //如果拿到的数据不为空，我们在这里做Viewpager的监听，当页面进行切换到时候，我们要拿到json里面mTopnewses里面的title字段进行头条新闻标题的更新

                //只要我们将指示器和viewpager绑定，我们需要将对ViewPAger的监听改为对指示器的监听
                indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        //当某页面被选中
                        NewsTabBean.TopNews topNews = mTopNewses.get(position);
                        mTvTitle.setText(topNews.title);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                //第一次我们进来并没有进行切换，第一次进来我们需要手动初始化第一个页面的标题
                mTvTitle.setText(mTopNewses.get(0).title);

                //指示器自作主张的认为他可以回复原来的页面，其实并没有，每次我们第一次进来我们都重新加载了第一个界面
                indicator.onPageSelected(0);//默认选择让第一个被选中，（解决页面销毁后重新初始化时， indicator仍保留上次位置的bug）
            }


            //拿到新闻列表的集合数据
            mNewsDatas = newsTabBean.data.news;
            if (mTopNewses != null) {
                //给listView设置数据适配
                mNewsAdater = new NewsAdater();
                listView.setAdapter(mNewsAdater);
            }

            //做头条新闻的轮询
            if (mHandler == null) {
                mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);

                        int currentItem = mViewPAger.getCurrentItem();
                        currentItem++;
                        if (currentItem == mTopNewses.size()) {//循环到最后 一个跳到第一页
                            currentItem = 0;
                        }
                        mViewPAger.setCurrentItem(currentItem);
                        mHandler.sendEmptyMessageDelayed(0, 3000);//持续发送延时3秒的信息，行程内循环
                    }
                };
                //保证内循环逻辑只执行一次
                mHandler.sendEmptyMessageDelayed(0, 3000);//发送延时3秒的信息

                mViewPAger.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                //停止广告自动轮番
                                //删除Hander所有的信息
                                mHandler.removeCallbacksAndMessages(null);
                                break;
                            case MotionEvent.ACTION_UP:
                                mHandler.sendEmptyMessageDelayed(0, 3000);
                                break;
                            case MotionEvent.ACTION_CANCEL:
                                //取消时间，当按下Viewpager后直接滑动listView，导致抬起事件无法响应，所以我们也要在此处做信息发送
                                mHandler.sendEmptyMessageDelayed(0, 3000);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
            }
        } else {//如果是加载更多时的数据，我们需要将其加到原来的集合了，刷新listview
            //加载更多,在原来的集合上进行追加
            ArrayList<NewsTabBean.NewsData> moreNews = newsTabBean.data.news;//加载更多的数据
            //将数据追加到原来的集合中
            mNewsDatas.addAll(moreNews);
            //刷新listview
            mNewsAdater.notifyDataSetChanged();
        }
    }
    /**
     * 加载下一页数据
     */
    private void getMoreDataFromServer() {
        HttpUtils httpUtils = new HttpUtils();
        //请求网络时我们并未开子线程，底层帮我们开启底层为异步的
        httpUtils.send(HttpRequest.HttpMethod.GET, mMoreUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String jsonTab = responseInfo.result;
                //返回来的是json数据，我们需要将其解析，为true表示有下一页数据，j8ianxing解析
                processData(jsonTab, true);//需要追加，也就数将ismore赋值为true
                //恢复界面
                listView.onRefreshComplete(false);//可以传递给控件参数
            }
            @Override
            public void onFailure(HttpException e, String s) {
                e.printStackTrace();//
                Toast.makeText(mActivity, s, Toast.LENGTH_LONG).show();
                listView.onRefreshComplete(false);//恢复界面
            }
        });
    }

    /**
     * 在此里面做了已读未读标记
     * 列表新闻适配数据
     */
    class NewsAdater extends BaseAdapter {
        //我们现在不适用默认的图片，而是使用服务器上的图片，那我们就使用XUtils提供的BitmapUtils
        private BitmapUtils mBitmapUtils;

        public NewsAdater() {
            mBitmapUtils = new BitmapUtils(mActivity);
            //dang 加载中一个图片，我们先给biMap添加一个默认图片
            mBitmapUtils.configDefaultLoadingImage(R.drawable.news_pic_default);
        }

        @Override
        public int getCount() {
            return mNewsDatas.size();
        }

        @Override
        public NewsTabBean.NewsData getItem(int position) {
            return mNewsDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(mActivity, R.layout.list_item_news, null);
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title_news);
                holder.tvData = (TextView) convertView.findViewById(R.id.tv_data);
                //将标记设置给concertView
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            NewsTabBean.NewsData news = getItem(position);
            holder.tvTitle.setText(news.title);
            holder.tvData.setText(news.pubdate);

            //通过bitMapXUtils工具通过url下载图片显示在ImageView之上
            mBitmapUtils.display(holder.ivIcon, news.listimage);//每条新闻的url


            //在我们每次重新进入界面时，重新加载布局，listview会重新适配数据，我们在此处根据sp中标记已读未读
            String readIds = SpUtil.getString(mActivity, ContantValue.READ_IDS, "");
            if (readIds.contains(news.id + "")) {//每个listview条目都有一个id
                //将次条目标记已读
                holder.tvTitle.setTextColor(Color.GRAY);
            } else {
                //此处一定要关
                holder.tvTitle.setTextColor(Color.BLACK);
            }
            return convertView;
        }
    }

    public class ViewHolder {
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvData;
    }

    /**
     * 头条新闻的适配器
     */
    class TopNewsAdater extends PagerAdapter {
        //我们现在不适用默认的图片，而是使用服务器上的图片，那我们就使用XUtils提供的BitmapUtils
        private BitmapUtils mBitmapUtils;

        public TopNewsAdater() {
            mBitmapUtils = new BitmapUtils(mActivity);
            //dang 加载中一个图片，我们先给biMap添加一个默认图片
            mBitmapUtils.configDefaultLoadingImage(R.drawable.news_pic_default);
        }

        @Override
        public int getCount() {
            return mTopNewses.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //给Viewpager设置头条图片
            ImageView imageView = new ImageView(mActivity);
            imageView.setImageResource(R.drawable.govaffairs);//默认图片
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);//设置图片的缩放方式，填充父容器


            //下载图片的url
            String murl = mTopNewses.get(position).topimage;//他的底层也是调用setImageResource方法，图片并不能完整填充

            //下载图片，将图片设置给ImageView ，避免内存溢出， BitMapXUtils
            //参数一下载的图片时填充给谁的，参数2要下载的图片的地址
            mBitmapUtils.display(imageView, murl);
            //添加给comtainer
            container.addView(imageView);
            return imageView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
