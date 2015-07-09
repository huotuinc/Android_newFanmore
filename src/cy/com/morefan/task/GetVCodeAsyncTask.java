package cy.com.morefan.task;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.bean.FMGetVC;
import cy.com.morefan.bean.FMGetVC.InnerClass;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.KJLoger;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class GetVCodeAsyncTask extends AsyncTask<String, Void, FMGetVC>
{
    public static int GetVCode_Code=1;
    Handler mHandler=null;
    
    public GetVCodeAsyncTask(Handler handler){
        mHandler = handler;
    }

    @Override
    protected void onPreExecute()
    {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected FMGetVC doInBackground(String... params)
    {
        JSONUtil<FMGetVC> jsonUtil = new JSONUtil<FMGetVC>();
        FMGetVC getVC = new FMGetVC();

        String url = params[0];
        String json = HttpUtil.getInstance().doGet(url);
        //KJLoger.i("注册获取验证码", json);
        try
        {
            getVC = jsonUtil.toBean(json, getVC);
        } catch (JsonSyntaxException e)
        {
            LogUtil.e("JSON_ERROR", e.getMessage());
            getVC.setResultCode(0);
            getVC.setResultDescription("解析json出错");
        }
        return getVC;
    }

    @Override
    protected void onPostExecute(FMGetVC result)
    {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        
        Message msg = mHandler.obtainMessage( GetVCode_Code );
        msg.obj = result;
        mHandler.sendMessage(msg);        
    }

}
