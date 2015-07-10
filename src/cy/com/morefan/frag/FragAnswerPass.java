package cy.com.morefan.frag;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.R;
import cy.com.morefan.bean.FMAnswer;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.ui.answer.AnswerActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.KJLoger;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.SoundUtil;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.view.PopExpUp;

public class FragAnswerPass extends BaseFragment
{
    int state = 0;

    WebView wvPage;

    ProgressDialog progressDlg = null;

    int taskId = 0;

    int rightCount = 0;

    int wrongCount = 0;

    String answerstring = "";

    String answerState = "";

    int chance = -1;

    float reword;
    
    //SoundUtil soundUtil=null;
    
    PopExpUp popExpUp;
    
    public void expUp(String exp , int soundId){
      if(null == popExpUp)
          popExpUp = new PopExpUp(getActivity());
      popExpUp.show(exp, soundId );
  }
    
    
    public void sound( int soundId){
      if(null == popExpUp)
          popExpUp = new PopExpUp(getActivity());
      popExpUp.sound(soundId );
  }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.frag_answerpass, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView)
    {
        //soundUtil = new SoundUtil(getActivity());
        //soundUtil.shakeSound(R.raw.success);
        
        wvPage = (WebView) rootView.findViewById(R.id.wvPage);
        wvPage.getSettings().setJavaScriptEnabled(true);
        wvPage.getSettings().setDomStorageEnabled(true);
        wvPage.setWebViewClient(new FmPassWebViewClient());

        Bundle bd = this.getArguments();
        if (bd != null)
        {
            if (bd.containsKey("taskid"))
            {
                taskId = bd.getInt("taskid");
            }
            if (bd.containsKey("rightcount"))
            {
                rightCount = bd.getInt("rightcount");
            }
            if (bd.containsKey("wrongcount"))
            {
                wrongCount = bd.getInt("wrongcount");
            }
            if (bd.containsKey("answers"))
            {
                answerstring = bd.getString("answers");
            }
        }

        AnswerParameter answerParam = new AnswerParameter();
        answerParam.setTaskId(taskId);
        answerParam.setAnswers(answerstring);
        new AnswerAsyncTask().execute(answerParam);
    }

    class FmPassWebViewClient extends WebViewClient
    {        
        
        @Override
        public void onReceivedError(WebView view, int errorCode,
                String description, String failingUrl)
        {
            // TODO Auto-generated method stub
            super.onReceivedError(view, errorCode, description, failingUrl);
            
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            // TODO Auto-generated method stub
            //Log.i("url", url);
            String temp = url.trim().toLowerCase(Locale.getDefault());
            if (temp.equals("newfanmore://appanswercallback"))
            {
                if (answerState.toLowerCase(Locale.getDefault()).equals(
                        "success"))
                {                                        
                    BigDecimal flow = new BigDecimal(reword);
                    DecimalFormat format = new DecimalFormat("0.##");
                    String flowString = format.format(flow);

                    //ToastUtils.showLongToast(getActivity(), "+" + flowString + "M流量");
                    
                    expUp( "+"+ flowString+"M流量" , 0 );
                    
//                    Bundle bd = new Bundle();
//                    bd.putInt("taskid", taskId );           
//                    MyBroadcastReceiver.sendBroadcast(getActivity(),
//                            MyBroadcastReceiver.ACTION_REFRESH_TASK_DETAIL , bd );
                    
                    MyBroadcastReceiver.sendBroadcast(getActivity(),
                            MyBroadcastReceiver.ACTION_FLOW_ADD);
                    
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable()
                    {
                        
                        @Override
                        public void run()
                        {
                            getActivity().finish();
                            
                        }
                    }, 1500);
                    
                    //getActivity().finish();
                    return true;
                } else if (answerState.toLowerCase(Locale.getDefault()).equals(
                        "failed"))
                {
                    if (chance > 0)
                    {
                        ((AnswerActivity) getActivity()).switchFragment(true);
                        return true;
                    } else
                    {
//                        Bundle bd = new Bundle();
//                        bd.putInt("taskid", taskId );            
//                        MyBroadcastReceiver.sendBroadcast(getActivity(),
//                                MyBroadcastReceiver.ACTION_REFRESH_TASK_DETAIL , bd );
                        getActivity().finish();
                        return true;
                    }
                } else if (answerState.toLowerCase(Locale.getDefault()).equals(
                        "rejected"))
                {
//                    Bundle bd = new Bundle();
//                    bd.putInt("taskid", taskId );            
//                    MyBroadcastReceiver.sendBroadcast(getActivity(),
//                            MyBroadcastReceiver.ACTION_REFRESH_TASK_DETAIL , bd );
                    getActivity().finish();
                    return true;
                }
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
        // TODO Auto-generated method stub

    }
    
      
    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        
//        if( soundUtil !=null){
//            soundUtil.Release();
//        }
        if( popExpUp!=null){
            popExpUp.close();
        }        
    }

    class AnswerParameter
    {
        private Integer taskId;

        private String answers;

        public Integer getTaskId()
        {
            return taskId;
        }

        public void setTaskId(Integer taskId)
        {
            this.taskId = taskId;
        }

        public String getAnswers()
        {
            return answers;
        }

        public void setAnswers(String answers)
        {
            this.answers = answers;
        }
    }

    class AnswerAsyncTask extends AsyncTask<AnswerParameter, Void, FMAnswer>
    {

        @Override
        protected FMAnswer doInBackground(AnswerParameter... params)
        {
            AnswerParameter param = params[0];
            String url = Constant.ANSWER;

            ObtainParamsMap obtainMap = new ObtainParamsMap(getActivity());
            String paramsStr = obtainMap.getMap();
            Map<String, String> signMap = new HashMap<>();
            signMap.put("taskId", param.getTaskId().toString());
            signMap.put("answers", param.getAnswers());
            String sign = obtainMap.getSign(signMap);

            try
            {
                url += "?taskId="
                        + URLEncoder.encode(param.getTaskId().toString(),
                                "UTF-8");
                url += "&answers="
                        + URLEncoder.encode(param.getAnswers(), "UTF-8");
                url += paramsStr;
                url += "&sign=" + URLEncoder.encode(sign, "UTF-8");

                //Log.i("answer", url);

                String responseStr = HttpUtil.getInstance().doGet(url);
                FMAnswer answer = new FMAnswer();
                JSONUtil<FMAnswer> jsonUtil = new JSONUtil<FMAnswer>();
                try
                {
                    answer = jsonUtil.toBean(responseStr, answer);
                } catch (JsonSyntaxException e)
                {
                    LogUtil.e("JSON_ERROR", e.getMessage());
                    answer.setResultCode(0);
                    answer.setResultDescription("解析json出错");
                }
                return answer;
            } catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                KJLoger.errorLog(e.getMessage());
                return null;
            } catch (Exception e)
            {
                // TODO: handle exception
                Log.e("ERROR", e.getMessage());
                return null;
            }

        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();

            reword = 0;

            if (progressDlg == null)
            {
                progressDlg = new ProgressDialog(getActivity());
            }
            progressDlg.show();
        }

        @Override
        protected void onPostExecute(FMAnswer result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if (progressDlg != null)
            {
                progressDlg.dismiss();
                progressDlg = null;
            }

            if (null == result)
            {
                ToastUtils.showLongToast(getActivity(), "网络请求发生错误！");
                return;
            }
            if (1 != result.getSystemResultCode())
            {
                ToastUtils.showLongToast(getActivity(), result.getSystemResultDescription());
                getActivity().finish();
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
            
            if (1 != result.getResultCode())
            {
                ToastUtils.showLongToast(getActivity(), result.getResultDescription());
                getActivity().finish();
                return;
            }

            answerState = "";
            if (result.getResultData().getIllgel() > 0)
            {
                answerState = "rejected";
                //soundUtil.shakeSound(R.raw.failed);       
                sound(R.raw.failed);
            } else if (result.getResultData().getReward() > 0)
            {
                answerState = "success";
                //soundUtil.shakeSound(R.raw.success);
                sound(R.raw.success);
            } else
            {
                answerState = "failed";
                //soundUtil.shakeSound(R.raw.failed);
                sound(R.raw.failed);
            }

            chance = result.getResultData().getChance();

            reword = result.getResultData().getReward();

            String url = Constant.BASE_ROOT_URL;
            url += "appanswer" + answerState + ".html?taskReward="
                    + result.getResultData().getReward();
            url += "&rights=" + rightCount + "&wrongs=" + wrongCount
                    + "&chance=" + chance;

            //Log.i("URL", url);

            wvPage.loadUrl(url);
            
            Bundle bd = new Bundle();
            bd.putInt("taskid", taskId );            
            MyBroadcastReceiver.sendBroadcast(getActivity(), MyBroadcastReceiver.ACTION_REFRESH_TASK_DETAIL , bd );
        }
    }


}
