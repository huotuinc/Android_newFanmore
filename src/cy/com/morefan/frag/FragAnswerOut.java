package cy.com.morefan.frag;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.bean.FMTaskDetail;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.ui.answer.AnswerActivity;
import cy.com.morefan.ui.user.LoginActivity;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.view.CountDownTimerButton;

public class FragAnswerOut extends FragAnswerBase //implements OnClickListener
{       
    @Override
    public void onReshow()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        
        btnAnswer.setVisibility(View.VISIBLE);
        //taskData.setQuestions(result.getResultData().getTaskDetail());            
//        CountDownTimerButton countDown = new CountDownTimerButton(
//              btnAnswer,
//              "答题领流量(%d)",
//              "答题领流量",
//              taskData.getBackTime() * 1000);
//        countDown.start();
//        wvPage.loadUrl(taskData.getContextURL()); 
        
        BigDecimal flow = new BigDecimal( taskData.getMaxBonus());
        DecimalFormat format = new DecimalFormat("0.##");
        String flowString = format.format(flow);
        
        String content= "答题领"+ flowString +"M流量";
        SetTaskButtonState(  content +"(%d)", content );
    }

    @Override
    public void onFragPasue()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View view)
    {
        if( view == btnAnswer ){
            if (false == app.isLogin(getActivity()))
            {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, 1000);   
            }
            else
            {
                new AnswerOutAsyncTask().execute(taskData.getTaskId());
                
                //StartNextFragment();
            }
        }
    }
    
    private void StartNextFragment(){
        BaseFragment frag= new FragAnswerOutList();
        Bundle bd = new Bundle();
        bd.putSerializable("questions", (Serializable)taskData.getQuestions());
        bd.putInt("taskid", taskData.getTaskId());
        frag.setArguments(bd);
        ((AnswerActivity)getActivity()).SwitchFragment(frag, "answerlist");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        
        if( resultCode == android.app.Activity.RESULT_OK ){
            //StartNextFragment();
            new AnswerOutAsyncTask().execute(taskData.getTaskId());
        }
        
    }
    
    class AnswerOutAsyncTask extends AsyncTask<Integer , Void , FMTaskDetail>{

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
                Log.e("answerout", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            rlWaiting.setVisibility(View.VISIBLE);
            //btnAnswer.setVisibility(View.GONE);                       
            
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(FMTaskDetail result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            
            rlWaiting.setVisibility(View.GONE);
            
            if(result==null){
                ToastUtils.showLongToast(getActivity() , "请求失败。");
                return;
            }
            if( 1!= result.getSystemResultCode()){
                ToastUtils.showLongToast( getActivity() , result.getSystemResultDescription());
                return;
            }
            if(1!= result.getResultCode()){
                ToastUtils.showLongToast( getActivity() , result.getResultDescription());
                return;
            }
            
//            btnAnswer.setVisibility(View.VISIBLE);
//            taskData.setQuestions(result.getResultData().getTaskDetail());            
//            CountDownTimerButton countDown = new CountDownTimerButton(
//                  btnAnswer,
//                  "答题领流量(%d)",
//                  "答题领流量",
//                  ((BaseActivity) getActivity()).application.answerTotalTimes * 1000);
//            countDown.start();
//            wvPage.loadUrl(taskData.getContextURL());
            
            taskData.setQuestions(result.getResultData().getTaskDetail());     
            
            StartNextFragment();
            
        }
        
    }

}
