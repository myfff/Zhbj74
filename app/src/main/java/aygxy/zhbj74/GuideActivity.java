package aygxy.zhbj74;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import utils.DensityUtils;

/*此处设置白点红点只需要加1即可，我们并未用图片选择器，此处我们根据ViewPager滑动过程中，
* 所滑动的的百分比，利用此乘小白点之间的间距，当切换界面的过程中，让小红花点进行移动*/
public class GuideActivity extends AppCompatActivity {

    int[] mImageIeds=new int[]{R.drawable.guide_1,R.drawable.guide_2,R.drawable.guide_3};
    private ArrayList<ImageView> mImageViewLists;
    private LinearLayout llContainer;
    private ViewPager mViewPager;
    private Button btStart;
    private ImageView ivRedPoint;
    private static final String TAG = "GuideActivity";
    private int mPointDis;//小红点之间的距离

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initUI();
        initData();
       MyAdapter myAdapter= new MyAdapter();
        mViewPager.setAdapter(myAdapter);

        //做ViewPager的事件监听（做滑动中的处理，先计算小白点之间的间距做一个监听回调，根据间距计算滑动时的左边距（间距*便宜百分比），重新设置小红点的位置（记得加上position*间距））
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**滑动过程回调（此时肯定画完）
             * @param position
             * @param positionOffset
             * @param positionOffsetPixels
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i(TAG, "onPageScrolled: 滑动当前位置+"+position+"偏移百分比"+positionOffset);
                ///更新小红点的要移动的距离，小红点的左边距
                //position*mPointDis必须加上，否则移动结束后，就会又回到原位置position为0,，1,2（即当前为第几个ViewPager）
                int leftMargin= (int) (mPointDis*positionOffset)+position*mPointDis;
                RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)ivRedPoint.getLayoutParams();

                layoutParams.leftMargin=leftMargin;
                ivRedPoint.setLayoutParams(layoutParams);
            }
            /**选中
             * @param position
             */
            @Override
            public void onPageSelected(int position) {
                if(position==mImageViewLists.size()-1){
                    btStart.setVisibility(View.VISIBLE);
                }else {
                    btStart.setVisibility(View.INVISIBLE);
                }
            }

            /**页面状态发生改变调用
             * @param state
             */
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


       /* //小红点移动的距离，第2个left-第一个left
        // (此处计算的小圆点的距离为0，因为onCreat()还未结束，未获取焦点，还未执行mersure，layout，draw，
        // 并不能获取两百点之间的距离)
        mPointDis = llContainer.getChildAt(1).getLeft()-llContainer.getChildAt(0).getLeft();
        Log.i(TAG, "onCreate: 小白点移动的距离1"+mPointDis);
        */
        //监听layout方法的完成，完成后我们进行测量
        //ViewTreeObserver视图树
        ivRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                //监听一次，我们不需要监听那么多次，进来就移除，值执行一次
                //移除避免重复监听
                ivRedPoint.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                ivRedPoint.removeOnLayoutChangeListener(this);此方法需要API6

                mPointDis = llContainer.getChildAt(1).getLeft()-llContainer.getChildAt(0).getLeft();
                Log.i(TAG, "onCreate: 小白点移动的距离"+mPointDis);
            }
        });

        //开始体验按钮
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GuideActivity.this,MainActivity.class));
            }
        });
    }

    private void initUI() {
        mViewPager = (ViewPager)findViewById(R.id.vp_guide);
        btStart = (Button)findViewById(R.id.bt_start);
        llContainer = (LinearLayout)findViewById(R.id.ll_container);
        ivRedPoint = (ImageView)findViewById(R.id.iv_red_point);
    }

    /**
     * 初始化要适配的数据
     */
    private void initData() {
        //创建一个list集合，将初始化的3张引导界面放入到list中
        mImageViewLists=new ArrayList<ImageView>();
        for (int i=0;i<mImageIeds.length;i++){
            ImageView view=new ImageView(this);
            view.setBackgroundResource(mImageIeds[i]);//通过设置背景，可以让宽高填充布局
//            view.setImageResource();
            mImageViewLists.add(view);


            //下面是三个白点
            ImageView  point=new ImageView(this);
            point.setImageResource(R.drawable.shape_point_gray);//当我们不需要图片填充父容器，就可以使用此方法

            //从第2个开始，左边要有间距
            //初始化布局参数，宽高包裹内容，父控件是谁，就声明睡得布局参数
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if(i>0){
                /*在此处我们做一下屏幕适配，代码中用的是Px在大的分辨率中圆点变大，间距不变，看着不好，
                我们做（而我们需要用dp）屏幕适配，提供一个工具类将dp转换为px， dp=px/屏幕密度*/

                //这样的话10会自动做不同屏幕密度的适配
                params.leftMargin= DensityUtils.dip2px((float) 10,this);//左边距为10
            }
            point.setLayoutParams(params);
            llContainer.addView(point);
        }
    }

    class MyAdapter extends PagerAdapter{

        /**个数
         * @return
         */
        @Override
        public int getCount() {
            return mImageViewLists.size();
        }

        /**一般都是固定写法，判断返回的是否为一个View对象
         * @param view
         * @param object
         * @return
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        /**初始化Item布局
         * @param container
         * @param position
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view=mImageViewLists.get(position);//此处我们提前将ImageView先加到集合中，到这里直接用
            container.addView(view);
            return view;
        }

        /**销毁一个item
         * @param container
         * @param position
         * @param object
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }


}
