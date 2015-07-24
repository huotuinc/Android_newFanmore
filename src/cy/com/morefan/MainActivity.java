package cy.com.morefan;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.adapter.MenuAdapter;
import cy.com.morefan.bean.FMCheckIn;
import cy.com.morefan.bean.FMPrepareCheckout;
import cy.com.morefan.bean.FMUserData;
import cy.com.morefan.bean.GlobalData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.frag.FragManager;
import cy.com.morefan.frag.FragManager.FragType;
import cy.com.morefan.listener.DataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.ui.account.MsgCenterActivity;
import cy.com.morefan.ui.ambitus.BuyFlowActivity;
import cy.com.morefan.ui.ambitus.DisciplesActivity;
import cy.com.morefan.ui.ambitus.ExchangeFlowActivity;
import cy.com.morefan.ui.ambitus.ExchangeFlowActivity.ExchangeFlowAsynTask;
import cy.com.morefan.ui.answer.AnswerActivity;
import cy.com.morefan.ui.user.LoginActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.BitmapLoader;
import cy.com.morefan.util.DateUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.L;
import cy.com.morefan.util.MenuListView;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ShareUtil;
import cy.com.morefan.util.SoundUtil;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.PopExpUp;

@SuppressLint("NewApi")
public class MainActivity extends BaseActivity implements OnClickListener,
        Callback, BroadcastListener
{

    private long exitTime = 0l;

    public MyApplication application;

    public FMUserData loginedUser;

    Handler mHandler = new Handler(this);

    public static TextView buyFlow;

    // 登录名称
    public static TextView loginUser;

    public static TextView txtTitle;

    // 流量
    public static TextView residualFlow;

    // 提示
    public static TextView tips;

    // 头像
    public static NetworkImageView headImage;

    // 标题处logo
    private TextView logoImage;

    private DrawerLayout layDrag;

    // 进入流量领取和购买界面
    public static RelativeLayout userLogoIm;

    // 登录后显示箭头
    public static ImageView loginArrows;

    // 分享弹出层
    private PopupWindow popupWindow = null;

    private Resources res;

    // 菜单背景图片
    private Drawable drawable;

    // 登出布局
    public static RelativeLayout logoutIm;

    // 登出显示文字
    public static TextView logoutTxt;

    // 监听广播
    private MyBroadcastReceiver myBroadcastReceiver;

    SoundUtil soundUtil = null;

    @Override
    protected void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();

        // 加载签到图标
        // SystemTools.loadBackground(buyFlow, drawable, "");
        // 判断系统登录情况

//        if (isLogin())
//        {
//            loginedUser = application.personal;
//
//            String imageUrl = MyApplication.readUserLogo(this);
//            // 显示登录界面
//            Util.loginUI();
//            BitmapLoader.create().displayUrl(MainActivity.this, headImage,
//                    imageUrl, R.drawable.ic_login_username,
//                    R.drawable.ic_login_username);
//            loginUser.setText(MyApplication.readUserName(MainActivity.this));
//            if (Util.isM(MyApplication.readUserBalance(MainActivity.this)))
//            {
//                residualFlow.setText(MyApplication
//                        .readUserBalance(MainActivity.this) + "MB");
//                tips.setText("可兑换流量");
//            } else
//            {
//                // 精确到GB
//                float flow = Float.parseFloat(MyApplication
//                        .readUserBalance(MainActivity.this)) / 1024;
//                residualFlow.setText(Util.decimalFloat(flow,
//                        Constant.ACCURACY_3) + "GB");
//            }
//
//        } else
//        {
//            BitmapLoader.create().displayUrl(MainActivity.this, headImage,
//                    Constant.LOGO_DEFAULT, R.drawable.ic_login_username,
//                    R.drawable.error);
//            // 显示登出界面
//            Util.logoutUI();
//        }
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        // 加载签到图标
        // SystemTools.loadBackground(buyFlow, drawable, "");
        // 判断系统登录情况
        if (isLogin())
        {
            loginedUser = application.personal;
            String imageUrl = MyApplication.readUserLogo(this);
            Util.loginUI();
            BitmapLoader.create().displayUrl(MainActivity.this, headImage,
                    imageUrl, 
                    R.drawable.ic_login_username, R.drawable.error);
            loginUser.setText(MyApplication.readUserName(MainActivity.this));
            if (Util.isM(MyApplication.readUserBalance(MainActivity.this)))
            {
                residualFlow.setText(MyApplication
                        .readUserBalance(MainActivity.this) + "MB");
                tips.setText("可兑换流量");
            } else
            {
                // 精确到GB
                float flow = Float.parseFloat(MyApplication
                        .readUserBalance(MainActivity.this)) / 1024;
                residualFlow.setText(Util.decimalFloat(flow,
                        Constant.ACCURACY_3) + "GB");
            }
            tips.setText("可兑换流量");
        } else
        {
//            BitmapLoader.create().displayUrl(MainActivity.this, headImage,
//                    Constant.LOGO_DEFAULT, R.drawable.ic_login_username,
//                    R.drawable.error);
            headImage.setImageUrl("", null);
            headImage.setBackgroundResource(R.drawable.mrtou);
            
            // 显示登出界面
            Util.logoutUI();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);
        res = MainActivity.this.getResources();
        application = (MyApplication) MainActivity.this.getApplication();
        initView();
        myBroadcastReceiver = new MyBroadcastReceiver(MainActivity.this, this,
                MyBroadcastReceiver.ACTION_FLOW_ADD);

        application.mFragManager = FragManager.getIns(this, R.id.layFrag);
        L.i(">>>onCreate:" + savedInstanceState + ","
                + application.mFragManager);

        if (null == savedInstanceState)
        {
            application.mFragManager.setCurrentFrag(FragType.Home);

        } else
        {
            application.mFragManager.setPreFragType(FragType.Home);
//            FragType curFragType = (FragType) savedInstanceState
//                    .getSerializable("curFragType");
//            L.i(">>>onCreate:" + curFragType);
//            application.mFragManager.setCurrentFrag(FragType.User);
        }

        soundUtil = new SoundUtil(MainActivity.this);

        operationAlarm();
    }

    /***
     * 
     * @创建人：jinxiangdong
     * @修改时间：2015年7月1日 下午3:26:01
     * @方法描述：
     * @方法名：operationAlarm
     * @参数：
     * @返回：void
     * @exception
     * @since
     */
    private void operationAlarm()
    {

        int messageType = 0;
        if (getIntent().hasExtra("type"))
        {
            messageType = getIntent().getIntExtra("type", 0);
        }
        if (messageType == Constant.MESSAGE_TYPE_SYSTEMMESSAGE)
        {
            Intent intentMsg = new Intent(this, MsgCenterActivity.class);
            startActivity(intentMsg);
            return;
        }        

        if (getIntent().hasExtra("task") == false)
            return;
       
        TaskData task = (TaskData) getIntent().getSerializableExtra("task");
        if (task == null)
            return;    
        
        // to task detail,得刷新任务列表
        Intent intentDetail = new Intent(this, AnswerActivity.class);
        intentDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentDetail.putExtra("task", task);
        startActivity(intentDetail);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        L.i(">>>onSaveInstanceState:"
                + application.mFragManager.getCurrentFragType());
        savedInstanceState.putSerializable("curFragType",
                application.mFragManager.getCurrentFragType());
        // TODO Auto-generated method stub
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState);
        L.i(">>>onSaveInstanceState:"
                + application.mFragManager.getCurrentFragType());
        savedInstanceState.putSerializable("curFragType",
                application.mFragManager.getCurrentFragType());
    }

    private void initView()
    {

        buyFlow = (TextView) this.findViewById(R.id.buyFlow);
        // 默认购买流量
        buyFlow.setTag("mark");
        /*
         * drawable = res.getDrawable(R.drawable.title_checkin);
         * SystemTools.loadBackground(buyFlow, drawable, "");
         */
        buyFlow.setText("签到");
        buyFlow.setOnClickListener(this);
        layDrag = (DrawerLayout) findViewById(R.id.layDrag);
        logoImage = (TextView) this.findViewById(R.id.btnLeft);
        logoImage.setText("");
        userLogoIm = (RelativeLayout) this.findViewById(R.id.userLogoIm);
        userLogoIm.setOnClickListener(this);
        logoutIm = (RelativeLayout) this.findViewById(R.id.logoutIm);
        logoutIm.setOnClickListener(this);
        logoutTxt = (TextView) this.findViewById(R.id.logoutTxt);
        txtTitle = (TextView) this.findViewById(R.id.txtTitle);
        txtTitle.setText(getString(R.string.app_name));
        // logoImage.setTag(R.drawable.ic_launcher);

        loginUser = (TextView) this.findViewById(R.id.txtUserName);
        residualFlow = (TextView) this.findViewById(R.id.txtFlow);
        tips = (TextView) this.findViewById(R.id.txtFlowDes);
        headImage = (NetworkImageView) this.findViewById(R.id.imgUserIcon);
        loginArrows = (ImageView) this.findViewById(R.id.loginArrows);

        // 加载初始化图片
//        BitmapLoader.create().displayUrl(MainActivity.this, headImage,
//                Constant.LOGO_DEFAULT, R.drawable.ic_login_username,
//                R.drawable.error);
        
        headImage.setOnClickListener(this);
       
//        if (isLogin())
//        {
//            Util.loginUI();
//            loginUser.setText( MyApplication.readUserName(MainActivity.this));
//            if (Util.isM(MyApplication.readUserBalance(MainActivity.this)))
//            {
//                residualFlow.setText( MyApplication
//                        .readUserBalance(MainActivity.this) + "MB");
//                tips.setText("可兑换流量");
//            } else
//            {
//                // 精确到GB
//                float flow = Float.parseFloat( MyApplication
//                        .readUserBalance(MainActivity.this)) / 1024;
//                residualFlow.setText(Util.decimalFloat(flow,
//                        Constant.ACCURACY_3) + "GB");
//            }
//            tips.setText("可兑换流量");
//        } else
//        {
//            Util.logoutUI();
//        }
        
        // init scroll
        // init menu
        MenuListView listMenu = (MenuListView) findViewById(R.id.listMenu);
        listMenu.setDivider(null);
        Resources res = getResources();
        TypedArray ar = res.obtainTypedArray(R.array.menu_imgs);
        int length = ar.length();
        int[] imgs = new int[length];
        for (int i = 0; i < length; i++)
            imgs[i] = ar.getResourceId(i, 0);
        ar.recycle();
        // int[] imgs = res.getIntArray(R.array.menu_imgs);
        String[] menus = res.getStringArray(R.array.menus);
        MenuAdapter menuAdapter = new MenuAdapter(this, imgs, menus);
        listMenu.setAdapter(menuAdapter);

        listMenu.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                    int position, long arg3)
            {
                switch (position)
                {
                case 0:
                {
                    application.mFragManager.setCurrentFrag(FragType.Home);
                    mHandler.sendEmptyMessage(DataListener.IS_HOME);
                }
                    break;
                case 1:
                {
                    // 判断是否登录
                    if (isLogin())
                    {
                        application.mFragManager.setCurrentFrag(FragType.User);
                        mHandler.sendEmptyMessage(DataListener.IS_USER);
                    } else
                    {

                        // 跳转到登陆界面
                        userLogin();
                    }
                }
                    break;
                case 2:
                {
                    // 判断是否登录
                    if (isLogin())
                    {
                        application.mFragManager.setCurrentFrag(FragType.Pre);
                        mHandler.sendEmptyMessage(DataListener.IS_PRE);
                    } else
                    {
                        // 跳转到登陆界面
                        userLogin();
                    }
                }
                    break;
                case 3:
                {
                    // 判断是否登录
                    if (isLogin())
                    {
                        application.mFragManager
                                .setCurrentFrag(FragType.Master);
                        mHandler.sendEmptyMessage(DataListener.IS_MASTER);
                    } else
                    {

                        // 跳转到登陆界面
                        userLogin();
                    }
                }
                    break;
                case 4:
                {
                    /*
                     * if (isLogin()) {
                     */
                    application.mFragManager.setCurrentFrag(FragType.More);
                    mHandler.sendEmptyMessage(DataListener.IS_MORE);
                    /*
                     * } else {
                     * 
                     * // 跳转到登陆界面 userLogin(); }
                     */

                }
                    break;
                case 5:
                {
                    if (isLogin())
                    {
                        application.mFragManager.setCurrentFrag(FragType.Mark);
                        mHandler.sendEmptyMessage(DataListener.IS_MARK);
                    } else
                    {

                        // 跳转到登陆界面
                        userLogin();
                    }
                }
                    break;
                case 6:
                {
                    if (isLogin())
                    {
                        // 送流量界面
                        application.mFragManager.setCurrentFrag(FragType.faqs);
                        mHandler.sendEmptyMessage(DataListener.IS_FAQS);
                    } else
                    {

                        // 跳转到登陆界面
                        userLogin();
                    }
                }
                    break;

                default:
                    break;
                }
                layDrag.closeDrawer(Gravity.LEFT);
            }
        });

    }

    @Override
    public void onClick(View v)
    {
        application.mFragManager.getCurrentFrag().onClick(v);
        switch (v.getId())
        {
        case R.id.btnLeft:
        {
            layDrag.openDrawer(Gravity.LEFT);
        }
            break;
        case R.id.buyFlow:
        {
            String tag = (String) buyFlow.getTag();
            if ("buy".equals(tag))
            {
                if (isLogin())
                {
                    ActivityUtils.getInstance().showActivity(MainActivity.this,
                            BuyFlowActivity.class);
                } else
                {
                    // 跳转到登录界面
                    userLogin();
                }
            } else if ("share".equals(tag))
            {
                if (isLogin())
                {
                    showPopup();
                } else
                {
                    // 跳转到登录界面
                    userLogin();
                }
            } else if ("master".equals(tag))
            {
                // 跳转到徒弟列表
                ActivityUtils.getInstance().showActivity(MainActivity.this,
                        DisciplesActivity.class);
            } else if ("mark".equals(tag))
            {                
                if (isLogin())
                {
                    // 先判断今天是否签到
                    mHandler.post(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            // TODO Auto-generated method stub
                            // 获取最新的签到信息
                            int signStatus = MyApplication.readInt(
                                    MainActivity.this,
                                    Constant.LOGIN_USER_INFO,
                                    Constant.LOGIN_USER_MARK);
                            // 判断今天是星期几
                            Calendar mCalendar = Calendar.getInstance();
                            // mCalendar.add(Calendar.DAY_OF_WEEK, 3);
                            int curWeekOfDay = mCalendar
                                    .get(Calendar.DAY_OF_WEEK) - 1;
                            curWeekOfDay = curWeekOfDay == 0 ? 7 : curWeekOfDay;
                            int weekIndex = curWeekOfDay - 1;
                            final int week = curWeekOfDay;
                            int sign = 0x1;
                            int status = signStatus >> (6 - weekIndex) & sign;
                            if (0 == status)
                            {
                                // 未签到
                                // ToastUtils.showLongToast(MainActivity.this,"正在签到");
                                new AsyncTask<Void, Void, FMCheckIn>()
                                {
                                    @Override
                                    protected FMCheckIn doInBackground(
                                            Void... params)
                                    {
                                        // TODO Auto-generated method stub
                                        FMCheckIn checkIn = new FMCheckIn();
                                        JSONUtil<FMCheckIn> jsonUtil = new JSONUtil<FMCheckIn>();
                                        String url;
                                        ObtainParamsMap obtainMap = new ObtainParamsMap(
                                                MainActivity.this);
                                        Map<String, String> paramMap = obtainMap
                                                .obtainMap();
                                        // 拼接注册url
                                        url = Constant.SIGN_IN;
                                        // 封装sign
                                        String signStr = obtainMap
                                                .getSign(paramMap);
                                        paramMap.put("sign", signStr);

                                        String jsonStr = HttpUtil.getInstance()
                                                .doPost(url, paramMap);
                                        try
                                        {
                                            checkIn = jsonUtil.toBean(jsonStr,
                                                    checkIn);
                                        } catch (JsonSyntaxException e)
                                        {
                                            LogUtil.e("JSON_ERROR",
                                                    e.getMessage());
                                            checkIn.setResultCode(0);
                                            checkIn.setResultDescription("解析json出错");
                                        }
                                        return checkIn;
                                    }

                                    @Override
                                    protected void onPreExecute()
                                    {
                                        // TODO Auto-generated method stub
                                        super.onPreExecute();

                                    }

                                    @Override
                                    protected void onPostExecute(
                                            FMCheckIn result)
                                    {
                                        // TODO Auto-generated method stub
                                        super.onPostExecute(result);
                                        // 刷新签到界面
                                        // 返回user对象，
                                        // 模拟代码
                                        if (1 == result.getResultCode())
                                        {
                                            // 如果签到成功，更新用户信息，token信息
                                            FMUserData userData = result
                                                    .getResultData().getUser();
                                            application.personal = userData;
                                            if (null != userData
                                                    && (!"".equals(userData
                                                            .getToken()) && null != userData
                                                            .getToken()))
                                            {
                                                // 刷新token
                                                MyApplication.writeTokenToLocal(
                                                        MainActivity.this,
                                                        result.getResultData()
                                                                .getUser()
                                                                .getToken(),
                                                        Constant.TOKEN_ADD);
                                                // 保存用户信息
                                                MyApplication
                                                        .writeUserInfoToLocal(
                                                                MainActivity.this,
                                                                userData.getName(),
                                                                userData.getBalance(),
                                                                userData.getPictureURL(),
                                                                userData.getSignInfo(),
                                                                userData.getInvCode(),
                                                                userData.getSigntoday(),
                                                                DateUtils
                                                                        .formatDate(
                                                                                userData.getBirthDate(),
                                                                                Constant.DATE_FORMAT),
                                                                userData.getMobile(),
                                                                userData.getCareer(),
                                                                userData.getIncoming(),
                                                                userData.getFavs(),
                                                                userData.getArea(),
                                                                DateUtils
                                                                        .formatDate(
                                                                                userData.getRegDate(),
                                                                                Constant.DATE_FORMAT),
                                                                userData.getWelcomeTip(),
                                                                userData.getInvalidCode(),
                                                                userData.getSex(),
                                                                userData.getRealName());
                                            }

                                            if (soundUtil != null)
                                            {
                                                soundUtil
                                                        .shakeSound(R.raw.checkin);
                                            }
                                            
                                            
                                            if (7 == week)
                                            {
                                                // 弹出签到已经领取流量信息
                                                if (127 == userData
                                                        .getSignInfo())
                                                {
                                                    ToastUtils
                                                            .showLongToast(
                                                                    MainActivity.this,
                                                                    "签到成功,你已经成功领取"
                                                                            + Util.getReward(MyApplication
                                                                                    .readString(
                                                                                            MainActivity.this,
                                                                                            Constant.INIT_INFO,
                                                                                            Constant.INIT_SIGN_MSG))
                                                                            + "M流量");
                                                } else
                                                {
                                                    ToastUtils
                                                            .showLongToast(
                                                                    MainActivity.this,
                                                                    "签到成功,"
                                                                            + MyApplication
                                                                                    .readString(
                                                                                            MainActivity.this,
                                                                                            Constant.INIT_INFO,
                                                                                            Constant.INIT_SIGN_MSG));
                                                }
                                            } else
                                            {
                                                ToastUtils.showLongToast(
                                                        MainActivity.this,
                                                        "签到成功");
                                            }

                                            // 刷新界面
                                            if (Util.isM( MyApplication
                                                    .readUserBalance(MainActivity.this)))
                                            {
                                                residualFlow.setText( MyApplication
                                                        .readUserBalance(MainActivity.this)
                                                        + "MB");
                                                tips.setText("可兑换流量");
                                            } else
                                            {
                                                // 精确到GB
                                                float flow = Float.parseFloat(MyApplication
                                                        .readUserBalance(MainActivity.this)) / 1024;
                                                residualFlow.setText(Util
                                                        .decimalFloat(
                                                                flow,
                                                                Constant.ACCURACY_3)
                                                        + "GB");
                                            }
                                        } else if (Constant.TOKEN_OVERDUE == result
                                                .getResultCode())
                                        {
                                            // 提示账号异地登陆，强制用户退出
                                            // 并跳转到登录界面
                                            ToastUtils.showLongToast(
                                                    MainActivity.this,
                                                    "账户登录过期，请重新登录");
                                            Handler mHandler = new Handler();
                                            mHandler.postDelayed(new Runnable()
                                            {

                                                @Override
                                                public void run()
                                                {
                                                    // TODO Auto-generated
                                                    // method stub
                                                    ActivityUtils
                                                            .getInstance()
                                                            .loginOutInActivity(
                                                                    (Activity) MainActivity.this);
                                                }
                                            }, 2000);
                                        } else
                                        {
                                            // 签到失败

                                            ToastUtils.showLongToast(
                                                    MainActivity.this, "签到失败");
                                        }

                                    }
                                }.execute();
                            } else if (1 == status)
                            {
                                // 已签到
                                ToastUtils.showLongToast(MainActivity.this,
                                        "今日已签到");
                            }

                        }
                    });
                } else
                {
                    // 跳转到登录界面
                    userLogin();
                }
            }

        }
            break;
        case R.id.imgUserIcon:
        {
            // 点击头像按钮
            // 登录则跳转到用户管理界面
            if (isLogin())
            {
                application.mFragManager.setCurrentFrag(FragType.User);
                mHandler.sendEmptyMessage(DataListener.IS_USER);
                layDrag.closeDrawer(Gravity.LEFT);
            } else
            {
                // 跳转到登录界面
                userLogin();
            }
        }
            break;
        case R.id.userLogoIm:
        {
            if (isLogin())
            {
                // 进入流量领取和购买界面
                ActivityUtils.getInstance().showActivity(MainActivity.this,
                        ExchangeFlowActivity.class);
            } else
            {
                // 跳转到登录界面
                userLogin();
            }
        }
            break;
        case R.id.logoutIm:
        {
            if (isLogin())
            {
                // 进入流量领取和购买界面
                ActivityUtils.getInstance().showActivity(MainActivity.this,
                        ExchangeFlowActivity.class);
            } else
            {
                // 跳转到登录界面
                userLogin();
            }
        }
            break;
        case R.id.layQQ:
        {
            // Qzone分享
            ShareUtil.share2Qzone(this, "粉猫分享", "",
                    "http://www.fanmore.cn");
        }
            break;
        case R.id.layWeiXin:
        {
            // 微信朋友圈分享
            ShareUtil.share2WeiXin(this, "粉猫微信分享", "",
                    "http://www.fanmore.cn");
        }
            break;
        case R.id.layXinLang:
        {
            // 新浪微博分享
            ShareUtil.share2Sina(this, "粉猫微博分享", "",
                    "http://www.fanmore.cn");
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
        switch (msg.what)
        {
        case DataListener.IS_HOME:
        {
            /*
             * drawable = res.getDrawable(R.drawable.title_checkin);
             * SystemTools.loadBackground(buyFlow, drawable, "");
             */
            buyFlow.setText("签到");
            txtTitle.setText(getString(R.string.app_name));
            buyFlow.setTag("mark");
            /*
             * logoImage.setBackgroundResource(R.drawable.ic_launcher);
             * logoImage.setTag(R.drawable.ic_launcher);
             */
        }
            break;
        case DataListener.IS_USER:
        {
            /*
             * drawable = res.getDrawable(R.drawable.white_bg);
             * SystemTools.loadBackground(buyFlow, drawable, "");
             */
            buyFlow.setText("");
            txtTitle.setText("账户设置");
            /*
             * logoImage.setBackgroundResource(R.drawable.ic_launcher);
             * logoImage.setTag(R.drawable.ic_launcher);
             */
        }
            break;
        case DataListener.IS_PRE:
        {
            /*
             * drawable = res.getDrawable(R.drawable.white_bg);
             * SystemTools.loadBackground(buyFlow, drawable, "买流量");
             */
            buyFlow.setText("");
            txtTitle.setText("今日预告");
            buyFlow.setTag("");
            /*
             * logoImage.setBackgroundResource(R.drawable.ic_launcher);
             * logoImage.setTag(R.drawable.ic_launcher);
             */
        }
            break;
        case DataListener.IS_MASTER:
        {
            /*
             * drawable = res.getDrawable(R.drawable.white_bg);
             * SystemTools.loadBackground(buyFlow, drawable, "徒弟列表");
             */
            buyFlow.setText("徒弟列表");
            txtTitle.setText("师徒联盟");
            buyFlow.setTag("master");
            /*
             * logoImage.setBackgroundResource(R.drawable.ic_launcher);
             * logoImage.setTag(R.drawable.ic_launcher);
             */
        }
            break;
        case DataListener.IS_MORE:
        {
            /*
             * drawable = res.getDrawable(R.drawable.white_bg);
             * SystemTools.loadBackground(buyFlow, drawable, "买流量");
             */
            buyFlow.setText("");
            txtTitle.setText("更多设置");
            buyFlow.setTag("");
            /*
             * logoImage.setBackgroundResource(R.drawable.ic_launcher);
             * logoImage.setTag(R.drawable.ic_launcher);
             */
        }
            break;
        case DataListener.IS_MARK:
        {
            /*
             * drawable = res.getDrawable(R.drawable.white_bg);
             * SystemTools.loadBackground(buyFlow, drawable, "分享");
             */
            buyFlow.setText("");
            txtTitle.setText("签到中心");
            buyFlow.setTag("");
            /*
             * logoImage.setBackgroundResource(R.drawable.ic_launcher);
             * logoImage.setTag(R.drawable.ic_launcher);
             */
        }
            break;
        case DataListener.IS_FAQS:
        {
            /*
             * drawable = res.getDrawable(R.drawable.white_bg);
             * SystemTools.loadBackground(buyFlow, drawable, "买流量");
             */
            buyFlow.setText("买流量");
            txtTitle.setText("送流量");
            buyFlow.setTag("buy");
            /*
             * logoImage.setBackgroundResource(R.drawable.ic_launcher);
             * logoImage.setTag(R.drawable.ic_launcher);
             */
        }
            break;

        default:
            break;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        // 2秒以内按两次推出程序
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if ((System.currentTimeMillis() - exitTime) > 2000)
            {
                ToastUtils.showLongToast(getApplicationContext(), "再按一次退出程序");
                exitTime = System.currentTimeMillis();
                // 切出菜单界面
                // layDrag.openDrawer(Gravity.LEFT);
            } else
            {
                try
                {
                    // 默认登出
                    application.isLogin = false;
                    application.personal = new FMUserData();
                    application.globalData = new GlobalData();
                    MainActivity.this.finish();
                    Runtime.getRuntime().exit(0);
                } catch (Exception e)
                {
                    Runtime.getRuntime().exit(-1);
                }
            }

            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        myBroadcastReceiver.unregisterReceiver();

        if (soundUtil != null)
        {
            soundUtil.Release();
        }
    }

    /**
     * 
     * @方法描述：
     * @方法名：initPopupWindow
     * @参数：
     * @返回：void
     * @exception
     * @since
     */
    private void initPopupWindow()
    {
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
        popupWindow = new PopupWindow(layout,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置PopupWindow显示和隐藏时的动画
        popupWindow.setAnimationStyle(R.style.AnimationPop);
    }

    /**
     * 显示菜单
     *
     * @方法描述：
     * @方法名：showPopup
     * @参数：
     * @返回：void
     * @exception
     * @since
     */
    private void showPopup()
    {

        if (popupWindow == null)
            initPopupWindow();

        // 设置位置
        // popupWindow.showAsDropDown(btnShare,-50,-180);
        popupWindow.showAtLocation(buyFlow, Gravity.BOTTOM, 0, 0); // 设置在屏幕中的显示位置
    }

    @Override
    public void onFinishReceiver(ReceiverType type, Object msg)
    {
        // TODO Auto-generated method stub
        new AsyncTask<Void, Void, FMPrepareCheckout>()
        {

            @Override
            protected FMPrepareCheckout doInBackground(Void... params)
            {
                // TODO Auto-generated method stub
                String url = Constant.PREPARE_CHECKOUT;
                ObtainParamsMap obtainMap = new ObtainParamsMap(
                        MainActivity.this);
                String paramString = obtainMap.getMap();
                Map<String, String> signMap = new HashMap<>();
                String sign = obtainMap.getSign(signMap);

                try
                {
                    url += "?sign=" + URLEncoder.encode(sign, "UTF-8");
                    url += paramString;

                    Log.i("exchanger", url);

                    if (Constant.IS_PRODUCTION_ENVIRONMENT)
                    {

                        String responseStr = HttpUtil.getInstance().doGet(url);
                        FMPrepareCheckout result = new FMPrepareCheckout();
                        JSONUtil<FMPrepareCheckout> jsonUtil = new JSONUtil<>();
                        try
                        {
                            result = jsonUtil.toBean(responseStr, result);
                        } catch (JsonSyntaxException e)
                        {
                            LogUtil.e("JSON_ERROR", e.getMessage());
                            result.setResultCode(0);
                            result.setResultDescription("解析json出错");
                        }
                        return result;
                    }

                } catch (UnsupportedEncodingException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e("exchange", e.getMessage());
                    return null;
                }
                return null;
            }

            @Override
            protected void onPreExecute()
            {
                // TODO Auto-generated method stub
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(FMPrepareCheckout result)
            {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                if (1 == result.getResultCode())
                {
                    BigDecimal balance = result.getResultData()
                            .getCurrentBalance();
                    if (balance == null)
                        balance = BigDecimal.ZERO;
                    balance = balance.setScale(1, RoundingMode.HALF_UP);
                    DecimalFormat format = new DecimalFormat("0.##");
                    String balanceString = format.format(balance);
                    MyApplication.writeString(MainActivity.this,
                            "login_user_info", "user_balance", balanceString);
                    if (Util.isM1(balance))
                    {
                        MainActivity.residualFlow.setText(MyApplication
                                .readUserBalance(MainActivity.this) + "MB");
                    } else
                    {
                        // 精确到GB
                        BigDecimal v = BigDecimal.valueOf(1024);
                        float flow = Float.parseFloat(MyApplication
                                .readUserBalance(MainActivity.this)) / 1024;
                        MainActivity.residualFlow.setText(Util.decimalFloat(
                                flow, Constant.ACCURACY_3) + "GB");
                    }
                }
            }

        }.execute();
    }

//    private PopExpUp popExpUp;
//	public void expUp(int exp){
//		if(exp < 1)
//			return;
//		if(null == popExpUp)
//			popExpUp = new PopExpUp(this);
//		popExpUp.show(exp);
//	}


}
