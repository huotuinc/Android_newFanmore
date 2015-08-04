package cy.com.morefan.ui.flow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMMakeProvide;
import cy.com.morefan.bean.FMMakeRequest;
import cy.com.morefan.bean.FMPrepareBuy;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.task.MakeProvideAsyncTask;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.BitmapLoader;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.CyButton;

import com.google.gson.JsonSyntaxException;
import com.huotu.android.library.libedittext.EditText;
import com.sina.weibo.sdk.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @类名称：SendFlowActivity
 * @类描述：赠送流量界面
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月12日 下午5:12:39
 * @修改备注：
 * @version:
 */
public class SendFlowActivity extends BaseActivity implements Callback,
        OnClickListener
{
    public Handler mHandler = new Handler(this);
    private Bundle bundle;
    private NetworkImageView img;
    private NetworkImageView imgRight;
    private TextView operator;
    private TextView isAccount;
    private EditText flowText;
    private Button sendFlow;
    private Button accetFlow;
    private CyButton backImage;
    private TextView title;
    private TextView phoneNumber;
    private TextView flows;
    //返回文字事件
    private TextView backText;
    //
    private float myFlow;
    
    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        switch (v.getId())
        {
        case R.id.backImage:
        {
            closeSelf(SendFlowActivity.this);
        }
            break;
        case R.id.sendFlow:
        {
            //送流量
            this.sendFlow();
            
        }
            break;
        case R.id.accetFlow:
        {
            //求流量
            this.accpetFlow();
            
        }
            break;
        case R.id.backtext:
        {
            closeSelf(SendFlowActivity.this);
        }
            break;
        default:
            break;
        }
    }
    
    /**
     * 
     *@方法描述：求流量
     *@方法名：accpetFlow
     *@参数：
     *@返回：void
     *@exception 
     *@since
     */
    private void accpetFlow() {
        if (TextUtils.isEmpty(flowText.getText())) {
            //
            flowText.setError("请输入流量");
            return;
        } else {
            int flow = 0;
            try {
                flow = Integer.parseInt(flowText.getText().toString());
            } catch (NumberFormatException ex) {
                ToastUtils.showLongToast(SendFlowActivity.this, "请输入正确的正数字");
                return;
            }
            if (flow <= 0) {
                ToastUtils.showLongToast(SendFlowActivity.this, "请输入大于零的正数字");
                return;
            }

            AlertDialog.Builder dialog = new AlertDialog.Builder(SendFlowActivity.this);
            LinearLayout llContent = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            llContent.setOrientation(LinearLayout.HORIZONTAL);
            params.setMargins(5, 5, 5, 5);
            llContent.setPadding(8, 10, 8, 0);
            llContent.setLayoutParams(params);

            final EditText etMessage = new EditText(this);
            etMessage.setHint("亲，送奴婢点流量吧");
            etMessage.setText("亲，送奴婢点流量吧");
            etMessage.setLayoutParams(params);
            etMessage.setSingleLine(true);
            llContent.addView(etMessage);
            dialog.setView(llContent);

            dialog.setTitle("向你的小伙伴说点什么");
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String to = "";
                    String message = etMessage.getText().toString().trim();
                    if (bundle.containsKey("fanmoreUsername")) {
                        to = bundle.getString("fanmoreUsername");
                    }
                    int flow = 0;
                    flow = Integer.parseInt(flowText.getText().toString());
                    String flowStr = String.valueOf(flow);
                    new MakeRequestAsyncTask(SendFlowActivity.this, to, message, flowStr).execute();
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                }
            });

            dialog.show();
        }
    }
    
    /**
     * 
     *@方法描述：送流量接口
     *@方法名：sendFlow
     *@参数：
     *@返回：void
     *@exception 
     *@since
     */
    private void sendFlow() {
        if (TextUtils.isEmpty(flowText.getText())) {
            //
            flowText.setError("请输入流量");
            return;
        }

        int flow =0;
        try
        {
            flow=Integer.parseInt(flowText.getText().toString());
            if( flow<=0){
                ToastUtils.showLongToast(SendFlowActivity.this,"请输入大于零的正数字");
                return;
            }
        }catch (NumberFormatException ex){
            ToastUtils.showLongToast(SendFlowActivity.this,"请输入正确的正数字");
            return;
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(SendFlowActivity.this);

        LinearLayout llContent = new LinearLayout(this);
        LinearLayout.LayoutParams params =new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        llContent.setOrientation(LinearLayout.HORIZONTAL);
        params.setMargins(5, 5, 5, 5);
        llContent.setPadding(8,10,8,0);
        llContent.setLayoutParams(params);

        final EditText etMessage=new EditText(this);
        etMessage.setHint("朕赏你点流量，还不谢恩");
        etMessage.setText("朕赏你点流量，还不谢恩");
        //params =new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
        //        RelativeLayout.LayoutParams.WRAP_CONTENT);
        //params.setMargins(4, 2, 4, 2);
        //etMessage.setPadding(4, 4, 4, 4);
        etMessage.setLayoutParams(params);
        etMessage.setSingleLine(true);

        llContent.addView(etMessage);

        dialog.setView(llContent);

        dialog.setTitle("向你的小伙伴说点什么");
        //dialog.setMessage("我要送" + flowText.getText().toString() + "M流量。");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                // 送流量接口
                //new SendFlowAsyncTask().execute();
                String mobile="";
                if( bundle.containsKey("originMobile")) {
                    mobile = bundle.getString("originMobile");
                }
                String message=etMessage.getText().toString().trim();//"朕赏你点流量,还不谢恩";
                int flow = Integer.parseInt(flowText.getText().toString());
                String flowStr= String.valueOf(flow);
                new MakeProvideAsyncTask(SendFlowActivity.this , mHandler , mobile , flowStr , message ).execute();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });

        dialog.show();

    }



    @Override
    public boolean handleMessage(Message msg)
    {
        // TODO Auto-generated method stub
        switch ( msg.what){
            case MakeProvideAsyncTask.SUCCESS:
                ToastUtils.showLongToast(this, msg.obj.toString());

                Bundle bd= msg.getData();
                int flow=0;
                if( bd !=null && bd.containsKey("flow")) {
                    flow = Integer.valueOf(bd.getString("flow"));
                }
                myFlow = myFlow-flow;
                setFlow(myFlow);

                break;
            case MakeProvideAsyncTask.FAIL:
                ToastUtils.showLongToast(this,msg.obj.toString());
                break;
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        this.setContentView(R.layout.send_flow_ui);
        bundle = SendFlowActivity.this.getIntent().getExtras();
        initView();
    }
    
    private void initView()
    {
        backImage = (CyButton) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(this);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("流量分享");
        //用户的logo
        img = (NetworkImageView) this.findViewById(R.id.img);
        String imageUrl = MyApplication.readUserLogo(this);
        BitmapLoader.create().displayUrl(SendFlowActivity.this, img, imageUrl,R.drawable.mrtou,R.drawable.mrtou);
        imgRight = (NetworkImageView) this.findViewById(R.id.imgRight);
        BitmapLoader.create().displayUrl(SendFlowActivity.this, imgRight, bundle.getString("fanmorePicUrl"),R.drawable.mrtou,R.drawable.mrtou);
        //operator = (TextView) this.findViewById(R.id.operator);
        //operator.setText(bundle.getString("fanmoreTele"));
        isAccount = (TextView) this.findViewById(R.id.isAccount);
        phoneNumber = (TextView) this.findViewById(R.id.phoneNumber);
        phoneNumber.setText(bundle.getString("originMobile"));
        flows = (TextView) this.findViewById(R.id.flows);
        //flows.setText(bundle.getFloat("fanmoreBalance") + "M");

        myFlow = Float.parseFloat( MyApplication.readUserBalance(this));
        setFlow(myFlow);

        flowText = (EditText) this.findViewById(R.id.flowText);
        sendFlow = (Button) this.findViewById(R.id.sendFlow);
        sendFlow.setOnClickListener(this);
        accetFlow = (Button) this.findViewById(R.id.accetFlow);
        accetFlow.setOnClickListener(this);
        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);
        String name = bundle.getString("fanmoreUsername");
        if(null == name)
        {
            isAccount.setText("非粉猫用户");
            accetFlow.setVisibility(View.GONE);
        }
        else if(null != name)
        {
            isAccount.setText("粉猫用户");
            accetFlow.setVisibility(View.VISIBLE);
        }
    }

    private void setFlow( float flow ){
        if (Util.isM( String.valueOf( flow )))
        {
            flows.setText( Util.decimalFloat( flow ,"0.##") + "MB");
        } else
        {
            // 精确到GB
            float temp = flow / 1024;
            flows.setText(Util.decimalFloat(temp,Constant.ACCURACY_3) + "GB");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            // finish自身
            SendFlowActivity.this.finish();
            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }
    
    class MakeRequestAsyncTask extends AsyncTask<Void,Void, FMMakeRequest>{
        Context context;
        String to;
        String flow;
        String message;

        public MakeRequestAsyncTask(Context context , String to ,String message, String flow){
            this.context=context;
            this.to=to;
            this.message=message;
            this.flow=flow;
        }

        @Override
        protected FMMakeRequest doInBackground(Void... params) {
            FMMakeRequest result=null;
            try {
                String url = Constant.MAKEREQUEST;

                ObtainParamsMap obtainMap = new ObtainParamsMap(
                        SendFlowActivity.this);
                String paramString = obtainMap.getMap();
                Map<String, String> signMap = new HashMap<>();
                signMap.put("to", to);
                signMap.put("message", message);
                signMap.put("fc", flow);
                String sign = obtainMap.getSign(signMap);
                url +="?to="+ URLEncoder.encode(to,"UTF-8");
                url +="&message="+URLEncoder.encode(message,"UTF-8");
                url +="&fc="+URLEncoder.encode(flow,"UTF-8");
                url += paramString;
                url +="&sign=" + URLEncoder.encode(sign, "UTF-8");

                String responseStr = HttpUtil.getInstance().doGet(url);
                result = new FMMakeRequest();
                JSONUtil<FMMakeRequest> jsonUtil = new JSONUtil<>();
                result= jsonUtil.toBean(responseStr , result );
                return result;
            }
            catch (JsonSyntaxException e)
            {
                LogUtil.e("JSON_ERROR", e.getMessage());
                result= new FMMakeRequest();
                result.setResultCode(0);
                result.setResultDescription("解析json出错");
                return result;
            }
            catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(FMMakeRequest fmMakeRequest) {
            super.onPostExecute(fmMakeRequest);
            SendFlowActivity.this.dismissProgress();

            if( fmMakeRequest==null){
                ToastUtils.showLongToast(SendFlowActivity.this,"请求失败");
                return;
            }
            if( fmMakeRequest.getSystemResultCode() != 1){
                ToastUtils.showLongToast(SendFlowActivity.this, fmMakeRequest.getSystemResultDescription());
                return;
            }
            if( Constant.TOKEN_OVERDUE == fmMakeRequest.getResultCode()){
                // 提示账号异地登陆，强制用户退出
                // 并跳转到登录界面
                ToastUtils.showLongToast(SendFlowActivity.this, "账户登录过期，请重新登录");
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        ActivityUtils.getInstance().loginOutInActivity(
                                (Activity) SendFlowActivity.this);
                    }
                }, 2000);
                return;
            }
            if( 1!= fmMakeRequest.getResultCode()){
                ToastUtils.showLongToast(SendFlowActivity.this, fmMakeRequest.getResultDescription());
                return;
            }

            ToastUtils.showLongToast(SendFlowActivity.this, "求流量完成");
        }

        @Override
        protected void onPreExecute() {
            SendFlowActivity.this.showProgress("正在发送请求...");
        }
    }
}
