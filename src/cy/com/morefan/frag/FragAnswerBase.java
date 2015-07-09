package cy.com.morefan.frag;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMTaskDetail;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.PreferenceHelper;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.CountDownTimerButton;
import cy.com.morefan.view.CountDownTimerButton.CountDownFinishListener;
import cy.com.morefan.view.FmWebViewClient;

public class FragAnswerBase extends BaseFragment implements OnClickListener
{
    Button btnAnswer = null;

    TaskData taskData = null;
    
    Long secondToStart =null;

    WebView wvPage = null;

    ProgressBar pgbarWaiting = null;

    View rootView = null;

    MyApplication app = null;

    RelativeLayout rlWaiting = null;
    
    ProgressDialog waitingDlg=null;
    
    CountDownTimerButton countDown =null;

    protected void initView(View rootView)
    {
        app = (MyApplication) getActivity().getApplication();

        btnAnswer = (Button) rootView.findViewById(R.id.btnAnswer);
        btnAnswer.setOnClickListener(this);
        btnAnswer.setClickable(false);
        btnAnswer.setBackgroundResource(R.drawable.btn_mark_gray);

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
    
    /**
     * 
     *@创建人：jinxiangdong
     *@修改时间：2015年6月25日 下午8:40:48
     *@方法描述：
     *@方法名：SetTaskButtonState
     *@参数：@param titleFormat
     *@参数：@param title
     *@返回：void
     *@exception 
     *@since
     */
    protected void setTaskButtonState( String titleFormat , String title ){
        
        if(countDown !=null){
            countDown.Stop();
        }
        
        if( taskData==null) return;
        
        if( null != secondToStart && secondToStart > 0 )
        {
            //String timeStr = cy.com.morefan.util.DateUtils.toTime( );            
            String formatText ="任务离上线还有%d";
            String text = "任务已经上线";
            //btnAnswer.setText(tipText);
            //btnAnswer.setClickable(false);           
            
            countDown = new CountDownTimerButton(
                    btnAnswer,
                    formatText,
                    text,
                    secondToStart * 1000 , countdownListener );
              countDown.start();                         
        }else
        {
            boolean disableCountdown = false ;//这个变量控制 按钮是否倒计时。
            Bundle bd = this.getArguments();
            if( bd.containsKey("disableCountdown")){
                disableCountdown = bd.getBoolean("disableCountdown");
            }
            
            disableCountdown = isReadTaskDetail( taskData.getTaskId());
            
            if( false == disableCountdown)
            {
                countDown = new CountDownTimerButton(
                        btnAnswer,
                        titleFormat,//"答题领流量(%d)",
                        title,//"答题领流量",
                        taskData.getBackTime() * 1000 , countdownListener );
                  countDown.start();      
            }else
            {
                btnAnswer.setClickable(true);
                btnAnswer.setBackgroundResource(R.drawable.btn_red_sel);
                btnAnswer.setText(title);
            }
        }
        //wvPage.loadUrl(taskData.getContextURL()); 
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.frag_answer, container, false);

//        Bundle bd = this.getArguments();
//        if (bd.containsKey("task"))
//        {
//            taskData = (TaskData) bd.getSerializable("task");
//        }

        initView(rootView);

        return rootView;
    }

    @Override
    public void onReshow()
    {
        // TODO Auto-generated method stub
        Bundle bd = this.getArguments();
        if (bd.containsKey("task"))
        {
            taskData = (TaskData) bd.getSerializable("task");
        }
        
        
        if( taskData==null) return;        
        if( Util.isConnect(getActivity()) ==false){
            ToastUtils.showLongToast(getActivity(), "无法连接网络，请检查网络设置");
            btnAnswer.setVisibility(View.GONE);
            return;
        }
        //new TaskDetailAsyncTask().execute(taskData.getTaskId());
        
        showAnswer();
        
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
        
        //showAnswer();        
    }
    

private void showAnswer( )
{
    secondToStart = taskData.getTimeToStart();
    
    btnAnswer.setVisibility(View.GONE);
    if( taskData==null || taskData.getQuestions() ==null || taskData.getQuestions().size()<1 ) return;
    

    if( taskData!=null && taskData.getContextURL() !=null && taskData.getContextURL().length()>0 ){
        wvPage.loadUrl(taskData.getContextURL());
    }    
        
    if( taskData.getReward() > 0 || taskData.getTaskFailed() >0 ) {
        btnAnswer.setVisibility(View.GONE);
        return;
    }
    
    if( taskData.getLast()<=0){
        btnAnswer.setVisibility(View.GONE);
        return;
    }
    
    btnAnswer.setVisibility(View.VISIBLE);
         
    BigDecimal flow = new BigDecimal( taskData.getMaxBonus());
    DecimalFormat format = new DecimalFormat("0.##");
    String flowString = format.format( flow);    
    String content ="";
    
    if( taskData.getType().equals( Constant.TASK_TYPE_ANSWER_OUT) ||
         taskData.getType().equals(Constant.TASK_TYPE_ANSWER)   )
    {
        content = "答题领"+ flowString +"M流量";
    }else if( taskData.getType().equals(Constant.TASK_TYPE_PLAY)){
        content = "玩游戏领"+ flowString +"M流量";
    }else if( taskData.getType().equals(Constant.TASK_TYPE_SIGN_UP)){
        content = "报名领"+ flowString +"M流量";
    }
    
    setTaskButtonState(content+"(%d)", content );    
}

      

