package cy.com.morefan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
import cy.com.morefan.MyApplication;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";
	public MyApplication application;
	private String imei = null;

	@Override
	public void onReceive(final Context context, Intent intent) {
        
	    Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            //注册后获取极光全局ID
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            imei = MyApplication.getPhoneIMEI(context);
            // 设置别名
            /*new AsyncTask<Void, Void, BaseBaseBean>()
            {
                String url = null;
                @Override
                protected BaseBaseBean doInBackground(Void... params)
                {
                    // TODO Auto-generated method stub
                    BaseBaseBean aliasBean = new BaseBaseBean();
                    JSONUtil<BaseBaseBean> jsonUtil = new JSONUtil<BaseBaseBean>();
                    ObtainParamsMap obtainMap = new ObtainParamsMap(context);
                    Map<String, String> paramMap = obtainMap.obtainMap();

                    // 拼接注册url
                    url = Constant.SETUP_ALIAS;
                    // 注册是POST提交
                    paramMap.put("alias", imei);
                    // 封装sign
                    String signStr = obtainMap.getSign(paramMap);
                    paramMap.put("sign", signStr);

                    String jsonStr = HttpUtil.getInstance().doPost(url, paramMap);
                    try
                    {
                        aliasBean = jsonUtil.toBean(jsonStr, aliasBean);
                    } catch (JsonSyntaxException e)
                    {
                        LogUtil.e("JSON_ERROR", e.getMessage());
                        aliasBean.setResultCode(0);
                        aliasBean.setResultDescription("解析json出错");
                    }
                    return aliasBean;
                }

                @Override
                protected void onPreExecute()
                {
                    // TODO Auto-generated method stub
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(BaseBaseBean result)
                {
                    // TODO Auto-generated method stub
                    super.onPostExecute(result);
                    if (1 == result.getResultCode())
                    {
                        // 保存别名到本地
                        JPushInterface.setAliasAndTags(context, imei,
                                null, new TagAliasCallback()
                                {

                                    @Override
                                    public void gotResult(int code, String alias,
                                            Set<String> tags)
                                    {
                                        // TODO Auto-generated method stub
                                        switch (code)
                                        {
                                        case 0:
                                            KJLoger.i("设置别名成功！");
                                            application.writeString(
                                                    LoadingActivity.this,
                                                    Constant.PUSH_INFO,
                                                    Constant.PUSH_INFO_ALIAS, imie);
                                            break;

                                        case 6002:
                                            KJLoger.i("设置别名超时！");
                                            break;

                                        default:
                                            break;
                                        }
                                    }
                                });

                    }
                }
            }.execute();*/
            
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            //处理接受的自定义消息
            
            
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            //接收到了自定义的通知
            //通知ID
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
           
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            //接受点击通知事件
            
            //添加统计
            JPushInterface.reportNotificationOpened(context, bundle.getString(JPushInterface.EXTRA_MSG_ID));
        	
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
            //接受富文本框
            
        	
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	//处理网络变更事件
        	
        } else
        {
            
        }
	}
	
}
