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

//有共性，父类已经加载好的布局（initview），我们无需复写，我们只要得到父类的布局中的控件重写initdata即可
// 简单当然设置个title，和给framelayout加个textView即可(initdata)
//没做处理的，我们只是给title手动设了一个标题，给framelayout动态添加了一个textView
    //总具体处理的，我们根据侧边栏的点击事件，动态加了四个布局（在initdata中处理）
public class GovAffairsPager extends BasePager {
    public GovAffairsPager(Activity activity) {
        super(activity);
    }
    /**
     * 给帧布局填充布局对象
     */
    @Override
    public void initData() {
        super.initData();
        TextView textView=new TextView(mActivity);
        textView.setText("政务");
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        //将textView加入帧布局
        mFlContent.addView(textView);
        mTvTitle.setText("人口管理");
        //显示菜单按钮
        mImgMenu.setVisibility(View.VISIBLE);
    }
}
