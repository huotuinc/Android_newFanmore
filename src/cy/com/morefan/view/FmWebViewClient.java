package cy.com.morefan.view;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FmWebViewClient extends WebViewClient
{
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        // TODO Auto-generated method stub
        Log.i("url", url);
        
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
