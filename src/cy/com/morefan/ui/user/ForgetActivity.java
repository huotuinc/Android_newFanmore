package cy.com.morefan.ui.user;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMForget;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.task.GetVCAsyncTask;
import cy.com.morefan.util.EncryptUtil;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.KJLoger;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.view.CountDownTimerButton;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.view.NoticeDialog;
import com.huotu.android.library.libedittext.EditText;
/**
 * 
 * @类名称：ForgetActivity
 * @类描述：忘记密码界面
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月10日 上午10:02:25
 * @修改备注：
 * @version:
 */
public class ForgetActivity extends BaseActivity implements OnClickListener
{

    private TextView titleName;

    private EditText edtPhone;

    private EditText edtCode;

    private TextView btnGet;

    private EditText edtPwd;

    private EditText edtRePwd;

    private Button btnComplete;
    //返回文字事件
    private TextView backText;
    private CountDownTimerButton countDownBtn;

    // 图标
    // private ImageView titleIcon;
    // 返回（关闭当前界面）
    private CyButton titleBack;
    
    private NoticeDialog noticeDialog;
    //切换短信获取方式
    private VoiceSmsReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        this.setContentView(R.layout.forget_user_ui);
        //registerFreshReceiver();
        initView();
        handleBtnEvent();

    }
    
    // 注册刷新列表广播
    private void registerFreshReceiver()
    {
        if(null == smsReceiver)
        {
            smsReceiver = new VoiceSmsReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(Constant.GET_VOICE_FORGET);
        ForgetActivity.this.registerReceiver(smsReceiver, filter);
    }

    private void initView()
    {
        titleName = (TextView) this.findViewById(R.id.title);
        titleName.setText("用户忘记密码");
        // titleIcon = (ImageView) this.findViewById(R.id.titleImage);
        titleBack = (CyButton) this.findViewById(R.id.backImage);
        edtPhone = (EditText) this.findViewById(R.id.edtPhone);
        edtCode = (EditText) this.findViewById(R.id.edtCode);
        btnGet = (TextView) this.findViewById(R.id.btnGet);
        //标记文本短信
        btnGet.setTag(Constant.SMS_TYPE_TEXT);
        btnGet.setText("获取验证码");
        edtPwd = (EditText) this.findViewById(R.id.edtPwd);
        edtRePwd = (EditText) this.findViewById(R.id.edtRePwd);
        btnComplete = (Button) this.findViewById(R.id.btnComplete);
        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);

    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        //ForgetActivity.this.unregisterReceiver(smsReceiver);
        if(null != countDownBtn)
        {
            countDownBtn.Stop();
        }
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        registerFreshReceiver();
        //标记文本短信
        btnGet.setTag(Constant.SMS_TYPE_TEXT);
        btnGet.setText("获取验证码");
    }

    
    @Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();
        ForgetActivity.this.unregisterReceiver(smsReceiver);
    }

    @Override
    protected void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();
        //标记文本短信
        btnGet.setTag(Constant.SMS_TYPE_TEXT);
        btnGet.setText("获取验证码");
    }

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        switch (v.getId())
        {
        case R.id.backImage:
        {
            closeSelf(ForgetActivity.this);
        }
            break;
        case R.id.btnGet:
        {
            // 手机不为空时获取验证码
            if (hasPhone())
            {
                getVC();
                
                countDownBtn = new CountDownTimerButton(btnGet, "%d秒重新发送", "获取验证码", 60000,null);
                countDownBtn.start();
                
            } else
            {
                edtPhone.setError("手机不能为空");
            }
        }
            break;
        case R.id.btnComplete:
        {

            if (canSubmit())
            {

                doRetrievePsw();
            }
        }
            break;
        case R.id.backtext:
        {
            closeSelf(ForgetActivity.this);
        }
            break;

        default:
            break;
        }

    }

    private void handleBtnEvent()
    {
        // 标题图标
        // titleIcon.setOnClickListener(this);
        // 标题返回
        titleBack.setOnClickListener(this);
        btnGet.setOnClickListener(this);
        btnComplete.setOnClickListener(this);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            // finish自身
            ForgetActivity.this.finish();
            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

    private boolean hasPhone()
    {
        if (TextUtils.isEmpty(edtPhone.getText()))
        {

            return false;
        } else
        {
            return true;
        }
    }

    /**
     * 
     * @方法描述：获取验证码
     * @方法名：getVC
     * @参数：
     * @返回：void
     * @exception
     * @since
     */
    private void getVC()
    {

        try
        {
            ObtainParamsMap obtainMap = new ObtainParamsMap(ForgetActivity.this);
            String url = Constant.GET_VD_INTERFACE + "?phone="
                    + URLEncoder.encode(edtPhone.getText().toString(), "UTF-8")
                    + "&type="
                    + URLEncoder.encode(Constant.GET_VD_TYPE_FORGET, "UTF-8")
                    + "&codeType=" + btnGet.getTag();
            url += obtainMap.getMap();
            //封装sign
            Map<String, String> signMap = new HashMap<String, String>();
            signMap.put("phone", edtPhone.getText().toString());
            signMap.put("type", Constant.GET_VD_TYPE_FORGET);
            signMap.put("codeType", (String) btnGet.getTag());
            String signStr = obtainMap.getSign(signMap);
            url += "&sign=" + signStr;
            new GetVCAsyncTask(ForgetActivity.this, Constant.GET_VD_TYPE_FORGET).execute(url);

        } catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            KJLoger.errorLog(e.getMessage());
        }
    }

    private boolean canSubmit()
    {
        if (!hasPhone())
        {
            edtPhone.setError("手机不能为空");
            return false;
        } else if (TextUtils.isEmpty(edtCode.getText()))
        {
            edtCode.setError("验证码不能为空");
            return false;
        } else if (TextUtils.isEmpty(edtPwd.getText()))
        {
            edtPwd.setError("密码不能为空");
            return false;
        } else if (TextUtils.isEmpty(edtRePwd.getText()))
        {
            edtRePwd.setError("密码确认不能为空");
            return false;
        } else if (!edtPwd.getText().toString()
                .equals(edtRePwd.getText().toString()))
        {
            edtRePwd.setError("两次密码输入不同");
            return false;
        } else
        {
            return true;
        }
    }
    
    private void doRetrievePsw()
    {
        //处理请求参数
        String url;
        
        // 拼接注册url
        url = Constant.FORGET_INTERFACE;
        new ForgetAsyncTask().execute(url);
    }
    
    class ForgetAsyncTask extends AsyncTask<String, Void, FMForget>
    {

        @Override
        protected FMForget doInBackground(String... params)
        {
            // TODO Auto-generated method stub
            String url = params[0];
            JSONUtil<FMForget> jsonUtil = new JSONUtil<FMForget>();
            FMForget forget = new FMForget();
            ObtainParamsMap obtainMap = new ObtainParamsMap(ForgetActivity.this);
            String paramMap = obtainMap.getMap();
            // 忘记密码是get方式提交
            //封装sign
            Map<String, String> signMap = new HashMap<String, String>();
            signMap.put("phone", edtPhone.getText().toString());
            signMap.put("password", EncryptUtil.getInstance().encryptMd532(edtPwd.getText().toString()));
            signMap.put("authcode", edtCode.getText().toString());
            String signStr = obtainMap.getSign(signMap);
            try
            {
                url = url
                        + "?phone="
                        + URLEncoder.encode(edtPhone.getText().toString(),
                                "UTF-8")
                        + "&password="
                        + URLEncoder.encode(EncryptUtil.getInstance()
                                .encryptMd532(edtPwd.getText().toString()),
                                "UTF-8") + "&authcode=" + URLEncoder.encode(edtCode.getText().toString(),
                                        "UTF-8");
                url += paramMap;
                url += "&sign=" + URLEncoder.encode(signStr, "UTF-8");
            
                String json = HttpUtil.getInstance().doGet(url);
                try
                {
                    forget = jsonUtil.toBean(json, forget);
                }
                catch(JsonSyntaxException e)
                {
                   LogUtil.e("JSON_ERROR", e.getMessage());
                   forget.setResultCode(0);
                   forget.setResultDescription("解析json出错");
                }
                //baseCode.setResultData("2023");
                return forget;
                
        } catch (UnsupportedEncodingException e)
            {
            // TODO Auto-generated catch block
            KJLoger.errorLog(e.getMessage());
            return null;
        }
        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
            btnComplete.setEnabled(false);
            showProgress();
            
        }

        @Override
        protected void onPostExecute(FMForget result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            btnComplete.setEnabled(true);
            dismissProgress();
            if (1 == result.getResultCode())
            {
                // 弹出注册成功提示框
                noticeDialog = new NoticeDialog(ForgetActivity.this,
                        R.style.NoticeDialog, "找回密码", "找回成功",
                        new NoticeDialog.LeaveMyDialogListener()
                        {

                            @Override
                            public void onClick(View view)
                            {
                                // TODO Auto-generated method stub
                                noticeDialog.dismiss();
                                noticeDialog = null;
                                closeSelf(ForgetActivity.this);
                            }
                        });
                noticeDialog.show();
            } else
            {
                // 弹出注册失败提示框
                noticeDialog = new NoticeDialog(ForgetActivity.this,
                        R.style.NoticeDialog, "找回密码", "找回失败",
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
    
    /**
     * 
     * @类名称：VoiceSmsReceiver
     * @类描述：切换语音短信广播
     * @创建人：aaron
     * @修改人：
     * @修改时间：2015年6月22日 下午8:41:11
     * @修改备注：
     * @version:
     */
    public class VoiceSmsReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (Constant.GET_VOICE_FORGET.equals(intent.getAction()))
            {
                //刷新获取按钮状态，设置为可获取语音
                btnGet.setText("尝试语音获取");
                btnGet.setTag(Constant.SMS_TYPE_VOICE);
                ToastUtils.showLongToast(ForgetActivity.this, "还没收到短信，请尝试语音获取");
            }
        }
    }
}
