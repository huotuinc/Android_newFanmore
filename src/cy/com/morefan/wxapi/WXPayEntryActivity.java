package cy.com.morefan.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cy.com.morefan.R;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.util.ToastUtils;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "WXPayEntryActivity";
	public static String WXPAYAPPID="";
	
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
			}else if(resp.errCode ==-2){
			    msg="用户取消支付";
			    this.finish();
			}
		    
		    //AlertDialog.Builder builder = new AlertDialog.Builder(this);
			//builder.setTitle("提示");
			//String msg = String.format( "微信支付结果：%s" , resp.errCode);
			//builder.setMessage( msg );
			//builder.show();
		    ToastUtils.showLongToast(this, msg);
			
		}
	}
}