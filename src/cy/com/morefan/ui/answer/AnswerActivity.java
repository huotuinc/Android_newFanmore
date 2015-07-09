package cy.com.morefan.ui.answer;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.MainActivity;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMTaskDetail;
import cy.com.morefan.bean.FMTaskTurnedNotify;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.frag.BaseFragment;
import cy.com.morefan.frag.FragAnswer;
import cy.com.morefan.frag.FragAnswerOut;
import cy.com.morefan.frag.FragPlay;
import cy.com.morefan.frag.FragSignup;
import cy.com.morefan.guide.GuideActivity;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.receiver.MyReceiver;
import cy.com.morefan.ui.user.LoginActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.LruImageCache;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ShareUtil;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.util.VolleyUtil;
import cy.com.morefan.view.CyButton;

public class AnswerActivity extends BaseActivity implements Callback, OnClickListener ,BroadcastListener
{
    private TaskData taskData = null;
    private CyButton backImage=null;    
    private TextView txtTitle=null;
    private TextView txtFunction=null;    
    private PopupWindow popupWindow=null;
    //返回文字事件
    private TextView backText;
    private int channelType=1;
    RelativeLayout rlWaiting=null;
    MyBroadcastReceiver myBroadcastReceiver;
    ProgressDialog waitingDlg=null;

