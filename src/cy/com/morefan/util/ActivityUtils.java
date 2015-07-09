package cy.com.morefan.util;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMUserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.ui.user.LoginActivity;

public class ActivityUtils
{

    public MyApplication application;
    private static class Holder
    {
        private static final ActivityUtils instance = new ActivityUtils();
    }
    
    private ActivityUtils()
    {
        
    }
 
    public static final ActivityUtils getInstance()
    {
        return Holder.instance;
    }
    
    public void showActivity(Activity aty, Class clazz)
    {
        Intent i = new Intent(aty, clazz);
        aty.startActivity(i);
    }
    
    public void showActivity(Activity aty , Class clazz , String key , Serializable serialize ){
        Intent i = new Intent(aty, clazz);
        i.putExtra(key, serialize);
        aty.startActivity(i);
    }

    public void skipActivity(Activity aty, Class clazz)
    {
        Intent i = new Intent(aty, clazz);
        aty.startActivity(i);
        aty.finish();
    }    
    
    public void skipActivity( Activity aty , Class clazz , String key , Serializable serialize ){
        Intent i = new Intent(aty , clazz);
        i.putExtra(key, serialize);
        aty.startActivity(i);
        aty.finish();
    }

    public void showActivity(Activity aty, Intent i)
    {
        aty.startActivity(i);
    }

    public void showActivity(Activity aty, String action)
    {
        Intent i = new Intent(action);
        aty.startActivity(i);
    }
    
    public void showActivityForResult(Activity aty, int requestCode , Class clazz)
    {
        Intent i = new Intent(aty,clazz);
        aty.startActivityForResult(i, requestCode);
    }
    
    public void showActivityForResult(Activity aty  ,int requestCode, Class clazz , Bundle bundle){
        Intent i = new Intent(aty,clazz);
        i.putExtras(bundle);
        aty.startActivityForResult(i, requestCode);
    }
    
    public void showActivityFromBottom(Activity aty, Class clazz, Bundle bundle)
    {
        Intent i = new Intent(aty, clazz);
        i.putExtras(bundle);
        aty.startActivity(i);
        aty.overridePendingTransition(R.anim.bottom_in, android.R.anim.fade_out);
    }
    
    public void showActivity(Activity aty, Class clazz, Bundle bundle)
    {
        Intent i = new Intent(aty, clazz);
        i.putExtras(bundle);
        aty.startActivity(i);
    }
    
    public void loginOutInFragment(Activity aty)
    {
        application = (MyApplication) aty.getApplication();
        application.isLogin = false;
        application.personal = new FMUserData();
        //清空本地token,设置本地token为""
        MyApplication.writeTokenToLocal(aty, "", Constant.TOKEN_CLEAR);
        //切换到登录界面
        showActivity(aty, LoginActivity.class);
    }
    
    public void loginOutInActivity(Activity aty)
    {
        application = (MyApplication) aty.getApplication();
        application.isLogin = false;
        application.personal = new FMUserData();
        //清空本地token,设置本地token为""
        MyApplication.writeTokenToLocal(aty, "", Constant.TOKEN_CLEAR);
        //切换到登录界面
        skipActivity(aty, LoginActivity.class);
    }
    
}
