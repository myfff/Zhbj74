package fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import aygxy.zhbj74.MainActivity;
import aygxy.zhbj74.R;
import base.baseimpl.NewsCenterPagerPager;
import domain.NewsMenu;

/*字体和图片我们 都可以创建背景选择器*/
/*在此处我们用XUtils中的注解*/
/**
 * Created by yh on 2016/12/14.
 */
/* 后 返回给新闻中心界面NewsCenterPagerPager时2步，先得到Main，再在里面得到Contentfragment进行数据传送*/

    /*在此里面我提供了一个可以接受NewsCenterPagerPager传递过来的数据，并用这些数据给listView适配数据
    * 做listView的监听改变状态，并在此处将当前选中的条目传到NewsCenterPagerPager*/


public class LeftMenuFragment extends BaseFragment {

    @ViewInject(R.id.lv_left_menu)//此时已经给listView赋值了
    private ListView mlvLeftMenu;
    private  ArrayList<NewsMenu.NewsMenuData> mNewsMenuDatas;//侧边栏网络数据对象
    private  int mCurrentPos=0;//选中的Menu
    private leftMenuAdater mMenuAdater;

    @Override
    protected void initData() {
    }

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.left_fragment, null);
//        mlvLeftMenu = (ListView)view.findViewById(R.id.lv_left_menu);
        ViewUtils.inject(this,view);//注入View好人事件
        return view;
    }

    /**通过此方法将json中的数据从NewsCenterPager中传递过来，然后用来给侧边栏填充数据
     * @param data  json数据中的data字段
     */
    public void setMenuData(ArrayList<NewsMenu.NewsMenuData> data) {
        //当前选中状态要归零，为了让我们每次切回到新闻中心页时侧边栏详情页默认的是新闻详情页和侧边栏的新闻标题可以同步
        mCurrentPos=0;

        //数据传进来，需要在侧边栏展示，用listView展示
        mNewsMenuDatas=data;//将用到的数据初始化

        //给listView适配数据
        mMenuAdater = new leftMenuAdater();
        mlvLeftMenu.setAdapter(mMenuAdater);


        //设置侧边栏listView的点击事件，监听状态切换颜色
        mlvLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentPos=position;//更新当前被选中的位置
                mMenuAdater.notifyDataSetChanged();//通知适配器数据的更新让listView进行刷新
                //点击一个条目之后，要收起侧边栏
                toggle();

                //侧边栏点中之后，要修改新闻中心的frameLayout布局中的内容
                setCurrentDetailPager(position);

            }
        });
    }

    /**设置当前菜单详情页
     * //设置新闻内容，前面我们从新闻中心拿到侧边栏的对象通过Activity，现在我们要在侧边栏中设置新闻中心的frameLayout对象
     * @param position
     * 要先拿到新闻中心的对象（过程比较曲折）
     */
    private void setCurrentDetailPager(int position) {
        //获取MainActivity对象
        MainActivity mainUI=(MainActivity)mActivity;
        //获取ContentFragment的对象
        ContentMenuFragment contentMenuFragment=mainUI.getContentMenuFragment();
        // 还要获取新闻中心页
        NewsCenterPagerPager centerPagerPager=contentMenuFragment.getNewscenterPager();
         //修改中心页的的frameLayout的布局
        centerPagerPager.setCurrentDetailPager(position);
    }

    /**
     * 收起侧边栏
     */
    private void toggle() {
        MainActivity mainUI= (MainActivity) mActivity;
        SlidingMenu slidingMenu=mainUI.getSlidingMenu();
        slidingMenu.toggle();//侧边栏开点击关，关点击开
    }

    class leftMenuAdater extends BaseAdapter{

        @Override
        public int getCount() {
            return mNewsMenuDatas.size();
        }

        @Override
        public NewsMenu.NewsMenuData getItem(int position) {
            return mNewsMenuDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=View.inflate(mActivity,R.layout.list_item_left,null);
            TextView tvMenu=(TextView)view.findViewById(R.id.tv_left_menu);
            NewsMenu.NewsMenuData menuData=getItem(position);
            tvMenu.setText(menuData.title);

            if(position==mCurrentPos){
                //选中，变红色
                tvMenu.setEnabled(true);
            }else {
                tvMenu.setEnabled(false);
            }
            return view;
        }
    }
}
