package cy.com.morefan.constant;

public class Constant
{

    public static final boolean USE_TEST_DATA = true;// 是否使用测试数据

    public static final String URL_API = "";

    public static final boolean IS_PRODUCTION_ENVIRONMENT = true;// 切换测试环境和生产环境阀值

    // public static final String BASE_URL =
    // "http://115.28.160.96:8080/fanmoreweb/app/";//基础URL
    //http://newtask.fanmore.cn
    //public static final String BASE_ROOT_URL ="http://newtask.fanmore.cn/";// "http://apitest.51flashmall.com:8080/fanmoreweb/";
    public static final String BASE_ROOT_URL = "http://apitest.51flashmall.com:8080/fanmoreweb/";
    //public static final String BASE_ROOT_URL ="http://192.168.0.23:8080/fanmoreweb/";
    /** liuqucheng **/
    //public static final String BASE_ROOT_URL = "http://192.168.0.23:8080/fanmoreweb/";
    /***/
    public static final String BASE_URL = BASE_ROOT_URL + "app/";// 基础URL

    // public static final String BASE_URL =
    // "http://192.168.1.117:8080/fanmoreweb/app/";//基础URL
    // public static final String BASE_URL =
    // "http://192.168.0.14:8080/app/";//基础URL
    public static final String REGISTRATION_INTERFACE = BASE_URL + "reg";// 注册
                                                                         // ---POST

    public static final String GET_VD_INTERFACE = BASE_URL + "sendSMS";// 获取验证码
                                                                       // --GET

    public static final String LOGIN_INTERFACE = BASE_URL + "login";// 登录 --GET

    public static final String INIT_INTERFACE = BASE_URL + "init";// 初始化 ---GET

    public static final String FORGET_INTERFACE = BASE_URL + "forgetPassword";// 忘记密码
                                                                              // --GET

    public static final String BINDING_INTEFACE = BASE_URL + "bindingmobile";// 绑定手机
                                                                             // ---POST

    public static final String MODIFYPSW_INTEFACE = BASE_URL + "modifyPassword";// 修改密码
                                                                                // --
                                                                                // POST

    public static final String TASK_DATA_INTEFACE = BASE_URL + "taskList"; // "http://192.168.0.25:8080/fanmore/app/taskList";//
                                                                           // BASE_URL
                                                                           // +
                                                                           // "taskList";//获取任务列表
                                                                           // ---GET

    public static final String PREPARE_CHECKOUT = BASE_URL + "prepareCheckout";// 准备兑现
                                                                               // ---GET

    public static final String CHECKOUT = BASE_URL + "checkout";// 实现兑现 ---POST

    public static final String PREPARE_BUY = BASE_URL + "prepareBuy";// 准备兑现
                                                                     // ---GET

    public static final String TASK_DETAIL_INTEFACE = BASE_URL + "taskDetail"; // "http://192.168.0.25:8080/fanmore/app/taskDetail";//任务明细
                                                                               // ---GET

    public static final String TASK_TURNED_NOTIFY = BASE_URL
            + "taskTurnedNotify";// 转发任务以后的通知 ---GET

    // 实现购买
    // 签到
    public static final String SIGN_IN = BASE_URL + "signin";// ---POST

    public static final String MESSAGE = BASE_URL + "messages";// 获取消息列表 ---GET

    public static final String DELETE_MESSAGE = BASE_URL + "deleteMessage";// 删除消息列表
                                                                           // ---POST
    public static final String DELETE_LIST =BASE_URL+"cleanRequestFC";//删除好友请求列表
                                                                           // ---POST
      public static final String TAKE_FRIEMDS_LIST =BASE_URL+"requestFCList";//获取好友请求列表
                                                                           // ---POST

    public static final String UPDATE_PROFILE = BASE_URL + "updateProfile";// 修改资料
                                                                           // ---POST

    public static final String DETAILS = BASE_URL + "details";// 流量明细 ---GET

    public static final String PREVIEW_TASK_LIST = BASE_URL + "previewTaskList";// 今日预告
                                                                                // ---GET

    public static final String MASTER_SYSTEM = BASE_URL + "shituInfo";// 师徒系统
                                                                      // ---GET

    public static final String DISCIPLE_LIST = BASE_URL + "appsList";// 徒弟列表
                                                                     // ---GET

    public static final String ANSWER = BASE_URL + "answer";// 答题 ---GET

    public static final String FEEDBACK = BASE_URL + "feedback";// 意见回馈 ---POST

