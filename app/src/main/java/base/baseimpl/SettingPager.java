package base.baseimpl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import base.BasePager;

/**
 * Created by yh on 2016/12/14.
 */
public class SettingPager extends BasePager {
    public SettingPager(Activity activity) {
        super(activity);
    }
    /**
     * 给帧布局填充布局对象（数据给了，但还没有调用此对象的此方法，
     * 必须在Contentfragment中geiViewpager适配数据时（也就是初始化数据）当实例化此对象时调用一次方法，这是布局中才会有数据）
     */
    @Override
    public void initData() {
        super.initData();
        TextView textView=new TextView(mActivity);
        textView.setText("设置");
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);

        //将textView加入帧布局
        mFlContent.addView(textView);
        mTvTitle.setText("设置");

        //隐藏菜单按钮
        mImgMenu.setVisibility(View.INVISIBLE);

    }
}