    @Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);

        this.setContentView(R.layout.answer);        
        
        initView();

        myBroadcastReceiver = new MyBroadcastReceiver(this, this,
                MyBroadcastReceiver.ACTION_SHARE_TO_WEIXIN_SUCCESS,
                MyBroadcastReceiver.ACTION_SHARE_TO_QZONE_SUCCESS,                 
                MyBroadcastReceiver.ACTION_SHARE_TO_SINA_SUCCESS  );        
        
        if (getIntent().hasExtra("task"))
        {
            taskData = (TaskData) getIntent().getSerializableExtra("task");
                                  
            Log.i("task", String.valueOf(taskData.getTaskId()));
                       
            new TaskDetailAsyncTask().execute( taskData.getTaskId());
        }  else
        {
            ToastUtils.showLongToast(AnswerActivity.this,  "发生未知错误");
            AnswerActivity.this.closeSelf(AnswerActivity.this);
        }
    }       
    
    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();               
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        
        if ( intent.hasExtra("task"))
        {
            taskData = (TaskData) intent.getSerializableExtra("task");
            
            Log.i("task", String.valueOf(taskData.getTaskId()));
                       
            new TaskDetailAsyncTask().execute( taskData.getTaskId());
        }  else
        {
            ToastUtils.showLongToast(AnswerActivity.this,  "发生未知错误");
            AnswerActivity.this.closeSelf(AnswerActivity.this);
        }        
    }


    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        
        myBroadcastReceiver.unregisterReceiver();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            // finish自身
            AnswerActivity.this.finish();
            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }
    
    public void switchFragment( BaseFragment fragment ,String fragTag )
    {
        switchFragment(fragment, fragTag, false);
    }
    
    public void switchFragment( BaseFragment fragment ,String fragTag ,  boolean disableCountdown){
        
        FragmentManager fragmentManager= this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        BaseFragment frag = (BaseFragment)fragmentManager.findFragmentByTag(fragTag);
        if(frag!=null){
            //fragmentTransaction.show(frag);          
            fragmentTransaction.remove(frag);
            fragmentTransaction.replace(R.id.rlContext, fragment,fragTag);
        }
        else{
            fragmentTransaction.replace(R.id.rlContext, fragment,fragTag);
        }
        fragmentTransaction.commit();
    }
    
    public void switchFragment(){
        switchFragment(false);
    }

    public void switchFragment(boolean disableCountdown){

        if (taskData.getType().equals(Constant.TASK_TYPE_ANSWER))
        {                   
            BaseFragment frag = new FragAnswer();
            Bundle bd = new Bundle();
            bd.putSerializable("task",(Serializable)taskData);
            bd.putBoolean("disableCountdown", disableCountdown );
            frag.setArguments(bd);
            
            switchFragment(frag , "answer" , disableCountdown );

        } else if (taskData.getType().equals( Constant.TASK_TYPE_SIGN_UP))
        {
            BaseFragment frag=new FragSignup();
            Bundle bd = new Bundle();
            bd.putSerializable("task",(Serializable)taskData);
            bd.putBoolean("disableCountdown", disableCountdown );
            frag.setArguments(bd);
            
            switchFragment(frag , "signup" , disableCountdown );
            
        } else if (taskData.getType().equals(Constant.TASK_TYPE_ANSWER_OUT))
        {
           BaseFragment frag=new FragAnswerOut();
           Bundle bd=new Bundle();
           bd.putSerializable("task", (Serializable)taskData);
           bd.putBoolean("disableCountdown", disableCountdown );
           frag.setArguments(bd);
           switchFragment(frag, "answerout", disableCountdown );
            
        } else if (taskData.getType().equals( Constant.TASK_TYPE_PLAY))
        {
           BaseFragment frag=new FragPlay();
           Bundle bd=new Bundle();
           bd.putSerializable("task", (Serializable)taskData);
           bd.putBoolean("disableCountdown", disableCountdown );
           frag.setArguments(bd);
           switchFragment(frag, "play" , disableCountdown );
        }
    }

    protected void initView()
    {
        txtTitle = (TextView)this.findViewById(R.id.title);
        txtTitle.setText("");
        txtFunction=(TextView)this.findViewById(R.id.functionBtn);
        txtFunction.setText("分享");
        txtFunction.setVisibility(View.VISIBLE);
        txtFunction.setOnClickListener(this);
        
        backImage = (CyButton)this.findViewById(R.id.backImage);
        backImage.setOnClickListener(this);
        
        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);
        
        rlWaiting=(RelativeLayout)this.findViewById(R.id.rlWaiting);                     
    }
    
    private void showWin()
    {
        if (false == isLogin())
        {
            Intent intent = new Intent( this , LoginActivity.class);
            startActivityForResult(intent, 1000);             
        } else
        {    
            showPopup();
        }
    }
    
    protected void getImagePath(){
        String imagepath = "#W0#H0"+ taskData.getPictureURL();
        File rootdir = this.getCacheDir();
        File fpath = new File(rootdir,  getFilenameForKey(imagepath));
        
        boolean iseixt = fpath.exists();
        Log.i("ttt", String.valueOf( iseixt));
        Log.i("ttt",  fpath.getAbsolutePath());
        
        
    
        
    }
    
    private String getFilenameForKey(String key) {
        int firstHalfLength = key.length() / 2;
        String localFilename = String.valueOf(key.substring(0, firstHalfLength).hashCode());
        localFilename += String.valueOf(key.substring(firstHalfLength).hashCode());
        return localFilename;
    }
    
    
    @Override
    public void onClick(View v)
    {
        if( v.getId() == R.id.backImage || v.getId() == R.id.backtext ){
            //closeSelf(AnswerActivity.this);
            //ToastUtils.showLongToast(AnswerActivity.this, "ssss");
            //Intent intent = new Intent(AnswerActivity.this, GuideActivity.class);
            //AnswerActivity.this.startActivity(intent);
            //AnswerActivity.this.finish();                    
            
            ActivityUtils.getInstance().skipActivity(AnswerActivity.this , MainActivity.class);
            //ToastUtils.showLongToast(AnswerActivity.this, "ssss");
            
        }else if( v.getId() == R.id.functionBtn ){
          showWin();
        }else if( v.getId() == R.id.layWeiXin){            
            String desc= taskData.getTitle(); //taskData.getDesc();
            String path ="#W0#H0"+ taskData.getPictureURL();          
            
//           if( null ==  LruImageCache.instance().getBitmap(path)){
//               VolleyUtil.loadImage(taskData.getPictureURL());
//           }
            
            String shareUrl = taskData.getShareURL();
            ShareUtil.share2WeiXin(this, desc , path  , shareUrl );
        }else if( v.getId() == R.id.layXinLang){
            String desc=taskData.getTitle(); // taskData.getDesc();
            String imageUrl = "#W0#H0"+ taskData.getPictureURL();             
            String shareUrl = taskData.getShareURL();
            ShareUtil.share2Sina(this, desc , imageUrl , shareUrl );
        }else if( v.getId() == R.id.layQQ){
            String desc= taskData.getTitle(); //taskData.getDesc();
            String imageUrl = taskData.getPictureURL();
            String shareUrl = taskData.getShareURL();
            //channelType=3;
            ShareUtil.share2Qzone(this, desc , imageUrl , shareUrl );
        }
//        else if(v.getId() == R.id.backtext)
//        {
//            closeSelf(AnswerActivity.this);
//        }
    }

    @Override
    public boolean handleMessage(Message msg)
    {
        // TODO Auto-generat ed method stub
        return false;
    }
    

    private void initPopupWindow() {
        LayoutInflater mInflater = LayoutInflater.from(this);
        View layout = mInflater.inflate(R.layout.pop_share_ui, null);
        layout.findViewById(R.id.layWeiXin).setOnClickListener(this);
        layout.findViewById(R.id.layQQ).setOnClickListener(this);
        layout.findViewById(R.id.layXinLang).setOnClickListener(this);
        layout.findViewById(R.id.layAll).getBackground().setAlpha(220);
        Button cancelBtn = (Button) layout.findViewById(R.id.cancelShare);
        cancelBtn.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
            }
        });
        popupWindow = new PopupWindow(layout, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        //设置PopupWindow显示和隐藏时的动画
        popupWindow.setAnimationStyle(R.style.AnimationPop);
    }
     //显示菜单
    private void showPopup(){

        if(popupWindow == null)
            initPopupWindow();

           //设置位置
        //popupWindow.showAsDropDown(btnShare,-50,-180);
        popupWindow.showAtLocation(txtFunction, Gravity.BOTTOM ,0,0); //设置在屏幕中的显示位置

        //popupWindow.showAtLocation(mRootView, Gravity.LEFT|Gravity.TOP,20,90); //设置在屏幕中的显示位置
    }

    @Override
    protected void onActivityResult(int requestCode , int resultCode , Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        
        if( requestCode ==0 && resultCode==2 ){
            //ToastUtils.showLongToast(this, "分享成功....。");            
        }               
        else if( requestCode == 1000 && resultCode== RESULT_OK){
            
            new TaskDetailAsyncTask().execute( taskData.getTaskId());
            
            //showPopup();
        }        
    }
    
    protected void commit(){
        TaskTurned params = new TaskTurned();
        params.taskId = taskData.getTaskId();
        params.channelType = channelType;
        new taskTurnedNotifyAsyncTask().execute( params );
    }
    
    class TaskTurned{
        private int taskId;
        private int channelType;
        public int getTaskId()
        {
            return taskId;
        }
        public void setTaskId(int taskId)
        {
            this.taskId = taskId;
        }
        public int getChannelType()
        {
            return channelType;
        }
        public void setChannelType(int channelType)
        {
            this.channelType = channelType;
        }
    }

    class taskTurnedNotifyAsyncTask extends AsyncTask< TaskTurned, Void, FMTaskTurnedNotify>
    {

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
            
            rlWaiting.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(FMTaskTurnedNotify result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            
            rlWaiting.setVisibility(View.GONE);
            
            if(result==null){
                ToastUtils.showLongToast(AnswerActivity.this  , "请求失败。");
                return;
            }
            if( 1!= result.getSystemResultCode()){
                ToastUtils.showLongToast( AnswerActivity.this , result.getSystemResultDescription());
                return;
            }
            if( Constant.TOKEN_OVERDUE == result.getResultCode()){
                // 提示账号异地登陆，强制用户退出
                // 并跳转到登录界面
                ToastUtils.showLongToast(  AnswerActivity.this, "账户登录过期，请重新登录");
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        ActivityUtils.getInstance().loginOutInActivity(
                                 AnswerActivity.this);
                    }
                }, 2000);
                return;
            }
            
            if(1!= result.getResultCode()){
                ToastUtils.showLongToast( AnswerActivity.this , result.getResultDescription());
                return;
            }
            
            if( result.getResultData().getIllgel() != 0 || result.getResultData().getReward() <= 0 ){
                //ToastUtils.showLongToast(AnswerActivity.this, "非0表示无法获取奖励，比如已经转发过了");
                if( popupWindow!=null){
                    popupWindow.dismiss();
                }
                
                ToastUtils.showLongToast(AnswerActivity.this, "分享成功");
                
                return;
            }
            if( result.getResultData().getReward()>0){
                
                BigDecimal flow = new BigDecimal( result.getResultData().getReward() ) ;
                DecimalFormat format = new DecimalFormat("0.##");
                String flowString = format.format( flow );    
                
                ToastUtils.showLongToast(AnswerActivity.this, "恭喜您，获得了"+ flowString +"M流量");
                if( popupWindow!=null){
                    popupWindow.dismiss();
                }
              //分享成功以后，发送广播
                MyBroadcastReceiver.sendBroadcast( AnswerActivity.this, MyBroadcastReceiver.ACTION_FLOW_ADD);
                return;
            }
        }

        @Override
        protected FMTaskTurnedNotify doInBackground(TaskTurned... params)
        {
            // TODO Auto-generated method stub
            TaskTurned entity = params[0];
            String url = Constant.TASK_TURNED_NOTIFY;
            ObtainParamsMap obtainMap = new ObtainParamsMap(AnswerActivity.this);
            String paramStr = obtainMap.getMap();
            Map<String,String> maps =new HashMap<>();
            maps.put("taskId",  String.valueOf( entity.getTaskId()));
            maps.put("channel",String.valueOf( entity.getChannelType() ));
            String signStr = obtainMap.getSign(maps);
            
            try
            {
                url+="?taskId="+ entity.getTaskId();
                url+="&channel="+ entity.getChannelType();
                url+= paramStr;
                url+="&sign="+ URLEncoder.encode(signStr,"UTF-8");
                
                String responseStr = HttpUtil.getInstance().doGet(url);
                JSONUtil<FMTaskTurnedNotify> jsonUtil=new JSONUtil<FMTaskTurnedNotify>();
                FMTaskTurnedNotify result = new FMTaskTurnedNotify();
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
    }

    @Override
    public void onFinishReceiver(ReceiverType type, Object msg)
    {        
        //ToastUtils.showLongToast(this, "onFinishReceiver+"+ type );
        if(type == ReceiverType.ShareToWeixinSuccess){
            channelType = ShareUtil.CHANNEL_WEIXIN;
            commit();          
        }else if(type == ReceiverType.ShareToSinaSuccess){
            channelType = ShareUtil.CHANNEL_SINA;
            commit();
        }else if(type == ReceiverType.ShareToQzoneSuccess){
            channelType = ShareUtil.CHANNEL_QZONE;
            commit();
        }
    }    

    class TaskDetailAsyncTask extends AsyncTask<Integer , Void , FMTaskDetail>{

        @Override
        protected FMTaskDetail doInBackground(Integer... params)
        {                        
            Integer taskId = params[0];
            String url = Constant.TASK_DETAIL_INTEFACE;
            ObtainParamsMap obtainMap=new ObtainParamsMap( AnswerActivity.this );
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
                e.printStackTrace();             
                Log.e("answer", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute()
        {
            rlWaiting.setVisibility(View.VISIBLE);
            
            if( waitingDlg==null){
                waitingDlg=new ProgressDialog( AnswerActivity.this );
                waitingDlg.setMessage("请稍等...");
                waitingDlg.setCancelable(true);
            }
            waitingDlg.show();
            
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(FMTaskDetail result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
                        
            rlWaiting.setVisibility(View.GONE);
            
            if( waitingDlg!=null){
                waitingDlg.dismiss();
                waitingDlg=null;
            }            
                        
            if(result==null){
                ToastUtils.showLongToast(AnswerActivity.this  , "请求失败。");
                AnswerActivity.this.closeSelf(AnswerActivity.this);
                return;
            }
            if( 1!= result.getSystemResultCode()){
                ToastUtils.showLongToast( AnswerActivity.this , result.getSystemResultDescription());
                AnswerActivity.this.closeSelf(AnswerActivity.this);
                return;
            }
            
            if( Constant.TOKEN_OVERDUE == result.getResultCode()){
                // 提示账号异地登陆，强制用户退出
                // 并跳转到登录界面
                ToastUtils.showLongToast(AnswerActivity.this , "账户登录过期，请重新登录");
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        ActivityUtils.getInstance().loginOutInActivity( AnswerActivity.this );
                    }
                }, 2000);
                return;
            }
            
            if(1!= result.getResultCode()){
                ToastUtils.showLongToast( AnswerActivity.this , result.getResultDescription());
                ActivityUtils.getInstance().loginOutInActivity( AnswerActivity.this );
                return;
            }
            
            if( null == result.getResultData() || result.getResultData().getTaskDetail()==null || result.getResultData().getTaskDetail().size()<1){
                ToastUtils.showLongToast(AnswerActivity.this, "任务数据不完整，请重试");
                AnswerActivity.this.closeSelf(AnswerActivity.this);
                return;
            }
            
            Long secondToStart = result.getResultData().getSecondToStart();
            
            //Log.i("answer", "倒计时="+ String.valueOf( secondToStart ) );                    
            
            taskData = result.getResultData().getTask();
            
            String path ="#W0#H0"+ taskData.getPictureURL();               
            if( null ==  LruImageCache.instance().getBitmap(path)){
                VolleyUtil.loadImage(taskData.getPictureURL());
            }   
            
            taskData.setTimeToStart( secondToStart );
            
            taskData.setQuestions(result.getResultData().getTaskDetail());
            
            switchFragment( false );         
  
        }              
    }            
 
}
