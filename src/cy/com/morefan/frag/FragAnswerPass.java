package cy.com.morefan.frag;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import android.app.ProgressDialog;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cy.com.morefan.MainActivity;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMAnswer;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.ui.answer.AnswerActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.KJLoger;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.view.FmWebViewClient;

public class FragAnswerPass extends BaseFragment
{
    RelativeLayout rlPass;

    RelativeLayout rlAgain;

    RelativeLayout rlFailure;

    int state = 0;

    TextView tvAgainFlow;

    TextView tvFailureFlow;

    WebView wvPage;

    ProgressDialog progressDlg = null;

    int taskId = 0;

    int rightCount = 0;

    int wrongCount = 0;

    String answerstring = "";
    
    String answerState="";
    
    int chance=-1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        // return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.frag_answerpass, container,
                false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView)
    {
        rlPass = (RelativeLayout) rootView.findViewById(R.id.rlPass);
        rlAgain = (RelativeLayout) rootView.findViewById(R.id.rlAgain);
        rlFailure = (RelativeLayout) rootView.findViewById(R.id.rlFailure);
        tvAgainFlow = (TextView) rootView.findViewById(R.id.txtAgainFlow);
        tvAgainFlow.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tvFailureFlow = (TextView) rootView.findViewById(R.id.txtFailureFlow);
        tvFailureFlow.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        wvPage = (WebView) rootView.findViewById(R.id.wvPage);
        wvPage.getSettings().setJavaScriptEnabled(true);
        
        wvPage.setWebViewClient(new FmPassWebViewClient());
      
        Bundle bd = this.getArguments();
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

        AnswerParameter answerParam = new AnswerParameter();
        answerParam.setTaskId(taskId);
        answerParam.setAnswers(answerstring);
        new AnswerAsyncTask().execute(answerParam);
    }
    
    class FmPassWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            // TODO Auto-generated method stub
            Log.i("url", url);
            String temp = url.trim().toLowerCase(Locale.getDefault());
            if( temp.equals( "newfanmore://appanswercallback" )){
                if( answerState.toLowerCase(Locale.getDefault()).equals("success")){
                    //ActivityUtils.getInstance().skipActivity(getActivity(), MainActivity.class);
                    getActivity().finish();
                    return true;
                }else if( answerState.toLowerCase(Locale.getDefault()).equals("failed")){
                    if( chance>0){
                        ((AnswerActivity)getActivity()).SwitchFragment();
                        return true;
                    }else{                     
                        getActivity().finish();
                        return true;
                    }
                }else if( answerState.toLowerCase(Locale.getDefault()).equals("rejected")){
                    getActivity().finish();
                    return true;
                }
            }
            
            return false;
        }
    }
    
    

    // private void AsynRequestAnswer(){
    // RequestQueue queue = Volley.newRequestQueue(getActivity());
    // String url= Constant.ANSWER;
    //
    // if(progressDlg ==null){
    // progressDlg=new ProgressDialog(getActivity());
    // }
    //
    // progressDlg.show();
    //
    //
    // JsonObjectRequest request = new JsonObjectRequest( Request.Method.GET ,
    // url, null ,
    // new com.android.volley.Response.Listener<JSONObject>(){
    // @Override
    // public void onResponse(JSONObject arg0)
    // {
    // // TODO Auto-generated method stub
    // if(progressDlg!=null){
    // progressDlg.dismiss();
    // progressDlg=null;
    // }
    //
    // }
    // } ,
    // new com.android.volley.Response.ErrorListener()
    // {
    // @Override
    // public void onErrorResponse(VolleyError arg0)
    // {
    // // TODO Auto-generated method stub
    // if(progressDlg!=null){
    // progressDlg.dismiss();
    // progressDlg=null;
    // }
    // ToastUtils.showLongToast(getActivity(), arg0.getMessage());
    // }
    // });
    // queue.add(request);
    // }

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
            // TODO Auto-generated method stub
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
                
                Log.i("answer", url);

                String responseStr = HttpUtil.getInstance().doGet(url);
                FMAnswer answer = new FMAnswer();
                JSONUtil<FMAnswer> jsonUtil = new JSONUtil<FMAnswer>();
                try
                {
                    answer = jsonUtil.toBean(responseStr, answer);
                }
                catch(JsonSyntaxException e)
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
            }catch (Exception e) {
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
                ToastUtils.showLongToast(getActivity(), "发生错误！");
                return;
            }
            if (1 != result.getSystemResultCode())
            {
                ToastUtils.showLongToast(getActivity(),
                        result.getSystemResultDescription());
                //ActivityUtils.getInstance().skipActivity(getActivity(), MainActivity.class);
                getActivity().finish();
                return;
            }
            if (1 != result.getResultCode())
            {
                ToastUtils.showLongToast(getActivity(),
                        result.getResultDescription());
                //ActivityUtils.getInstance().skipActivity(getActivity(), MainActivity.class);
                getActivity().finish();
                return;
            }

            answerState = "";
            if (result.getResultData().getIllgel() > 0)
            {
                answerState = "rejected";
            } else if (result.getResultData().getReward() > 0)
            {
                answerState = "success";
            } else
            {
                answerState = "failed";
            }

            chance = result.getResultData().getChance();
            
            String url = Constant.BASE_ROOT_URL;
            url += "appanswer" + answerState + ".html?taskReward="
                    + result.getResultData().getReward();
            url += "&rights=" + rightCount + "&wrongs=" + wrongCount
                    + "&chance=" + chance;
            
            Log.i("URL", url);

            wvPage.loadUrl(url);
        }

    }

}
