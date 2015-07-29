package cy.com.morefan.ui.user;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMGetVC;
import cy.com.morefan.bean.FMRegisterBean;
import cy.com.morefan.bean.FMUserData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.task.GetVCAsyncTask;
import cy.com.morefan.task.GetVCodeAsyncTask;
import cy.com.morefan.ui.ambitus.RlueActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.DateUtils;
import cy.com.morefan.util.EncryptUtil;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.KJLoger;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.view.CountDownTimerButton;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.view.NoticeDialog;
import cy.com.morefan.view.CountDownTimerButton.CountDownFinishListener;
import com.huotu.android.library.libedittext.EditText;

/**
 * 
 * @类名称：RegisterActivity
 * @类描述：注册界面
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月10日 上午10:02:56
 * @修改备注：
 * @version:
 */
public class RegisterActivity extends BaseActivity implements OnClickListener ,Callback
{
    /**
     * 
     */
    private TextView titleName;// 界面名称

    // private CyButton imgTag;// 验证码问号按钮

    private EditText edtInvitationCode;// 邀请码

    private EditText edtPhone;// 手机号

    private EditText edtCode;// 验证码

    private TextView btnGet;// 获取验证码按钮

    private EditText edtPwd;// 密码

    private EditText edtRePwd;// 密码确认

    private TextView txtYinSi;// 隐私说明按钮

    private Button btnReg;// 注册按钮

    private NoticeDialog noticeDialog;

    // 返回文字事件
    private TextView backText;

    // 按钮倒计时控件
    private CountDownTimerButton countDownBtn;

    // 图标
    // private ImageView titleIcon;

    // 返回（关闭当前界面）
    private CyButton titleBack;

    // 切换短信获取方式
    //private VoiceSmsReceiver smsReceiver;
    
    //private MyBroadcastReceiver myBroadcastReceiver=null;
    
    private Handler mHandler=null;
    
    FMGetVC getVCResult=null;

    @Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        this.setContentView(R.layout.register_user_ui);
        // registerFreshReceiver();
        initView();
        handleBtnEvent();
        
        //myBroadcastReceiver= new MyBroadcastReceiver(RegisterActivity.this, this , MyBroadcastReceiver.GET_VOICE_REGISTER  );
    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        // RegisterActivity.this.unregisterReceiver(smsReceiver);
        if (null != countDownBtn)
        {
            countDownBtn.Stop();
        }
        
        //myBroadcastReceiver.unregisterReceiver();
    }

    // 注册刷新列表广播
