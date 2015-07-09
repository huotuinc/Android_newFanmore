package cy.com.morefan.ui.info;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMBindPhone;
import cy.com.morefan.bean.FMUserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.task.GetVCAsyncTask;
import cy.com.morefan.ui.ambitus.ExchangeItemActivity;
import cy.com.morefan.ui.user.LoginActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.KJLoger;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.view.CountDownTimerButton;
import cy.com.morefan.view.CyButton;
import cy.lib.edittext.CyEditText;
/**
 * 
 * @类名称：BindingActivity
 * @类描述：绑定手机界面
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月10日 上午10:02:11
 * @修改备注：
 * @version:
 */
public class BindingActivity extends BaseActivity implements Callback,
        OnClickListener
{

    private CyButton backImage;
    private TextView title;
    private CyEditText edtPhone;
    private CyEditText edtCode;
    private TextView btnGet;
    private Button nextStep;
    public MyApplication application;
    //返回文字事件
    private TextView backText;
    //切换短信获取方式
    private VoiceSmsReceiver smsReceiver;
    private CountDownTimerButton countDownBtn;
    
    @Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.binding_ui);
       // registerFreshReceiver();
        application = (MyApplication) BindingActivity.this.getApplication();
        initView();
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
        filter.addAction(Constant.GET_VOICE_BINING);
        BindingActivity.this.registerReceiver(smsReceiver, filter);
    }

    private void initView()
    {
        backImage = (CyButton) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(this);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("绑定手机");
        edtPhone = (CyEditText) this.findViewById(R.id.edtPhone);
        edtCode = (CyEditText) this.findViewById(R.id.edtCode);
        btnGet = (TextView) this.findViewById(R.id.btnGet);
        //标记文本短信
        btnGet.setTag(Constant.SMS_TYPE_TEXT);
        btnGet.setText("获取验证码");
        btnGet.setOnClickListener(this);
        nextStep = (Button) this.findViewById(R.id.nextStep);
        nextStep.setOnClickListener(this);
        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);
    }
    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        //BindingActivity.this.unregisterReceiver(smsReceiver);
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
        BindingActivity.this.unregisterReceiver(smsReceiver);
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
          //提示退出后本次登录无效
            //跳转到登录界面
            AlertDialog.Builder dialog = new AlertDialog.Builder(BindingActivity.this);
            dialog.setTitle("绑定手机");
            dialog.setMessage("未绑定手机，退出该界面后本次登录无效，需重新登录。");
            dialog.setPositiveButton("不绑定", new DialogInterface.OnClickListener()
            {
                
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // TODO Auto-generated method stub
                    //清空登录信息
                    application.isLogin = false;
                    application.personal = new FMUserData();
                    //清空本地token,设置本地token为""
                    application.writeTokenToLocal(BindingActivity.this, "", Constant.TOKEN_CLEAR);
                    // 跳转到登录界面
                    ActivityUtils.getInstance().skipActivity(BindingActivity.this, LoginActivity.class);
                }
            });
            dialog.setNegativeButton("继续绑定", new DialogInterface.OnClickListener()
            {
                
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // TODO Auto-generated method stub
                    
                }
            });
            
            dialog.show();
        }
            break;
        case R.id.btnGet:
        {
            if(hasPhone())
            {
                doGetVD();
                
                countDownBtn = new CountDownTimerButton(btnGet,"%d秒重新发送","获取验证码",60000,null);
                countDownBtn.start();
            }
            else
            {
                edtPhone.setError("手机号不能为空！");
            }
        }
            break;
        case R.id.nextStep:
        {
            if(canNext())
            {
                donext();
            }
        }
            break;
        case R.id.backtext:
        {
          //提示退出后本次登录无效
            //跳转到登录界面
            AlertDialog.Builder dialog = new AlertDialog.Builder(BindingActivity.this);
            dialog.setTitle("绑定手机");
            dialog.setMessage("未绑定手机，退出该界面后本次登录无效，需重新登录。");
            dialog.setPositiveButton("不绑定", new DialogInterface.OnClickListener()
            {
                
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // TODO Auto-generated method stub
                    //清空登录信息
                    application.isLogin = false;
                    application.personal = new FMUserData();
                    //清空本地token,设置本地token为""
                    application.writeTokenToLocal(BindingActivity.this, "", Constant.TOKEN_CLEAR);
                    // 跳转到登录界面
                    ActivityUtils.getInstance().skipActivity(BindingActivity.this, LoginActivity.class);
                }
            });
            dialog.setNegativeButton("继续绑定", new DialogInterface.OnClickListener()
            {
                
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // TODO Auto-generated method stub
                    
                }
            });
            
            dialog.show();
        }
            break;
        default:
            break;
        }
    }

    @Override
    public boolean handleMessage(Message msg)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            //提示退出后本次登录无效
            //跳转到登录界面
            AlertDialog.Builder dialog = new AlertDialog.Builder(BindingActivity.this);
            dialog.setTitle("绑定手机");
            dialog.setMessage("未绑定手机，退出该界面，本次登录无效，需重新登录。");
            dialog.setPositiveButton("不绑定", new DialogInterface.OnClickListener()
            {
                
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // TODO Auto-generated method stub
                    //清空登录信息
                    application.isLogin = false;
                    application.personal = new FMUserData();
                    //清空本地token,设置本地token为""
                    application.writeTokenToLocal(BindingActivity.this, "", Constant.TOKEN_CLEAR);
                    // 跳转到登录界面
                    ActivityUtils.getInstance().skipActivity(BindingActivity.this, LoginActivity.class);
                }
            });
            dialog.setNegativeButton("继续绑定", new DialogInterface.OnClickListener()
            {
                
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // TODO Auto-generated method stub
                    
                }
            });
            
            dialog.show();
            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }
    
    private boolean hasPhone()
    {
        if(TextUtils.isEmpty(edtPhone.getText()))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    private void doGetVD()
    {
        try
        {
            ObtainParamsMap obtainMap = new ObtainParamsMap(BindingActivity.this);
            String url = Constant.GET_VD_INTERFACE + "?phone="
                    + URLEncoder.encode(edtPhone.getText().toString(), "UTF-8")
                    + "&type="
                    + URLEncoder.encode(Constant.GET_VD_TYPE_BINDLE, "UTF-8")
                    + "&codeType=" + btnGet.getTag();
            url += obtainMap.getMap();
            // 封装sign
            Map<String, String> signMap = new HashMap<String, String>();
            signMap.put("phone", edtPhone.getText().toString());
            signMap.put("type", Constant.GET_VD_TYPE_BINDLE);
            signMap.put("codeType", (String) btnGet.getTag());
            String signStr = obtainMap.getSign(signMap);
            url += "&sign=" + signStr;
            KJLoger.i("注册获取验证码", url);
            new GetVCAsyncTask(BindingActivity.this, Constant.GET_VD_TYPE_BINDLE).execute(url);

        } catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            KJLoger.errorLog(e.getMessage());
        }
    }
    
    private boolean canNext()
    {
        if(!hasPhone())
        {
            edtPhone.setError("手机号不能为空！");
            return false;
        } 
        else if (TextUtils.isEmpty(edtCode.getText()))
        {
            edtCode.setError("验证码不能为空！");
            return false;
        }
        else
        {
            return true;
        }
    }
    
    private void donext()
    {
        new BindPhoneAsyncTask().execute(Constant.BINDING_INTEFACE);
    }
    
    class BindPhoneAsyncTask extends AsyncTask<String, Void, FMBindPhone>
    {
        
        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
            //设置按钮不可用
            nextStep.setEnabled(false);
        }

        @Override
        protected void onPostExecute(FMBindPhone result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (1 == result.getResultCode())
            {
                //跳转到首页
                nextStep.setEnabled(true);
                closeSelf(BindingActivity.this);
            } 
            else if (Constant.TOKEN_OVERDUE == result.getResultCode())
            {
                // 提示账号异地登陆，强制用户退出
                // 并跳转到登录界面
                ToastUtils.showLongToast(BindingActivity.this, "账户登录过期，请重新登录");
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        ActivityUtils.getInstance().loginOutInActivity(
                                (Activity) BindingActivity.this);
                    }
                }, 2000);
            }
            else
            {
                //清空登录信息
                application.isLogin = false;
                application.personal = new FMUserData();
                //清空本地token,设置本地token为""
                application.writeTokenToLocal(BindingActivity.this, "", Constant.TOKEN_CLEAR);
                //跳转到登录界面
                AlertDialog.Builder dialog = new AlertDialog.Builder(BindingActivity.this);
                dialog.setTitle("绑定手机");
                dialog.setMessage("绑定手机失败。");
                dialog.setPositiveButton("回去登录", new DialogInterface.OnClickListener()
                {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // TODO Auto-generated method stub
                        // 跳转到登录界面
                        nextStep.setEnabled(true);
                        ActivityUtils.getInstance().skipActivity(BindingActivity.this, LoginActivity.class);
                    }
                });
                dialog.setNegativeButton("继续绑定", new DialogInterface.OnClickListener()
                {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // TODO Auto-generated method stub
                        nextStep.setEnabled(true);
                    }
                });
                
                dialog.show();
                
            }
            
            
        }

        @Override
        protected FMBindPhone doInBackground(String... params)
        {
            // TODO Auto-generated method stub
            String url = params[0];
            JSONUtil<FMBindPhone> jsonUtil = new JSONUtil<FMBindPhone>();
            FMBindPhone bindPhoneData = new FMBindPhone();
            //
            //封装参数
            ObtainParamsMap obtainMap = new ObtainParamsMap(BindingActivity.this);
            Map<String, String> paramMap = obtainMap.obtainMap();
            paramMap.put("phone", edtPhone.getText().toString());
            paramMap.put("authcode", edtCode.getText().toString());
            // 封装sign
            String signStr = obtainMap.getSign(paramMap);
            paramMap.put("sign", signStr);
            
            String json = HttpUtil.getInstance().doPost(url, paramMap);
            try
            {
                bindPhoneData = jsonUtil.toBean(json, bindPhoneData);
            }
            catch(JsonSyntaxException e)
            {
               LogUtil.e("JSON_ERROR", e.getMessage());
               bindPhoneData.setResultCode(0);
               bindPhoneData.setResultDescription("解析json出错");
            }
            return bindPhoneData;
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
            if (Constant.GET_VOICE_BINING.equals(intent.getAction()))
            {
                //刷新获取按钮状态，设置为可获取语音
                btnGet.setText("尝试语音获取");
                btnGet.setTag(Constant.SMS_TYPE_VOICE);
                ToastUtils.showLongToast(BindingActivity.this, "还没收到短信，请尝试语音获取");
            }
        }
    }
}
