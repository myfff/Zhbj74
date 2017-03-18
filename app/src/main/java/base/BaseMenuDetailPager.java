package base;

import android.view.View;

import aygxy.zhbj74.MainActivity;

/**
 *
 * Created by yh on 2016/12/15.
 *  页签标签页对象
 *  抽取父类，因为父类的每个具体类中的布局不一样，我们就不具体实现定义抽象（initView）让每个父类必须去实现
 *  TabPageIndicator（页签指示器）+Viewpager
 */
public abstract  class BaseMenuDetailPager  {
    public  MainActivity mActivity;
    public View mRootView;//菜单详情页的跟布局；也就是各个标签页中的frameLayout要填充的一个布局
    public  BaseMenuDetailPager(MainActivity  mainActivity){
       this.mActivity=mainActivity;
        mRootView = initView();
    }
    //初始化布局，必须由子类去实现
    public  abstract View initView() ;
    //初始化数据
    public void initData(){
    }
}
