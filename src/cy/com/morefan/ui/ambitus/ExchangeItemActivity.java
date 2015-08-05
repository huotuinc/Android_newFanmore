package cy.com.morefan.ui.ambitus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMRegisterBean;
import cy.com.morefan.bean.FMRegisterBean.InnerUser;
import cy.com.morefan.bean.FMUserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.ui.account.MsgCenterActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.DateUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.view.CyButton;
/**
 *
 * @类名称：ExchangeItemActivity
 * @类描述：兑换流量包界面
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月10日 上午9:59:51
 * @修改备注：
 * @version:
 */
public class ExchangeItemActivity extends BaseActivity implements
        OnClickListener, Callback, OnItemClickListener
{
    private CyButton backImage;

    // 返回文字事件
    private TextView backText;

    private TextView title;

    private ListView gdFlow;

    public MyApplication application;

    private ExchangeDataAdapter adapter;

    private List<BigDecimal> flowTargets = new ArrayList<BigDecimal>();

    public Handler mHandler = new Handler(this);

    @Override
    public boolean handleMessage(Message msg)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub

        switch (v.getId())
        {
        case R.id.backImage:
        {
            closeSelf(ExchangeItemActivity.this);
        }
            break;
        case R.id.backtext:
        {
            closeSelf(ExchangeItemActivity.this);
        }
            break;
        default:
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        application = (MyApplication) ExchangeItemActivity.this
                .getApplication();
        this.setContentView(R.layout.pop_exchangeflow);
        backImage = (CyButton) this.findViewById(R.id.backImage);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("流量包列表");
        gdFlow = (ListView) this.findViewById(R.id.gdFlow);
        backImage.setOnClickListener(this);
        adapter = new ExchangeDataAdapter();
        gdFlow.setOnItemClickListener(this);
        gdFlow.setAdapter(adapter);
        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);
        Bundle bundle = ExchangeItemActivity.this.getIntent().getExtras();
        flowTargets = (List<BigDecimal>) bundle.getSerializable("flowItem");
        Collections.sort(flowTargets);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            // finish自身
            ExchangeItemActivity.this.finish();
            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
            final int position, long id)
    {
        // TODO Auto-generated method stub
        // 判断当前用户剩余流量是否可以兑换该流量包
        //float balance = Float.parseFloat(application
        ///        .readUserBalance(ExchangeItemActivity.this));
        
        BigDecimal balance = new BigDecimal( application.readUserBalance( ExchangeItemActivity.this));
        
        //float currPackage = flowTargets.get(position).floatValue();
        
        BigDecimal currPackage= flowTargets.get(position);
        
        // 比较大小
        //int filter = doFilter(balance, currPackage);
        
        int filter = balance.compareTo(currPackage);
        
        if (filter < 0)
        {
            // 提示余额不足，无法兑换
            ToastUtils.showLongToast(ExchangeItemActivity.this, "您当前的流量余额只有"
                    + balance + "M，无法兑换！");
        } else
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(
                    ExchangeItemActivity.this);
            dialog.setTitle("兑换流量");
            dialog.setMessage("请确认兑换");
            dialog.setPositiveButton("兑换",
                    new DialogInterface.OnClickListener()
                    {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // TODO Auto-generated method stub
                            // 实现兑换
                            dialog.dismiss();
                            new ExchangeAsyncTask().execute(flowTargets
                                    .get(position));
                        }
                    });
            dialog.setNegativeButton("不兑换",
                    new DialogInterface.OnClickListener()
                    {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }
                    });

            dialog.show();
        }

    }

    public class ExchangeDataAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            // TODO Auto-generated method stub
            return flowTargets.size();
        }

        @Override
        public Object getItem(int position)
        {
            // TODO Auto-generated method stub
            return flowTargets.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            // TODO Auto-generated method stub
            ViewHolder holder = null;
            if (convertView == null)
            {
                holder = new ViewHolder();
                convertView = View.inflate(ExchangeItemActivity.this,
                        R.layout.exchangeflow_item, null);
                holder.exchangeFlowC = (TextView) convertView
                        .findViewById(R.id.exchangeFlowC);
                holder.flowArrows = (ImageView) convertView
                        .findViewById(R.id.flowArrows);
                convertView.setTag(holder);
            } else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            // 加载图片
            if (flowTargets.size() > 0)
            {
                holder.exchangeFlowC.setText(flowTargets.get(position) + "M流量包");

                // if( flowDatas.get(position).equals(selectedFlow)){
                // holder.buyFlowC.setBackgroundResource(R.drawable.btn_mark_blue_sel);
                // holder.buyFlowC.setTextColor(Color.WHITE);
                // }else{
                // holder.buyFlowC.setBackgroundResource(R.drawable.shape_stoke_gray);
                // holder.buyFlowC.setTextColor(Color.BLACK);
                // }
            }
            return convertView;
        }

        class ViewHolder
        {
            TextView exchangeFlowC;

            ImageView flowArrows;
        }

    }

    /*
     * 兑换接口
     */
    public class ExchangeAsyncTask extends
            AsyncTask<Number, Void, FMRegisterBean>
    {

        @Override
        protected FMRegisterBean doInBackground(Number... params)
        {
            // TODO Auto-generated method stub
            Number flow = params[0];
            FMRegisterBean registerBean = new FMRegisterBean();
            JSONUtil<FMRegisterBean> jsonUtil = new JSONUtil<FMRegisterBean>();
            String url;
            ObtainParamsMap obtainMap = new ObtainParamsMap(
                    ExchangeItemActivity.this);
            Map<String, String> paramMap = obtainMap.obtainMap();

            // 拼接注册url
            url = Constant.CHECKOUT;
            // 注册是POST提交
            paramMap.put("amount", String.valueOf(flow));
            // 封装sign
            String signStr = obtainMap.getSign(paramMap);
            paramMap.put("sign", signStr);
            if (Constant.IS_PRODUCTION_ENVIRONMENT)
            {
                String jsonStr = HttpUtil.getInstance().doPost(url, paramMap);
                try
                {
                    registerBean = jsonUtil.toBean(jsonStr, registerBean);
                } catch (JsonSyntaxException e)
                {
                    LogUtil.e("JSON_ERROR", e.getMessage());
                    registerBean.setResultCode(0);
                    registerBean.setResultDescription("解析json出错");
                }
            } else
            {
                registerBean.setResultCode(1);
                InnerUser iUser = registerBean.new InnerUser();
                FMUserData user = new FMUserData();
                iUser.setUser(user);
                registerBean.setResultData(iUser);
            }
            return registerBean;
        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(FMRegisterBean result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if (null == result)
            {
                ToastUtils.showLongToast(ExchangeItemActivity.this, "请求失败。");
                return;
            }
            else if (1 == result.getResultCode())
            {
             // 更新用户资料
                // 记录注册时返回的用户信息，并可以直接登录
                FMUserData regUser = result.getResultData().getUser();
                application.personal = regUser;
                application.isLogin = true;
                // 记录更新token
                if (!"".equals(regUser.getToken()) && null != regUser.getToken())
                {
                    application.writeTokenToLocal(ExchangeItemActivity.this,
                            regUser.getToken(), Constant.TOKEN_ADD);
                    application.writeUserInfoToLocal(ExchangeItemActivity.this,
                            regUser.getName(), regUser.getBalance(), regUser
                                    .getPictureURL(), regUser.getSignInfo(),
                            regUser.getInvCode(), regUser.getSigntoday(), DateUtils
                                    .formatDate(regUser.getBirthDate(),
                                            Constant.DATE_FORMAT), regUser
                                    .getMobile(), regUser.getCareer(), regUser
                                    .getIncoming(), regUser.getFavs(), regUser
                                    .getArea(), DateUtils.formatDate(
                                    regUser.getRegDate(), Constant.DATE_FORMAT),
                            regUser.getWelcomeTip(), regUser.getInvalidCode(),
                            regUser.getSex(), regUser.getRealName());
                }
                ToastUtils.showLongToast(ExchangeItemActivity.this, "兑换成功");
                // 关闭界面
                ExchangeItemActivity.this.setResult(RESULT_OK);
                closeSelf(ExchangeItemActivity.this);
            }
            else if (Constant.TOKEN_OVERDUE == result.getResultCode())
            {
                // 提示账号异地登陆，强制用户退出
                // 并跳转到登录界面
                ToastUtils.showLongToast(ExchangeItemActivity.this, "账户登录过期，请重新登录");
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        ActivityUtils.getInstance().loginOutInActivity(
                                (Activity) ExchangeItemActivity.this);
                    }
                }, 2000);
            }
        }
    }

    /**
     * 
     * @方法描述：
     * @方法名：doFilter
     * @参数：@param balance 当前用户剩余流量
     * @参数：@param currPackage 选中的兑换的流量包
     * @参数：@return
     * @返回：int
     * @exception
     * @since
     */
    private int doFilter(float balance, float currPackage)
    {
        Float b = new Float(balance);
        Float c = new Float(currPackage);
        int num = b.compareTo(c);
        return num;
    }

}
