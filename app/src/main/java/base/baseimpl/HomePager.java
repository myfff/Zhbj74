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
/*子类继承父类的方法已有变量
* initView一开始就调用了，得到了一个mRootView和 mFlContent部分布局，我们只需初始化initDate将布局填入到mFlContent即可
* 但是initdata并没有人调用*/

public class HomePager extends BasePager {
    public HomePager(Activity activity) {
        super(activity);
    }
    /**
     * 给帧布局填充布局对象
     */
    @Override
    public void initData() {
        super.initData();
        TextView textView = new TextView(mActivity);
        textView.setText("首页");
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        //将textView加入帧布局
        mFlContent.addView(textView);
        //头标题也要更改
        mTvTitle.setText("智慧北京");
        //隐藏菜单按钮
        mImgMenu.setVisibility(View.INVISIBLE);
    }
}
