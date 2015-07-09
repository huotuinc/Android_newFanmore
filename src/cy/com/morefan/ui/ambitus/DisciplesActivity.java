package cy.com.morefan.ui.ambitus;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.TextView;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.frag.FraDisciplesContribution;
import cy.com.morefan.frag.FraDisciplesTime;
import cy.com.morefan.frag.FragmentTabAdapter;
import cy.com.morefan.view.CyButton;
/**
 * 
 * @类名称：DisciplesActivity
 * @类描述：徒弟界面
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月10日 上午9:59:33
 * @修改备注：
 * @version:
 */
public class DisciplesActivity extends BaseActivity implements Callback,
        OnClickListener
{

    private CyButton backImage;//返回按钮
    //返回文字事件
    private TextView backText;
    private RadioGroup title;//切换组件
    private TextView functionBtn;//刷新按钮
    // 列表刷新提示
    private TextView listNotice;
    //内容主题
    public List<Fragment> fragments = new ArrayList<Fragment>();
    
    public Handler mHandler = new Handler(this);
    
    @Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        this.setContentView(R.layout.disciples_ui);
        this.initView();
        
    }
    
    private void initView()
    {
        backImage = (CyButton) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(this);
        title = (RadioGroup) this.findViewById(R.id.title);
        functionBtn = (TextView) this.findViewById(R.id.functionBtn);
        //functionBtn.setOnClickListener(this);
        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);
        listNotice = (TextView) this.findViewById(R.id.dsisciplesNotice);
        fragments.add(new FraDisciplesTime(listNotice));
        fragments.add(new FraDisciplesContribution(listNotice));
        FragmentTabAdapter tabAdapter = new FragmentTabAdapter(this, fragments, R.id.tab_content, title);
        tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener(){
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
                
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        switch (v.getId())
        {
        case R.id.backImage:
        {
            closeSelf(DisciplesActivity.this);
        }
            break;
        case R.id.backtext:
        {
            closeSelf(DisciplesActivity.this);
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
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            // finish自身
            DisciplesActivity.this.finish();
            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

}
