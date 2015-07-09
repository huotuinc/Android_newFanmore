package cy.com.morefan.frag;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.MainActivity;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.adapter.DataListAdapter;
import cy.com.morefan.bean.FMCheckIn;
import cy.com.morefan.bean.FMUserData;
import cy.com.morefan.bean.IBaseData;
import cy.com.morefan.bean.MarkData;
import cy.com.morefan.bean.MarkData.WEEK_NAME;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.DataListener;
import cy.com.morefan.service.ApiService;
import cy.com.morefan.ui.user.LoginActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.DateUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.L;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.SoundUtil;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.util.Util;

public class FragMark extends BaseFragment implements DataListener, Callback,
        OnClickListener
{
    private GridView gridView;

    public MyApplication application;

    private DataListAdapter adapter;

    private List<IBaseData> datas;

    private ApiService apiService;

    private TextView des;

    private Button btnMark;

    private int curWeekOfDay;// 当前是星期几

    private Handler mHandler = new Handler(this);

    SoundUtil soundUtil = null;

    @Override
    public boolean handleMessage(Message msg)
    {

        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setRetainInstance(true);
        application = (MyApplication) getActivity().getApplication();
        apiService = new ApiService(this, getActivity());
        datas = new ArrayList<IBaseData>();
        adapter = new DataListAdapter(getActivity(), datas);
        Calendar mCalendar = Calendar.getInstance();
        // mCalendar.add(Calendar.DAY_OF_WEEK, 3);

        curWeekOfDay = mCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        curWeekOfDay = curWeekOfDay == 0 ? 7 : curWeekOfDay;
        L.i(">>>" + curWeekOfDay);
        checkMarkStatus();
        super.onCreate(savedInstanceState);

        soundUtil = new SoundUtil(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_mark, container, false);
        gridView = (GridView) rootView.findViewById(R.id.grid);
        gridView.setAdapter(adapter);

        btnMark = (Button) rootView.findViewById(R.id.btnMark);
        btnMark.setText("签到");
        btnMark.setOnClickListener(this);
        des = (TextView) rootView.findViewById(R.id.des);
        des.setText(MyApplication.readString(getActivity(), Constant.INIT_INFO,
                Constant.INIT_SIGN_MSG));
        refreshView();
        return rootView;
    }

    private void checkMarkStatus()
    {
        int tody = curWeekOfDay - 1;
        datas.clear();
        // int signStatus =
        // Integer.parseInt(String.valueOf(application.personal.getSignInfo()),2);
        // //binary to int
        int signStatus = MyApplication.readInt(getActivity(),
                Constant.LOGIN_USER_INFO, Constant.LOGIN_USER_MARK);
        WEEK_NAME[] names = MarkData.WEEK_NAME.values();
        // 周一在最高位
        for (int i = 6; i >= 0; i--)
        {
            int sign = 0x1;
            MarkData item = new MarkData();
            item.index = 6 - i;
            // 判断是否漏签
            if (item.index < tody)
            {
                int status = signStatus >> i & sign;
                if (0 == status)
                {
                    item.status = 2;
                } else
                {
                    item.status = signStatus >> i & sign;
                }
            } else
            {
                item.status = signStatus >> i & sign;
            }
            // item.status = signStatus >> i & sign;
            item.name = names[item.index].toString();
            L.i(">>>" + item.name + ":" + item.status);
            datas.add(item);

        }
    }

    private void refreshView()
    {
        adapter.notifyDataSetChanged();
        MarkData curMarkData = (MarkData) datas.get(curWeekOfDay - 1);
        btnMark.setBackgroundResource(0 == curMarkData.status ? R.drawable.btn_mark_blue_sel
                : R.drawable.btn_mark_gray);
        btnMark.setClickable(0 == curMarkData.status ? true : false);
        btnMark.setText(0 == curMarkData.status ? "签到" : "今日已签到");
    }

    @Override
    public void onReshow()
    {
        // TODO Auto-generated method stub
        Calendar mCalendar = Calendar.getInstance();
        curWeekOfDay = mCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        curWeekOfDay = curWeekOfDay == 0 ? 7 : curWeekOfDay;
        L.i(">>>" + curWeekOfDay);
        checkMarkStatus();
        refreshView();
    }

    @Override
    public void onFragPasue()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
        case R.id.btnMark:
        {
            if (false == application.isLogin(getActivity()))
            {//判断是否登录
                ActivityUtils.getInstance().showActivity(getActivity(),
                        LoginActivity.class);
                return;
            }

            new CheckInAsyncTask().execute();
        }
            break;

        default:
            break;
        }

    }

    @Override
    public void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();

        if (soundUtil != null)
        {
            soundUtil.Release();
        }
    }

    @Override
    public void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();
        checkMarkStatus();
        refreshView();
    }

    @Override
    public void onDataFinish(int type, String msg, Bundle extra,
            IBaseData... data)
    {
        mHandler.obtainMessage(type).sendToTarget();

    }

    @Override
    public void onDataFail(int type, String msg, Bundle extra)
    {

    }

    public class CheckInAsyncTask extends AsyncTask<Void, Void, FMCheckIn>
    {

        @Override
        protected FMCheckIn doInBackground(Void... params)
        {
            // TODO Auto-generated method stub
            FMCheckIn checkIn = new FMCheckIn();
            JSONUtil<FMCheckIn> jsonUtil = new JSONUtil<FMCheckIn>();
            String url;
            ObtainParamsMap obtainMap = new ObtainParamsMap(getActivity());
            Map<String, String> paramMap = obtainMap.obtainMap();
            // 拼接注册url
            url = Constant.SIGN_IN;
            // 封装sign
            String signStr = obtainMap.getSign(paramMap);
            paramMap.put("sign", signStr);

            String jsonStr = HttpUtil.getInstance().doPost(url, paramMap);
            try
            {
                checkIn = jsonUtil.toBean(jsonStr, checkIn);
            } catch (JsonSyntaxException e)
            {
                LogUtil.e("JSON_ERROR", e.getMessage());
                checkIn.setResultCode(0);
                checkIn.setResultDescription("解析json出错");
            }
            return checkIn;
        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(FMCheckIn result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            // 刷新签到界面
            // 返回user对象，
            // 模拟代码
            if (1 == result.getResultCode())
            {
                // 如果签到成功，更新用户信息，token信息
                FMUserData userData = result.getResultData().getUser();
                application.personal = userData;
                if (null != userData
                        && (!"".equals(userData.getToken()) && null != userData
                                .getToken()))
                {
                    // 刷新token
                    MyApplication.writeTokenToLocal(getActivity(), result
                            .getResultData().getUser().getToken(),
                            Constant.TOKEN_ADD);
                    // 保存用户信息
                    MyApplication.writeUserInfoToLocal(getActivity(), userData
                            .getName(), userData.getBalance(), userData
                            .getPictureURL(), userData.getSignInfo(), userData
                            .getInvCode(), userData.getSigntoday(), DateUtils
                            .formatDate(userData.getBirthDate(),
                                    Constant.DATE_FORMAT),
                            userData.getMobile(), userData.getCareer(),
                            userData.getIncoming(), userData.getFavs(),
                            userData.getArea(),
                            DateUtils.formatDate(userData.getRegDate(),
                                    Constant.DATE_FORMAT), userData
                                    .getWelcomeTip(),
                            userData.getInvalidCode(), userData.getSex(),
                            userData.getRealName());
                }
                checkMarkStatus();
                refreshView();
                // 刷新界面
                if (Util.isM(MyApplication.readUserBalance(getActivity())))
                {
                    MainActivity.residualFlow.setText(MyApplication
                            .readUserBalance(getActivity()) + "MB");
                    MainActivity.tips.setText("可兑换流量");
                } else
                {
                    // 精确到GB
                    float flow = Float.parseFloat(MyApplication
                            .readUserBalance(getActivity())) / 1024;
                    MainActivity.residualFlow.setText(Util.decimalFloat(flow,
                            Constant.ACCURACY_3) + "GB");
                }
                if (7 == curWeekOfDay)
                {
                    // 签满一周后，提示领取流量
                    if (127 == userData.getSignInfo())
                    {
                        ToastUtils
                                .showLongToast(
                                        getActivity(),
                                        "签到成功,你已经成功领取"
                                                + Util.getReward(MyApplication
                                                        .readString(
                                                                getActivity(),
                                                                Constant.INIT_INFO,
                                                                Constant.INIT_SIGN_MSG))
                                                + "M流量");
                    } else
                    {
                        ToastUtils.showLongToast(
                                getActivity(),
                                "签到成功，"
                                        + MyApplication.readString(getActivity(),
                                                Constant.INIT_INFO,
                                                Constant.INIT_SIGN_MSG));
                    }
                } else
                {
                    ToastUtils.showLongToast(getActivity(), "签到成功");
                }
                // 播放声音
                if (soundUtil != null)
                {
                    soundUtil.shakeSound(R.raw.checkin);
                }
                /*
                 * // 提示签到成功，获取多少流量 ToastUtils .showLongToast( getActivity(),
                 * "签到成功，" + application .readString( getActivity(),
                 * Constant.INIT_INFO, Constant.INIT_SIGN_MSG));
                 */

            } else if (Constant.TOKEN_OVERDUE == result.getResultCode())
            {
                // 提示账号异地登陆，强制用户退出
                // 并跳转到登录界面
                ToastUtils.showLongToast(getActivity(), "账户登录过期，请重新登录");
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        ActivityUtils.getInstance().loginOutInFragment(
                                (Activity) getActivity());
                    }
                }, 2000);
            } else
            {
                // 签到失败
                ToastUtils.showLongToast(getActivity(), "签到失败");
            }

        }

    }
}
