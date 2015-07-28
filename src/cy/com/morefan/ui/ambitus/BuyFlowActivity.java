package cy.com.morefan.ui.ambitus;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.AliPayResult;
import cy.com.morefan.bean.FMPrepareBuy;
import cy.com.morefan.bean.PayGoodBean;
import cy.com.morefan.bean.Purchase;
import cy.com.morefan.bean.WXPayResult;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.task.DeliveryGoodAsyncTask;
import cy.com.morefan.ui.account.MsgCenterActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.AliPayUtil;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.SystemTools;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.util.WXPayUtil;
import cy.com.morefan.util.WXPayUtilEx;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.wxapi.WXPayEntryActivity;

/**
 * 
 * @类名称：BuyFlowActivity
 * @类描述：购买流量界面
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月10日 上午9:58:58
 * @修改备注：
 * @version:
 */
public class BuyFlowActivity extends BaseActivity implements Callback,
        OnClickListener, OnItemClickListener , MyBroadcastReceiver.BroadcastListener
{

    private CyButton backImage;

    private TextView title;

    private GridView flowGrid;

    private TextView operator;

    private TextView oldPrice;

    private TextView newPrice;

    private Button rechargeBtn;

    private Purchase selectedPurchase = null;// 选中的流量

    private FlowCAdapter caAdapter = null;

    private RelativeLayout rlWaiting=null;
    
    private ProgressBar pgbarWaiting;

    private TextView phone;

    private MyBroadcastReceiver myBroadcastReceiver;

    // 返回文字事件
    private TextView backText;

    private List<Purchase> datas = new ArrayList<Purchase>();

    private PopupWindow payPopupWindow = null;

    private Handler handler = new Handler(this);

    private FMPrepareBuy result;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backImage: {
                closeSelf(BuyFlowActivity.this);
            }
            break;
            case R.id.rechargeBtn: {
                // 充值
                doRecharge();
            }
            break;
            case R.id.btnAliPay: {
                aliPay();
            }
            break;
            case R.id.btnWXPay: {
                wxPay();
            }
            break;
            case R.id.btnCancel: {
                hidePopup();
            }
            break;
            case R.id.backtext: {
                closeSelf(BuyFlowActivity.this);
            }
            break;
            default:
                break;
        }
    }

    /**
     * 微信支付
     */
    private void wxPay() {
        if (null == selectedPurchase) {
            ToastUtils.showLongToast(BuyFlowActivity.this, "请选择需要购买的流量套餐");
            return;
        }
        if (result == null) {
            ToastUtils.showLongToast(BuyFlowActivity.this, "支付信息空,无法进行支付操作");
            return;
        }
        if (null == result.getResultData().getWxpayNotifyUri() || result.getResultData().getWxpayNotifyUri().length() < 1) {
            ToastUtils.showLongToast(BuyFlowActivity.this, "支付通知地址空，无法进行支付操作");
            return;
        }

        //
        String body = selectedPurchase.getM() + "M，售价:￥"+ selectedPurchase.getPrice().toString();
        BigDecimal price = selectedPurchase.getPrice().multiply(new BigDecimal(100)); //单位分，正数
        String priceStr = String.valueOf(price.intValue());
        long productid = selectedPurchase.getPurchaseid();
        int producttype = 0;//套餐
        new WXPayAsyncTask(handler, body, priceStr, producttype, productid).execute();
    }

    /**
     * 支付宝
     *
     * @创建人：jinxiangdong
     * @修改时间：2015年6月16日 上午11:18:01
     * @方法描述：
     * @方法名：aliPay
     * @参数：
     * @返回：void
     * @exception
     * @since
     */
    private void aliPay()
    {
        if (null == selectedPurchase)
        {
            ToastUtils.showLongToast(BuyFlowActivity.this, "请选择需要购买的流量套餐");
            return;
        }
        if (result == null)
        {
            ToastUtils.showLongToast(BuyFlowActivity.this, "支付信息空,无法进行支付操作");
            return;
        }
        if( null == result.getResultData().getAlipayNotifyUri() || result.getResultData().getWxpayNotifyUri().length()<1 ){
            ToastUtils.showLongToast(BuyFlowActivity.this, "支付通知地址空,无法进行支付操作");
            return;
        }

        AliPayUtil aliPay = new AliPayUtil(this, handler);
        String body = selectedPurchase.getM() + "M，售价:￥" + selectedPurchase.getPrice().toString();
        String subject = "粉猫APP购买流量";
        String price = selectedPurchase.getPrice().toString();
        int productType=0;
        long productId= selectedPurchase.getPurchaseid();
        String notifyurl= result.getResultData().getAlipayNotifyUri();

        aliPay.pay(subject, body, price, notifyurl, productType, productId);
    }

    /**
     * 弹出支付界面
     */
    private void doRecharge()
    {
        showPopup();
    }

    // 显示菜单
    private void showPopup()
    {
        if (payPopupWindow == null)
            initPopupWindow();
        // 设置位置
        payPopupWindow.showAtLocation(rechargeBtn, Gravity.BOTTOM, 0, 0); // 设置在屏幕中的显示位置
    }

    private void hidePopup(){
        if( payPopupWindow==null)return;
        payPopupWindow.dismiss();
        payPopupWindow=null;
    }

    private void initPopupWindow()
    {
        LayoutInflater mInflater = LayoutInflater.from(this);
        View layout = mInflater.inflate(R.layout.pop_pay, null);
        layout.findViewById(R.id.btnAliPay).setOnClickListener(this);
        layout.findViewById(R.id.btnWXPay).setOnClickListener(this);
        layout.findViewById(R.id.btnCancel).setOnClickListener(this);
        // layout.findViewById(R.id.rlPay).getBackground().setAlpha(220);
        payPopupWindow = new PopupWindow(layout,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        payPopupWindow.setFocusable(true);
        payPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置PopupWindow显示和隐藏时的动画
        payPopupWindow.setAnimationStyle(R.style.AnimationPop);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case AliPayUtil.SDK_PAY_FLAG: {
                DealAliPayResult(msg);
            }
            break;
            case WXPayUtil.SDK_WX_ACCESSTOKEN_FAIL: {
                String info = msg.obj.toString();
                ToastUtils.showLongToast(BuyFlowActivity.this, info);
            }
            break;
//            case WXPayUtilEx.SDK_WXPAY_PAY: {
//
//            }
//            break;
            case DeliveryGoodAsyncTask.PAY_ERROR: {
                ToastUtils.showLongToast(BuyFlowActivity.this, msg.obj.toString());
            }
            break;
            case DeliveryGoodAsyncTask.PAY_OK: {
                hidePopup();
                MyBroadcastReceiver.sendBroadcast(BuyFlowActivity.this, MyBroadcastReceiver.ACTION_FLOW_ADD);
                ToastUtils.showLongToast(BuyFlowActivity.this, msg.obj.toString());
                BuyFlowActivity.this.finish();
            }
            break;
            default:
                break;
        }

        return false;
    }

    private void DealAliPayResult(Message msg)
    {
        PayGoodBean payData= (PayGoodBean)msg.obj;

        AliPayResult payResult = new AliPayResult((String) payData.getTag());
        // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
        String resultInfo = payResult.getResult();

        String resultStatus = payResult.getResultStatus();

        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
        if (TextUtils.equals(resultStatus, "9000"))
        {
            //Toast.makeText(BuyFlowActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
            new DeliveryGoodAsyncTask( BuyFlowActivity.this , handler , payData.getOrderNo() , payData.getProductType(),payData.getProductId() ).execute();
            return;
        } else
        {
            // 判断resultStatus 为非“9000”则代表可能支付失败
            // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
            if (TextUtils.equals(resultStatus, "8000"))
            {
                Toast.makeText(BuyFlowActivity.this, "支付结果确认中",
                        Toast.LENGTH_SHORT).show();
                hidePopup();
            }else if(TextUtils.equals(resultStatus,"6001")){
                String paymsg= payResult.getMemo();
                ToastUtils.showLongToast(BuyFlowActivity.this, paymsg);
                hidePopup();
            }
            else
            {
                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                Toast.makeText(BuyFlowActivity.this, "支付失败" , Toast.LENGTH_SHORT)
                        .show();
                hidePopup();
            }
        }
    }

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        this.setContentView(R.layout.buy_flow_ui);
        this.initView();
        this.initGridView();
    }

    private void initView()
    {
        backImage = (CyButton) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(this);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("购买流量");
        flowGrid = (GridView) this.findViewById(R.id.flowGrid);
        flowGrid.setOnItemClickListener(this);
        operator = (TextView) this.findViewById(R.id.operator);
        oldPrice = (TextView) this.findViewById(R.id.oldPrice);
        newPrice = (TextView) this.findViewById(R.id.newPrice);
        rechargeBtn = (Button) this.findViewById(R.id.rechargeBtn);
        rechargeBtn.setOnClickListener(this);
        rechargeBtn.setEnabled(false);
        phone = (TextView) this.findViewById(R.id.phone);
        pgbarWaiting = (ProgressBar) this.findViewById(R.id.pgbarWaiting);
        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);
        rlWaiting =(RelativeLayout)this.findViewById(R.id.rlWaiting);

        myBroadcastReceiver=new MyBroadcastReceiver(this,this, MyBroadcastReceiver.ACTION_WX_PAY_CALLBACK);
    }

    private void initGridView()
    {
        // 获取数据
        getData();
        // 新建适配器
        caAdapter = new FlowCAdapter();
        // 配置适配器
        flowGrid.setAdapter(caAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if( myBroadcastReceiver==null)return;
        myBroadcastReceiver.unregisterReceiver();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            // finish自身
            BuyFlowActivity.this.finish();
            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

    private void getData()
    {
        new BuyFlowAsyncTask().execute();
    }

    @Override
    public void onFinishReceiver(MyBroadcastReceiver.ReceiverType type, Object msg) {
        if( type == MyBroadcastReceiver.ReceiverType.WX_Pay_Callback) {
            this.finish();
        }
    }

    class FlowCAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return datas.size();
        }

        @Override
        public Object getItem(int position)
        {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = null;
            if (convertView == null)
            {
                holder = new ViewHolder();
                convertView = View.inflate(BuyFlowActivity.this,
                        R.layout.buy_flow_item, null);
                holder.buyFlowC = (TextView) convertView
                        .findViewById(R.id.buyFlowC);
                convertView.setTag(holder);
            } else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            // 加载图片
            if (datas.size() > 0)
            {
                Purchase entity = datas.get(position);

                holder.buyFlowC.setText(entity.getM() + "M");

                if (null != selectedPurchase
                        && entity.getPurchaseid().equals(
                                selectedPurchase.getPurchaseid()))
                {
                    holder.buyFlowC
                            .setBackgroundResource(R.drawable.shape_stoke_blue);
                    holder.buyFlowC.setTextColor(Color.WHITE);
                } else
                {
                    holder.buyFlowC
                            .setBackgroundResource(R.drawable.shape_stoke_gray);
                    holder.buyFlowC.setTextColor(Color.BLACK);
                }
            }
            return convertView;
        }

        class ViewHolder
        {
            TextView buyFlowC;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        if (datas.size() < 1)
            return;
        selectedPurchase = datas.get(position);

        setFlowInfo();
    }

    private void setFlowInfo()
    {
        if (selectedPurchase == null)
            return;

        caAdapter.notifyDataSetChanged();

        oldPrice.setText(selectedPurchase.getMsg());

        oldPrice.setVisibility(View.VISIBLE);

        newPrice.setText("售价：￥" + selectedPurchase.getPrice());

        newPrice.setVisibility(View.VISIBLE);
    }

    /**
     * 获得 可购买的流量 接口
     * 
     * @类名称：BuyFlowAsyncTask
     * @类描述：
     * @创建人：jinxiangdong
     * @修改人：
     * @修改时间：2015年6月9日 下午4:04:38
     * @修改备注：
     * @version:
     */
    class BuyFlowAsyncTask extends AsyncTask<Void, Void, FMPrepareBuy>
    {

        @Override
        protected FMPrepareBuy doInBackground(Void... params)
        {
            String url = Constant.PREPARE_BUY;
            ObtainParamsMap obtainMap = new ObtainParamsMap(
                    BuyFlowActivity.this);
            String paramString = obtainMap.getMap();
            Map<String, String> signMap = new HashMap<>();
            String sign = obtainMap.getSign(signMap);

            try
            {
                url += "?sign=" + URLEncoder.encode(sign, "UTF-8");
                url += paramString;

                Log.i("buyflow", url);

                String responseStr = HttpUtil.getInstance().doGet(url);
                // FMPrepareBuy result = new FMPrepareBuy();
                result = new FMPrepareBuy();
                JSONUtil<FMPrepareBuy> jsonUtil = new JSONUtil<>();
                try
                {
                    result = jsonUtil.toBean(responseStr, result);
                } catch (JsonSyntaxException e)
                {
                    LogUtil.e("JSON_ERROR", e.getMessage());
                    result.setResultCode(0);
                    result.setResultDescription("解析json出错");
                }
                return result;

            } catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("buyflow", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            BuyFlowActivity.this.showProgress();
        }

        @Override
        protected void onPostExecute(FMPrepareBuy result)
        {
            super.onPostExecute(result);
            BuyFlowActivity.this.dismissProgress();

            if (result == null)
            {
                ToastUtils.showLongToast(BuyFlowActivity.this, "请求失败。");
                return;
            }else if(1 != result.getSystemResultCode()){
                ToastUtils.showLongToast(BuyFlowActivity.this, result.getSystemResultDescription());
                return;
            }
            else if (1 == result.getResultCode())
            {
                String mobileMsg = result.getResultData().getMobileMsg();
                operator.setText(mobileMsg);
                String phoneString = MyApplication.readString(
                        BuyFlowActivity.this, "login_user_info", "user_mobile");

                phone.setText(phoneString);
                datas = result.getResultData().getPurchases();
                // 并切换按钮样式
                SystemTools.loadBackground(rechargeBtn,
                        BuyFlowActivity.this.getResources().getDrawable(R.drawable.btn_red_sel));
                rechargeBtn.setEnabled(true);
                if (datas != null && datas.size() > 0)
                {
                    selectedPurchase = datas.get(0);
                }
                setFlowInfo();
                // caAdapter.notifyDataSetChanged();
            } else if (null == result.getResultData())
            {
                ToastUtils.showLongToast(BuyFlowActivity.this, "返回的数据不正确。");
                return;
            } else if (Constant.TOKEN_OVERDUE == result.getResultCode())
            {
                // 提示账号异地登陆，强制用户退出
                // 并跳转到登录界面
                ToastUtils.showLongToast(BuyFlowActivity.this, "账户登录过期，请重新登录");
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        ActivityUtils.getInstance().loginOutInActivity(
                                (Activity) BuyFlowActivity.this);
                    }
                }, 2000);
            }else if( 1 != result.getResultCode()){
                ToastUtils.showLongToast(BuyFlowActivity.this, result.getResultDescription());
                return;
            }

        }
    }
   
    class WXPayAsyncTask extends AsyncTask<Void, Void, WXPayResult>
    {
        private Handler handler;
        private String body;
        private String price;
        private int productType;
        private long productId;

        public WXPayAsyncTask(Handler handler , String body , String price , int productType, long productId )
        {
            this.handler = handler;
            this.body=body;
            this.price=price;
            this.productType=productType;
            this.productId= productId;
        }

        @Override
        protected void onPreExecute()
        {
            BuyFlowActivity.this.showProgress();
        }

        @Override
        protected void onPostExecute(WXPayResult result)
        {
            BuyFlowActivity.this.dismissProgress();
            
            if( result !=null && result.getCode() != 1 ){
                ToastUtils.showLongToast(BuyFlowActivity.this, result.getMessage());
            }
        }

        @Override
        protected WXPayResult doInBackground(Void... params)
        {
            WXPayResult payResult = new cy.com.morefan.bean.WXPayResult();
            try
            {
                WXPayUtilEx wxPay = new WXPayUtilEx(BuyFlowActivity.this,
                        handler, result.getResultData().getWxpayNotifyUri()  );

               payResult =  wxPay.pay( this.body , this.price , productType , productId );
                
            } catch (Exception ex)
            {
                payResult.setCode(0);
                payResult.setMessage(ex.getMessage());
            }
            return payResult;
        }
    }
}