//    private void registerFreshReceiver()
//    {
//        if (null == smsReceiver)
//        {
//            smsReceiver = new VoiceSmsReceiver();
//        }
//        IntentFilter filter = new IntentFilter();
//        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
//        filter.addAction(Constant.GET_VOICE_REGISTER);
//        RegisterActivity.this.registerReceiver(smsReceiver, filter);
//    }

    private void initView()
    {
        titleName = (TextView) this.findViewById(R.id.title);
        titleName.setText("新用户注册");
        // titleIcon = (ImageView) this.findViewById(R.id.titleImage);
        titleBack = (CyButton) this.findViewById(R.id.backImage);

        // imgTag = (CyButton) this.findViewById(R.id.imgTag);

        edtInvitationCode = (EditText) this
                .findViewById(R.id.edtInvitationCode);
        edtPhone = (EditText) this.findViewById(R.id.edtPhone);
        edtCode = (EditText) this.findViewById(R.id.edtCode);
        btnGet = (TextView) this.findViewById(R.id.btnGet);
        // 标记文本短信
        btnGet.setTag(Constant.SMS_TYPE_TEXT);
        btnGet.setText("获取验证码");

        edtPwd = (EditText) this.findViewById(R.id.edtPwd);
        edtRePwd = (EditText) this.findViewById(R.id.edtRePwd);
        txtYinSi = (TextView) this.findViewById(R.id.txtYinSi);
        // 添加下划线
        txtYinSi.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtYinSi.getPaint().setAntiAlias(true);
        btnReg = (Button) this.findViewById(R.id.btnReg);

        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);
        
        mHandler = new Handler(this);
    }

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        switch (v.getId())
        {
        /*
         * case R.id.titleImage: {
         * ToastUtils.showLongToast(RegisterActivity.this, "粉猫。"); } break;
         */
        case R.id.backImage:
        {
            ActivityUtils.getInstance().skipActivity(RegisterActivity.this,
                    LoginActivity.class);
        }
            break;
        case R.id.btnGet:
        {
            // 判断手机是否填写
            if (isWritePhone())
            {
                getCode();

                countDownBtn = new CountDownTimerButton(btnGet, "%d秒重新发送",
                        "获取验证码", 60000,  new CountDownFinish());
                countDownBtn.start();
            } else
            {
                edtPhone.setError("手机号码不能为空");
            }
        }
            break;
        case R.id.txtYinSi:
        {
            showUseItem();
        }
            break;
        case R.id.btnReg:
        {
            if (canRegister())
            {
                doRegister();
            } else
            {
                //
            }
        }
            break;
        case R.id.backtext:
        {
            ActivityUtils.getInstance().skipActivity(RegisterActivity.this,
                    LoginActivity.class);
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
        // imgTag.setOnClickListener(this);
        btnGet.setOnClickListener(this);
        txtYinSi.setOnClickListener(this);
        btnReg.setOnClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            // finish自身
            RegisterActivity.this.finish();
            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 
     * @方法描述：获取验证码
     * @方法名：getCode
     * @参数：
     * @返回：void
     * @exception
     * @since
     */
    private void getCode()
    {
        // 异步获取验证码
        try
        {
            ObtainParamsMap obtainMap = new ObtainParamsMap(
                    RegisterActivity.this);

            String url = Constant.GET_VD_INTERFACE + "?phone="
                    + URLEncoder.encode(edtPhone.getText().toString(), "UTF-8")
                    + "&type="
                    + URLEncoder.encode(Constant.GET_VD_TYPE_REG, "UTF-8")
                    + "&codeType=" + btnGet.getTag();
            url += obtainMap.getMap();
            // 封装sign
            Map<String, String> signMap = new HashMap<String, String>();
            signMap.put("phone", edtPhone.getText().toString());
            signMap.put("type", Constant.GET_VD_TYPE_REG);
            signMap.put("codeType", (String) btnGet.getTag());
            String signStr = obtainMap.getSign(signMap);
            url += "&sign=" + signStr;
            KJLoger.i("注册获取验证码", url);
            
            //new GetVCAsyncTask(RegisterActivity.this, Constant.GET_VD_TYPE_REG)
            //        .execute(url);
            
            getVCResult=null;
            new GetVCodeAsyncTask( mHandler ).execute(url);            

        } catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            KJLoger.errorLog(e.getMessage());
        }
    }

    /**
     * 
     * @方法描述：跳转到使用条款界面
     * @方法名：showUseItem
     * @参数：
     * @返回：void
     * @exception
     * @since
     */
    private void showUseItem()
    {
        // 跳转到隐私说明界面
        ActivityUtils.getInstance().showActivity(RegisterActivity.this,
                RlueActivity.class);
    }

    private void doRegister()
    {
        UserData regUserData = new UserData();
        regUserData.setPhoneNumber(edtPhone.getText().toString());
        if (!TextUtils.isEmpty(edtInvitationCode.getText()))
        {
            regUserData.setInvitationCode(edtInvitationCode.getText()
                    .toString());
        }
        regUserData.setVerificationCode(edtCode.getText().toString());
        regUserData.setPassword(edtPwd.getText().toString());
        new RegisterAsyncTask().execute(regUserData);
    }

    private boolean isWritePhone()
    {
        if (!TextUtils.isEmpty(edtPhone.getText()))
        {
            return true;
        } else
        {
            return false;
        }
    }

    private boolean canRegister()
    {
        if (!isWritePhone())
        {
            edtPhone.setError("手机号码不能为空");
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

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        //registerFreshReceiver();
        btnGet.setText("获取验证码");
        btnGet.setTag(Constant.SMS_TYPE_TEXT);
    }

    @Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();
        //RegisterActivity.this.unregisterReceiver(smsReceiver);
    }

    @Override
    protected void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();
        btnGet.setText("获取验证码");
        btnGet.setTag(Constant.SMS_TYPE_TEXT);
    }

    /**
     * 
     * @类名称：RegisterAsyncTask
     * @类描述：异步提交注册信息
     * @创建人：aaron
     * @修改人：
     * @修改时间：2015年5月27日 下午3:25:04
     * @修改备注：
     * @version:
     */
    class RegisterAsyncTask extends AsyncTask<UserData, Void, FMRegisterBean>
    {

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
            // 注册按钮不能用
            btnReg.setEnabled(false);
            showProgress();
        }

        @Override
        protected void onPostExecute(FMRegisterBean result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
                        
            btnReg.setEnabled(true);
            dismissProgress();
            if (1 == result.getResultCode())
            {
                // 更新用户资料
                // 记录注册时返回的用户信息，并可以直接登录
                FMUserData regUser = result.getResultData().getUser();
                application.personal = regUser;
                application.isLogin = true;
                // 记录更新token
                if (!"".equals(regUser.getToken())
                        && null != regUser.getToken())
                {
                    MyApplication.writeTokenToLocal(RegisterActivity.this,
                            regUser.getToken(), Constant.TOKEN_ADD);
                    MyApplication.writeUserInfoToLocal(RegisterActivity.this,
                            regUser.getName(), regUser.getBalance(), regUser
                                    .getPictureURL(), regUser.getSignInfo(),
                            regUser.getInvCode(), regUser.getSigntoday(),
                            DateUtils.formatDate(regUser.getBirthDate(),
                                    Constant.DATE_FORMAT), regUser.getMobile(),
                            regUser.getCareer(), regUser.getIncoming(), regUser
                                    .getFavs(), regUser.getArea(), DateUtils
                                    .formatDate(regUser.getRegDate(),
                                            Constant.DATE_FORMAT), regUser
                                    .getWelcomeTip(), regUser.getInvalidCode(),
                            regUser.getSex(), regUser.getRealName());
                }
                // 弹出注册成功提示框
                noticeDialog = new NoticeDialog(RegisterActivity.this,
                        R.style.NoticeDialog, "注册结果", "注册成功",
                        new NoticeDialog.LeaveMyDialogListener()
                        {

                            @Override
                            public void onClick(View view)
                            {
                                // TODO Auto-generated method stub
                                noticeDialog.dismiss();
                                noticeDialog = null;
                                // 新版注册完后直接绑定手机，无需判断是否绑定手机。
                                closeSelf(RegisterActivity.this);
                            }
                        });
                noticeDialog.show();
            } else
            {
                String msg = result.getResultDescription();
                if( msg==null || msg.length()<1){
                    msg="注册失败";
                }
                // 弹出注册失败提示框
                noticeDialog = new NoticeDialog(RegisterActivity.this,
                        R.style.NoticeDialog, "注册结果", msg ,
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

        @Override
        protected FMRegisterBean doInBackground(UserData... params)
        {
            // TODO Auto-generated method stub
            UserData regUserData = params[0];
            FMRegisterBean registerBean = new FMRegisterBean();
            JSONUtil<FMRegisterBean> jsonUtil = new JSONUtil<FMRegisterBean>();
            String url;
            ObtainParamsMap obtainMap = new ObtainParamsMap(
                    RegisterActivity.this);
            Map<String, String> paramMap = obtainMap.obtainMap();

            // 拼接注册url
            url = Constant.REGISTRATION_INTERFACE;
            // 注册是POST提交
            paramMap.put("phone", regUserData.getPhoneNumber());
            paramMap.put(
                    "password",
                    EncryptUtil.getInstance().encryptMd532(
                            regUserData.getPassword()));
            KJLoger.i("注册接口", regUserData.getPassword());
            KJLoger.i(
                    "注册接口",
                    EncryptUtil.getInstance().encryptMd532(
                            regUserData.getPassword()));
            paramMap.put("authcode", regUserData.getVerificationCode());
            if (null != regUserData.getInvitationCode())
            {
                paramMap.put("invcode", regUserData.getInvitationCode());

            } else
            {
                paramMap.put("invcode", "");
            }
            // 封装sign
            String signStr = obtainMap.getSign(paramMap);
            paramMap.put("sign", signStr);

            String jsonStr = HttpUtil.getInstance().doPost(url, paramMap);
            KJLoger.i("注册", jsonStr);
            try
            {
                registerBean = jsonUtil.toBean(jsonStr, registerBean);
            } catch (JsonSyntaxException e)
            {
                LogUtil.e("JSON_ERROR", e.getMessage());
                registerBean.setResultCode(0);
                registerBean.setResultDescription("解析json出错");
            }
            return registerBean;
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
//    public class VoiceSmsReceiver extends BroadcastReceiver
//    {
//
//        @Override
//        public void onReceive(Context context, Intent intent)
//        {
//            if (Constant.GET_VOICE_REGISTER.equals(intent.getAction()))
//            {
//                // 刷新获取按钮状态，设置为可获取语音
//                btnGet.setText("尝试语音获取");
//                btnGet.setTag(Constant.SMS_TYPE_VOICE);
//                ToastUtils.showLongToast(RegisterActivity.this,
//                        "还没收到短信，请尝试语音获取");
//            }
//        }
//    }

    @Override
    public boolean handleMessage(Message msg)
    {
        // TODO Auto-generated method stub
        if( msg.what == GetVCodeAsyncTask.GetVCode_Code){
                                  
            FMGetVC result =( FMGetVC) msg.obj;
            getVCResult = result;
            if( null==result) {
                ToastUtils.showLongToast(RegisterActivity.this, "请求数据发生异常，请重试");
                return true;
            }
            if( 1 != result.getSystemResultCode()){
                String errormsg = result.getSystemResultDescription();
                if(null==errormsg || errormsg.length()<1 ){
                    errormsg ="服务器发生"+ result.getSystemResultCode()+"错误";
                }
                ToastUtils.showLongToast(RegisterActivity.this, errormsg );
                return true;
            }
            
            if( 1!= result.getResultCode()){
                String errormsg = result.getResultDescription();
                if(null==errormsg || errormsg.length()<1 ){
                    errormsg ="服务器发生"+ result.getSystemResultCode()+"错误";
                }
                ToastUtils.showLongToast(RegisterActivity.this , errormsg );
                return true;
            }
           
            //boolean supportVoice = result.getResultData().isVoiceAble();
            //if( supportVoice){
                //Intent voiceIntent = new Intent(Constant.GET_VOICE_REGISTER);
                //MyBroadcastReceiver.sendBroadcast(RegisterActivity.this , MyBroadcastReceiver.GET_VOICE_REGISTER );
            //}
            
            return true;
        }
        return false;
    }

//    @Override
//    public void onFinishReceiver(ReceiverType type, Object msg)
//    {
//        // TODO Auto-generated method stub
//        if( type == ReceiverType.Register){
//            // 刷新获取按钮状态，设置为可获取语音
//            btnGet.setText("尝试语音获取");
//            btnGet.setTag(Constant.SMS_TYPE_VOICE);
//            ToastUtils.showLongToast(RegisterActivity.this,
//                    "还没收到短信，请尝试语音获取");
//        }
//    }

    /**
     * 倒计时控件 完成时，回调类
     * @类名称：CountDownFinish
     * @类描述：
     * @创建人：jinxiangdong
     * @修改人：
     * @修改时间：2015年7月8日 上午9:17:06
     * @修改备注：
     * @version:
     */
    class CountDownFinish  implements CountDownFinishListener{

        @Override
        public void finish()
        {
            if( getVCResult !=null && getVCResult.getResultData()!=null && getVCResult.getResultData().isVoiceAble()){
                // 刷新获取按钮状态，设置为可获取语音
                btnGet.setText("尝试语音获取");
                btnGet.setTag(Constant.SMS_TYPE_VOICE);
                ToastUtils.showLongToast(RegisterActivity.this,
                        "还没收到短信，请尝试语音获取");
            }
        }
        
    }
    
}
