package cy.com.morefan.ui.info;

import java.util.Map;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMFeedBack;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.ui.account.MsgCenterActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.view.NoticeDialog;
/**
 * 
 * @类名称：FeedBackActivity
 * @类描述：意见反馈界面
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月10日 上午9:57:54
 * @修改备注：
 * @version:
 */
public class FeedBackActivity extends BaseActivity implements Callback,
        OnClickListener
{

    private EditText edtContent;
    private Button btnCommit;
    private TextView title;
    private CyButton backImage;
    private Handler mHandler = new Handler(this);
    private NoticeDialog noticeDialog;
    
    //返回文字事件
    private TextView backText;

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub

        switch (v.getId())
        {
        case R.id.btnCommit:
        {

            if(canCommit())
            {
                this.doCommit();
            }
            else
            {
                edtContent.setError("反馈意见不能为空");
            }
        }
            break;
        case R.id.backImage:
        {
            closeSelf(FeedBackActivity.this);
        }
            break;
        case R.id.backtext:
        {
            closeSelf(FeedBackActivity.this);
        }
            break;

        default:
            break;
        }
    }

    @Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        this.setContentView(R.layout.feedback);

        this.initView();
        this.handleEvent();

    }

    private void initView()
    {
        edtContent = (EditText) this.findViewById(R.id.edtContent);
        btnCommit = (Button) this.findViewById(R.id.btnCommit);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("意见反馈");
        backImage = (CyButton) this.findViewById(R.id.backImage);
        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);
    }

    private void handleEvent()
    {
        backImage.setOnClickListener(this);
        btnCommit.setOnClickListener(this);
    }

    @Override
    public boolean handleMessage(Message msg)
    {
        // TODO Auto-generated method stub
        return false;
    }

    private boolean canCommit()
    {
        if(TextUtils.isEmpty(edtContent.getText()))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    private void doCommit()
    {
        new CommitAsyncTask().execute(edtContent.getText().toString());
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            // finish自身
            FeedBackActivity.this.finish();
            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }
    
    
    public class CommitAsyncTask extends AsyncTask<String, Void, FMFeedBack>
    {

        @Override
        protected FMFeedBack doInBackground(String... params)
        {
            String content= params[0];
            
            FMFeedBack feedBackBean = new FMFeedBack();
            JSONUtil<FMFeedBack> jsonUtil = new JSONUtil<FMFeedBack>();
            String url;
            ObtainParamsMap obtainMap = new ObtainParamsMap(
                    FeedBackActivity.this);
            Map<String, String> paramMap = obtainMap.obtainMap();
            // url
            url = Constant.FEEDBACK;
            // POST提交            
            //FeedBackBean feedback=new FeedBackBean();
            String name = MyApplication.readString( FeedBackActivity.this , "user_name");
            String contact=MyApplication.readString(FeedBackActivity.this, "user_mobile");
//            long datelong = System.currentTimeMillis();            
//            feedback.setContact(contact);
//            feedback.setContent(content);
//            feedback.setCreateTime( datelong );
//            feedback.setDoTime( datelong );
//            feedback.setId(0);
//            feedback.setIsDo(0);
//            feedback.setName(name);
//            feedback.setRemark("");
//            feedback.setTurnUserId("");
//            JSONUtil<FeedBackBean> feedJsonUtil=new JSONUtil<FeedBackBean>();
//            String feedbackjsonString = feedJsonUtil.toJson(feedback);
            
            
            
            paramMap.put("contact", contact );
            paramMap.put("name", name);
            paramMap.put("content", content);            
            // 封装sign
            String signStr = obtainMap.getSign(paramMap);
            paramMap.put("sign", signStr);
            if(Constant.IS_PRODUCTION_ENVIRONMENT)
            {
                String jsonStr = HttpUtil.getInstance().doPost(url, paramMap);
                try
                {
                    feedBackBean = jsonUtil.toBean(jsonStr, feedBackBean);
                }
                catch(JsonSyntaxException e)
                {
                   LogUtil.e("JSON_ERROR", e.getMessage());
                   feedBackBean.setResultCode(0);
                   feedBackBean.setResultDescription("解析json出错");
                }
                                
            }
            else
            {
                feedBackBean.setResultCode(1);
            }
            
            return feedBackBean;
        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(FMFeedBack result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            
            if(1 == result.getResultCode())
            {
                //反馈意见成功
                noticeDialog = new NoticeDialog(FeedBackActivity.this,
                        R.style.NoticeDialog, "意见反馈", "反馈成功",
                        new NoticeDialog.LeaveMyDialogListener()
                        {

                            @Override
                            public void onClick(View view)
                            {
                                // TODO Auto-generated method stub
                                noticeDialog.dismiss();
                                noticeDialog = null;
                                closeSelf(FeedBackActivity.this);
                            }
                        });
                noticeDialog.show();
            }
            else if (Constant.TOKEN_OVERDUE == result.getResultCode())
            {
                // 提示账号异地登陆，强制用户退出
                // 并跳转到登录界面
                ToastUtils.showLongToast(FeedBackActivity.this, "账户登录过期，请重新登录");
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        ActivityUtils.getInstance().loginOutInActivity(
                                (Activity) FeedBackActivity.this);
                    }
                }, 2000);
            }
            else
            {
                //反馈意见失败
                noticeDialog = new NoticeDialog(FeedBackActivity.this,
                        R.style.NoticeDialog, "意见反馈", "反馈失败，请重新提交",
                        new NoticeDialog.LeaveMyDialogListener()
                        {

                            @Override
                            public void onClick(View view)
                            {
                                // TODO Auto-generated method stub
                                noticeDialog.dismiss();
                                noticeDialog = null;
                            }
                        });
                noticeDialog.show();
            }
        }
        
        
        
    }
}
