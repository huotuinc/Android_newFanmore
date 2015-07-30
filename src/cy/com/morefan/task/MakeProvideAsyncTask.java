package cy.com.morefan.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.bean.FMMakeProvide;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;

/**
 * Created by Administrator on 2015/7/29.
 */
public class MakeProvideAsyncTask extends AsyncTask<Void,Void, FMMakeProvide> {
    String mobile;
    Context context;
    String flow;
    String message;
    Handler handler;
    public static final int SUCCESS=5052;
    public static final int FAIL=5053;


    public MakeProvideAsyncTask(Context context ,Handler handler , String mobile , String flow,String message){
        this.context=context;
        this.handler=handler;
        this.mobile=mobile;
        this.flow=flow;
        this.message=message;
    }

    @Override
    protected FMMakeProvide doInBackground(Void... params) {
        FMMakeProvide result=null;
        try {
            String url = Constant.MAKEPROVIDE;

            ObtainParamsMap obtainMap = new ObtainParamsMap(context);
            String paramString = obtainMap.getMap();
            Map<String, String> signMap = new HashMap<>();
            signMap.put("originMobile", mobile);
            signMap.put("message", message);
            signMap.put("fc", flow);
            String sign = obtainMap.getSign(signMap);
            url +="?originMobile="+ URLEncoder.encode(mobile, "UTF-8");
            url +="&message="+URLEncoder.encode(message,"UTF-8");
            url +="&fc="+URLEncoder.encode(flow,"UTF-8");
            url += paramString;
            url +="&sign=" + URLEncoder.encode(sign, "UTF-8");

            String responseStr = HttpUtil.getInstance().doGet(url);
            result = new FMMakeProvide();
            JSONUtil<FMMakeProvide> jsonUtil = new JSONUtil<>();
            result= jsonUtil.toBean(responseStr , result );
            return result;
        }
        catch (JsonSyntaxException e)
        {
            LogUtil.e("JSON_ERROR", e.getMessage());
            result= new FMMakeProvide();
            result.setResultCode(0);
            result.setResultDescription("解析json出错");
            return result;
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ((BaseActivity) context).showProgress("正在赠送流量...");
    }

    @Override
    protected void onPostExecute(FMMakeProvide fmMakeProvide) {
        super.onPostExecute(fmMakeProvide);
        ((BaseActivity)context).dismissProgress();

        if( fmMakeProvide==null){
            //ToastUtils.showLongToast(context, "请求失败");
            Message msg = handler.obtainMessage( FAIL);
            msg.obj="请求失败";
            handler.sendMessage(msg);
            return;
        }
        if( fmMakeProvide.getSystemResultCode() != 1){
            //ToastUtils.showLongToast(context, fmMakeProvide.getSystemResultDescription());
            Message msg = handler.obtainMessage( FAIL);
            msg.obj=fmMakeProvide.getSystemResultDescription();
            handler.sendMessage(msg);
            return;
        }
        if( Constant.TOKEN_OVERDUE == fmMakeProvide.getResultCode()){
            // 提示账号异地登陆，强制用户退出
            // 并跳转到登录界面
            ToastUtils.showLongToast(context, "账户登录过期，请重新登录");
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    ActivityUtils.getInstance().loginOutInActivity(
                            (Activity) context);
                }
            }, 2000);
            return;
        }
        if( 1!= fmMakeProvide.getResultCode()){
            //ToastUtils.showLongToast(context, fmMakeProvide.getResultDescription());
            Message msg = handler.obtainMessage( FAIL);
            msg.obj=fmMakeProvide.getResultDescription();
            handler.sendMessage(msg);
            return;
        }

        MyBroadcastReceiver.sendBroadcast(context, MyBroadcastReceiver.ACTION_FLOW_ADD);

        Message msg = handler.obtainMessage( SUCCESS);
        msg.obj="赠送流量成功";
        handler.sendMessage(msg);

        if( null != fmMakeProvide.getResultData() && null != fmMakeProvide.getResultData().getSmsContent() && fmMakeProvide.getResultData().getSmsContent().length()>0 ){
            sendFlowBySms( mobile , fmMakeProvide.getResultData().getSmsContent() );
        }else {
            //ToastUtils.showLongToast(context, "赠送流量成功");
        }
    }


    /**
     * 送给非粉猫用户，则发送短信通知
     */
    private void sendFlowBySms( String mobile , String smsContext ){

        Uri smsToUri = Uri.parse("smsto:"+ mobile );

        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);

        intent.putExtra("sms_body", smsContext);

        context.startActivity(intent);
    }
}

