package cy.com.morefan;

import cn.jpush.android.api.JPushInterface;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.umeng.analytics.MobclickAgent;

import cy.com.morefan.ui.user.ForgetActivity;
import cy.com.morefan.ui.user.LoginActivity;
import cy.com.morefan.ui.user.RegisterActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.SystemBarTintManager;
import cy.com.morefan.view.WindowProgress;

public class BaseActivity extends FragmentActivity{
	protected static final String NULL_NETWORK = "无网络或当前网络不可用!";

    public MyApplication application;

	private WindowProgress progress;

	protected Handler handler = new Handler();

	@Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        
        application = (MyApplication) BaseActivity.this.getApplication();
        //禁止横屏
        BaseActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    @Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		//设置应用支持沉浸模式
		/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
		}*/
	}
	@TargetApi(19)
	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);


		SystemBarTintManager mTintManager = new SystemBarTintManager(this);
		mTintManager.setStatusBarTintEnabled(true);
		mTintManager.setNavigationBarTintEnabled(true);
		mTintManager.setTintColor(getResources().getColor(R.color.theme_color_title_transport));

		//将调整系统窗口布局以适应你自定义的布局
		ViewGroup rootView = (ViewGroup) getRootView(this);
		rootView.setFitsSystemWindows(true);
		
	}

	@Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();
        JPushInterface.onPause(BaseActivity.this);

		MobclickAgent.onPause(this);
    }
	
    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        JPushInterface.onResume(BaseActivity.this);

		MobclickAgent.onResume(this);
    }
    
	private static View getRootView(Activity context)
    {
        return ((ViewGroup)context.findViewById(android.R.id.content)).getChildAt(0);
    }
	
	/**
	 * 
	 *@方法描述：系统统一登录方法
	 *@方法名：userLogin
	 *@参数：
	 *@返回：void
	 *@exception 
	 *@since
	 */
	public void userLogin(){
	    ActivityUtils.getInstance().showActivity(BaseActivity.this, LoginActivity.class);
	    
	}
	
	public void userRegister()
	{
	    ActivityUtils.getInstance().skipActivity(BaseActivity.this, RegisterActivity.class);
	}
	
	public void userForget()
	{
	    ActivityUtils.getInstance().showActivity(BaseActivity.this, ForgetActivity.class);
	}
	
	public void closeSelf(Activity aty)
	{
	    aty.finish();
	}
	
	/**
	 *
	 *@方法描述：判断是否登录，并更新application中的标志位
	 *@方法名：isLogin
	 *@参数：@return
	 *@返回：boolean
	 *@exception 
	 *@since
	 */
	public boolean isLogin()
	{
	    /**
	     * 判断是否登录的顺序：1、先判断本地token:
	     * token存在则强制认为已登录，同时强制设置isLogin为true
	     * 
	     */
	    String localToken = MyApplication.readLocalToken(BaseActivity.this);
	    if(!"".equals(localToken) && null != localToken)
	    {
	        return application.isLogin = true;
	    }
	    else
	    {
	        //如果本地不存在token，强制认为没登陆或者已经退出登录。
	        return application.isLogin = false;
        }    
	    
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            //finish自身
            BaseActivity.this.finish();
            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

	public void showProgress(){
		showProgress("正在努力加载中...");
	}

	public void showProgress(final String msg) {
		//网络访问前先检测网络是否可用
		if(!Util.isConnect(BaseActivity.this)){
			ToastUtils.showLongToast(this , NULL_NETWORK);
			return;
		}

		if(progress == null){
			progress = new WindowProgress(this);
		}

		handler.post(new Runnable() {
			@Override
			public void run() {
				if(!BaseActivity.this.isFinishing())
					try {
						progress.showProgress(msg);
					} catch (Exception e) {
						System.out.println(e.toString());
					}

			}
		});

	}
	public void dismissProgress(){
		if(progress == null)
			return;

		handler.post(new Runnable() {
			@Override
			public void run() {
				progress.dismissProgress();
			}
		});


	}
	

}
