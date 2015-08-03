package cy.com.morefan.ui.ambitus;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.MainActivity;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMPrepareCheckout;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.ui.account.MsgCenterActivity;
import cy.com.morefan.ui.flow.FriendsResActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.view.RingView;
/**
 * 
 * @类名称：ExchangeFlowActivity
 * @类描述：兑换流量界面
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月10日 上午9:59:51
 * @修改备注：
 * @version:
 */
public class ExchangeFlowActivity extends BaseActivity implements Callback,
        OnClickListener ,MyBroadcastReceiver.BroadcastListener
{
    public Handler mHandler = new Handler(this);

    private RingView flowCount;

    private CyButton backImage;

    private TextView title;

    private TextView functionBtn;

    private Button exchangeBtn;

    private Button buyFlowBtn;

    private MyApplication app=null;
    
    private ProgressBar pgbarWaiting=null;
    
    private RelativeLayout rlWaiting=null;
    
    private List<BigDecimal> flowTargets=new ArrayList<BigDecimal>();
    
    private TextView tip;
    private Button friends;
    //返回文字事件
    private TextView backText;
    //
    public int RequestCodeCheckOut=2001;

    private TextView tvRed;

    // 监听广播
    private MyBroadcastReceiver myBroadcastReceiver;


    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub

        switch (v.getId())
        {
            case R.id.backImage:
            {
    
                closeSelf(ExchangeFlowActivity.this);
            }
            break;
            case R.id.functionBtn:
            {                               
                //跳转到流量明细界面
                ActivityUtils.getInstance().showActivity(ExchangeFlowActivity.this, DetailsActivity.class);
            }
            break;
            case R.id.exchangeBtn:
            {
                //兑换流量
                doExchangeFlow();
            }
            break;
            case R.id.buyFlowBtn:
            {
                //跳转到购买流量界面
                ActivityUtils.getInstance().showActivity(ExchangeFlowActivity.this, BuyFlowActivity.class);
            }
            break;
            case R.id.friends:
            {
                //跳转到好友求流量界面
                ActivityUtils.getInstance().showActivity(ExchangeFlowActivity.this, FriendsResActivity.class);
            }
            break;
            case R.id.backtext:
            {
                closeSelf(ExchangeFlowActivity.this);
            }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        this.setContentView(R.layout.exchange_ui);
        initView();
        //addView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setRedTip();
    }

    private void initView()
    {
        app= (MyApplication)getApplication();
        //detector = new GestureDetector(this);
        flowCount = (RingView) this.findViewById(R.id.flowCount);

        //float balance = app.personal.getBalance();
        //if( balance==null) balance= BigDecimal.ZERO;

        String balanceStr = MyApplication.readUserBalance(ExchangeFlowActivity.this);

        BigDecimal balance = new BigDecimal(balanceStr);
        balance = balance.setScale(1, RoundingMode.HALF_UP);
        
        flowCount.setText(balance + "M");

        flowCount.setAngle( balance.floatValue() );//设置完成的值

        
        tip = (TextView)this.findViewById(R.id.tips);        
                
        backImage = (CyButton) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(this);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("兑换流量");
        functionBtn = (TextView) this.findViewById(R.id.functionBtn);
        functionBtn.setVisibility(View.VISIBLE);
        functionBtn.setText("明细");
        functionBtn.setOnClickListener(this);
        //selectFlow = (ViewFlipper) this.findViewById(R.id.selectFlow);
        exchangeBtn = (Button) this.findViewById(R.id.exchangeBtn);
        exchangeBtn.setOnClickListener(this);
        buyFlowBtn = (Button) this.findViewById(R.id.buyFlowBtn);
        buyFlowBtn.setOnClickListener(this);

        rlWaiting=(RelativeLayout)this.findViewById(R.id.rlWaiting);
        pgbarWaiting = (ProgressBar)this.findViewById(R.id.pgbarWaiting);
        friends = (Button) this.findViewById(R.id.friends);
        friends.setText("好友请求");
        friends.setOnClickListener(this);
        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);
        tvRed=(TextView)this.findViewById(R.id.tvRed);


        myBroadcastReceiver = new MyBroadcastReceiver(this, this,
                MyBroadcastReceiver.ACTION_FLOW_ADD,MyBroadcastReceiver.ACTION_REQUESTFLOW);

        new ExchangeFlowAsynTask().execute();
    }

    /**
     * 当有消息时，显示红点
     */
    private void setRedTip(){
        String username = MyApplication.readUserName(this);
        boolean hasMessage= MyApplication.readBoolean(this, Constant.LOGIN_USER_INFO , username , false);
        tvRed.setVisibility(hasMessage ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            // finish自身
            ExchangeFlowActivity.this.finish();
            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }
    
    private void doExchangeFlow()
    {
        //跳转到选择流量包的界面，界面从底部滑出
        Bundle datas = new Bundle();
        datas.putSerializable("flowItem", (ArrayList<BigDecimal>) flowTargets);
        //ActivityUtils.getInstance().showActivityFromBottom(ExchangeFlowActivity.this, ExchangeItemActivity.class, datas);
        
        ActivityUtils.getInstance().showActivityForResult(ExchangeFlowActivity.this, RequestCodeCheckOut, ExchangeItemActivity.class, datas);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if( requestCode == RequestCodeCheckOut && resultCode==RESULT_OK ){
            new ExchangeFlowAsynTask().execute();
        }
    }

    @Override
    public void onFinishReceiver(MyBroadcastReceiver.ReceiverType type, Object msg) {

        if( type== MyBroadcastReceiver.ReceiverType.FlowAdd) {
            new ExchangeFlowAsynTask().execute();
        }else if( type == MyBroadcastReceiver.ReceiverType.requestFlow){
            setRedTip();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        myBroadcastReceiver.unregisterReceiver();
    }

    /**
     * 准备兑现 接口
     * 仅仅是为了获取可以兑换的流量列表
     * app端应该根据当前流量自己计算可以获得领取的最高流量
     * @类名称：ExchangeFlowAsynTask
     * @类描述：
     * @创建人：jinxiangdong
     * @修改人：
     * @修改时间：2015年6月9日 上午10:45:10
     * @修改备注：
     * @version:
     */
    public class ExchangeFlowAsynTask extends AsyncTask<Void, Void , FMPrepareCheckout>{

        @Override
        protected FMPrepareCheckout doInBackground(Void... params)
        {
            // TODO Auto-generated method stub
            String url = Constant.PREPARE_CHECKOUT;
            ObtainParamsMap obtainMap = new ObtainParamsMap( ExchangeFlowActivity.this );
            String paramString= obtainMap.getMap();
            Map<String , String> signMap = new HashMap<>();
            String sign = obtainMap.getSign(signMap);
            
            try {
                url += "?sign=" + URLEncoder.encode(sign, "UTF-8");
                url += paramString;

                Log.i("exchanger", url);

                String responseStr = HttpUtil.getInstance().doGet(url);
                FMPrepareCheckout result = new FMPrepareCheckout();
                JSONUtil<FMPrepareCheckout> jsonUtil = new JSONUtil<>();
                try {
                    result = jsonUtil.toBean(responseStr, result);
                } catch (JsonSyntaxException e) {
                    LogUtil.e("JSON_ERROR", e.getMessage());
                    result.setResultCode(0);
                    result.setResultDescription("解析json出错");
                }
                return result;


            } catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("exchange", e.getMessage());
                return null;
            }            
        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
            
            //rlWaiting.setVisibility(View.VISIBLE);
            //pgbarWaiting.setVisibility(View.VISIBLE);

            showProgress();
            
        }

        @Override
        protected void onPostExecute(FMPrepareCheckout result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            
            //rlWaiting.setVisibility(View.GONE);
            //pgbarWaiting.setVisibility(View.GONE);

            dismissProgress();
            
            if(result==null){
                ToastUtils.showLongToast(ExchangeFlowActivity.this, "请求失败。");
                return;
            }
            else if(1 == result.getResultCode()){
                BigDecimal balance= result.getResultData().getCurrentBalance();
                if( balance==null) balance=BigDecimal.ZERO;
                
                
                balance= balance.setScale(1,RoundingMode.HALF_UP);
                DecimalFormat format = new DecimalFormat("0.##");
                String balanceString = format.format(balance);
                MyApplication.writeString(ExchangeFlowActivity.this , "login_user_info", "user_balance", balanceString);
                if(Util.isM1(balance))
                {
                    flowCount.setText(  balanceString +"MB"); 
                    MainActivity.residualFlow.setText(application.readUserBalance(ExchangeFlowActivity.this) + "MB");
                }
                else
                {
                    //精确到GB
                    BigDecimal v = BigDecimal.valueOf(1024);
                    flowCount.setText(Util.decimalFloat(balance.divide(v).floatValue(), Constant.ACCURACY_3) +"GB");   
                    float flow = Float.parseFloat(application.readUserBalance(ExchangeFlowActivity.this))/1024;
                    MainActivity.residualFlow.setText(Util.decimalFloat(flow, Constant.ACCURACY_3) + "GB");
                }
                       
                flowTargets = result.getResultData().getTargets();
                BigDecimal minBalance=BigDecimal.valueOf(Integer.MAX_VALUE);
                Boolean isFind = false;
                flowCount.invalidate();
                
                if( flowTargets ==null){                
                    return;
                }
                
                for( BigDecimal item : flowTargets){
                    if( item.compareTo( balance) == 0 ){
                        
                        balance = balance .setScale(1, RoundingMode.HALF_UP );
                        
                        tip.setText("你可以兑现"+ balance+"M流量");                     
                        flowCount.setAngle(100);
                        break;
                    }else if( item.compareTo( balance) > 0 ){
                        if( item.compareTo(minBalance) < 0){
                            minBalance = item;
                            isFind =true;
                        }
                    }                
                }
                if( isFind)
                {
                    BigDecimal requireflow= minBalance.subtract(balance);
                    tip.setText("你还差"+ Util.decimalFloat(requireflow.floatValue(), Constant.ACCURACY_1) +"M流量，可兑换"+ minBalance +"M流量");
                    BigDecimal persent = balance.divide(minBalance, 1, RoundingMode.HALF_UP ); // balance * 1.0f/ minBalance*100;
                    persent=persent.multiply(new BigDecimal(100));
                    flowCount.setAngle( persent.floatValue() );
                    flowCount.invalidate();
                }
                else
                {
                    //获取最大的流量包，
                    BigDecimal max = getMaxFlow(flowTargets);
                    float balance1 = balance.subtract(max).floatValue();
                    balance1 = Util.decimalFloat(balance1, Constant.ACCURACY_1).floatValue();
                    Float f1 = new Float(balance1);
                    Float f2 = new Float(1024);
                    if(-1 == f1.compareTo(f2))
                    {
                        tip.setText("你可以兑换"+ max +"M流量包，兑换后剩余"+ Util.decimalFloat(balance.subtract(max).floatValue(), Constant.ACCURACY_1) +"M流量");
                    }
                    else
                    {
                        tip.setText("你可以兑换"+ max +"M流量包，兑换后剩余"+ (Util.decimalFloat(f1/1024, Constant.ACCURACY_3)) +"GB流量");
                    }
                    BigDecimal persent = balance.divide((max.add(balance)), 1, RoundingMode.HALF_UP ); // balance * 1.0f/ minBalance*100;
                    persent=persent.multiply(new BigDecimal(100));
                    flowCount.setAngle( persent.floatValue() );
                    flowCount.invalidate();
                    
                }
            }            
            else if (Constant.TOKEN_OVERDUE == result.getResultCode())
            {
                // 提示账号异地登陆，强制用户退出
                // 并跳转到登录界面
                ToastUtils.showLongToast(ExchangeFlowActivity.this, "账户登录过期，请重新登录");
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        ActivityUtils.getInstance().loginOutInActivity(
                                (Activity) ExchangeFlowActivity.this);
                    }
                }, 2000);
            }
        }        
    }
    
    /**
     * 
     *@方法描述：获取最大流量包
     *@方法名：getMaxFlow
     *@参数：@param flowTargets
     *@参数：@return
     *@返回：BigDecimal
     *@exception 
     *@since
     */
    private BigDecimal getMaxFlow(List<BigDecimal> flowTargets)
    {
        Collections.sort(flowTargets, new FlowComparator());
        return flowTargets.get(flowTargets.size()-1);
        
    }
    
    public class FlowComparator implements Comparator
    {

        @Override
        public int compare(Object lhs, Object rhs)
        {
            // TODO Auto-generated method stub
            BigDecimal flow1 = (BigDecimal) lhs;
            BigDecimal flow2 = (BigDecimal) rhs;
            return flow1.compareTo(flow2);
        }
        
    }
}
