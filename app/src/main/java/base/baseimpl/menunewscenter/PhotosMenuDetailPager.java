package base.baseimpl.menunewscenter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import aygxy.zhbj74.MainActivity;
import aygxy.zhbj74.R;
import base.BaseMenuDetailPager;
import domain.Photosbean;
import utils.CacheUtil;
import utils.ContantValue;
import utils.MyBitmapUtil;

/**
 * Created by yh on 2016/12/15.
 */

public class PhotosMenuDetailPager extends BaseMenuDetailPager implements View.OnClickListener {
    @ViewInject(R.id.lv_photos)
    private ListView lvPhotos;
    @ViewInject(R.id.gv_photos)
    private GridView gvPhotos;
    private ArrayList<Photosbean.PhotosNews> mNewList;

    private ImageButton btnPhoto;//

    public PhotosMenuDetailPager(MainActivity mainActivity, ImageButton btnphoto) {
        super(mainActivity);
        this.btnPhoto = btnphoto;
        btnPhoto.setOnClickListener(this);
    }
    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager_photos_menu_detail, null);
        ViewUtils.inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        //
        String cacheJson = CacheUtil.getCache(mActivity, ContantValue.PHOTOS_URL);
        if (!TextUtils.isEmpty(cacheJson)) {
            //如果缓存非空，直接解析，
            processsData(cacheJson);
        }
        getDataFromServer();
    }

    /**
     * 获取组图资源
     */
    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, ContantValue.PHOTOS_URL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String json = responseInfo.result;
                processsData(json);

                //将缓存的数据放到了sp中，
                CacheUtil.setCache(mActivity, ContantValue.PHOTOS_URL, json);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(mActivity, s, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 解析数据
     *
     * @param json
     */
    private void processsData(String json) {
        Gson gson = new Gson();
        Photosbean photosbean = gson.fromJson(json, Photosbean.class);

        mNewList = photosbean.data.news;


        lvPhotos.setAdapter(new PhotosAtater());
        gvPhotos.setAdapter(new PhotosAtater());//和listviewd的适配数据是一样
    }


    class PhotosAtater extends BaseAdapter {

        //  private final BitmapUtils utils;
        private final MyBitmapUtil myBitmapUtil;//用我们自己写的图片缓存对象

        public PhotosAtater() {

            myBitmapUtil = new MyBitmapUtil();
            //    utils = new BitmapUtils(mActivity);
            //设置默认的图片
            // utils.configDefaultLoadingImage(R.drawable.pic_list_item_bg);
        }


        @Override
        public int getCount() {
            return mNewList.size();
        }

        @Override
        public Photosbean.PhotosNews getItem(int position) {
            return mNewList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                convertView = View.inflate(mActivity, R.layout.list_item_photos, null);
                holder.icPic = (ImageView) convertView.findViewById(R.id.iv_pic);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Photosbean.PhotosNews item = getItem(position);
            System.out.println(item);
            holder.tvTitle.setText(item.title);
            //图片用bitmapUtils加载,给那个控件设置，根据Url设置图片
            myBitmapUtil.display(holder.icPic, item.listimage);//bitmapUtil根据URL设置图片
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView icPic;
        TextView tvTitle;

    }

    private boolean islistView = true;//标记机当前是否是listview的展示

    /**
     * 组图切换按钮点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (islistView) {
            //切成gv
            gvPhotos.setVisibility(View.VISIBLE);
            lvPhotos.setVisibility(View.GONE);
            //右侧按钮也要做图片的切换
            btnPhoto.setImageResource(R.drawable.icon_pic_grid_type);
            islistView = false;
        } else {
            //切成listView
            gvPhotos.setVisibility(View.GONE);
            lvPhotos.setVisibility(View.VISIBLE);
            btnPhoto.setImageResource(R.drawable.icon_pic_list_type);
            islistView = true;
        }
    }
}
