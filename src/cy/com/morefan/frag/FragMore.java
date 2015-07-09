package cy.com.morefan.frag;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.android.volley.toolbox.Volley;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.IBaseData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.DataListener;
import cy.com.morefan.service.ApiService;
import cy.com.morefan.ui.info.AboutActivity;
import cy.com.morefan.ui.info.FeedBackActivity;
import cy.com.morefan.ui.user.LoginActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.ImageUtil;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.util.VolleyUtil;

/**
 * 更多设置
 * 
 * @author cy
 *
 */
public class FragMore extends BaseFragment implements DataListener, Callback
{

    public MyApplication application;

    private ApiService api;

    private List<IBaseData> datas;

    private Handler mHandler = new Handler(this);

    private LinearLayout feedBackLabel;// 意见反馈面板

    private LinearLayout cleanBufferLabel;// 清理缓存面板

    private LinearLayout aboutLabel;// 关于我们面板

    private LinearLayout versionLabel;// 当前版本面板

    private TextView bufferData;// 缓存数据

    private TextView currVersion;// 当前版本

    private TextView feedBackText;// 缓存数据

    private TextView cleanBufferText;// 当前版本

    private TextView aboutText;// 缓存数据

    private TextView versionText;// 当前版本

    private LinearLayout ruleL;// 规则说明