    public static final String SENT_FLOW = BASE_URL + "sentFlow";// 获取通讯录信息
                                                                 // ---POST

    public static final String SETUP_ALIAS = BASE_URL + "updateDeviceToken";// 将极光的别名推送给服务端
                                                                            // ---POST
    public static final String CONTACTINFO = BASE_URL + "contactInfo";//获取联系人流量信息 POST

    public static final String MAKEREQUEST= BASE_URL + "makeRequest";//求流量 GET

    public static final String MAKEPROVIDE = BASE_URL +"makeProvide";//赠送流量

    public static final String DELETEREQUEST = BASE_URL +"deleteRequestFC";//删除一条求赠信息

    public static final String DELIVERGOOD = BASE_URL +"deliverGood";//发货接口

    // 获取验证码类型
    public static final String GET_VD_TYPE_REG = "1";//

    public static final String GET_VD_TYPE_FORGET = "2";//

    public static final String GET_VD_TYPE_BINDLE = "3";//

    // 标准时间定义
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATE_FORMAT_COMPACT = "yyyy/MM/dd";

    public static final String ONLYTIME_FORMAT = "HH:mm:ss";

    // 任务类别
    // 类别：答题、问卷
    public static final String TASK_TYPE_ANSWER = "1";

    // 类别：报名
    public static final String TASK_TYPE_SIGN_UP = "2";

    // 类别：答题/答案外取
    public static final String TASK_TYPE_ANSWER_OUT = "3";

    // 类别：游戏
    public static final String TASK_TYPE_PLAY = "4";

    // 任务状态
    // 状态1：预发布中
    public static final String TASK_STATE_PRE_RELEASE = "0";

    // 状态2：发布中
    public static final String TASK_STATE_RELEASE = "1";

    // 状态3：发布结束
    public static final String TASK_STATE_RELEASE_END = "2";

    /**
     * 操作平台码
     */
    public static final String OPERATION_CODE = "FM2015AD";

    public static final String APPKEY = "b73ca64567fb49ee963477263283a1bf";

    // 平台安全码
    public static final String APP_SECRET = "1165a8d240b29af3f418b8d10599d0da";

    /**
     * capCode
     */
    public static final String CAP_CODE = "default";

    /**
     * 
     */
    public final static String SP_NAME_NORMAL = "MFSP_NORMAL  ";// sp内容不会被清理

    public final static String SP_NAME_SINA_TOKEN = "MFSP_SINA_TOKEN";// sp内容不会被清理

    public static String SINA_KEY_SECRET;

    /**
     * 设置闹钟按钮切换状态
     */
    public final static int ALARM_STATUS_E = 150;

    public final static int ALARM_STATUS_D = 151;

    /**
     * token添加的类型
     */
    public final static String TOKEN_ADD = "add";// 添加token

    public final static String TOKEN_CLEAR = "clear";// 清空token

    /**
     * 设置闹钟action
     */
    public final static String ALARM_ACTION = "cy.com.morefan.alarm";

    public final static int ANIMATION_COUNT = 1000;

    // 任务类列表每页显示5条
    public final static int PAGES_TASK = 10;

    // 消息类列表每页显示10条
    public final static int PAGES_COMMON = 10;

    /**
     * 微信appid
     */
    public final static String WX_APPID = "wxd8c58460d0199dd5";

    public final static String WX_APPSECRET = "8ad99de44bd96a323eb40dc161e7d8e8";

    // 配置文件
    // 初始化信息文件名
    public final static String INIT_INFO = "init_info";

    // 流量值最小充值
    public final static String INIT_AMOUNT_TO_CHECKOUT = "amountToCheckout";

    // 连续签到每日可增加0.1M
    public final static String INIT_SIGN_MSG = "signmsg";

    // 关于我们的连接
    public final static String INIT_ABOUT_URL = "abouturl";

    // 帮助的连接
    public final static String INIT_HELP_URL = "helpurl";

    // 最少阅读时间单位秒
    public final static String INIT_LESS_READ_SECONDS = "lessreadseconds";

    // 更新验证码
    public final static String INIT_UPDATE_MD5 = "updatemd5";

    // 更新链接
    public final static String INIT_UPDATE_URL = "updateurl";

    // 更新说明
    public final static String INIT_UPDATE_TIPS = "updatetips";

    // 规则说明
    public final static String INIT_RULE_URL = "ruleURL";

