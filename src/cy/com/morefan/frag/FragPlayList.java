package cy.com.morefan.frag;

import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import cy.com.morefan.R;
import cy.com.morefan.bean.Question;
import cy.com.morefan.ui.answer.AnswerActivity;
import cy.com.morefan.util.ToastUtils;
  
public class FragPlayList extends BaseFragment implements OnClickListener , Callback
{
    Button btnGet=null;
    Button btnClose=null;  
    RelativeLayout rlBottom=null;
    WebView wvPage=null;
    List<Question> questions = null;
    ProgressBar pgbarLoading=null;
    RelativeLayout rlWaiting=null;    
    Handler myHandler =new Handler(this);
    
    int taskId=0;    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        Bundle bd = this.getArguments();        
        if( bd.containsKey("questions"))
        {
            questions = (List<Question>) bd.getSerializable("questions");
        }
        if (bd.containsKey("taskid"))
        {
            taskId = bd.getInt("taskid");
        }
        
        View rootView = inflater.inflate(R.layout.frag_playlist, container,false);
        btnGet = (Button)rootView.findViewById(R.id.btnGet);
        btnGet.setOnClickListener(this);
        btnClose = (Button)rootView.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);
        rlBottom = (RelativeLayout)rootView.findViewById(R.id.rlBottom);
        
        pgbarLoading = (ProgressBar)rootView.findViewById(R.id.pgbarLoading);
        rlWaiting = (RelativeLayout)rootView.findViewById(R.id.rlWaiting);
        
        wvPage=(WebView)rootView.findViewById(R.id.wvPage);
        wvPage.getSettings().setJavaScriptEnabled(true);    
        wvPage.addJavascriptInterface( new Object()
        {
             /**
             * 游戏页面回调接口
             *@方法描述：
             *@方法名：playCallBack
             *@参数：@param parameter
             *@返回：void
             *@exception 
             *@since
             */
            @JavascriptInterface
            public void playCallBack(String parameter){
                Message msg = myHandler.obtainMessage(1001, "测试测试！");
                myHandler.sendMessage(msg);
                //ToastUtils.showLongToast(getActivity(), parameter);
            }
        }, "fmmore");
        
        wvPage.setWebViewClient(new FmCallWebclient());       
        wvPage.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {
                // TODO Auto-generated method stub
//                rlWaiting.setVisibility(View.VISIBLE);
//                //pgbarLoading.setVisibility(View.VISIBLE);
//                if( newProgress >= 100){
//                    rlWaiting.setVisibility(View.GONE);
//                    //pgbarLoading.setVisibility(View.GONE);
//                }
                
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title)
            {
                // TODO Auto-generated method stub
                //ToastUtils.showLongToast(getActivity(), title);
                super.onReceivedTitle(view, title);
            }
            
        });
        
        if( questions!=null && questions.size()>0){
            wvPage.loadUrl(questions.get(0).getRelexUrl());
        }
                
        return rootView;        
    }
    
    class FmCallWebclient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            // TODO Auto-generated method stub
            //Log.i("url", url);
            String temp = url.trim().toLowerCase(Locale.getDefault());
            
            if( temp.equals( "newfanmore://finishgame")){
                //rlBottom.setVisibility(View.VISIBLE);
                BaseFragment frag = new FragAnswerPass();
                Bundle bd = new Bundle();
                bd.putInt("taskid", taskId);
                int rightCount=1;
                bd.putInt("rightcount", rightCount);
                int wrongCount=0;
                bd.putInt("wrongcount", wrongCount);
                String answerString="";
                bd.putSerializable("answers", answerString);
                frag.setArguments(bd);

                ((AnswerActivity) getActivity()).switchFragment(frag, "answerpass");
                
                return true;
            }
            
            
            return false;
        }
    }

    @Override
    public void onReshow()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFragPasue()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View view)
    {
        if( view == btnGet){
            //获得流量事件
            BaseFragment frag=new FragAnswerPass();
            Bundle bd=new Bundle();
            bd.putInt("taskid", taskId);
            bd.putInt("rightcount", 1);
            bd.putInt("wrongcount",0);
            String answerString = questions.get(0).getQid()+"|";
            bd.putString("answers", answerString);
            frag.setArguments(bd);
            
            ((AnswerActivity)getActivity()).switchFragment(frag, "answerpass");            
        }else if( view == btnClose){
            //关闭事件
            rlBottom.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean handleMessage(Message msg)
    {
        // TODO Auto-generated method stub
        if( msg.what==1001){
            ToastUtils.showLongToast(getActivity(), msg.obj.toString());
        }
        return false;
    }

    
}
