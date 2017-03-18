package base;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import aygxy.zhbj74.MainActivity;
import aygxy.zhbj74.R;
//5 title +framelayout,有共性initview自己写，无需子类复写直接用即可

/*BasePager基类，5个标签页（都有title+frameLayout布局（父容器），有共性抽取让子类复写，将共有的都已经写入到父类
此里面维护了一个全部局mRootView，通过全局变量我们可以拿到title和framelayout子布局，所以在构造函数中初始化，
* title都有我们将其抽取（并且此处的标题是我们自己设置的，并非来自服务器）
* 而下面的一直在改变，我们将其用一个FrameLayout布局先占位，以后什么再进行填充（在代码中动态添加）
*在全布局的里面还维护了一个帧布局mFlcontent，再次用来加布局*/

//相当于一个自定义的view，当我们创建其对象的时候会加载其对应的布局

/**
 * Created by yh on 2016/12/14.
 */

public class BasePager {
    ////我们在加载一个布局时，都需要传进一个上下文对象，我们需要在此类一实例化的同事将上下文对象Activity给初始化出来
    public Activity mActivity;
    public ImageButton btnphoto;//组图切换按钮

    public View mRootView;//5个标签页的根布局对象
    public ImageButton mImgMenu;
    public TextView mTvTitle;
    public FrameLayout mFlContent;//帧布局我们一般在此布局里面加上fragment


    public BasePager(Activity activity) {
        this.mActivity = activity;
        mRootView = initView();
    }

    /**
     * 初始化布局（基本）
     */
    public View initView() {
        View view = View.inflate(mActivity, R.layout.basepager, null);
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mImgMenu = (ImageButton) view.findViewById(R.id.btn_menu);
        btnphoto = (ImageButton) view.findViewById(R.id.btn_photo);
        mFlContent = (FrameLayout) view.findViewById(R.id.fl_content);
        //给侧边栏做按钮的点击事件
        mImgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        return view;
    }

    /**
     * 收起侧边栏
     */
    private void toggle() {
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        slidingMenu.toggle();//侧边栏开点击关，关点击开
    }
    //初始化数据
    public void initData() {
    }
}
