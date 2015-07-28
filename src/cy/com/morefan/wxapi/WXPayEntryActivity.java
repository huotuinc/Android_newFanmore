package cy.com.morefan.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import cy.com.morefan.R;
import cy.com.morefan.bean.FMDeliveryGood;
import cy.com.morefan.bean.PayGoodBean;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.task.DeliveryGoodAsyncTask;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.LoadingUtil;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.util.WXPayUtilEx;

public class WXPayEntryActivity extends Activity implements Handler.Callback, IWXAPIEventHandler{
	
	private static final String TAG = "WXPayEntryActivity";
	public static String WXPAYAPPID= WXPayUtilEx.wxpayAppId;
	private Handler handler= new Handler(this);
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this,  WXPAYAPPID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			String msg = "";
		    if( resp.errCode== 0)
			{
		        msg="支付成功";
			}else if( resp.errCode== -1){
			    msg="支付失败";
			    this.finish();
				ToastUtils.showLongToast(WXPayEntryActivity.this, msg);
				return;
			}else if(resp.errCode ==-2){
			    msg="用户取消支付";
			    this.finish();
				ToastUtils.showLongToast(WXPayEntryActivity.this, msg);
				return;
			}

			PayResp payResp = (PayResp)resp;
			if(null==payResp){
				Log.i("wxpay>>>payResp=null","");
				msg="支付失败";
				ToastUtils.showLongToast(WXPayEntryActivity.this, msg);
				this.finish();
				return;
			}else{
				Log.i("wxpay>>>extData", payResp.extData==null? "": payResp.extData );
				//Log.i("wxpay>>>prepayid",payResp.prepayId);
			}

			PayGoodBean para=new PayGoodBean();
			JSONUtil<PayGoodBean> jsonUtil=new JSONUtil<>();
			para = jsonUtil.toBean( payResp.extData, para);

			new DeliveryGoodAsyncTask( WXPayEntryActivity.this , handler ,  para.getOrderNo(),para.getProductType(), para.getProductId() ).execute();
		}
	}

	@Override
	public boolean handleMessage(Message msg) {

		switch ( msg.what){
			case DeliveryGoodAsyncTask.PAY_ERROR:
			{
				ToastUtils.showLongToast(this, msg.obj.toString());
				this.finish();
			}
			break;
			case DeliveryGoodAsyncTask.PAY_OK:
			{
				ToastUtils.showLongToast(this, msg.obj.toString());
				MyBroadcastReceiver.sendBroadcast(this,MyBroadcastReceiver.ACTION_FLOW_ADD);
				MyBroadcastReceiver.sendBroadcast(this,MyBroadcastReceiver.ACTION_WX_PAY_CALLBACK);
				this.finish();
			}
			break;
		}


		return false;
	}

//	class DDDeliverGoodAsyncTask extends AsyncTask<Void,Void, FMDeliveryGood>{
//		String orderNo;
//		int productType;
//		long productId;
//		Context context;
//		LoadingUtil util;
//
//		public DDDeliverGoodAsyncTask( Context context , String orderNo , int productType , long productId){
//			this.context=context;
//			this.orderNo=orderNo;
//			this.productType = productType;
//			this.productId=productId;
//			this.util=new LoadingUtil((Activity)this.context);
//		}
//
//		@Override
//		protected FMDeliveryGood doInBackground(Void... params) {
//			FMDeliveryGood result=null;
//			try {
//				String url = Constant.DELIVERGOOD;
//
//				ObtainParamsMap obtainMap = new ObtainParamsMap(context);
//				Map<String, String> signMap = obtainMap.obtainMap();
//				signMap.put("orderNo", orderNo);
//				signMap.put("productType", String.valueOf( productType));
//				signMap.put("productId", String.valueOf( productId));
//				String sign = obtainMap.getSign(signMap);
//
//				signMap.put("sign", sign);
//
//				String responseStr = HttpUtil.getInstance().doPost(url , signMap);
//
//				Log.i("deliverygood", responseStr);
//
//
//				JSONUtil<FMDeliveryGood> jsonutil=new JSONUtil<FMDeliveryGood>();
//				result = new FMDeliveryGood();
//				result = jsonutil.toBean(responseStr, result);
//				return result;
//			}
//			catch (JsonSyntaxException jsex){
//				LogUtil.e("JSON_ERROR", jsex.getMessage());
//				result=new FMDeliveryGood();
//				result.setResultCode(0);
//				result.setResultDescription("解析json出错");
//				return result;
//			}
//		}
//
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//
//			util.showProgress();
//		}
//
//		@Override
//		protected void onPostExecute(FMDeliveryGood fmDeliveryGood) {
//			super.onPostExecute(fmDeliveryGood);
//
//			util.dismissProgress();
//
//			if( fmDeliveryGood==null){
//				ToastUtils.showLongToast(this.context ,"请求失败");
//				((Activity)this.context).finish();
//				return;
//			}
//
//			if( fmDeliveryGood.getSystemResultCode()!=1){
//				ToastUtils.showLongToast( this.context , fmDeliveryGood.getSystemResultDescription());
//				((Activity)this.context).finish();
//				return;
//			}
//			if(fmDeliveryGood.getResultCode()!=1){
//				ToastUtils.showLongToast( this.context , fmDeliveryGood.getResultDescription());
//				((Activity)this.context).finish();
//				return;
//			}
//
//			ToastUtils.showLongToast( this.context , "支付成功");
//			((Activity)this.context).finish();
//		}
//	}
}