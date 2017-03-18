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

public class SmartServicePager extends BasePager {
    public SmartServicePager(Activity activity) {
        super(activity);
    }
    /**
     * 给帧布局填充布局对象
     */
    @Override
    public void initData() {
        super.initData();
        TextView textView=new TextView(mActivity);
        textView.setText("智慧服务");
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        //将textView加入帧布局
        mFlContent.addView(textView);
        mTvTitle.setText("生活");

        //显示菜单按钮
        mImgMenu.setVisibility(View.VISIBLE);


    }
}
