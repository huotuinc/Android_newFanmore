package cy.com.morefan.task;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.bean.FMGetVC;
import cy.com.morefan.bean.FMGetVC.InnerClass;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.KJLoger;

/**
 * 
 * @类名称：GetVCAsyncTask
 * @类描述：异步请求验证码
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年5月27日 下午7:00:59
 * @修改备注：
 * @version:由于无电话卡测试，HttpCode模拟返回验证码
 */
public class GetVCAsyncTask extends AsyncTask<String, Void, FMGetVC>
{

    private String type;
    private Context context;
    public GetVCAsyncTask(Context context, String type)
    {
        // TODO Auto-generated constructor stub
        this.type = type;
        this.context = context;
    }
    @Override
    protected FMGetVC doInBackground(String... params)
    {
        JSONUtil<FMGetVC> jsonUtil = new JSONUtil<FMGetVC>();
        FMGetVC getVC = new FMGetVC();
        // TODO Auto-generated method stub
        if(Constant.IS_PRODUCTION_ENVIRONMENT)
        {
            String url = params[0];
            String json = HttpUtil.getInstance().doGet(url);
            KJLoger.i("注册获取验证码", json);
            try
            {
                getVC = jsonUtil.toBean(json, getVC);
            }
            catch(JsonSyntaxException e)
            {
               LogUtil.e("JSON_ERROR", e.getMessage());
               getVC.setResultCode(0);
               getVC.setResultDescription("解析json出错");
            }
            //baseCode.setResultData("2023");
        }
        else
        {
            getVC.setResultCode(1);
            InnerClass inner = getVC.new InnerClass();
            inner.setVoiceAble(true);
            getVC.setResultData(inner);
        }
        return getVC;
    }

    @Override
    protected void onPostExecute(FMGetVC result)
    {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        if(1 == result.getResultCode())
        {
            
            KJLoger.i("注册获取验证码", "验证码获取成功，请等待");
            //判断服务端是否接受语音短信支持
            InnerClass inner = result.getResultData();
            boolean supportV = inner.isVoiceAble();
            Intent voiceI = null;
            PendingIntent voicePI = null;
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if(supportV)
            {
                //判断获取验证码的类型
                if(Constant.GET_VD_TYPE_REG.equals(type))
                {
                    //给注册界面广播，改变按钮的样式，提示可以尝试语音短信
                    
                    // 如果未设置闹钟
                    voiceI = new Intent(MyBroadcastReceiver.GET_VOICE_REGISTER);
                    voicePI = PendingIntent.getBroadcast(context, 0, voiceI, PendingIntent.FLAG_CANCEL_CURRENT);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.SECOND, 60);
                    alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), voicePI);
                }
                else if(Constant.GET_VD_TYPE_FORGET.equals(type))
                {
                    //给忘记密码界面广播，改变按钮的样式，提示可以尝试语音短信
                    // 如果未设置闹钟
                    voiceI = new Intent(Constant.GET_VOICE_FORGET);
                    voicePI = PendingIntent.getBroadcast(context, 0, voiceI, PendingIntent.FLAG_CANCEL_CURRENT);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.SECOND, 60);
                    alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), voicePI);
                    
                }
                else if(Constant.GET_VD_TYPE_BINDLE.equals(type))
                {
                    //给绑定手机界面广播，改变按钮的样式，提示可以尝试语音短信
                    // 如果未设置闹钟
                    voiceI = new Intent(Constant.GET_VOICE_BINING);
                    voicePI = PendingIntent.getBroadcast(context, 0, voiceI, PendingIntent.FLAG_CANCEL_CURRENT);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.SECOND, 60);
                    alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), voicePI);
                }
            }
            else
            {
                //无语音，无提示
                
            }
        }
        else
        {
          //判断服务端是否接受语音短信支持
            InnerClass inner = result.getResultData();
            boolean supportV = inner.isVoiceAble();
            Intent voiceI = null;
            PendingIntent voicePI = null;
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if(supportV)
            {
                //判断获取验证码的类型
                if(Constant.GET_VD_TYPE_REG.equals(type))
                {
                    //给注册界面广播，改变按钮的样式，提示可以尝试语音短信
                    
                    // 如果未设置闹钟
                    voiceI = new Intent(MyBroadcastReceiver.GET_VOICE_REGISTER);
                    voicePI = PendingIntent.getBroadcast(context, 0, voiceI, PendingIntent.FLAG_CANCEL_CURRENT);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.SECOND, 60);
                    alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), voicePI);
                }
                else if(Constant.GET_VD_TYPE_FORGET.equals(type))
                {
                    //给忘记密码界面广播，改变按钮的样式，提示可以尝试语音短信
                    // 如果未设置闹钟
                    voiceI = new Intent(Constant.GET_VOICE_FORGET);
                    voicePI = PendingIntent.getBroadcast(context, 0, voiceI, PendingIntent.FLAG_CANCEL_CURRENT);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.SECOND, 60);
                    alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), voicePI);
                    
                }
                else if(Constant.GET_VD_TYPE_BINDLE.equals(type))
                {
                    //给绑定手机界面广播，改变按钮的样式，提示可以尝试语音短信
                    // 如果未设置闹钟
                    voiceI = new Intent(Constant.GET_VOICE_BINING);
                    voicePI = PendingIntent.getBroadcast(context, 0, voiceI, PendingIntent.FLAG_CANCEL_CURRENT);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.SECOND, 60);
                    alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), voicePI);
                }
            }
            else
            {
                //无语音，无提示
                
            }
        }
    }
    
    

}

