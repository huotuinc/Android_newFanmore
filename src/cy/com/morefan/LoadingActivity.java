package cy.com.morefan;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;
import cn.jpush.android.api.JPushInterface;
import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;
import cy.com.morefan.bean.GBBaseData;
import cy.com.morefan.bean.GlobalData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.guide.GuideActivity;
import cy.com.morefan.service.LocationService;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.KJLoger;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;

public class LoadingActivity extends BaseActivity implements OnClickListener
{
    private RelativeLayout loadLayout;

    private Intent locationI = null;

    private boolean isConnection = false;// 假定无网络连接 

    private  TaskData alertTaskData=null;//预告的任务信息

    private int messageType = 0;//消息的类型

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);

        setContentView(R.layout.loading);

        initJPush();

        initView();
        handlerView();
    }

    /**
     * 根据开关 打开推送功能
     *@创建人：jinxiangdong
     *@修改时间：2015年7月9日 上午9:23:12
     *@方法描述：
     *@方法名：initJPush
     *@参数：
     *@返回：void
     *@exception
     *@since
     */
    protected void initJPush(){
        int enable = MyApplication.readInt(this , Constant.LOGIN_USER_INFO,  Constant.PUSH_ENABLE , 1 );
        if( enable == 1){
          if(  JPushInterface.isPushStopped(LoadingActivity.this))
          {
              JPushInterface.resumePush(LoadingActivity.this);
          }
        }else {
            if( false == JPushInterface.isPushStopped(LoadingActivity.this)){
                JPushInterface.stopPush(LoadingActivity.this);
            }
        }
    }

    private void initView()
    {
        loadLayout = (RelativeLayout) this.findViewById(R.id.loadL);

        if( getIntent().hasExtra("task")){
            alertTaskData = (TaskData)getIntent().getSerializableExtra("task");
        }

        if(getIntent().hasExtra("type")){
            messageType = getIntent().getIntExtra("type", 0);
        }
    }

    private void handlerView()
    {
        AlphaAnimation anima = new AlphaAnimation(0.0f, 1.0f);
        anima.setDuration(Constant.ANIMATION_COUNT);// 设置动画显示时间
        loadLayout.setAnimation(anima);
        anima.setAnimationListener(new AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                // 开始检测网络
                isConnection = MyApplication.checkNet(LoadingActivity.this);
                if (!isConnection)
                {
                    // 提示连接网络
                    AlertDialog.Builder dialog = new Builder(
                            LoadingActivity.this);
                    dialog.setTitle("网络设置")
                            .setMessage("网络不可用，请设置")
                            .setPositiveButton("设置",
                                    new DialogInterface.OnClickListener()
                                    {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which)
                                        {
                                            // TODO Auto-generated method stub

                                            Intent intent = null;
                                            // 判断手机系统的版本 即API大于10 就是3.0或以上版本
                                            if (android.os.Build.VERSION.SDK_INT > 10)
                                            {
                                                intent = new Intent(
                                                        android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                            } else
                                            {
                                                intent = new Intent();
                                                ComponentName component = new ComponentName(
                                                        "com.android.settings",
                                                        "com.android.settings.WirelessSettings");
                                                intent.setComponent(component);
                                                intent.setAction("android.intent.action.VIEW");
                                            }
                                            LoadingActivity.this
                                                    .startActivity(intent);
                                        }
                                    })
                            .setNegativeButton("取消",
                                    new DialogInterface.OnClickListener()
                                    {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which)
                                        {
                                            // TODO Auto-generated method stub
                                            dialog.dismiss();
                                            // 未设置网络，关闭应用
                                            closeSelf(LoadingActivity.this);
                                        }
                                    }).show();

                }

                // 采用百度地图定位，获取经纬度
                locationI = new Intent(LoadingActivity.this,
                        LocationService.class);
                LoadingActivity.this.startService(locationI);

                Set<Integer> days = new HashSet<Integer>();
                // 一周七天可推送
                for (int i = 0; i < 7; i++)
                {
                    days.add(i);
                }
                // 每天6点到23点可推送
                // 本地代码设置极光推送时间
                JPushInterface.setPushTime(LoadingActivity.this, days, 6, 23);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                new InitAsyncTask().execute();
            }
        });
    }

    class InitAsyncTask extends AsyncTask<Void, Void, GBBaseData>
    {
        String initParams = null;

        @Override
        protected GBBaseData doInBackground(Void... params)
        {
            JSONUtil<GBBaseData> jsonUtil = new JSONUtil<GBBaseData>();
            GBBaseData globalData = new GBBaseData();
            //
            try{
                String json = HttpUtil.getInstance().doGet(initParams);
                globalData = jsonUtil.toBean(json, globalData);
            } catch (JsonSyntaxException e)
            {
                LogUtil.e("JSON_ERROR", e.getMessage());
                globalData.setResultCode(0);
                globalData.setResultDescription("解析json出错");
            }
            return globalData;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            // 获取请求参数
            ObtainParamsMap obtainMap = new ObtainParamsMap(
                    LoadingActivity.this);
            String paramMap = obtainMap.getMap();
            String signStr = obtainMap.getSign(null);
            initParams = Constant.INIT_INTERFACE;
            try
            {
                initParams += "?sign=" + URLEncoder.encode(signStr, "UTF-8");
            } catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                KJLoger.errorLog(e.getMessage());
            }
            initParams += paramMap;

            KJLoger.i("初始化", initParams);

        }

        @Override
        protected void onPostExecute(GBBaseData result)
        {
            super.onPostExecute(result);

            if( null == result || result.getSystemResultCode() !=1 )
            {
                ToastUtils.showLongToast(LoadingActivity.this, "获取数据异常");
            }

            if (1 == result.getResultCode())
            {
                // 解析初始化获取的数据，并保存文件
                // 记录更新token
                if (null != result.getResultData().getUser()
                        && null != result.getResultData().getUser().getToken())
                {
                    MyApplication.writeTokenToLocal(LoadingActivity.this, result
                            .getResultData().getUser().getToken(),
                            Constant.TOKEN_ADD);
                    if (null != result.getResultData().getUser()
                            .getWelcomeTip()
                            && !"".equals(null != result.getResultData()
                                    .getUser().getToken()))
                    {
                        if( result.getResultData().getUser().getWelcomeTip().length()>0 ){
                            ToastUtils.showShortToast(LoadingActivity.this, result.getResultData().getUser().getWelcomeTip());
                        }
                    }
                }
                GlobalData global = result.getResultData().getGlobal();
                MyApplication.writeGlobalInfoToLocal(LoadingActivity.this,
                        global.getAmountToCheckout(), global.getSignMsg(),
                        global.getAboutURL(), global.getHelpURL(),
                        global.getLessReadSeconds(), result.getResultData()
                                .getUpdate().getUpdateMD5(), result
                                .getResultData().getUpdate().getUpdateUrl(),
                        result.getResultData().getUpdate().getUpdateTips(),
                        global.getServiceURL(), global.getRuleURL(),
                        global.getPrivacyPoliciesURL(),
                        global.getCustomerServicePhone());
                // 将公共信息记录在application中
                application.globalData = result.getResultData().getGlobal();

            }

            //判断是否需要引导
            String guideTag = MyApplication.readString(LoadingActivity.this, Constant.GUIDE_INFO, Constant.GUIDE_INFO_TAG);
            if(null == guideTag || "".equals(guideTag))
            {
                //进入引导界面
                ActivityUtils.getInstance().skipActivity(LoadingActivity.this, GuideActivity.class);
            } else
            {
                // 跳转到首页                
                Intent i = new Intent(LoadingActivity.this  , MainActivity.class);
                i.putExtra("task", alertTaskData);
                i.putExtra("type", messageType);
                LoadingActivity.this.startActivity(i);
                LoadingActivity.this.finish();
            }
        }

    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (null != locationI)
        {
            stopService(locationI);
        }
    }
    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub

    }
}
