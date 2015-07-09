package cy.com.morefan.wxapi;

import java.io.File;




//import cy.com.morefan.util.Util;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.LoadingActivity;
import cy.com.morefan.R;
//import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.util.L;
import cy.com.morefan.util.LruImageCache;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.util.Util;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler{
	// APP_ID 替换为你的应用从官方网站申请到的合法appId
	public static String WX_APP_ID = "wx8ba33b7341047b58";//  "wx8ba33b7341047b58";

	private static final int VERSION_602 = 0x22010003;

	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	// IWXAPI 是第三方app和微信通信的openapi接口
	private static IWXAPI api;

	private boolean clickShare;

	/**
	 * 发送到会话
	 * 发送朋友圈
	 *
	 */
	public enum WXtype{
		Conversation, Moments
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.i(">>>>>>>>>onCreate" );
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		api = WXAPIFactory.createWXAPI(this, WX_APP_ID, true);
		boolean isRegister = api.registerApp(WXEntryActivity.WX_APP_ID);

		clickShare = false;
		
		
		
		
		
		
		//setContentView(R.layout.task_detail);
		
		
		
		
		
		
		api.handleIntent(getIntent(), this);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			boolean isShare = bundle.getBoolean("isShare", false);
			String name = bundle.getString("name");
			//String des = bundle.getString("des");
			String path = bundle.getString("path");
			String url = bundle.getString("url");
			WXtype type = (WXtype) bundle.getSerializable("type");
			//byte[] a = (byte[]) bundle.getSerializable("aa");
			if (isShare)
				share(path, name, url, type);
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		L.i(">>>>>>>>>onResume" );
//		if(clickShare){
//			clickShare = false;
//			toast("分享成功!");
//			//result = R.string.errcode_success;
//			MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_SHARE_TO_WEIXIN_SUCCESS);
//			finish();
//		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		api.handleIntent(intent, this);
		super.onNewIntent(intent);
	}


//	@Override
//	protected void onBack() {
//		finish();
//		super.onBack();
//	}
	public boolean checkWeiXin(WXtype tyXtype){
		if(!api.isWXAppInstalled()){
			ToastUtils.showLongToast(this,"请先安装微信!");
			return false;
		}
		int wxSdkVersion = api.getWXAppSupportAPI();
		if (tyXtype == WXtype.Moments && wxSdkVersion < TIMELINE_SUPPORTED_VERSION) {
			ToastUtils.showLongToast(this,"您的微信版本暂不支持分享朋友圈!");
			return false;
		}

		return true;
	}
	public boolean canBack(){
//		try {
//			PackageInfo info = getPackageManager().getPackageInfo("com.tencent.mm", 0);
//			L.i(">>>>wxSdkVersion:"+BusinessStatic.getInstance().WEIXIN_IGNORE_VERSION + ">>"+info.versionCode + "," + info.versionName);
//			if(!TextUtils.isEmpty(BusinessStatic.getInstance().WEIXIN_IGNORE_VERSION) && BusinessStatic.getInstance().WEIXIN_IGNORE_VERSION.contains(String.valueOf(info.versionCode))){
//				L.i(">>>>wxSdkVersion:"+info.versionCode);
//				return false;
//			}
//		} catch (NameNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return true;
	}
	
	public void share(Bitmap bmp , String name , String url , WXtype tyXtype){
	    if(!checkWeiXin(tyXtype)){
            finish();
            return;
        }
	    
	    WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        if(tyXtype == WXtype.Moments){
            msg.title = name;
        }else{
            msg.title = WXEntryActivity.this.getString(R.string.app_name);// "粉猫流量宝";
            //The length should be within 1KB.
            msg.description = name;
        }   
        
        if( null!= bmp ){            
            msg.thumbData = Util.bitmap2Bytes(bmp);  // cy.com.morefan.util.Util.readFromFile(path, 0, (int) (new File(path).length()));
        }
        
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        
        req.scene = tyXtype == WXtype.Moments ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
        
        if(!canBack()){
            L.i(">>>>>>>shareBack");
            //toast("分享成功!");
            //result = R.string.errcode_success;
            MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_WX_NOT_BACK);
            //clickShare = true;
        }
        setResult(2);
        finish();        
	}

	public void share(String path, String name, String url, WXtype tyXtype) {
		if(!checkWeiXin(tyXtype)){
			finish();
			return;
		}

		/**
		 * intentWX.putExtra("title", taskData.taskName);
		 * intentWX.putExtra("des", taskData.des); intentWX.putExtra("img",
		 * fragDes.getTaskImage()); intentWX.putExtra("content",
		 * taskData.content);
		 */
		// Bundle extra = getIntent().getExtras();
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		// IWXAPI api = WXAPIFactory.createWXAPI(this, WXEntryActivity.APP_ID);
		// //先注册，才能分享
		// api.registerApp(WXEntryActivity.APP_ID);
		// 分享网页
		// intentWX.putExtra("title", taskData.taskName);
		// // intentWX.putExtra("des", taskData.des);
		// // intentWX.putExtra("img", fragDes.getTaskImage());
		// // intentWX.putExtra("content", taskData.content);
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = url;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		if(tyXtype == WXtype.Moments){
			msg.title = name;
		}else{
			msg.title = getString(R.string.app_name); //"粉猫流量宝";
			//The length should be within 1KB.
			msg.description = name;
		}
		// taskData.taskName;
		//msg.description = des;// taskData.des;
		// Bitmap thumb = BitmapFactory.decodeResource(getResources(),
		// R.drawable.ic_launcher);
		//限制内容大小不超过32KB
		//check
		
		
		
		
//		boolean isExist;
//		if( path==null) isExist=false;
//		else {
//		    File f=new File(path);
//		    isExist = f.exists();
//		}
//		
//		if( isExist ){		
//		    Util.compressImage(path);
//		    msg.thumbData = cy.com.morefan.util.Util.readFromFile(path, 0,
//				(int) (new File(path).length()));
//		}
		
		
		File file=new File(path);
		boolean isexist = file.exists();
		if( isexist ){
		    msg.thumbData = cy.com.morefan.util.Util.readFromFile(path, 0, (int)file.length() );
		}
		else
		{
		    Bitmap bmp = LruImageCache.instance().getBitmap(path);
		    if( bmp !=null ){
		        msg.thumbData = Util.bitmap2Bytes(bmp);  
		    }
		}		
		
		
		//msg.thumbData = Util.InputStreamToByte(getResources().openRawResource(R.drawable.icon));
//		msg.thumbData = cy.com.morefan.util.Util.readFromFileByWX(path);



		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		// req.scene = isTimelineCb.isChecked() ?
		// SendMessageToWX.Req.WXSceneTimeline :
		// SendMessageToWX.Req.WXSceneSession;
		req.scene = tyXtype == WXtype.Moments ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);

		if(!canBack()){
			L.i(">>>>>>>shareBack");
			//toast("分享成功!");
			//result = R.string.errcode_success;
			MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_WX_NOT_BACK);
			//clickShare = true;
		}
		setResult(2);
		finish();



	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {
		Intent intent = new Intent(this, LoadingActivity.class);
		startActivity(intent);
		finish();

	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		//int result = 0;
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			//ToastUtils.showLongToast( this , "分享成功!");
			//result = R.string.errcode_success;
			MyBroadcastReceiver.sendBroadcast(this,
					MyBroadcastReceiver.ACTION_SHARE_TO_WEIXIN_SUCCESS);
			finish();
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			//result = R.string.errcode_cancel;
			//ToastUtils.showLongToast(this , "分享取消!");
			finish();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			//result = R.string.errcode_deny;
		    //ToastUtils.showLongToast(this, "授权失败!");
			break;
		default:
			//result = R.string.errcode_unknown;
		    ToastUtils.showLongToast(this, "未知错误!");
			break;
		}

	}


}