    // 投放指南
    public final static String INIT_SERVICE_URL = "serviceURL";

    // 隐私
    public final static String INIT_PRIVATE_URL = "privateURL";

    // 客服
    public final static String INIT_CUSTOMER_PHONE = "customerPhone";

    // 口令文件
    public final static String LOGIN_AUTH_INFO = "login_auth_info";

    // 口令参数
    public final static String LOGIN_AUTH_TOKEN = "user_token";

    // 定位信息文件
    public final static String LOCATION_INFO = "location_info";

    // 城市码
    public final static String LOCATION_CITY_CODE = "cityCode";

    // 城市
    public final static String LOCATION_CITY = "city";

    // 地址
    public final static String LOCATION_ADDRESS = "address";

    // 纬度
    public final static String LOCATION_LATITUDE = "latitude";

    // 经度
    public final static String LOCATION_LONGITUDE = "Longitude";

    // 登录信息文件
    public final static String LOGIN_USER_INFO = "login_user_info";

    // 用户名
    public final static String LOGIN_USER_NAME = "user_name";

    // 流量余额
    public final static String LOGIN_USER_BALANCE = "user_balance";

    // 用户头像
    public final static String LOGIN_USER_LOGO = "user_logo";

    // 签到信息
    public final static String LOGIN_USER_MARK = "user_mark";

    // 邀请码
    public final static String LOGIN_USER_INVCODE = "user_invcode";

    // 签到后获取流量
    public final static String LOGIN_USER_SIGN_TODAY = "user_signtoday";

    // 生日
    public final static String LOGIN_USER_BRITHDAY = "user_birthdate";

    // 手机
    public final static String LOGIN_USER_MOBILE = "user_mobile";

    // 职业
    public final static String LOGIN_USER_JOB = "user_career";

    // 收入
    public final static String LOGIN_USER_INCOMING = "user_incoming";

    // 爱好
    public final static String LOGIN_USER_FAVS = "user_favs";

    // 区域
    public final static String LOGIN_USER_AREA = "user_area";

    // 注册时间
    public final static String LOGIN_USER_REG_DATE = "user_regDate";

    // 欢迎标识
    public final static String LOGIN_USER_WELCOME_TIP = "user_welcometip";

    // 绑定手机
    public final static String LOGIN_USER_INVALID_CODE = "user_invalidcode";

    // 性别
    public final static String LOGIN_USER_SEX = "user_sex";

    // 用户姓名
    public final static String LOGIN_USER_ACCOUNT = "account_name";

    // 推送信息本地别名
    public final static String PUSH_INFO = "push_info";

    // 推送信息本地别名
    public final static String PUSH_INFO_ALIAS = "push_info_alias";
    
    //开启推送消息
    public final static String PUSH_ENABLE = "push_enable";

    // 刷新数据方式
    public final static int REFRESH = 0;// 下拉

    public final static int LOAD_MORE = 1;// 下拉

    // 短信获取方式:文本
    public final static String SMS_TYPE_TEXT = "0";

    // 短信获取方式:语音
    public final static String SMS_TYPE_VOICE = "1";

    // 广播
    // 注册时获取语音
    //public final static String GET_VOICE_REGISTER = "cy.com.morefan.voice.register";

    // 忘记密码时获取语音
    public final static String GET_VOICE_FORGET = "cy.com.morefan.voice.forget";

    // 绑定手机时获取语音
    public final static String GET_VOICE_BINING = "cy.com.morefan.voice.binding";

    // 判断是否是第一次登录
    public final static String GUIDE_INFO = "guide_info";

    // 引导标识
    public final static String GUIDE_INFO_TAG = "guide_info_tag";

    // 设置提醒任务的编号文件
    public final static String TASK_ALARM_INFO = "task_info_alarm";

    // 设置提醒任务的编号
    public final static String TASK_ALARM_INFO_ID = "task_info_alarm_id";

    // token过期返回码
    public final static int TOKEN_OVERDUE = 56001;

    // 精确到一位
    public final static String ACCURACY_1 = ".0";

    // 精确到三位
    public final static String ACCURACY_3 = ".000";

    // 初始化头像连接
    //public final static String LOGO_DEFAULT = "http://hottech.gitcafe.io/resources/fanmoreicon/images/mrtou_h.png";
    
    //系统消息
    public static int MESSAGE_TYPE_SYSTEMMESSAGE=2; 
    //任务消息
    public static int MESSAGE_TYPE_TASK=1;

}