    @Override
    public void onClick(View view)
    {
        // TODO Auto-generated method stub

    }
    
    
    protected void startNextFragment(){
        
    }
    
    /**
     * 标记任务是否阅读过
     *@创建人：jinxiangdong
     *@修改时间：2015年6月27日 下午3:17:18
     *@方法描述：
     *@方法名：saveReadTask
     *@参数：
     *@返回：void
     *@exception 
     *@since
     */
    protected void saveReadTask( int taskid ){
        String key = String.valueOf( taskid);        
        PreferenceHelper.writeBoolean(getActivity(), "task", key, true);
    }
    
    /**
     * 判断任务是否阅读过
     *@创建人：jinxiangdong
     *@修改时间：2015年6月27日 下午3:20:12
     *@方法描述：
     *@方法名：isReadTaskDetail
     *@参数：@param taskid
     *@参数：@return
     *@返回：boolean
     *@exception 
     *@since
     */
    protected boolean isReadTaskDetail(  int taskid){
        String key = String.valueOf(taskid);
        if(null == key) return false;
        return PreferenceHelper.readBoolean(getActivity(), "task", key ,false);
    }
    
    
    class TaskDetailAsyncTask extends AsyncTask<Integer , Void , FMTaskDetail>{

        @Override
        protected FMTaskDetail doInBackground(Integer... params)
        {                        
            Integer taskId = params[0];
            String url = Constant.TASK_DETAIL_INTEFACE;
            ObtainParamsMap obtainMap=new ObtainParamsMap(getActivity());
            String paraString = obtainMap.getMap();
            Map<String , String > signMap = new HashMap<String, String>();
            signMap.put("taskId", String.valueOf( taskId));
            String sign = obtainMap.getSign(signMap);
                        
            try
            {
                url+="?taskId="+taskId;
                url+= paraString;
                url+="&sign="+ URLEncoder.encode(sign,"UTF-8");
                
                String responseStr = HttpUtil.getInstance().doGet(url);
                JSONUtil<FMTaskDetail> jsonUtil=new JSONUtil<FMTaskDetail>();
                FMTaskDetail result = new FMTaskDetail();
                try
                {
                    result = jsonUtil.toBean(responseStr, result);
                }
                catch(JsonSyntaxException e)
                {
                   LogUtil.e("JSON_ERROR", e.getMessage());
                   result.setResultCode(0);
                   result.setResultDescription("解析json出错");
                }
                return result;
            } catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();             
                Log.e("answer", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            rlWaiting.setVisibility(View.VISIBLE);
            //btnAnswer.setVisibility(View.GONE);
            
            if( waitingDlg==null){
                waitingDlg=new ProgressDialog(getActivity());
                waitingDlg.setMessage("请稍等...");
                waitingDlg.setCancelable(false);
            }
            waitingDlg.show();
            
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(FMTaskDetail result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            
            if( taskData!=null && taskData.getContextURL() !=null && taskData.getContextURL().length()>0 ){
                wvPage.loadUrl(taskData.getContextURL());
            }
            
            rlWaiting.setVisibility(View.GONE);
            
            if( waitingDlg!=null){
                waitingDlg.dismiss();
                waitingDlg=null;
            }            
                        
            if(result==null){
                ToastUtils.showLongToast(getActivity() , "请求失败。");
                btnAnswer.setVisibility(View.GONE);
                return;
            }
            if( 1!= result.getSystemResultCode()){
                ToastUtils.showLongToast( getActivity() , result.getSystemResultDescription());
                btnAnswer.setVisibility(View.GONE);
                return;
            }
            if( Constant.TOKEN_OVERDUE == result.getResultCode()){
                // 提示账号异地登陆，强制用户退出
                // 并跳转到登录界面
                ToastUtils.showLongToast(getActivity(), "账户登录过期，请重新登录");
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        ActivityUtils.getInstance().loginOutInActivity(
                                (Activity) getActivity());
                    }
                }, 2000);
                return;
            }
            
            if(1!= result.getResultCode()){
                ToastUtils.showLongToast( getActivity() , result.getResultDescription());
                btnAnswer.setVisibility(View.GONE);
                return;
            }
            
            secondToStart = result.getResultData().getSecondToStart();
            
            //Log.i("answer", "倒计时="+ String.valueOf( secondToStart ) );
            
            taskData = result.getResultData().getTask();
            
            taskData.setQuestions(result.getResultData().getTaskDetail());
            
            if( taskData.getQuestions() ==null || taskData.getQuestions().size()<1){
                btnAnswer.setVisibility(View.GONE);
            }              
               
            showAnswer();
        }              
    }            

    private CountDownFinishListener countdownListener = new CountDownFinishListener()
    {        
        @Override
        public void finish()
        {
            if( taskData!=null)
            {
                saveReadTask( taskData.getTaskId() );
                secondToStart = 0L;
                taskData.setTimeToStart(0L);
                
                Bundle bd = new Bundle();
                bd.putInt("index", -1 );
                MyBroadcastReceiver.sendBroadcast(getActivity(), MyBroadcastReceiver.REFRESH_TASK_STATUS, bd);
                
            }
        }
    };

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        
        if( countDown !=null ){
            countDown.Stop();
            countDown=null;
        }
    }      

}
