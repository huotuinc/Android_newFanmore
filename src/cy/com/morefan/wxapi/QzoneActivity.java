package cy.com.morefan.wxapi;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.util.ToastUtils;
//import cy.com.morefan.listener.MyBroadcastReceiver;
//import cy.com.morefan.util.SPUtil;

public class QzoneActivity extends BaseActivity implements Callback{
	/**
	 * QZONE appid
	 */
	 public static String APP_ID="101066268";
	private Tencent tencent;
	public enum Qtype{
		QQ, Qzone
	}
	
	Handler myHandler = new Handler(this);
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			tencent = Tencent.createInstance(APP_ID, this);
			String des = bundle.getString("des");
			String imgUrl = bundle.getString("imgUrl");
			String contentUrl = bundle.getString("contentUrl");
			Qtype type = (Qtype) bundle.getSerializable("type");
			
			int imageType= bundle.getInt("imageType", 0);
			
			share(des, imgUrl, contentUrl, type , imageType );
		}
	}
	private void share(String des, String imgUrl, String contentUrl, final Qtype type , int imageType ){
		/**
    	 * Tencent.SHARE_TO_QQ_KEY_TYPE	选填	Int	SHARE_TO_QZONE_TYPE_IMAGE_TEXT（图文）
 		   Tencent.SHARE_TO_QQ_TITLE	必填	Int	分享的标题，最多200个字符
		   Tencent.SHARE_TO_QQ_SUMMARY	选填	String	分享的摘要，最多600字符
		   Tencent.SHARE_TO_QQ_TARGET_URL	必填	String	需要跳转的链接，URL字符串
		   Tencent.SHARE_TO_QQ_IMAGE_URL	选填	String	分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃）。

    	 */
		final Bundle params = new Bundle();
         params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
         params.putString(QzoneShare.SHARE_TO_QQ_TITLE, des);
         //params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, taskData.content);
         params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, contentUrl);
         ArrayList<String> imageUrls = new ArrayList<String>();
         if( null != imgUrl && imgUrl.trim().length()> 0){
             imageUrls.add(imgUrl);
         }
         params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
//         if( imageType == 0 ){
//             params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
//         }else if( imageType == 1 ){
//         }
         
         /**
          * 用异步方式启动分享
          * @param params
          */
         new Thread(new Runnable() {
             @Override
             public void run() {
            	 if(type == Qtype.Qzone){
            		 tencent.shareToQzone(QzoneActivity.this, params, qqListener);
            	 }else{
            		 tencent.shareToQQ(QzoneActivity.this, params, qqListener);
            	 }

             }
         }).start();

	}
	IUiListener qqListener = new IUiListener() {
		@Override
		public void onCancel() {
			//ToastUtils.showLongToast(QzoneActivity.this, "取消分享");
		    //toast("取消分享");
			finish();
		}

		@Override
		public void onError(UiError e) {
		    //ToastUtils.showLongToast(QzoneActivity.this, "onError: " + e.errorMessage);
		    toast(e.errorMessage);
			finish();
		}
		
		private void toast(final String msg){
		    myHandler.post(new Runnable()
            {
                
                @Override
                public void run()
                {
                    // TODO Auto-generated method stub
                    ToastUtils.showLongToast(QzoneActivity.this, "onError: " + msg);
                }
            });
		}

		@Override
		public void onComplete(Object response) {
			// 正常情况下返回码为0
		    //ToastUtils.showLongToast(QzoneActivity.this, "分享成功");
		    //toast("分享成功");
		    
			 MyBroadcastReceiver.sendBroadcast(QzoneActivity.this, MyBroadcastReceiver.ACTION_SHARE_TO_QZONE_SUCCESS);
		
			 setResult(2);
			 
			 finish();
//			userService.commitSend(taskData.id,UserData.getUserData().loginCode, ShareUtil.CHANNEL_QZONE);
//			showProgress();
		}
	};
    @Override
    public boolean handleMessage(Message msg)
    {
        // TODO Auto-generated method stub
        return false;
    }


}
