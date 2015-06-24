package cy.com.morefan.ui.ambitus;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.view.CyButton;
/**
 * 
 * @类名称：RlueActivity
 * @类描述：注册隐私界面
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月10日 上午10:00:21
 * @修改备注：
 * @version:
 */
public class RlueActivity extends BaseActivity implements Callback,
        OnClickListener
{

    private CyButton backImage;
    public MyApplication application;
    private TextView title;
    private WebView rulePage;
    //返回文字事件
    private TextView backText;
    
    @Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        application = (MyApplication) RlueActivity.this.getApplication();
        this.setContentView(R.layout.rule_ui);
        this.initView();
        loadWebPage();
    }

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        switch (v.getId())
        {
        case R.id.backImage:
        {
            closeSelf(RlueActivity.this);
        }
            break;
        case R.id.backtext:
        {
            closeSelf(RlueActivity.this);
        }
            break;
        default:
            break;
        }
    }

    @Override
    public boolean handleMessage(Message msg)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    private void initView()
    {
        backImage = (CyButton) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(this);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("使用条款说明");
        rulePage = (WebView) this.findViewById(R.id.rulePage);
        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);
    }
    private void loadWebPage()
    {
        initWebSetting();
    }
    private void initWebSetting()
    {
        WebSettings settings = rulePage.getSettings();
        settings.setJavaScriptEnabled(true);
        //阻塞图片下载
        settings.setBlockNetworkImage(true);
        rulePage.loadUrl(application.readString(RlueActivity.this, Constant.INIT_INFO, Constant.INIT_PRIVATE_URL));
        
        rulePage.setWebChromeClient(new ChromeView());
        rulePage.setWebViewClient(new Client());
    }
    
    private class Client extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            // TODO Auto-generated method stub
            return false;
        }
 
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }
 
        @Override
        public void onPageFinished(WebView view, String url)
        {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            view.getSettings().setBlockNetworkImage(false);
        }
         
    }
    
    private class ChromeView extends WebChromeClient
    {
        @Override
        public void onProgressChanged(WebView view, int newProgress)
        {
            // TODO Auto-generated method stub
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                //progressbar.setVisibility(GONE);
            } else {
                /*if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);*/
            }
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            // finish自身
            RlueActivity.this.finish();
            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

}
