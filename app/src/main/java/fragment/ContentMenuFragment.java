package fragment;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.List;

import aygxy.zhbj74.MainActivity;
import aygxy.zhbj74.R;
import base.BasePager;
import base.baseimpl.GovAffairsPager;
import base.baseimpl.HomePager;
import base.baseimpl.NewsCenterPagerPager;
import base.baseimpl.SettingPager;
import base.baseimpl.SmartServicePager;
import view.NoScollviewPager;

/**
 * Created by yh on 2016/12/14.
 */
/*
此布局为一个ViewPager+RadioGroup布局

在此里面给viewpager动态初始化5个标签*/

/*自定义viewpager，禁止滑动，
监听RadioGroup
* 监听viewpager，做性能优化加载数据，做开启与关闭侧边栏*/
//再3  已经继承父类，有上下文对象，有onCreaterview（）方法，并且要实现父类的方法initView ，initdata
public class ContentMenuFragment extends BaseFragment {
    private List<BasePager> mPagers ;//5个标签页集合适配viewpager
    private view.NoScollviewPager mViewPager;  //
    private RadioGroup mRgGroup;
    /**
     * 复写父类的方法
     * 先初始化整个布局
      @return
     */
    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.content_fragment, null);
        mViewPager = (NoScollviewPager) view.findViewById(R.id.vp_content);
        mRgGroup = (RadioGroup) view.findViewById(R.id.rg_group);
        return view;
    }

    /**
     * 复写子类方法
     * 初始化部分ViewPager要用到的布局（BasePager）
     * 先初始化Viewpager要用到的数据集合
     */
    @Override
    protected void initData() {
        mPagers = new ArrayList<BasePager>();
        mPagers.add(new HomePager(mActivity));
        mPagers.add(new NewsCenterPagerPager(mActivity));
        mPagers.add(new SmartServicePager(mActivity));
        mPagers.add(new GovAffairsPager(mActivity));
        mPagers.add(new SettingPager(mActivity));

        mViewPager.setAdapter(new ContentAdater());

        //因为我们将ViewPager的滑动给禁用，如果想让其5个界面切换我们需要做下面RadioGroup 的监听
        mRgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home:
                        //首页,参数表示是否具有滑动动画
                        mViewPager.setCurrentItem(0, false);
                        break;
                    case R.id.rb_news:
                        //新闻中心
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.rb_smart:
                        //智慧服务
                        mViewPager.setCurrentItem(2);
                        break;
                    case R.id.rb_govaffairs:
                        //政务
                        mViewPager.setCurrentItem(3);
                        break;
                    case R.id.rb_setting:
                        //设置
                        mViewPager.setCurrentItem(4, false);
                        break;

                }
            }
        });


        // 因为只有在切换时才加载某一页， 我们需要手动加载第一页并且初始化第一页的数据
        mPagers.get(0).initData();//
        //首页要禁用侧边栏，我们需要手动调用
        setSlidingMenuEnable(false);
        //监听Viewpager的滑动来初始化数据，优化性能，并做侧边栏的开启与禁用
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            /**被选中
             * @param position
             */
            @Override
            public void onPageSelected(int position) {
                //我们做了viewpager的监听，当我们监听到选中了某个界面时才初始化界面数据，优化性能
                BasePager pager = mPagers.get(position);
                pager.initData();//此时才初始化数据

                if (position == 0 || position == mPagers.size() - 1) {
                    //首页和设置界面要禁用侧边栏
                    setSlidingMenuEnable(false);
                } else {
                    //否则开启侧边栏
                    setSlidingMenuEnable(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

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

//给viewpager设置适配
    class ContentAdater extends PagerAdapter {

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
            BasePager pager = mPagers.get(position);//得到一个BasePager对象，虽然其不是一个View，但是其里面构造了一个View
            View view = pager.mRootView;//获取当前页面对象的整体布局，此被初始化，时并没有数据
            //由于我们在baseimp中并未调用initdata初始化数据，所以我们发现ViewPager中的真布局出并未有内容，在此处调用一下即可
            // pager.initData();//后来我们发现Viewpager会自动加载下一页的数据，为了节省流量和性能，我们将不再此处调用initData方法，我们给ViewPager设置监听当加载某个页面后，
            container.addView(view);
            return view;
        }
    @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
    /**设置默页为新闻页
     * @return
     */
    public NewsCenterPagerPager getNewscenterPager() {
        NewsCenterPagerPager centerPagerPager = (NewsCenterPagerPager) mPagers.get(1);
        return centerPagerPager;
    }

}
