package cy.com.morefan.frag;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMMaster;
import cy.com.morefan.bean.FMQMaster;
import cy.com.morefan.bean.IBaseData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.DataListener;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.KJLoger;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ShareUtil;

/**
 * 师徒联盟
 * 
 * @author cy
 *
 */
public class FragMaster extends BaseFragment implements DataListener, Callback
{

    private Handler mHandler = new Handler(this);

    private Button shareCode;// 分享按钮

    private TextView discipleTotal;// 徒弟总贡献

    private TextView ruleText;// 昨日总贡献布局

    private TextView yestodayTotal;// 昨日总贡献

    private TextView discipleListC;// 徒弟人数

    private LinearLayout weixinShare;// 微信分享

    private LinearLayout qqShare;// qq分享

    private LinearLayout sinaShare;// 新浪分享

    public MyApplication application;

    // 分享弹出层
    private PopupWindow popupWindow = null;
    private String shareURL;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
        application = (MyApplication) getActivity().getApplication();
        shareURL = new String();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View rootView = inflater
                .inflate(R.layout.frag_master, container, false);
        initView(rootView);
        new LoadMasterData().execute("");
        return rootView;
    }

    private void initView(View rootView)
    {
        shareCode = (Button) rootView.findViewById(R.id.shareCode);
        shareCode.setText("分享邀请码"
                + application.readString(getActivity(),
                        Constant.LOGIN_USER_INFO, Constant.LOGIN_USER_INVCODE));
        discipleTotal = (TextView) rootView.findViewById(R.id.discipleTotal);
        yestodayTotal = (TextView) rootView.findViewById(R.id.yestodayTotal);
        discipleListC = (TextView) rootView.findViewById(R.id.discipleListC);
        ruleText = (TextView) rootView.findViewById(R.id.ruleText);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        shareCode.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                // 跳转到分享界面
                showPopup();

            }
        });

    }

    @Override
    public void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        new LoadMasterData().execute("");
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();
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

    public class LoadMasterData extends AsyncTask<String, Void, FMQMaster>
    {
        private String masterParam = null;

        @Override
        protected FMQMaster doInBackground(String... params)
        {
            // TODO Auto-generated method stub
            JSONUtil<FMQMaster> jsonUtil = new JSONUtil<FMQMaster>();
            FMQMaster masterData = new FMQMaster();
            //
            KJLoger.i(masterParam);
            String json = HttpUtil.getInstance().doGet(masterParam);
            try
            {
                masterData = jsonUtil.toBean(json, masterData);
            } catch (JsonSyntaxException e)
            {
                LogUtil.e("JSON_ERROR", e.getMessage());
                masterData.setResultCode(0);
                masterData.setResultDescription("解析json出错");
            }
            // masterData.setResultCode(1);
            return masterData;
        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
            // 封装参数
            ObtainParamsMap obtainMap = new ObtainParamsMap(getActivity());
            String paramMap = obtainMap.getMap();
            // 封装sign
            String signStr = obtainMap.getSign(null);
            masterParam = Constant.MASTER_SYSTEM;
            try
            {
                masterParam += "?sign=" + URLEncoder.encode(signStr, "UTF-8");
            } catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                KJLoger.errorLog(e.getMessage());
            }
            masterParam += paramMap;

            KJLoger.i("初始化师徒联盟信息", masterParam);
        }

        @Override
        protected void onPostExecute(FMQMaster result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (1 == result.getResultCode())
            {
                // 师徒联盟信息初始化成功
                FMMaster master = result.getResultData();
                if (null != master)
                {
                    discipleTotal.setText(master.getTotalM() + "M");
                    yestodayTotal.setText(master.getYestodayM() + "M");
                    discipleListC.setText(master.getApprNum() + "人");
                    ruleText.setText(master.getAboutURL());
                    shareURL = master.getShareURL();
                } else
                {
                    discipleTotal.setText("0M");
                    yestodayTotal.setText("0M");
                    discipleListC.setText("0人");
                    ruleText.setText("点击分享此页，好友注册并填入邀请码即可成为你的徒弟，徒弟赚取流量的同时会贡献10%给师傅，现在徒孙也会贡献给师傅哦~");
                }

            } else
            {
                // 师徒联盟
                discipleTotal.setText("0M");
                yestodayTotal.setText("0M");
                discipleListC.setText("0人");
                ruleText.setText("点击分享此页，好友注册并填入邀请码即可成为你的徒弟，徒弟赚取流量的同时会贡献10%给师傅，现在徒孙也会贡献给师傅哦~");
            }

        }

    }

    /**
     * 显示菜单
     *
     * @方法描述：
     * @方法名：showPopup
     * @参数：
     * @返回：void
     * @exception
     * @since
     */
    private void showPopup()
    {

        if (popupWindow == null)
        {
            initPopupWindow();
        }

        // 设置位置
        // popupWindow.showAsDropDown(btnShare,-50,-180);
        popupWindow.showAtLocation(shareCode, Gravity.BOTTOM, 0, 0); // 设置在屏幕中的显示位置
    }

    /**
     * 
     * @方法描述：
     * @方法名：initPopupWindow
     * @参数：
     * @返回：void
     * @exception
     * @since
     */
    private void initPopupWindow()
    {
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View layout = mInflater.inflate(R.layout.pop_share_ui, null);
        weixinShare = (LinearLayout) layout.findViewById(R.id.layWeiXin);
        qqShare = (LinearLayout) layout.findViewById(R.id.layQQ);
        sinaShare = (LinearLayout) layout.findViewById(R.id.layXinLang);
        layout.findViewById(R.id.layAll).getBackground().setAlpha(220);
        Button cancelBtn = (Button) layout.findViewById(R.id.cancelShare);
        popupWindow = new PopupWindow(layout,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体的背景
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        // 设置PopupWindow显示和隐藏时的动画
        popupWindow.setAnimationStyle(R.style.AnimationPop);

        weixinShare.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                // 微信朋友圈分享
                ShareUtil.share2WeiXin(getActivity(), "师徒联盟分享", "", shareURL);
            }
        });

        qqShare.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                // Qzone分享
                ShareUtil.share2Qzone(getActivity(), "师徒联盟分享", "", shareURL);
            }
        });

        sinaShare.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                // 新浪微博分享
                ShareUtil.share2Sina(getActivity(), "师徒联盟分享", "", shareURL);
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
            }
        });

    }

}
