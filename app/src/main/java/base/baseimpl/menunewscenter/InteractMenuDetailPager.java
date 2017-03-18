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
//，父类没有加载布局，我们必须自己去实现加载一个布局initView（而不需要initdata）
// 没有做处理的我们只是简单地加了一个textView
    //并没有做initdata的处理

public class InteractMenuDetailPager extends BaseMenuDetailPager {
    public InteractMenuDetailPager(MainActivity mainActivity) {
        super(mainActivity);
    }
    @Override
    public View initView() {
        //返回一个新闻菜单页的布局
        TextView textView=new TextView(mActivity);
        textView.setText("互动————菜单页详情");
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }
}
