package aygxy.zhbj74;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import utils.ContantValue;
import utils.SpUtil;

public class SplashActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        RelativeLayout rlRoot=(RelativeLayout)findViewById(R.id.rlRoot);

        //旋转动画
        RotateAnimation rotateAnimation=new RotateAnimation(0,360,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setFillAfter(true);
        //缩放动画
        ScaleAnimation scaleAnimation=new ScaleAnimation(0,1,0,1,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);
        //渐变动画
        AlphaAnimation alphaAnimation=new AlphaAnimation(0,1);
        alphaAnimation.setDuration(2000);
        alphaAnimation.setFillAfter(true);

        //动画集合
        AnimationSet set=new AnimationSet(true);
        set.addAnimation(rotateAnimation);
        set.addAnimation(scaleAnimation);
        set.addAnimation(alphaAnimation);
        rlRoot.startAnimation(set);
        //动画执行结束后进行界面的跳转，我们做动画执行的监听
        set.setAnimationListener(new Animation.AnimationListener() {
            /**动画开始
             * @param animation
             */
            @Override
            public void onAnimationStart(Animation animation) {

            }

            /**动画结束，第一次跳新手引导页，否则跳主界面
             * @param animation
             */
            @Override
            public void onAnimationEnd(Animation animation) {
                boolean isFirstEnter=SpUtil.getBoolean(SplashActivity.this, ContantValue.IS_FIRST_ENTER,true);
                Intent intent;
                if(isFirstEnter){
                    //进入
                    intent=new Intent(SplashActivity.this,GuideActivity.class);
                }else {
                    intent=new Intent(SplashActivity.this,MainActivity.class);
                }
                startActivity(intent);
                finish();
//            SpUtil.putBoolean(SplashActivity.this,ContantValue.IS_FIRST_ENTER,false);
            }
            /**动画重复执行时执行
             * @param animation
             */
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
