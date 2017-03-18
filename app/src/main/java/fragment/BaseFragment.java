package fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yh on 2016/12/14.
 */
//
/*两个子类无共性，我们只需抽象(initview)出来让子类实现*/
/*每个里面的initView都是初始化自己的布局，initData都是用来初始化数据的*/
/**/
public abstract  class BaseFragment extends Fragment {
    //我们在加载一个布局时，都需要传进一个上下文对象，我们需要在此类一实例化的同事将上下文对象Activity给初始化出来
 public Activity mActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //得到与Fragment绑定的activity
        mActivity = getActivity();
    }

    //初始化fragment的布局

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //写抽象方法，让子类去调用
        View view=initView();
        return view;
    }
    //fragment所依赖的activity的oncreat（）方法执行完毕
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化数据
        initData();
    }

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
    * 初始化fragment布局
    */
  public abstract View initView();

}
