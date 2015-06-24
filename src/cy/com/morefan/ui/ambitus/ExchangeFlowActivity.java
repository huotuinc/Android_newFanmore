package cy.com.morefan.ui.ambitus;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.Bidi;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import android.content.Intent;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMPrepareCheckout;
import cy.com.morefan.bean.FMPrepareCheckout.InnerData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.ui.flow.FriendsResActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;
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
        OnClickListener
{

    public Handler mHandler = new Handler(this);

    private RingView flowCount;

    //private String flowSize;

    private CyButton backImage;

    private TextView title;

    private TextView functionBtn;

    //private ViewFlipper selectFlow;

    private Button exchangeBtn;

    private Button buyFlowBtn;

//    private Animation leftInAnimation;
//
//    private Animation leftOutAnimation;
//
//    private Animation rightInAnimation;
//
//    private Animation rightOutAnimation;
//    
//    private PopupWindow popupWindow;
//    
//    private String selectedFlow="";
    
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
    
    
    //private GestureDetector detector; // 手势检测

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
        
        flowCount.setText( balance +"M" );

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
        friends.setText("好友请求(6)");
        friends.setOnClickListener(this);
        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);
        new ExchangeFlowAsynTask().execute();
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
        
        ActivityUtils.getInstance().showActivityForResult(ExchangeFlowActivity.this, RequestCodeCheckOut , ExchangeItemActivity.class, datas);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if( requestCode == RequestCodeCheckOut && resultCode==RESULT_OK ){
            new ExchangeFlowAsynTask().execute();
        }
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
    class ExchangeFlowAsynTask extends AsyncTask<Void, Void , FMPrepareCheckout>{

        @Override
        protected FMPrepareCheckout doInBackground(Void... params)
        {
            // TODO Auto-generated method stub
            String url = Constant.PREPARE_CHECKOUT;
            ObtainParamsMap obtainMap = new ObtainParamsMap( ExchangeFlowActivity.this );
            String paramString= obtainMap.getMap();
            Map<String , String> signMap = new HashMap<>();
            String sign = obtainMap.getSign(signMap);
            
            try
            {
                url += "?sign="+ URLEncoder.encode( sign , "UTF-8");
                url += paramString;
                
                Log.i("exchanger", url);
                
                if( Constant.IS_PRODUCTION_ENVIRONMENT){
                
                    String responseStr=HttpUtil.getInstance().doGet(url);
                    FMPrepareCheckout result= new FMPrepareCheckout();
                    JSONUtil<FMPrepareCheckout> jsonUtil=new JSONUtil<>();
                    try
                    {
                        result = jsonUtil.toBean(responseStr, result);   
                    }
                    catch(JsonSyntaxException e)
                    {
                       LogUtil.e("JSON_ERROR", e.getMessage());
                       result.setResultCode(0);
                       result.setResultDescription("解析json出错");
                    }
                    return result;                
                }
                else {
                    FMPrepareCheckout result= new FMPrepareCheckout();
                    //result.setResultData(new InnerData() );
                    
                    result.setSystemResultCode(1);
                    result.setResultCode(1);
                    result.getResultData().setCurrentBalance( new BigDecimal(110));//
                    List<BigDecimal> targets=new ArrayList<BigDecimal>();
                    targets.add( new BigDecimal(30));
                    targets.add(new BigDecimal(50));
                    targets.add(new BigDecimal(80));
                    targets.add(new BigDecimal(100));
                    targets.add(new BigDecimal(130));
                    targets.add(new BigDecimal(150));
                    targets.add(new BigDecimal(200));
                    targets.add(new BigDecimal(500));
                    targets.add(new BigDecimal(1000));
                    result.getResultData().setTargets(targets);
                    return result;
                }
                
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
            
            rlWaiting.setVisibility(View.VISIBLE);
            pgbarWaiting.setVisibility(View.VISIBLE);
            
        }

        @Override
        protected void onPostExecute(FMPrepareCheckout result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            
            rlWaiting.setVisibility(View.GONE);
            pgbarWaiting.setVisibility(View.GONE);
            
            if(result==null){
                ToastUtils.showLongToast(ExchangeFlowActivity.this, "请求失败。");
                return;
            }
            if( 1 !=result.getSystemResultCode()){
                ToastUtils.showLongToast(ExchangeFlowActivity.this, result.getSystemResultDescription());
                return;
            }
            if(1!= result.getResultCode()){
                ToastUtils.showLongToast(ExchangeFlowActivity.this, result.getResultDescription());
                return;
            }            
            
            BigDecimal balance= result.getResultData().getCurrentBalance();
            if( balance==null) balance=BigDecimal.ZERO;
            
            
            balance= balance.setScale(1,RoundingMode.HALF_UP);
            DecimalFormat format = new DecimalFormat("0.##");
            String balanceString = format.format(balance);
            MyApplication.writeString(ExchangeFlowActivity.this , "login_user_info", "user_balance", balanceString);
            
            flowCount.setText(  balanceString +"M");            
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
            if( isFind){
                BigDecimal requireflow= minBalance.subtract(balance);
                tip.setText("你还差"+ requireflow +"M流量，可兑换"+ minBalance +"M流量");
                BigDecimal persent = balance.divide(minBalance, 1, RoundingMode.HALF_UP ); // balance * 1.0f/ minBalance*100;
                persent=persent.multiply(new BigDecimal(100));
                flowCount.setAngle( persent.floatValue() );
                flowCount.invalidate();
            }            
        }        
    }
}
