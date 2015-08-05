package cy.com.morefan.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import java.util.Map;

import cy.com.morefan.bean.FMDeliveryGood;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.LoadingUtil;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;

/**
 *
 * @类名称：DeliveryGoodAsyncTask
 * @类描述：发货接口
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年5月27日 下午7:00:59
 * @修改备注：
 * @version:
 */
public class DeliveryGoodAsyncTask extends AsyncTask<Void,Void, FMDeliveryGood> {
    String orderNo;
    int productType;
    long productId;
    Context context;
    Handler handler;
    LoadingUtil util;
    public static final int PAY_ERROR=3003;
    public static final int PAY_OK=3004;

    public DeliveryGoodAsyncTask( Context context , Handler handler, String orderNo , int productType , long productId){
        this.context=context;
        this.orderNo=orderNo;
        this.productType = productType;
        this.productId=productId;
        this.handler=handler;
        this.util=new LoadingUtil((Activity)this.context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        util.showProgress();
    }

    @Override
    protected FMDeliveryGood doInBackground(Void... params) {
        FMDeliveryGood result=null;
        try {
            String url = Constant.DELIVERGOOD;

            ObtainParamsMap obtainMap = new ObtainParamsMap(context);
            Map<String, String> signMap = obtainMap.obtainMap();
            signMap.put("orderNo", orderNo);
            signMap.put("productType", String.valueOf( productType));
            signMap.put("productId", String.valueOf( productId));
            String sign = obtainMap.getSign(signMap);
            signMap.put("sign", sign);

            String responseStr = HttpUtil.getInstance().doPost(url , signMap);

            Log.i("deliverygood", responseStr);

            JSONUtil<FMDeliveryGood> jsonutil=new JSONUtil<FMDeliveryGood>();
            result = new FMDeliveryGood();
            result = jsonutil.toBean(responseStr, result);
            return result;
        }
        catch (JsonSyntaxException jsex){
            LogUtil.e("JSON_ERROR", jsex.getMessage());
            result=new FMDeliveryGood();
            result.setResultCode(0);
            result.setResultDescription("解析json出错");
            return result;
        }
    }

    @Override
    protected void onPostExecute(FMDeliveryGood fmDeliveryGood) {
        super.onPostExecute(fmDeliveryGood);

        util.dismissProgress();

        if( fmDeliveryGood==null){
            Message msg= handler.obtainMessage();
            msg.what= PAY_ERROR;
            msg.obj = "请求失败";
            handler.sendMessage(msg);
            return;
        }

        if( fmDeliveryGood.getSystemResultCode()!=1){
            Message msg= handler.obtainMessage();
            msg.what=PAY_ERROR;
            msg.obj= fmDeliveryGood.getSystemResultDescription();
            handler.sendMessage(msg);
            return;
        }
        if(fmDeliveryGood.getResultCode()!=1){
            Message msg= handler.obtainMessage();
            msg.what=PAY_ERROR;
            msg.obj= fmDeliveryGood.getResultDescription();
            handler.sendMessage(msg);
            return;
        }

        Message msg= handler.obtainMessage();
        msg.what=PAY_OK;
        msg.obj = "支付成功";
        handler.sendMessage(msg);
    }

}
