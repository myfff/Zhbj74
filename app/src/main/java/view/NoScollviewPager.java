package view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by yh on 2016/12/14.
 */
/*后  如果我们不禁用侧边栏的话，侧边栏将ViewPager事件拦截，使得Viewpager无法响应，而滑出侧边栏，
所以此时我们只有禁用Viewpager的滑动事件，重写onTouchEvent，返回True不做处理即可
但如果想要切换5个标签页，我们只有用groupButton的点击事件

如果有右边侧边栏，我们不管向右向左滑动，都会讲侧边栏滑出，因为侧边栏对滑动事件做了拦截

这样之后我们只要滑动最外层就会出现侧边栏，我们又做了侧边栏的开启与禁用*/

/*5个标签页的ViewPager不让其滑动效果
如果用原生的ViewPager，我们会发现当我们左右滑动的时候，会发现滑动的是侧边栏SidingMenu而不是我们想要滑动的viewpager
,所以我们需要自定义一个View继承ViewPager，，冲刺额触摸事件
，在里面返回true，代表消费了此事件没有做相应的滑动的处理，此时Viewpager的滑动事件就会被禁用*/


   /*注意
   * onInterceptTouchEvent()是ViewGroup的一个方法，目的是在系统向该ViewGroup及其各个childView触发onTouchEvent()之前对相关事件进行一次拦截.

down事件首先会传递到onInterceptTouchEvent()方法

1，如果该ViewGroup的onInterceptTouchEvent()在接收到down事件处理完成之后return false，那么后续的move, up等事件将继续会先传递给该ViewGroup，之后才和down事件一样传递给最终的目标view的onTouchEvent()处理。
2，如果该ViewGroup的onInterceptTouchEvent()在接收到down事件处理完成之后return true，那么后续的move, up等事件将不再传递给onInterceptTouchEvent()，而是和down事件一样传递给该ViewGroup的onTouchEvent()处理，注意，目标view将接收不到任何事件。
3，如果最终需要处理事件的view的onTouchEvent()返回了false，那么该事件将被传递至其上一层次的view的onTouchEvent()处理。

4，如果最终需要处理事件的view 的onTouchEvent()返回了true，那么后续事件将可以继续传递给该view的onTouchEvent()处理。

*/
public class NoScollviewPager extends ViewPager {
    public NoScollviewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public NoScollviewPager(Context context ) {
        super(context );
    }
    /**重写此方法，什么都不做，从而实现对滑动事事件的禁用
     * @param ev
     * @return
     */

    /*是否向上进行回传处理*/
    @Override
    public boolean onTouchEvent  (MotionEvent ev) {
   //     return  false; 处理事件，不再返回上一级处理，true消费此事件，
        return true;//  没有处理事件（在touch中处理），返回给上一级处理
    }

   /*是否对下进行拦截*/
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;//不拦截事件，准许向下传递给子控件
       // return  true   拦截事件，不准许再向下传递
    }
}
