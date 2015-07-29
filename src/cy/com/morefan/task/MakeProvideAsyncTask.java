package cy.com.morefan.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

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


    public MakeProvideAsyncTask(Context context , String mobile , String flow,String message){
        this.context=context;
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
            ToastUtils.showLongToast(context, "请求失败");
            return;
        }
        if( fmMakeProvide.getSystemResultCode() != 1){
            ToastUtils.showLongToast(context, fmMakeProvide.getSystemResultDescription());
            return;
        }
        if( Constant.TOKEN_OVERDUE == fmMakeProvide.getResultCode()){
            // 提示账号异地登陆，强制用户退出
            // 并跳转到登录界面
            ToastUtils.showLongToast(context, "账户登录过期，请重新登录");
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable()
            {

                @Override
                public void run()
                {
                    // TODO Auto-generated method stub
                    ActivityUtils.getInstance().loginOutInActivity(
                            (Activity) context);
                }
            }, 2000);
            return;
        }
        if( 1!= fmMakeProvide.getResultCode()){
            ToastUtils.showLongToast( context , fmMakeProvide.getResultDescription());
            return;
        }

        MyBroadcastReceiver.sendBroadcast(context, MyBroadcastReceiver.ACTION_FLOW_ADD);
        ToastUtils.showLongToast(context ,"赠送流量成功");
    }
}

