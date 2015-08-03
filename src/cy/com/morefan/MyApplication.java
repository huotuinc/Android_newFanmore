package cy.com.morefan;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Looper;
import android.telephony.TelephonyManager;
import cn.jpush.android.api.JPushInterface;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;

import cy.com.morefan.bean.FMUserData;
import cy.com.morefan.bean.GlobalData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.frag.FragManager;
import cy.com.morefan.util.KJConfig;
import cy.com.morefan.util.KJLoger;
import cy.com.morefan.util.PreferenceHelper;
import cy.com.morefan.util.Util;
import cy.com.morefan.util.VolleyUtil;

public class MyApplication extends Application
{
    /**
     * 保存用户是否登录
     */
    public boolean isLogin = false;

    /**
     * 答题的总时间 5秒
     */
    public int answerTotalTimes = 5;

    /**
     * 城市码
     */
    public String cityCode;

    public int localType;

    /**
     * 城市
     */
    public String city;

    /**
     * 地址
     */
    public String address;

    /**
     * 纬度
     */
    public double latitude;

    /**
     * 经度
     */
    public double Longitude;

    /**
     * 面板主控件
     */
    public FragManager mFragManager;

    private static ActivityStack activityManager;

    // 记录用户设置提醒的任务ID
    public List<Integer> taskList = new ArrayList<Integer>();

    /**
     * 定位服务的一个句柄
     */
    public Intent locationI = new Intent();

    /**
     * 记录每一个跳转的activity
     */
    public List<Activity> recentlyAtyList = new ArrayList<Activity>();

    // 初始化获取的数据

    public GlobalData globalData = new GlobalData();

    /**
     * 保存登录的用户信息
     */
    public FMUserData personal = new FMUserData();

    public LocationClient mLocationClient;

    public GeofenceClient mGeofenceClient;

    public MyLocationListener mMyLocationListener;

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        mGeofenceClient = new GeofenceClient(getApplicationContext());
        // 初始化Volley实例
        VolleyUtil.init(this);
        // 极光初始化
        // JPushInterface.setDebugMode(true);// 日志，生产环境关闭
        JPushInterface.init(this);

        // 全局异常处理
        new CrashHandler();

