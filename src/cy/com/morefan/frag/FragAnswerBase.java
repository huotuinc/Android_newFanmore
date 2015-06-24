package cy.com.morefan.frag;

import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.view.CountDownTimerButton;
import cy.com.morefan.view.FmWebViewClient;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class FragAnswerBase extends BaseFragment implements OnClickListener
{
    Button btnAnswer = null;

    TaskData taskData = null;

    WebView wvPage = null;

    ProgressBar pgbarWaiting = null;

    View rootView = null;

    MyApplication app = null;

    RelativeLayout rlWaiting = null;

    protected void initView(View rootView)
    {
        app = (MyApplication) getActivity().getApplication();

        btnAnswer = (Button) rootView.findViewById(R.id.btnAnswer);
        btnAnswer.setOnClickListener(this);

        rlWaiting = (RelativeLayout) rootView.findViewById(R.id.rlWaiting);
        pgbarWaiting = (ProgressBar) rootView.findViewById(R.id.pgbarWaiting);

        wvPage = (WebView) rootView.findViewById(R.id.wvPage);
        wvPage.getSettings().setJavaScriptEnabled(true);
        wvPage.setWebViewClient(new FmWebViewClient());
        wvPage.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {
                // TODO Auto-generated method stub
                rlWaiting.setVisibility(View.VISIBLE);
                if (newProgress == 100)
                {
                    rlWaiting.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }
    
    
    protected void SetTaskButtonState( String titleFormat , String title ){
        if( taskData==null) return;
        
        if( taskData.getTimeToStart()>0)
        {
            String timeStr = cy.com.morefan.util.DateUtils.toTime( taskData.getTimeToStart());            
            String tipText =String .format( "任务离上线还有%s", timeStr );
            btnAnswer.setText(tipText);
            btnAnswer.setClickable(false);
            
        }else
        {
            CountDownTimerButton countDown = new CountDownTimerButton(
                    btnAnswer,
                    titleFormat,//"答题领流量(%d)",
                    title,//"答题领流量",
                    taskData.getBackTime() * 1000);
              countDown.start();             
        }
        wvPage.loadUrl(taskData.getContextURL()); 
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.frag_answer, container, false);

        Bundle bd = this.getArguments();
        if (bd.containsKey("task"))
        {
            taskData = (TaskData) bd.getSerializable("task");
        }

        initView(rootView);

        return rootView;
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
    public void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        
        showAnswer();
        
    }
    

private void showAnswer()
{
    btnAnswer.setVisibility(View.GONE);
    if( taskData==null) return;
    
    
    boolean d = taskData.getLast() <=0;
    
    if( taskData.getLast()<=0){
        btnAnswer.setVisibility(View.GONE);
        return;
    }
    
     d = taskData.getReward() > 0;
    
     Log.i("test", taskData.getReward()+ "d="+ d );
       
     
    if( taskData.getReward() > 0) {
        btnAnswer.setVisibility(View.GONE);
        return;
    }
    
    btnAnswer.setVisibility(View.VISIBLE);
}

    
    

    @Override
    public void onClick(View view)
    {
        // TODO Auto-generated method stub

    }
}
