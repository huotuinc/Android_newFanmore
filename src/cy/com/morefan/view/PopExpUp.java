package cy.com.morefan.view;

import cy.com.morefan.R;
import cy.com.morefan.util.SoundUtil;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class PopExpUp{

	private View mainView;
	private Context mContext;
	private TextView txtExp;
	public PopExpUp(Context context){
		this.mContext = context;
		initView(context);
	}
	private Dialog dialog;
	//private Shake shake;
	private SoundUtil soundUtil;
	
	private void initView(Context context) {
		if(dialog == null){
			mainView = LayoutInflater.from(context).inflate(R.layout.pop_exp_up, null);
			dialog = new Dialog(context, R.style.PopDialog);
			dialog.setContentView(mainView);
			 Window window = dialog.getWindow();
			window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置
			 window.setWindowAnimations(R.style.AnimationPopTranslucent);  //添加动画

//			 //设置视图充满屏幕宽度
//			 WindowManager.LayoutParams lp = window.getAttributes();
//			 lp.width = (int)(DensityUtil.getSize(mContext)[0]); //设置宽度
//			 window.setAttributes(lp);
		}

		txtExp = (TextView) mainView.findViewById(R.id.txtExp);
		//shake = new Shake(context, null);
		soundUtil = new SoundUtil(context);
		mainView.setFocusableInTouchMode(true);
		mainView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK ){
//					 if(listener != null)
//						 listener.onCheckInBack(0);
					dialog.dismiss();
				}
				return false;
			}
		});


	}
	public void show(String exp , int soundId){
		
		txtExp.setText(exp);
		dialog.show();
		//shake.shakeSound(R.raw.exp_up);
		if( soundId>0){
		soundUtil.shakeSound(soundId);
		}
		
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				dialog.dismiss();

			}
		}, 1000);
	}
	
	public void sound(int resid){
	    if( soundUtil==null){
	        soundUtil=new SoundUtil(mContext);
	    }
	    soundUtil.shakeSound(resid);
	}
	
	public void show(String exp ){
	    show(exp, 0);
	}

	public void close(){
	    if( soundUtil!=null){
	        soundUtil.Release();
	    }
	}
	
	private Handler mHandler = new Handler();

}
