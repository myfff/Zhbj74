package base.baseimpl.menunewscenter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import aygxy.zhbj74.MainActivity;
import base.BaseMenuDetailPager;

/**
 * Created by yh on 2016/12/15.
 */


public class TopicMenuDetailPager extends BaseMenuDetailPager {
    public TopicMenuDetailPager(MainActivity mainActivity) {
        super(mainActivity);
    }
    @Override
    public View initView() {
        //返回一个新闻菜单页的布局
        TextView textView=new TextView(mActivity);
        textView.setText("专题——菜单页详情");
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }
}
