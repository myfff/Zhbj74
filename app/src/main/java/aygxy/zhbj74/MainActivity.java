package aygxy.zhbj74;

import android.os.Bundle;
import android.view.WindowManager;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import fragment.ContentMenuFragment;
import fragment.LeftMenuFragment;
/*动态添加fragment
一开始主main里面加载的布局，后来我们将用Frragment替换（第一个参数为要替换的布局id）*/

public class MainActivity extends SlidingFragmentActivity {
    private static final String TAG_CONTENT = "tagcontent";
    private static final String TAG_LEFT = "tagleft";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //必须在setContentView之前去标题
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //设置左边侧滑栏
        setBehindContentView(R.layout.left_menu);
        SlidingMenu slidingMenu = getSlidingMenu();
        /*//设置右边侧滑栏
        slidingMenu.setSecondaryMenu(R.layout.right_menu);

        //设置模式，左右都有侧滑
        slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);*/
        //设置全屏触摸
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        //设置侧边栏宽度
        // slidingMenu.setBehindOffset(200);

        //做屏幕适配之代码适配
        WindowManager wm = getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        slidingMenu.setBehindOffset(width * 150 / 320);
        initFragment();
    }

    private void initFragment() {
        //【1】//拿到Fragment管理器（此方法兼容）
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        //【2】开启事务
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        //【3】用fragment替换(fragmeng要替换的布局id，fragment的实例对象，fragment的标志)
        transaction.replace(R.id.left_menu, new LeftMenuFragment(), TAG_LEFT);
        transaction.replace(R.id.main_menu, new ContentMenuFragment(), TAG_CONTENT);

//      Fragment contentFragment=fragmentManager.findFragmentByTag(TAG_CONTENT);//通过此标签获取相对应的Fragment

        //【5】返回栈,模拟返回栈，返回上一个
        transaction.addToBackStack(null);
        //【5】提交事务
        transaction.commit();
    }

    /**
     * 在活动中获取fragment
     * 获取左边侧边栏可以通过id，Tag
     *
     * @return
     */
    public LeftMenuFragment getLeftMenuFragment() {
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();

        //注意此处不可以改为这样获得fragment
        // LeftMenuFragment leftMenuFragment=(LeftMenuFragment)getSupportFragmentManager().
        //   findFragmentById(R.id.fl_left_fragment) ;
        LeftMenuFragment leftMenuFragment = (LeftMenuFragment) manager.findFragmentByTag(TAG_LEFT);
        return leftMenuFragment;

    }

    /**
     * 获取ContentFragment
     *
     * @return
     */
    public ContentMenuFragment getContentMenuFragment() {
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        ContentMenuFragment contentMenuFragment = (ContentMenuFragment) manager.findFragmentByTag(TAG_CONTENT);
        return contentMenuFragment;
    }
}
