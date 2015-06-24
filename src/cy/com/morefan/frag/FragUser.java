package cy.com.morefan.frag;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cy.com.morefan.MainActivity;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMUserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.frag.FragManager.FragType;
import cy.com.morefan.listener.DataListener;
import cy.com.morefan.ui.account.AccountInfoActivity;
import cy.com.morefan.ui.account.MsgCenterActivity;
import cy.com.morefan.ui.account.PswManagentActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.BitmapLoader;
import cy.com.morefan.util.Util;

public class FragUser extends BaseFragment implements Callback
{

    public Handler mHandler = new Handler(this);

    public MyApplication application;

    private TextView phoneText;

    private LinearLayout userDataLayout;

    private LinearLayout changePswLayout;

    private LinearLayout msgCenterLayout;

    private Button logoutBtn;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setRetainInstance(true);
        application = (MyApplication) getActivity().getApplication();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_user, container, false);
        initView(rootView);
        initData();
        return rootView;
    }

    private void initView(View parentView)
    {
        phoneText = (TextView) parentView.findViewById(R.id.phoneNumber);
        // phoneLabel.setLineSpacing(1.5f, 1.5f);
        userDataLayout = (LinearLayout) parentView
                .findViewById(R.id.userDataLabel);
        changePswLayout = (LinearLayout) parentView
                .findViewById(R.id.changePswLabel);
        msgCenterLayout = (LinearLayout) parentView
                .findViewById(R.id.msgCenterLabel);
        logoutBtn = (Button) parentView.findViewById(R.id.logoutBtn);
        
    }
    
    private void initData()
    {
        String phoneNumber = application.readString(getActivity(), Constant.LOGIN_USER_INFO, Constant.LOGIN_USER_MOBILE);
        if(null == phoneNumber || "".equals(phoneNumber))
        {
            phoneText.setText("用户未绑定手机");
        }
        else
        {
            phoneText.setText(phoneNumber);
        }
        
    }

    @Override
    public void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        String phoneNumber = application.readString(getActivity(), Constant.LOGIN_USER_INFO, Constant.LOGIN_USER_MOBILE);
        if (null == phoneNumber || "".equals(phoneNumber))
        {
            phoneText.setText("用户未绑定手机");
        } else
        {
            phoneText.setText(phoneNumber);
        }

    }

    @Override
    public void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();
        String phoneNumber = application.readString(getActivity(), Constant.LOGIN_USER_INFO, Constant.LOGIN_USER_MOBILE);
        if (null == phoneNumber || "".equals(phoneNumber))
        {
            phoneText.setText("用户未绑定手机");
        } else
        {
            phoneText.setText(phoneNumber);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        // 跳转到用户资料界面
        userDataLayout.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                ActivityUtils.getInstance().showActivity(getActivity(),
                        AccountInfoActivity.class);
            }
        });

        // 跳转到修改密码界面
        changePswLayout.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                ActivityUtils.getInstance().showActivity(getActivity(),
                        PswManagentActivity.class);
            }
        });

        // 跳转到消息中心界面
        msgCenterLayout.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                ActivityUtils.getInstance().showActivity(getActivity(),
                        MsgCenterActivity.class);
            }
        });

        // 设置登出系统按钮事件
        logoutBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                application.isLogin = false;
                application.personal = new FMUserData();
                //清空本地token,设置本地token为""
                application.writeTokenToLocal(getActivity(), "", Constant.TOKEN_CLEAR);
                // 切换到主界面
                application.mFragManager.setCurrentFrag(FragType.Home);

                // 发出登出命令，同步刷新菜单界面
                mHandler.sendEmptyMessage(DataListener.LOGINOUT_FLUSH);
            }
        });

    }

    @Override
    public void onReshow()
    {
        // TODO Auto-generated method stub

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
        case DataListener.LOGINOUT_FLUSH:
        {
            //登出按钮执行后同步菜单栏
            Util.logoutUI();
            MainActivity.txtTitle.setText("首页");
            BitmapLoader.create().displayUrl(getActivity(), MainActivity.headImage,
                    "http://hottech.gitcafe.io/resources/fanmoreicon/images/mrtou_h.png",
                    R.drawable.ic_login_username, R.drawable.ic_login_username);
            MainActivity.buyFlow.setText("签到");
            MainActivity.buyFlow.setTag("mark");
        }
            break;

        default:
            break;
        }
        return false;
    }

}
