package base.baseimpl.menunewscenter;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;

import aygxy.zhbj74.MainActivity;
import aygxy.zhbj74.R;
import base.BaseMenuDetailPager;
import base.BaseTabDetailPager;
import domain.NewsMenu;

/*后，我们只做了新闻菜单详情页的处理，我们还需要给新闻菜单详情页的ViewPager适配不同的页签
（页签内容是根据在新闻详情页中传给我们的数据来处理）*/

/*我们 将指示器和Viewpager绑定，做ViewPagerIndicator的时间监听
* 正如使用tablayout与viewpager搭配使用一样，当我们使用ViewPagerIndicator与viewpager搭配上会用一样，在给viewpager适配时都不要忘了给他们同事适配*/

/**
 * 引入ViewPagerIndicator使用流程
 * 导入库文件
 * 1，指示器和Viewpager绑定，在ViewpagerAdater中重写getPagerTitle返回标题
 * 2，在清单文件中增加样式
 * 3，背景修改为白色
 * 4 滑动指示器的时候侧边栏不出来（再我们导进的类库中重写下面的事件）
 * //事件分发
 * //dispatchTouchEvent->onInterceptTouchEvent->onTouchEvent
 *
 * @Override public boolean dispatchTouchEvent(MotionEvent ev) {
 * //请求所有父控件及祖宗控件不要拦截事件
 * getParent().requestDisallowInterceptTouchEvent(true);
 * return super.dispatchTouchEvent(ev);}
 * 只是让tab滑动时不拉出侧边栏
 * <p>
 * 5，我们还要处理，当我们滑动没有到北京时，侧边栏也不出来，而当我们滑到北京时，再接着滑滑动侧边栏就出来
 * 我们让此ViewPager监听改变状态，最终发现我们只能给指示器设置监听
 * <p>
 * Created by yh on 2016/12/15.
 * <p>
 * <p>
 * 在新闻详情页中，我们向左滑动页签时时，侧滑菜单栏拦截了指示器的事件，我们让其那层viewpager不可滑动
 * ，我们需要在TabPageIndicator.java中重写一个方法，告诉所有人都不可以拦截我的相应事件，当要滑动到坐左边互动出侧边栏我们做了侧边栏的开启与禁用
 因为下面的viewpager与其绑定，与其有类似的效果（此Viewpager并非是头布局那个） 是要用不同页签来适配的那个
 */

public class NewsMenuDetailPager extends BaseMenuDetailPager implements ViewPager.OnPageChangeListener {
    @ViewInject(R.id.indicator)
    private TabPageIndicator mIndicator;//同样用注解的方式找到指示器
    @ViewInject(R.id.vp_news_menu_detail)//注解后面并不加分号
    private ViewPager mViewPager;//新闻菜单详情页的Viewpager，里面要加入页签Tab
    private ArrayList<NewsMenu.NewsTabData> mTabData;//页签网络数据
    private ArrayList<BaseTabDetailPager> mPagers;//viewpager需要适配的数据集合
    public NewsMenuDetailPager(MainActivity mainActivity, ArrayList<NewsMenu.NewsTabData> children) {
        super(mainActivity);
        mTabData = children;//所有也前夜的信息
    }
    /**
     * @return
     */
    @Override
    public View initView() {
        View rootView = View.inflate(mActivity, R.layout.pager_news_menu_detail, null);
        //加入注解和事件，我们只要用注解声明ViewPager对象即可，这样声明才可以找到里面的控件
        com.lidroid.xutils.ViewUtils.inject(this, rootView);
        return rootView;
    }

    @Override
    public void initData() {
        //在此里面要初始化多个页签（要初始化页签的人个数要根据服务器返回的为依据，要拿到json.data.get(0).children字段里面的值
        // 接下来我们根据网络数据来初始化页签界面的数据
        mPagers = new ArrayList<BaseTabDetailPager>();
        for (int i = 0; i < mTabData.size(); i++) {
            //有多少页签就初始化多少页签
            BaseTabDetailPager TabDetailPager = new BaseTabDetailPager(mActivity, mTabData.get(i));//还要把那个标题对象传递过来
            mPagers.add(TabDetailPager);
        }
        mViewPager.setAdapter(new NewsMenuDetailAdater());
        //将指示器和ViewPager绑定在一起，必须是在ViewPager适配数据以后才可以
        mIndicator.setViewPager(mViewPager);//还要给指示器适配数据，也再pagerAdater中重写一个方法getPagerTitle

        //设置Viewpager的监听，如果想左滑动到北京的时候，就进行侧边栏的显示，否则隐藏
//        mViewPager.setOnPageChangeListener(this);//如果此处我们给Viewpager设置监听，由于他和指示器是合作伙伴，这时指示器就不会和Viewpager同步
        mIndicator.setOnPageChangeListener(this);//由于以上原因我们只能给指示器设置监听
    }

    //为新闻菜单详情页设置数据（菜单详情页中又有不同的页）
    class NewsMenuDetailAdater extends PagerAdapter {
        /**
         * 指定指示器的标题
         *
         * @param position
         * @return
         */
        @Override
        public CharSequence getPageTitle(int position) {
            NewsMenu.NewsTabData data = mTabData.get(position);
            return data.title;
        }

        @Override
        public int getCount() {
            return mPagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //为ViewPager适配数据
            BaseTabDetailPager pager = mPagers.get(position);//
            View view = pager.mRootView;//我们要的是BaseTabDetailPager中个跟布局，一应要加到Viewpager里面
            container.addView(view);//能不能别马虎，又浪费了1个小时
            pager.initData();//初始化数据填充到跟布局
            return view;//最后将数据返
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


    //下面3个位Viewpager的监听实现
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            //开启侧边栏
            setSlidingMenuEnable(true);
        } else {
            //禁用侧边栏
            setSlidingMenuEnable(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * 禁用或者开启侧边栏效果
     *
     * @param enble
     */
    private void setSlidingMenuEnable(boolean enble) {
        //我们想要拿到侧边栏需要先拿到Mainactivity的对象，分析可知上下文对象现在只有一个便是mActivity
        MainActivity mainUI = (MainActivity) mActivity;
        //得到侧边栏对象
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        if (enble) {
            //设置侧边栏全屏可拉伸
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            //禁用侧边栏
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }

    /**就是指示器添加了一个按钮的点击事件
     * 用框架XUtil为方法进行注解
     * 做tab中的按钮点击事件
     * @param view
     */
    @OnClick(R.id.btn_next)
    public void nextTab(View view) {
        int currentItem = mViewPager.getCurrentItem();//获取当前界面
        currentItem++;
        mViewPager.setCurrentItem(currentItem);//设置当前界面的下一个界面
    }
}