    private LinearLayout deliveryL;// 投放指南
    private LinearLayout hotLineLabel;// 火图客服
    private TextView currHotLine;// 热线电话
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setRetainInstance(true);
        api = new ApiService(this, getActivity());
        datas = new ArrayList<IBaseData>();
        application = (MyApplication) getActivity().getApplication();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.frag_more, container, false);
        initView(rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        feedBackLabel.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if(isLogin())
                {
                    // TODO Auto-generated method stub
                    ActivityUtils.getInstance().showActivity(getActivity(),
                            FeedBackActivity.class);
                }else
                {

                    // 跳转到登陆界面
                    ActivityUtils.getInstance().showActivity(getActivity(), LoginActivity.class);
                }
            }
        });
        cleanBufferLabel.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                new AsyncTask<String, Void, Void>()
                {

                    @Override
                    protected Void doInBackground(String... params)
                    {
                        // TODO Auto-generated method stub
                        String cacheDir = params[0];
                        ImageUtil.deleteFileByPath( cacheDir );
                        // 清空缓存操作
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result)
                    {
                        // TODO Auto-generated method stub
                        super.onPostExecute(result);
                        // 更新界面
                        ToastUtils.showLongToast(getActivity(), "清除缓存成功");
                        //bufferData.setText("0KB");
                        getCacheData();
                    }

                }.execute( getActivity().getCacheDir() + File.separator+"volley");
            }
        });
        aboutLabel.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                Bundle bundle = new Bundle();
                bundle.putString("title", "关于我们");
                bundle.putString("url", MyApplication.readString(getActivity(),
                        Constant.INIT_INFO, Constant.INIT_ABOUT_URL));
                ActivityUtils.getInstance().showActivity(getActivity(),
                        AboutActivity.class, bundle);
            }
        });
        versionLabel.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub

            }
        });
        ruleL.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                Bundle bundle = new Bundle();
                bundle.putString("title", "规则说明");
                bundle.putString("url", MyApplication.readString(getActivity(),
                        Constant.INIT_INFO, Constant.INIT_RULE_URL));
                ActivityUtils.getInstance().showActivity(getActivity(),
                        AboutActivity.class, bundle);
            }
        });
        deliveryL.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                Bundle bundle = new Bundle();
                bundle.putString("title", "投放指南");
                bundle.putString("url", MyApplication.readString(getActivity(),
                        Constant.INIT_INFO, Constant.INIT_SERVICE_URL));
                ActivityUtils.getInstance().showActivity(getActivity(),
                        AboutActivity.class, bundle);
            }
        });
        //拨热线号码
        hotLineLabel.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                if(TextUtils.isEmpty(currHotLine.getText()))
                {
                    ToastUtils.showLongToast(getActivity(), "热线占线中");
                }
                else
                {
                    String phone = currHotLine.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                    getActivity().startActivity(intent);
                }
                
            }
        });

    }

    @Override
    public void onReshow()
    {
        // TODO Auto-generated method stub
        //bufferData.setText("12234KB");
        currVersion.setText("V" + MyApplication.getAppVersion(getActivity()));
        currHotLine.setText( MyApplication.readString(getActivity(), Constant.INIT_INFO, Constant.INIT_CUSTOMER_PHONE));
        
        getCacheData();
    }
    
    protected void getCacheData(){
        try{
         long size=ImageUtil.getFileSize( getActivity().getCacheDir() + File.separator + "volley");
         String txt =  Formatter.formatFileSize(getActivity(), size);
         bufferData.setText(  txt );
        }catch(Exception ex){
            Log.e("error",ex.getMessage());
        }
    }

    @Override
    public void onFragPasue()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View view)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean handleMessage(Message msg)
    {
        // TODO Auto-generated method stub
        switch (msg.what)
        {
        case DONE_UNIT_LIST:
            /*
             * IBaseData[] serDatas = (IBaseData[]) msg.obj; for(int i = 0,
             * length = serDatas.length; i < length; i++){
             * datas.add(serDatas[i]); } adapter.notifyDataSetChanged(); break;
             */

        default:
            break;
        }
        return false;
    }

    @Override
    public void onDataFinish(int type, String msg, Bundle extra,
            IBaseData... data)
    {
        // TODO Auto-generated method stub
        mHandler.obtainMessage(type, data).sendToTarget();
    }

    @Override
    public void onDataFail(int type, String msg, Bundle extra)
    {
        // TODO Auto-generated method stub
        mHandler.obtainMessage(type, msg).sendToTarget();
    }

    private void initView(View parentView)
    {
        feedBackLabel = (LinearLayout) parentView
                .findViewById(R.id.feedBackLabel);
        cleanBufferLabel = (LinearLayout) parentView
                .findViewById(R.id.cleanBufferLabel);
        aboutLabel = (LinearLayout) parentView.findViewById(R.id.aboutLabel);
        versionLabel = (LinearLayout) parentView
                .findViewById(R.id.versionLabel);
        bufferData = (TextView) parentView.findViewById(R.id.bufferData);
        bufferData.setText("0KB");
        currVersion = (TextView) parentView.findViewById(R.id.currVersion);
        currVersion.setText("V" + MyApplication.getAppVersion(getActivity()));

        feedBackText = (TextView) parentView.findViewById(R.id.feedBackText);
        feedBackText.setLineSpacing(1.5f, 1.5f);
        cleanBufferText = (TextView) parentView
                .findViewById(R.id.cleanBufferText);
        cleanBufferText.setLineSpacing(1.5f, 1.5f);
        aboutText = (TextView) parentView.findViewById(R.id.aboutText);
        aboutText.setLineSpacing(1.5f, 1.5f);
        versionText = (TextView) parentView.findViewById(R.id.versionText);
        versionText.setLineSpacing(1.5f, 1.5f);

        ruleL = (LinearLayout) parentView.findViewById(R.id.ruleL);
        deliveryL = (LinearLayout) parentView.findViewById(R.id.deliveryL);
        
        hotLineLabel = (LinearLayout) parentView.findViewById(R.id.hotLineLabel);
        currHotLine = (TextView) parentView.findViewById(R.id.currHotLine);
        currHotLine.setText( MyApplication.readString(getActivity(), Constant.INIT_INFO, Constant.INIT_CUSTOMER_PHONE));
    }
    
    @Override
    public void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
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
        String localToken = MyApplication.readLocalToken(getActivity());
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

}
