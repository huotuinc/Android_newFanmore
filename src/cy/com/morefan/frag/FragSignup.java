package cy.com.morefan.frag;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.bean.FMTaskDetail;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.ui.answer.AnswerActivity;
import cy.com.morefan.ui.user.LoginActivity;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;
/*
 * 报名类答题
 * @类名称：FragSignup
 * @类描述：
 * @创建人：jinxiangdong
 * @修改人：
 * @修改时间：2015年6月2日 下午1:46:53
 * @修改备注：
 * @version:
 */
public class FragSignup extends FragAnswerBase //implements OnClickListener
{   
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        
        if( requestCode ==1000 && resultCode == android.app.Activity.RESULT_OK){
            startNextFragment();
            //new SignUpAsyncTask().execute( taskData.getTaskId() );
        }
            
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        
        btnAnswer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFragPasue()
    {
        // TODO Auto-generated method stub

    }
    
    @Override
    protected void startNextFragment(){
        BaseFragment frag= new FragSignupList();
        Bundle bd = new Bundle();
        bd.putSerializable("questions", (Serializable)taskData.getQuestions());
        bd.putInt("taskid", taskData.getTaskId());
        frag.setArguments(bd);
        ((AnswerActivity)getActivity()).switchFragment(frag, "signuplist");
    }

    @Override
    public void onClick(View view)
    {
        if( view == btnAnswer ){
            saveReadTask( taskData.getTaskId() );
            
           if( false == app.isLogin(getActivity())){
               Intent intent = new Intent(getActivity(), LoginActivity.class);
               startActivityForResult(intent, 1000);
           }else
           {
               startNextFragment();
           }
        }
    }    
}
