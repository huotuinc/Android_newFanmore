package cy.com.morefan.ui.flow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.util.BitmapLoader;
import cy.com.morefan.view.CyButton;
import com.huotu.android.library.libedittext.EditText;
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
    private void accpetFlow()
    {
        if(TextUtils.isEmpty(flowText.getText()))
        {
            //
            flowText.setError("请输入流量");
            return;
        }
        else
        {
            // 求流量接口
            //new AccpetFlowAsyncTask().execute();
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
    private void sendFlow()
    {
        if(TextUtils.isEmpty(flowText.getText()))
        {
            //
            flowText.setError("请输入流量");
            return;
        }
        else
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(SendFlowActivity.this);
            dialog.setTitle("送流量");
            dialog.setMessage("我要送"+ flowText.getText().toString() + "M流量。");
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
            {
                
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // TODO Auto-generated method stub
                    // 送流量接口
                    //new SendFlowAsyncTask().execute();
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener()
            {
                
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // TODO Auto-generated method stub
                    
                }
            });
            
            dialog.show();
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
        operator = (TextView) this.findViewById(R.id.operator);
        operator.setText(bundle.getString("fanmoreTele"));
        isAccount = (TextView) this.findViewById(R.id.isAccount);
        phoneNumber = (TextView) this.findViewById(R.id.phoneNumber);
        phoneNumber.setText(bundle.getString("originMobile"));
        flows = (TextView) this.findViewById(R.id.flows);
        flows.setText(bundle.getInt("fanmoreBalance") + "M");
        String name = bundle.getString("fanmoreUsername");
        if(null == name)
        {
            isAccount.setText("非粉猫用户");
        }
        else if(null != name)
        {
            isAccount.setText("粉猫用户");
        }
        flowText = (EditText) this.findViewById(R.id.flowText);
        sendFlow = (Button) this.findViewById(R.id.sendFlow);
        sendFlow.setOnClickListener(this);
        accetFlow = (Button) this.findViewById(R.id.accetFlow);
        accetFlow.setOnClickListener(this);
        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);
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
    

}
