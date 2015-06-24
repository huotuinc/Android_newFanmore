package cy.com.morefan.ui.user;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMLogin;
import cy.com.morefan.bean.FMLogin.InnerUser;
import cy.com.morefan.bean.FMUserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.ui.info.BindingActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.DateUtils;
import cy.com.morefan.util.EncryptUtil;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.KJLoger;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.view.NoticeDialog;
import cy.lib.edittext.CyEditText;
/**
 * 
 * @类名称：LoginActivity
 * @类描述：登录界面
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月10日 上午10:02:35
 * @修改备注：
 * @version:
 */
public class LoginActivity extends BaseActivity implements OnClickListener
{
    // 图标
    // private ImageView titleIcon;

    // 返回（关闭当前界面）
    private CyButton titleBack;

    // 用户名
    private CyEditText userName;

    // 密码
    private CyEditText passWord;

    // 登录按钮
    private Button loginBtn;

    // 忘记密码
    private TextView forgetPswBtn;

    // 界面名称
    private TextView titleName;

    // 注册
    private TextView registerBtn;

    // 登录进度
    private ProgressBar progressBar;

    //
    public MyApplication apolication;

    private NoticeDialog noticeDialog;
    //返回文字事件
    private TextView backText;

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub

        switch (v.getId())
        {

        case R.id.btnLogin:
        {

            int tag = canLogin(userName, passWord);
            // 验证登录信息
            if (0 == tag)
            {
                String userNmaeText = userName.getText().toString();
                String passWordText = passWord.getText().toString();
                // 处理登录事件
                doLogin(userNmaeText, passWordText);
            } else if (1 == tag)
            {
                // 用户名为空
                userName.setError("用户名不能为空");
            } else if (2 == tag)
            {
                passWord.setError("密码不能为空");
            } else if (3 == tag)
            {
                userName.setError("用户名不能为空");
                passWord.setError("密码不能为空");
            }
            else if( 4==tag){
                passWord.setError("密码太短");
            }

        }
            break;
        case R.id.btnForget:
        {
            // ToastUtils.showShortToast(LoginActivity.this, "忘记密码处理界面");

            userForget();

        }
            break;
        case R.id.btnReg:
        {
            // ToastUtils.showShortToast(LoginActivity.this, "进入注册界面");
            userRegister();
        }
            break;
        /*
         * case R.id.titleImage: { ToastUtils.showLongToast(LoginActivity.this,
         * "粉猫_流量猫。"); } break;
         */
        case R.id.backImage:
        {
            closeSelf(LoginActivity.this);
        }
            break;
        case R.id.backtext:
        {
            closeSelf(LoginActivity.this);
        }
            break;

        default:
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.user_login);
        apolication = (MyApplication) LoginActivity.this.getApplication();
        initView();
        handleBtnEvent();

    }

    private void initView()
    {
        titleName = (TextView) this.findViewById(R.id.title);
        titleName.setText("用户登录");
        userName = (CyEditText) this.findViewById(R.id.edtUserName);
        passWord = (CyEditText) this.findViewById(R.id.edtPwd);
        loginBtn = (Button) this.findViewById(R.id.btnLogin);
        forgetPswBtn = (TextView) this.findViewById(R.id.btnForget);
        registerBtn = (TextView) this.findViewById(R.id.btnReg);
        // titleIcon = (ImageView) this.findViewById(R.id.titleImage);
        titleBack = (CyButton) this.findViewById(R.id.backImage);
        progressBar = (ProgressBar) this.findViewById(R.id.loginPB);
        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);
    }

    private void handleBtnEvent()
    {
        // 登录按钮事件
        loginBtn.setOnClickListener(this);
        // 忘记密码处理事件
        forgetPswBtn.setOnClickListener(this);
        // 注册按钮事件
        registerBtn.setOnClickListener(this);
        // 标题图标
        // titleIcon.setOnClickListener(this);
        // 标题返回
        titleBack.setOnClickListener(this);
    }

    /**
     * 
     * @方法描述：判断登录可行 0：可以登录，1：用户名为空，2：密码为空， 3：用户名密码为空 , 4: 密码长度< 3
     * @方法名：canLogin
     * @参数：@param userName
     * @参数：@param passWord
     * @参数：@return
     * @返回：int
     * @exception
     * @since
     */
    private int canLogin(CyEditText userName, CyEditText passWord)
    {
        if (TextUtils.isEmpty(userName.getText())
                && TextUtils.isEmpty(passWord.getText()))
        {
            return 3;
        } else if (TextUtils.isEmpty(userName.getText()))
        {
            return 1;
        } else if (TextUtils.isEmpty(passWord.getText()))
        {
            return 2;
        }else if( passWord.getText().toString().trim().length()<3 ) {
            return 4;
        }
        else
        {
            return 0;
        }
    }

    /**
     * 
     * @方法描述：登录操作
     * @方法名：doLogin
     * @参数：@param userName
     * @参数：@param passWord
     * @返回：void
     * @exception
     * @since
     */
    private void doLogin(String userName, String passWord)
    {
        // doSomething

        FMUserData loginUser = new FMUserData();
        loginUser.setName(userName);
        new LoginAsyncTask().execute(loginUser);
    }

    /**
     * 
     * @类名称：LoginAsyncTask
     * @类描述：实现异步
     * @创建人：aaron
     * @修改人：
     * @修改时间：2015年5月27日 上午9:32:54
     * @修改备注：
     * @version:
     */
    class LoginAsyncTask extends AsyncTask<FMUserData, Void, FMLogin>
    {

        @Override
        protected FMLogin doInBackground(FMUserData... params)
        {
            // TODO Auto-generated method stub
            FMUserData remoteUser;
            FMLogin loginrBean = new FMLogin();
            JSONUtil<FMLogin> jsonUtil = new JSONUtil<FMLogin>();
            remoteUser = params[0];
            ObtainParamsMap obtainMap = new ObtainParamsMap(LoginActivity.this);
            String paramMap = obtainMap.getMap();
            // 封装sign
            Map<String, String> signMap = new HashMap<String, String>();
            signMap.put("username", userName.getText().toString());
            signMap.put(
                    "password",
                    EncryptUtil.getInstance().encryptMd532(
                            passWord.getText().toString()));
            String signStr = obtainMap.getSign(signMap);
            String url = Constant.LOGIN_INTERFACE;
            try
            {
                url = url
                        + "?username="
                        + URLEncoder.encode(userName.getText().toString(),
                                "UTF-8")
                        + "&password="
                        + URLEncoder.encode(EncryptUtil.getInstance()
                                .encryptMd532(passWord.getText().toString()),
                                "UTF-8");
                url += paramMap;
                url += "&sign=" + URLEncoder.encode(signStr, "UTF-8");
                // 根据返回的json封装新的user数据集
                //切换到测试环境
                if(Constant.IS_PRODUCTION_ENVIRONMENT)
                {
                    String jsonStr = HttpUtil.getInstance().doGet(url);
                    try
                    {
                        loginrBean = jsonUtil.toBean(jsonStr, loginrBean);
                    }
                    catch(JsonSyntaxException e)
                    {
                       LogUtil.e("JSON_ERROR", e.getMessage());
                       loginrBean.setResultCode(0);
                       loginrBean.setResultDescription("解析json出错");
                    }
                }
                else
                {
                    loginrBean.setResultCode(1);
                    InnerUser iUser = loginrBean.new InnerUser();
                    iUser.setRequireMobile(1);
                    FMUserData user = new FMUserData();
                    user.setBalance( 100);
                    user.setMobile("13588741728");
                    user.setName(userName.getText().toString());
                    user.setToken("1234567890");
                    iUser.setUser(user);
                    loginrBean.setResultData(iUser);
                }
                

            } catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                KJLoger.errorLog(e.getMessage());
            }
            return loginrBean;
        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            // 登录按钮不可用
            loginBtn.setClickable(false);
        }

        @Override
        protected void onPostExecute(FMLogin result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            // 判断登录是否成功
            if (1 == result.getResultCode())
            {
                // 返回数据表示成功
                FMUserData loginUser = result.getResultData().getUser();
                apolication.personal = loginUser;
                apolication.isLogin = true;
                if(!"".equals(loginUser.getToken()) && null != loginUser.getToken())
                {
                // 记录更新token
                application.writeTokenToLocal(LoginActivity.this,
                        loginUser.getToken(), Constant.TOKEN_ADD);
                if(!"".equals(loginUser.getWelcomeTip()) && null != loginUser.getWelcomeTip())
                {
                    ToastUtils.showShortToast(LoginActivity.this, loginUser.getWelcomeTip());
                }
                // 保存用户信息
                apolication.writeUserInfoToLocal(LoginActivity.this,
                        loginUser.getName(), loginUser.getBalance(),
                        loginUser.getPictureURL(), loginUser.getSignInfo(),
                        loginUser.getInvCode(), loginUser.getSigntoday(),
                        DateUtils.formatDate(loginUser.getBirthDate(), Constant.DATE_FORMAT), loginUser.getMobile(),
                        loginUser.getCareer(), loginUser.getIncoming(),
                        loginUser.getFavs(), loginUser.getArea(),
                        DateUtils.formatDate(loginUser.getRegDate(), Constant.DATE_FORMAT), loginUser.getWelcomeTip(),
                        loginUser.getInvalidCode(),loginUser.getSex(),loginUser.getRealName());
                }
                progressBar.setVisibility(View.GONE);
                // 登录按钮可用
                loginBtn.setClickable(true);
                // 判断用户是否需要绑定手机
                if (0 == result.getResultData().getRequireMobile())
                {              
                    setResult(RESULT_OK);
                    
                    // 已经绑定手机
                    closeSelf(LoginActivity.this);                   
                    
                   
                    
                    
                } else
                {
                    // 跳转到绑定手机界面
                    ActivityUtils.getInstance().skipActivity(
                            LoginActivity.this, BindingActivity.class);
                }
            } else
            {
                // 失败
                apolication.isLogin = false;
                progressBar.setVisibility(View.GONE);
                // 登录按钮可用
                loginBtn.setClickable(true);
                // 弹出登录失败提示
                noticeDialog = new NoticeDialog(LoginActivity.this,
                        R.style.NoticeDialog, "用户登录", "登录失败",
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            // finish自身
            LoginActivity.this.finish();
            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

}
