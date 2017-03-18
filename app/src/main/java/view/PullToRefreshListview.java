package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import aygxy.zhbj74.R;

/**
 * 下拉刷新的头布局
 * Created by yh on 2016/12/18.
 */

public class PullToRefreshListview extends ListView implements AbsListView.OnScrollListener {

    private View mHerderView;
    private int mHerderViewMeasuredWidth;
    private int mHerderViewHeight;
    private int startY;

    private static final int PULL_TO_REFRESH = 0;//下拉刷新
    private static final int RELASE_REFRESH = 1;//释放刷新
    private static final int REFRESHING = 2;//刷新中
    private float currentState = 0;//默认的状态为下拉刷新
    private int padding;
    private RotateAnimation rotateUpAnimation;
    private RotateAnimation rotateDownAnimation;


    private ImageView mArrowView;
    private ProgressBar pb;
    private TextView mlastTimeDesc;
    private TextView mTitleText;
    private View mFooterView;
    private int mFooterHinght;
    private boolean isLoadingMore;//是否是正在加载更多


    /**
     * @param context
     */
    public PullToRefreshListview(Context context) {
        super(context);
        initHeader();
        initFooterView();
    }

    public PullToRefreshListview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeader();
        initFooterView();
    }


    public PullToRefreshListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeader();
        initFooterView();
    }

    /**
     * 初始化头布局
     */
    public void initHeader() {
        mHerderView = View.inflate(getContext(), R.layout.list_herder_view_refresh, null);

        //在此处找到header中的控件
        mArrowView = (ImageView) mHerderView.findViewById(R.id.iv_arrow);
        pb = (ProgressBar) mHerderView.findViewById(R.id.pb_loading);
        mTitleText = (TextView) mHerderView.findViewById(R.id.tv_title);
        mlastTimeDesc = (TextView) mHerderView.findViewById(R.id.tv_data);
        this.addHeaderView(mHerderView);
      initAnimation();


        //在这里一开始我们要隐藏头布局
        mHerderView.measure(0, 0);//设置一种规则
        mHerderViewHeight = mHerderView.getMeasuredHeight();
        mHerderViewMeasuredWidth = mHerderView.getMeasuredWidth();

        mHerderView.setPadding(0, -mHerderViewHeight, 0, 0);
    }

    /**
     * 初始化脚布局
     */
    private void initFooterView() {
        mFooterView = View.inflate(getContext(), R.layout.list_footer_view_refresh, null);
        mFooterView.measure(0, 0);

        //获取控件的这是高度
        mFooterHinght = mFooterView.getMeasuredHeight();
        //先设置其不可见
        mFooterView.setPadding(0, -mFooterHinght, 0, 0);
        this.addFooterView(mFooterView);

        //给listView设置滑动监听
        this.setOnScrollListener(this);
    }


    /*上拉
    当为上拉加载时进行接口回调
    （当界面加载刷新成功的时候，我们在需要onRefreshComplete（）将isLoadingMore重新复制为more）
    下面为上拉加载时监听listView实现的方法
     * 滑动加载状态改变调用，我们只实现它（空闲和触摸是才会改变）
     *
     * @param view
     * @param scrollState
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //如果正在加载更多就不再一次响应加载
        if (isLoadingMore) {
            //不在加载
            return;
        }
        // 最新状态为空闲，并且当前界面     显示了所有界面的最后一条，加载更多
        //只有当这种状态时我们次啊需要通知外界去加载更多
        if (SCROLL_STATE_IDLE == scrollState && getLastVisiblePosition() >= getCount() - 1) {
            isLoadingMore = true;
            //加载更多
            mFooterView.setPadding(0, 0, 0, 0);//让footerView显示出来
            setSelection(getCount());//跳到最后一条（即加载出来的东西，无需手动滑动加载更多
            //当上拉时加载更多在红要向外通知
            if (mListener != null) {
                mListener.onLoadMore();
            }
        }
    }

    /**下拉
     * 头布局
     * 我们在此处做各个触摸事件的处理，什么时候进行下拉刷新，释放刷新，刷新中

     * move时   dy>0 当前条目为0  （判断dy+头布局宽度>0此时状态为释放刷新，否则为下拉刷新)  当正在刷新中不响应事件
     *up时，如果padding>0  抬起时状态设为为刷新，否则让其还会恢复下拉刷新状态
     *  @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //当用户按住头条新闻的ViewPager进行下拉刷新，ACTION_DOWN会被ViewPager消费，导致startY没有被赋值，就在此处重新获取一下
                if (startY == -1) {
                    startY = (int) ev.getY();
                }

                //如果正在刷新，我们就不响应再次下拉事件
                if (currentState == REFRESHING) {
//                    return super.onTouchEvent(ev);
                    break;//这样写也可以
                }
                int endY = (int) ev.getY();
                //当前显示item的位置
                int firstVisiblePosition = getFirstVisiblePosition();
                int dY = endY - startY;
                //只有当listview 的条目为第一个时并且手指向下滑动的距离大于0，我们就让刷新列表下拉根据下拉的距离的多少显示出来多少
                if (dY > 0 && firstVisiblePosition == 0) {
                    padding = dY - mHerderViewHeight;
                    mHerderView.setPadding(0, padding, 0, 0);
                    if (padding >= 0 && currentState != RELASE_REFRESH) {//头布局全部显示
                        //切换成释放刷新模式
                        currentState = RELASE_REFRESH;
//                        System.out.println("切换成释放刷新状态"+ paddingTop +currentState);
                        //根据（移动过程中）我们在此处赋值的模式，抽取方法做不同的状态切换（1下拉刷新，2 释放刷新））
                        updateHerder();
                    } else if (padding < 0 && currentState != PULL_TO_REFRESH) {//头布局不完全显示
                        //切换成下拉刷新状态
                        currentState = PULL_TO_REFRESH;
//                        System.out.println("切换成下拉刷新状态"+ paddingTop +currentState);
                        updateHerder();
                    }
                    return true;//当前事件被我们处理并消费
                }
                break;
            //那么刷新列表应该放置的位置=头布局的高度-dy
            case MotionEvent.ACTION_UP:
//             startY = -1;
                if (padding < 0) {//不完全显示恢复下拉刷新
                    mHerderView.setPadding(0, -mHerderViewHeight, 0, 0);
                    //默认为下拉刷新，此时我们无需重新赋值
                } else {
                    //>=0 完全显示，执行正在刷新
                    mHerderView.setPadding(0, 0, 0, 0);
                    currentState = REFRESHING;
                    updateHerder();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**下拉
     * 根据状态切换，在刷新的时候要进行回调
     */
    public void updateHerder() {
        switch ((int) currentState) {
            case PULL_TO_REFRESH://切换为下拉刷新
                mArrowView.startAnimation(rotateUpAnimation);
                mTitleText.setText("下拉刷新");
                break;
            case RELASE_REFRESH://切换回释放刷新
                mArrowView.startAnimation(rotateDownAnimation);
                mTitleText.setText("释放刷新");
                break;
            case REFRESHING://刷新中
                mArrowView.clearAnimation();//要清除箭头动画，否则无法将箭头隐藏掉
                mArrowView.setVisibility(View.INVISIBLE);
                pb.setVisibility(View.VISIBLE);
                mTitleText.setText("正在刷新中。。。");
                if (mListener != null) {
                    //通知调用者，让其网络加载更多的数据
                    mListener.onRefresh();
                }
                break;
        }
    }

    /**
     * 初始化头布局的动画
     */
    private void initAnimation() {
        //向上转，围绕着自己的中心，逆时针旋转0——>-180
        rotateUpAnimation = new RotateAnimation(0f, -180f
                , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateUpAnimation.setDuration(300);
        rotateUpAnimation.setFillAfter(true);//动画停留在结束位置

        //向下旋转
        //向上转，围绕着自己的中心，逆时针旋转0——>-180
        rotateDownAnimation = new RotateAnimation(-180f, -360
                , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateDownAnimation.setDuration(300);
        rotateDownAnimation.setFillAfter(true);//动画停留在结束位置
    }

    //3 定义接口的成员变量
    private OnRefreshListener mListener;


    /**
     * 1, 下拉刷新的回调接口
     */
    public interface OnRefreshListener {
        void onRefresh();//下拉刷新
        void onLoadMore();//上拉加载更多
    }


    /**
     * 2,暴露接口，设置监听
     *
     * @param mListener
     */
    public void setORefreshListener(OnRefreshListener mListener) {
        this.mListener = mListener;
    }


    /**
     * 刷新结束，回复界面
     */
    public void onRefreshComplete(boolean sucess) {
        if (isLoadingMore) {//上拉一定走这个
            //加载更多
            mFooterView.setPadding(0, -mFooterHinght, 0, 0);
            isLoadingMore = false;
        } else {//下拉走这个
            currentState = PULL_TO_REFRESH;
            mTitleText.setText("下拉刷新");//重新设置回下拉刷新
            mHerderView.setPadding(0, -mHerderViewHeight, 0, 0);
            pb.setVisibility(View.INVISIBLE);//进度框也隐藏
            mArrowView.setVisibility(View.VISIBLE);
            if (sucess) {
                //zhiyou网络请求成功时才刷新时间
                String time = getTime();
                mlastTimeDesc.setText("最后的刷新时间：" + time);
            }
        }
    }

    /**
     * 获取系统当前的时间
     *
     * @return
     */
    private String getTime() {
        long currentTime = System.currentTimeMillis();
        //正则表达式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy—MM-dd HH:mm:ss");
        //格式化
        return dateFormat.format(currentTime);
    }

    /**
     * 滚动的时候
     *
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }
}
