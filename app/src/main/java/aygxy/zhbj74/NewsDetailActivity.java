package aygxy.zhbj74;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class NewsDetailActivity extends AppCompatActivity implements View.OnClickListener {
    @ViewInject(R.id.ll_control)
    private LinearLayout llControl;
    @ViewInject(R.id.btn_menu)
    private ImageButton btnMenu;

    @ViewInject(R.id.btn_back)
    private ImageButton btnBack;

    @ViewInject(R.id.btn_share)
    private ImageButton btnshare;

    @ViewInject(R.id.btn_textsize)
    private ImageButton btnTextSize;

    @ViewInject(R.id.web_view)
    private WebView mWebView;

    @ViewInject(R.id.pb_loading)
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_news);
        ViewUtils.inject(this);//当前只需这样传即可
        //
        llControl.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.VISIBLE);
        btnMenu.setVisibility(View.INVISIBLE);
        String mUrl = getIntent().getStringExtra("url");
        //设置链接
        mWebView.loadUrl(mUrl);

        //做按钮的点击事件
        btnBack.setOnClickListener(this);
        btnTextSize.setOnClickListener(this);
        btnshare.setOnClickListener(this);

//        mWebView.loadUrl("http://www.baidu.com");
        WebSettings settings = mWebView.getSettings();
        settings.setBuiltInZoomControls(true);//显示缩放按钮(swap不适用)
        settings.setUseWideViewPort(true);//双击缩放(swap不适用)
        settings.setJavaScriptEnabled(true);//支持js功能
        //
        mWebView.setWebViewClient(new WebViewClient() {
            //开始加载网页
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                System.out.println("开始加载网页");
            }

            //网页加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                System.out.println("网页加载结束");
                progressBar.setVisibility(View.INVISIBLE);
            }

            //所有连接跳转会走此方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String request) {
                System.out.println("跳转连接" + request);
                view.loadUrl(request);
                return true;
            }
        });
        mWebView.goBack();//跳到上一页
        mWebView.goForward();//跳转下一页

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                System.out.println("加载进度" + newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //
                System.out.println("网页标题" + title);
            }
        });
    }


    /**
     * 做按钮点击事件的监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_textsize:
                //弹出对话框，做字体的更改
                showChooseAlertDialog();
                break;
            case R.id.btn_share:
                break;

        }
    }

    /**
     * 弹出更改字体大小的对话框
     */
    private int mTempWhich;//记录临时选择的字体大小（点击确定之前）
    private int mCurrentWhich = 2;//记录选择后的字体大小（点击确定之后）

    private void showChooseAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("设置字体大小");
        String[] size = new String[]{"超大号字体", "大号字体", "正常字体", "小号字体", "超小号字体"};
        dialog.setSingleChoiceItems(size, mCurrentWhich, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //当点击了某个之后，
                mTempWhich = which;
            }
        });

        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //点击确定之后的操作
                //确定之后我们将根据选择的字体来修改网页字体大小
                WebSettings settings = mWebView.getSettings();
                switch (mTempWhich) {
                    case 0:
                        settings.setTextSize(WebSettings.TextSize.LARGEST);
                        break;
                    case 1:
                        settings.setTextSize(WebSettings.TextSize.LARGER);
                        break;
                    case 2:
                        settings.setTextSize(WebSettings.TextSize.NORMAL);
                        break;
                    case 3:
                        settings.setTextSize(WebSettings.TextSize.SMALLEST);
                        break;
                    case 4:
                        settings.setTextSize(WebSettings.TextSize.SMALLER);
                        break;
                }
                mCurrentWhich = mTempWhich;//要记录当时的字体大小
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.show();
    }
}
