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
import cy.com.morefan.bean.BaseBaseBean;
import cy.com.morefan.bean.FMDeleteRequestFC;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.HttpUtil;
import android.os.Message;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;

/**
 * Created by Administrator on 2015/7/30.
 */
public class DeleteRequestFCAsyncTask extends AsyncTask<Void, Void, FMDeleteRequestFC> {
    Context context;
    Handler handler;
    Integer infoId;
    public static final int SUCCESS=4002;
    public static final int FAIL=4003;

    public DeleteRequestFCAsyncTask( Context context,Handler handler, Integer infoId){
        this.context=context;
        this.handler=handler;
        this.infoId=infoId;
    }

    protected FMDeleteRequestFC doInBackground(Void... params) {

        String url = Constant.DELETEREQUEST;
        ObtainParamsMap obtainMap = new ObtainParamsMap(context);
        String paraString = obtainMap.getMap();
        Map<String, String> signMap = new HashMap<String, String>();
        signMap.put("infoId",String.valueOf( infoId));

        String sign = obtainMap.getSign(signMap);
        try

        {
            url += "?sign=" + URLEncoder.encode(sign, "UTF-8") + paraString+"&infoId="+ URLEncoder.encode(String.valueOf(infoId), "UTF-8");


        } catch (
                UnsupportedEncodingException e
                )

        {
            e.printStackTrace();
        }

        String responseStr = HttpUtil.getInstance().doGet(url);
        JSONUtil<FMDeleteRequestFC> jsonUtil = new JSONUtil<FMDeleteRequestFC>();
        FMDeleteRequestFC result = new FMDeleteRequestFC();
        try

        {
            result = jsonUtil.toBean(responseStr, result);
        } catch (
                JsonSyntaxException e
                )

        {
            LogUtil.e("JSON_ERROR", e.getMessage());
            result.setResultCode(0);
            result.setResultDescription("解析json出错");
        }

        return result;
    }


    protected void onPreExecute() {
        super.onPreExecute();

        ((BaseActivity) context).showProgress();
    }

    @Override
    protected void onPostExecute(FMDeleteRequestFC result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        ((BaseActivity) context).dismissProgress();

        if( result==null){
            //ToastUtils.showLongToast(context, "请求失败");
            Message msg=handler.obtainMessage(FAIL);
            msg.obj="请求失败";
            handler.sendMessage(msg);
            return;
        }
        if( result.getSystemResultCode() != 1){
            //ToastUtils.showLongToast(context, result.getSystemResultDescription());
            Message msg=handler.obtainMessage(FAIL);
            msg.obj=result.getSystemResultDescription();
            handler.sendMessage(msg);
            return;
        }
        if( Constant.TOKEN_OVERDUE == result.getResultCode()){
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
        if( 1!= result.getResultCode()){
            //ToastUtils.showLongToast( context , result.getResultDescription());
            Message msg=handler.obtainMessage(FAIL);
            msg.obj=result.getResultDescription();
            handler.sendMessage(msg);
            return;
        }

        android.os.Message msg = handler.obtainMessage(SUCCESS);
        handler.sendMessage(msg);

    }

}