        solveAsyncTaskOnPostExecuteBug();
    }
    
    /**
     * 解决有些android版本 AsyncTask 无法执行  onPostExecute方法的问题
     *@创建人：jinxiangdong
     *@修改时间：2015年7月7日 上午11:23:32
     *@方法描述：
     *@方法名：solveAsyncTaskOnPostExecuteBug
     *@参数：
     *@返回：void
     *@exception 
     *@since
     */
    protected void solveAsyncTaskOnPostExecuteBug(){
        try
        {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener
    {

        @Override
        public void onReceiveLocation(BDLocation location)
        {
            if (null == location)
            {
                return;
            } else
            {
                localType = location.getLocType();

                if (BDLocation.TypeGpsLocation == localType)
                {
                    latitude = location.getLatitude();
                    Longitude = location.getLongitude();
                    cityCode = location.getCityCode();
                    city = location.getCity();
                    address = location.getAddrStr();
                } else if (BDLocation.TypeNetWorkLocation == localType)
                {
                    latitude = location.getLatitude();
                    Longitude = location.getLongitude();
                    cityCode = location.getCityCode();
                    city = location.getCity();
                    address = location.getAddrStr();
                }
            }

            // 将定位信息写入配置文件
            if (null != cityCode)
            {
                PreferenceHelper.writeString(getApplicationContext(),
                        Constant.LOCATION_INFO, "cityCode", cityCode);
            }
            if (null != city)
            {
                PreferenceHelper.writeString(getApplicationContext(),
                        Constant.LOCATION_INFO, "city", city);
            }
            if (null != address)
            {
                PreferenceHelper.writeString(getApplicationContext(),
                        Constant.LOCATION_INFO, "address", address);
            }
            PreferenceHelper.writeString(getApplicationContext(),
                    Constant.LOCATION_INFO, "latitude",
                    String.valueOf(latitude));
            PreferenceHelper.writeString(getApplicationContext(),
                    Constant.LOCATION_INFO, "Longitude",
                    String.valueOf(Longitude));
        }

    }

    /**
     * 获取手机IMEI码
     */
    public static String getPhoneIMEI(Context cxt)
    {
        TelephonyManager tm = (TelephonyManager) cxt
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * 判断网络是否连接
     */
    public static boolean checkNet(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null;// 网络是否连接
    }

    /**
     * 仅wifi联网功能是否开启
     */
    public static boolean checkOnlyWifi(Context context)
    {
        if (PreferenceHelper.readBoolean(context, KJConfig.SETTING_FILE,
                KJConfig.ONLY_WIFI))
        {
            return isWiFi(context);
        } else
        {
            return true;
        }
    }

    /**
     * 判断是否为wifi联网
     */
    public static boolean isWiFi(Context cxt)
    {
        ConnectivityManager cm = (ConnectivityManager) cxt
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // wifi的状态：ConnectivityManager.TYPE_WIFI
        // 3G的状态：ConnectivityManager.TYPE_MOBILE
        State state = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        return State.CONNECTED == state;
    }

    /**
     * 获取当前应用程序的版本号
     */
    public static String getAppVersion(Context context)
    {
        String version = "0";
        try
        {
            version = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e)
        {
            KJLoger.errorLog(e.getMessage());
        }
        return version;
    }

    /**
     * 获取设备的可用内存大小
     * 
     * @param cxt
     *            应用上下文对象context
     * @return 当前内存大小
     */
    public static int getDeviceUsableMemory(Context cxt)
    {
        ActivityManager am = (ActivityManager) cxt
                .getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        // 返回当前系统的可用内存
        return (int) (mi.availMem / (1024 * 1024));
    }

    /**
     * 清理后台进程与服务
     * 
     * @param cxt
     *            应用上下文对象context
     * @return 被清理的数量
     */
    public static int gc(Context cxt)
    {
        long i = getDeviceUsableMemory(cxt);
        int count = 0; // 清理掉的进程数
        ActivityManager am = (ActivityManager) cxt
                .getSystemService(Context.ACTIVITY_SERVICE);
        // 获取正在运行的service列表
        List<RunningServiceInfo> serviceList = am.getRunningServices(100);
        if (serviceList != null)
            for (RunningServiceInfo service : serviceList)
            {
                if (service.pid == android.os.Process.myPid())
                    continue;
                try
                {
                    android.os.Process.killProcess(service.pid);
                    count++;
                } catch (Exception e)
                {
                    e.getStackTrace();
                    continue;
                }
            }

        // 获取正在运行的进程列表
        List<RunningAppProcessInfo> processList = am.getRunningAppProcesses();
        if (processList != null)
            for (RunningAppProcessInfo process : processList)
            {
                // 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
                // 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
                if (process.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE)
                {
                    // pkgList 得到该进程下运行的包名
                    String[] pkgList = process.pkgList;
                    for (String pkgName : pkgList)
                    {
                        KJLoger.debug("======正在杀死包名：" + pkgName);
                        try
                        {
                            am.killBackgroundProcesses(pkgName);
                            count++;
                        } catch (Exception e)
                        { // 防止意外发生
                            e.getStackTrace();
                            continue;
                        }
                    }
                }
            }
        KJLoger.debug("清理了" + (getDeviceUsableMemory(cxt) - i) + "M内存");
        return count;
    }

    // 记录用户信息到本地
    /**
     * 
     * @方法描述：
     * @方法名：writeUserInfoToLocal
     * @参数：@param context 上下文环境
     * @参数：@param name 用户名
     * @参数：@param balance 流量余额
     * @参数：@param pictureURL 头像
     * @参数：@param signInfo 当前签到信息
     * @参数：@param invCode 邀请码
     * @参数：@param signtoday 今日获取的流量
     * @参数：@param birthDate 生日
     * @参数：@param mobile 手机
     * @参数：@param career 职业
     * @参数：@param incoming 收入
     * @参数：@param favs 爱好
     * @参数：@param area 区域（可以使用定位信息）
     * @参数：@param regDate 注册时间
     * @参数：@param welcomeTip 欢迎提示
     * @参数：@param invalidCode 是否绑定手机
     * @返回：void
     * @exception
     * @since
     */
    public static void writeUserInfoToLocal(Context context, String name,
            float balance, String pictureURL, int signInfo, String invCode,
            float signtoday, String birthDate, String mobile, int career,
            int incoming, String favs, String area, String regDate,
            String welcomeTip, int invalidCode, int sex, String realName)
    {

        // 用户名
        if (null != name)
        {
            PreferenceHelper.writeString(context, "login_user_info",
                    "user_name", name);
        }
        // 流量余额
        // 设置余额精度
        String balanceStr = String.valueOf(Util.decimalFloat(balance,
                Constant.ACCURACY_1));
        if (null != balanceStr)
        {
            PreferenceHelper.writeString(context, "login_user_info",
                    "user_balance", balanceStr);
        }
        // 用户头像
        if (null != pictureURL)
        {
            PreferenceHelper.writeString(context, "login_user_info",
                    "user_logo", pictureURL);
        }
        // 签到信息
        if (signInfo >= 0)
        {
            PreferenceHelper.writeInt(context, "login_user_info", "user_mark",
                    signInfo);
        }
        // 邀请码
        if (null != invCode)
        {
            PreferenceHelper.writeString(context, "login_user_info",
                    "user_invcode", invCode);
        }
        String signtodayStr = String.valueOf(signtoday);
        if (null != signtodayStr && !"".equals(signtodayStr))
        {
            // 今日签到获取流量
            PreferenceHelper.writeString(context, "login_user_info",
                    "user_signtoday", signtodayStr);
        }
        // 生日
        if (null != birthDate && !"".equals(birthDate))
        {
            PreferenceHelper.writeString(context, "login_user_info",
                    "user_birthdate", birthDate);
        }
        // 手机
        if (null != mobile && !"".equals(mobile))
        {
            PreferenceHelper.writeString(context, "login_user_info",
                    "user_mobile", mobile);
        }
        // 职业索引
        if (career >= 0)
        {
            PreferenceHelper.writeInt(context, "login_user_info",
                    "user_career", career);
        }
        // 收入索引
        if (incoming >= 0)
        {
            PreferenceHelper.writeInt(context, "login_user_info",
                    "user_incoming", incoming);
        }
        // 爱好，以逗号隔开
        if (null != favs)
        {
            PreferenceHelper.writeString(context, "login_user_info",
                    "user_favs", favs);
        }
        // 区域
        if (null != area)
        {
            PreferenceHelper.writeString(context, "login_user_info",
                    "user_area", area);
        }
        // 注册时间
        if (null != regDate && !"".equals(regDate))
        {
            PreferenceHelper.writeString(context, "login_user_info",
                    "user_regDate", regDate);
        }
        // 欢迎标识
        if (null != welcomeTip)
        {
            PreferenceHelper.writeString(context, "login_user_info",
                    "user_welcometip", welcomeTip);
        }
        // 绑定手机
        if (invalidCode >= 0)
        {
            PreferenceHelper.writeInt(context, "login_user_info",
                    "user_invalidcode", invalidCode);
        }
        // 性别
        if (sex >= 0)
        {
            PreferenceHelper.writeInt(context, Constant.LOGIN_USER_INFO,
                    Constant.LOGIN_USER_SEX, sex);
        }
        // 用户姓名
        if (null != realName)
        {
            PreferenceHelper.writeString(context, Constant.LOGIN_USER_INFO,
                    Constant.LOGIN_USER_ACCOUNT, realName);
        }

    }

    @Override
    public void onLowMemory()
    {
        // TODO Auto-generated method stub
        super.onLowMemory();
        // 解除定位绑定
        mLocationClient.unRegisterLocationListener(mMyLocationListener);
    }

    /**
     * 
     * @方法描述：添加/清楚token到本地
     * @方法名：writeTokenToLocal
     * @参数：@param context
     * @参数：@param token
     * @参数：@param type
     * @返回：void
     * @exception
     * @since
     */
    public static void writeTokenToLocal(Context context, String token,
            String type)
    {
        if (Constant.TOKEN_ADD.equals(type))
        {
            // 本地token
            if (null != token)
            {
                PreferenceHelper.writeString(context, Constant.LOGIN_AUTH_INFO,
                        Constant.LOGIN_AUTH_TOKEN, token);
            }
        } else if (Constant.TOKEN_CLEAR.equals(type))
        {
            PreferenceHelper.writeString(context, Constant.LOGIN_AUTH_INFO,
                    Constant.LOGIN_AUTH_TOKEN, token);
        }
    }

    //
    /**
     * 
     * @方法描述：记录公共信息到本地(更新类型，收入，职业，爱好 没有记录到文件中)
     * @方法名：writeGlobalInfoToLocal
     * @参数：@param context
     * @参数：@param amountToCheckout
     * @参数：@param signMsg
     * @参数：@param aboutURL
     * @参数：@param helpURL
     * @参数：@param lessReadSeconds
     * @返回：void
     * @exception
     * @since
     */
    public static void writeGlobalInfoToLocal(Context context,
            int amountToCheckout, String signMsg, String aboutURL,
            String helpURL, int lessReadSeconds, String updateMD5,
            String updateUrl, String updateTips, String serviceURL,
            String ruleURL, String privacyPoliciesURL,
            String customerServicePhone)
    {
        if (amountToCheckout >= 0)
        {
            // 流量充值最小值
            PreferenceHelper.writeInt(context, Constant.INIT_INFO,
                    Constant.INIT_AMOUNT_TO_CHECKOUT, amountToCheckout);
        }
        // 连续签到每日可增加0.1M
        if (null != signMsg && !"".equals(signMsg))
        {
            PreferenceHelper.writeString(context, Constant.INIT_INFO,
                    Constant.INIT_SIGN_MSG, signMsg);
        }
        // 关于我们的连接
        if (null != aboutURL && !"".equals(aboutURL))
        {
            PreferenceHelper.writeString(context, Constant.INIT_INFO,
                    Constant.INIT_ABOUT_URL, aboutURL);
        }
        // 帮助的连接
        if (null != helpURL && !"".equals(helpURL))
        {
            PreferenceHelper.writeString(context, Constant.INIT_INFO,
                    Constant.INIT_HELP_URL, helpURL);
        }
        // 最少阅读时间单位秒
        if (lessReadSeconds >= 0)
        {
            PreferenceHelper.writeInt(context, Constant.INIT_INFO,
                    Constant.INIT_LESS_READ_SECONDS, lessReadSeconds);
        }
        // 更新验证码
        if (null != updateMD5 && !"".equals(updateMD5))
        {
            PreferenceHelper.writeString(context, Constant.INIT_INFO,
                    Constant.INIT_UPDATE_MD5, updateMD5);
        }
        // 更新链接
        if (null != updateUrl && !"".equals(updateUrl))
        {
            PreferenceHelper.writeString(context, Constant.INIT_INFO,
                    Constant.INIT_UPDATE_URL, updateUrl);
        }
        // 更新说明
        if (null != updateTips && !"".equals(updateTips))
        {
            PreferenceHelper.writeString(context, Constant.INIT_INFO,
                    Constant.INIT_UPDATE_TIPS, updateTips);
        }
        // 投放指南
        if (null != serviceURL && !"".equals(serviceURL))
        {
            PreferenceHelper.writeString(context, Constant.INIT_INFO,
                    Constant.INIT_SERVICE_URL, serviceURL);
        }
        // 规则说明
        if (null != ruleURL && !"".equals(ruleURL))
        {
            PreferenceHelper.writeString(context, Constant.INIT_INFO,
                    Constant.INIT_RULE_URL, ruleURL);
        }
        // 隐私
        if (null != privacyPoliciesURL && !"".equals(privacyPoliciesURL))
        {
            PreferenceHelper.writeString(context, Constant.INIT_INFO,
                    Constant.INIT_PRIVATE_URL, privacyPoliciesURL);
        }
        // 客服
        if (null != customerServicePhone && !"".equals(customerServicePhone))
        {
            PreferenceHelper.writeString(context, Constant.INIT_INFO,
                    Constant.INIT_CUSTOMER_PHONE, customerServicePhone);
        }

    }

    /**
     * 
     * @方法描述：读取配置文件
     * @方法名：readLocalToken
     * @参数：@param context
     * @参数：@return
     * @返回：String
     * @exception
     * @since
     */
    public static String readLocalToken(Context context)
    {
        return PreferenceHelper.readString(context, Constant.LOGIN_AUTH_INFO,
                Constant.LOGIN_AUTH_TOKEN);
    }

    /**
     * 
     * @方法描述：读取登录用户名
     * @方法名：readUserName
     * @参数：@param context
     * @参数：@return
     * @返回：String
     * @exception
     * @since
     */
    public static String readUserName(Context context)
    {
        return PreferenceHelper.readString(context, "login_user_info",
                "user_name");
    }

    /**
     * 
     * @方法描述：读取用户头像
     * @方法名：readUserLogo
     * @参数：@param context
     * @参数：@return
     * @返回：String
     * @exception
     * @since
     */
    public static String readUserLogo(Context context)
    {
        return PreferenceHelper.readString(context, "login_user_info",
                "user_logo");
    }

    /**
     * 
     * @方法描述：读取用户剩余流量
     * @方法名：readUserLogo
     * @参数：@param context
     * @参数：@return
     * @返回：String
     * @exception
     * @since
     */
    public static String readUserBalance(Context context)
    {
        return PreferenceHelper.readString(context, "login_user_info",
                "user_balance");
    }

    public static String readString(Context context, String fileName, String key)
    {
        return PreferenceHelper.readString(context, fileName, key);
    }

    public static void writeString(Context context, String fileName,
            String key, String value)
    {
        PreferenceHelper.writeString(context, fileName, key, value);
    }
    
    public static void writeInt( Context context , String fileName , String key , int value){
        PreferenceHelper.writeInt(context, fileName, key, value);
    }

    public static void writeBoolean(Context context , String fileName , String key , boolean value){
        PreferenceHelper.writeBoolean(context, fileName, key,value);
    }

    public static String readString(Context context, String key)
    {
        return PreferenceHelper.readString(context, "login_user_info", key);
    }

    public static int readInt(Context context, String fileName, String key)
    {
        return PreferenceHelper.readInt(context, fileName, key);
    }
    
    public static int readInt(Context context, String fileName, String key, int defaultVal )
    {
        return PreferenceHelper.readInt(context, fileName, key , defaultVal );
    }

    public  static boolean readBoolean(Context context , String fileName , String key , boolean defaultVal){
        return PreferenceHelper.readBoolean(context,fileName, key , defaultVal);
    }

    /**
     * 
     * @方法描述：判断是否登录，并更新application中的标志位
     * @方法名：isLogin
     * @参数：@return
     * @返回：boolean
     * @exception
     * @since
     */
    public boolean isLogin(Context context)
    {
        /**
         * 判断是否登录的顺序：1、先判断本地token: token存在则强制认为已登录，同时强制设置isLogin为true
         * 
         */
        String localToken = readLocalToken(context);
        if (!"".equals(localToken) && null != localToken)
        {
            return isLogin = true;
        } else
        {
            // 如果本地不存在token，强制认为没登陆或者已经退出登录。
            return isLogin = false;
        }

    }

    class CrashHandler implements UncaughtExceptionHandler
    {
        public CrashHandler()
        {
            Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器
        }

        @Override
        public void uncaughtException(Thread thread, final Throwable ex)
        {
            new Thread()
            {
                public void run()
                {
                    Looper.prepare();
                    finshApp();
                    // restartApp(getApplicationContext());
                    Looper.loop();
                    // MyBroadcastReceiver.sendBroadcast(getApplicationContext(),
                    // MyBroadcastReceiver.);
                    // Intent intent = new Intent(MainApplication.this,
                    // LoadingActivity.class);
                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    // Intent.FLAG_ACTIVITY_NEW_TASK);
                    // startActivity(intent);

                };
            }.start();

        }

    }

    public static void finshApp()
    {
        ArrayList<Activity> activities = getActivityManager().getAll();
        if( activities==null )return;
        List<Activity> popList = new ArrayList<Activity>();
        for (Activity item : activities)
        {
            if (null != item)
            {
                popList.add(item);
                item.finish();
            }

        }
        getActivityManager().popAll(popList);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static void restartApp(Context activity)
    {
        // ArrayList<Activity> activities = getActivityManager().getAll();
        // for(Activity item : activities)
        // item.finish();
        // android.os.Process.killProcess(android.os.Process.myPid());
        // if(activities.size() != 0)
        // return;
        ArrayList<Activity> activities = getActivityManager().getAll();
        List<Activity> popList = new ArrayList<Activity>();
        for (Activity item : activities)
        {
            if (null != item)
            {
                popList.add(item);
                item.finish();
            }

        }
        getActivityManager().popAll(popList);

        Intent intent = new Intent(activity, LoadingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        // activity.finish();
    }

    public static ActivityStack getActivityManager()
    {
        if (activityManager == null)
            activityManager = new ActivityStack();
        return activityManager;
    }

    /**
     * 自定义activity管理堆栈
     * 
     * @author edushi
     *
     */
    static class ActivityStack
    {
        private ArrayList<Activity> activities;

        public ArrayList<Activity> getAll()
        {
            return activities;
        }

        /**
         * 弹出activity
         * 
         * @param activity
         */
        public void popActivity(Activity activity)
        {
            if (activities != null && activities.contains(activity))
                activities.remove(activity);
        }

        public void popAll(List<Activity> mActivities)
        {
            if (activities != null)
                activities.removeAll(mActivities);
        }

        /**
         * 加入activity
         * 
         * @param activity
         */
        public void pushActivity(Activity activity)
        {
            if (activities == null)
                activities = new ArrayList<Activity>();
            if (!activities.contains(activity))
                activities.add(activity);

        }
    }
}
