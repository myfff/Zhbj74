package view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
/**
 * 头条新闻自定义ViewPager  他的滑动我们也要做处理
 * <p>
 * 注意侧滑菜单和Viewpager的滑动并不是一回事，往往是侧边栏影响了Viewpager滑动，我们才做处理，只有第一层适合leftFragment相连接，因为Viewpager在ContentFragment中
 * 到了后面的页签Viewpager（因为和指示器相连，指示器重写了方法所以没对其自定义），和头条的Viewpager（自定义）我们没做触摸时间的禁用，
 * 而只是请求父不拦截我的事件(其实父控件也并没有做拦截处理的)，并且想要开启侧边栏自己写
 * 到最后一层重写Viewpager方法和侧边栏无关系，只是因为上层Viewpager阻拦了底层的滑动事件。所以要重写
 * Created by yh on 2016/12/17.
 *
 *  顶层ContentFragment中，如果我们不重写Viewpager，当我们向滑动切换标签会滑出侧边栏，所以要让ViewPagre不可以滑动做标签的切换，我们还在1,5页签禁用了侧边栏的滑出
 * 1，加标签页，顶层ContentFragment的Viewpager中，为了侧边栏，我们重写了第一层Viewpager的onTouchEvent方法使其对滑动事件不相应（其布局ViewPAger+RadioGroup）
 * 5个标签只可点击切换
 * , 2，加4个详情页，第一层，5个标签页填充在了顶层ContentFragment的Viewpager中，（对新闻中心做也详细实现了其布局（title+framelayout）详解）
 * ,3，加多个页签，第2层，我们在第一层的Framelayout上又加了四个侧滑菜单详情页，详细页中我们对新闻菜单详情页对了详解。（其布局用到了TabPageIndicator+viewpager）
 * 在此层上我们为了让侧滑栏只有在我们滑到第一个时才可以滑出，我们做了指示器的监听，指示器是用来和Viewpager配合使用的
 * zai这一层我们并没有对此层的Viewpager做滑动处理
 * 4，对多个页签布局做处理，布局（Viewpager+listview）,由于上一层我们未处理Viewpager的滑动事件，所以我们处理此层的滑动时，会被上一层截断，我们在此处做Viewpager的处理
 */

public class TopNewsViewPager extends ViewPager {

    private int startX;
    private int startY;
    private int currentItem;

    public TopNewsViewPager(Context context) {
        super(context);
    }

    public TopNewsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 我们不能一下子全部拦截完,否则就不能切换各个页签
     * 1，上下滑动父控件拦截
     * 2，当右滑到某个页签的第一个页面需要父控件拦截，父控件做处理，切换页签
     * 2，当左滑到某个页签的最后一个页面需要父控件拦截，父控件做处理，切换页签
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //请求所有的父亲都不可以拦截我的事件
        //我们需要向请求一下父控件不要拦截，才有可能走到下面
         getParent().requestDisallowInterceptTouchEvent(true);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) ev.getX();
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int endX = (int) ev.getX();
                int endY = (int) ev.getY();

                //滑动距离
                int moveX = endX - startX;
                int moveY = endY = startY;

                if (Math.abs(moveX) > Math.abs(moveY)) {
                    //做事件的父控件是否进行拦截事件
                    if (moveX > 0) {
                        //向右滑动（ ）
                        currentItem = getCurrentItem();
                        if (currentItem == 0) {
                            //  /遇见页签的第一个父控件做拦截
                            //父控件 就是中间的一层已经做了 侧边栏的开启(将其交给了父控件处理）
                            getParent().requestDisallowInterceptTouchEvent(false);//父控件拦截返回flase
                        }
                    } else {
                        //向左滑动，
                        int conut = getAdapter().getCount();//获取和此Viewpager（头条的有几个数据）绑定的Adater的数据总共是多少
                        if (currentItem == conut - 1) {
                            //向左滑动，遇见也页签的最后一个父控件进行拦截
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }

                } else {
                    //用户手指上下滑动，父控件拦截事件
                    getParent().requestDisallowInterceptTouchEvent(false);
                }

                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(ev);
    }
}
