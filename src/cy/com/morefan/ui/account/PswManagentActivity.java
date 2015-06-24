package cy.com.morefan.ui.account;

import java.util.Map;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMModifyPsw;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.ui.user.ForgetActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.EncryptUtil;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.view.NoticeDialog;
import cy.lib.edittext.CyEditText;
/**
 * 
 * @类名称：PswManagentActivity
 * @类描述：密码修改界面
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月10日 上午9:58:49
 * @修改备注：
 * @version:
 */
public class PswManagentActivity extends BaseActivity implements Callback,
        OnClickListener
{

    Handler mHandler = new Handler(this);

    private TextView titleName;

    private CyEditText edtOld;// 旧密码

    private CyEditText edtNew;// 新密码

    private CyEditText edtNewRe;// 新密码确认

    private CyButton backImage;

    private NoticeDialog noticeDialog;

    private TextView commit;

    private TextView forgetPsw;
    //返回文字事件
    private TextView backText;

    @Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        this.setContentView(R.layout.psw_change_ui);

        initView();
    }

    private void initView()
    {
        titleName = (TextView) this.findViewById(R.id.title);
        backImage = (CyButton) this.findViewById(R.id.btnBack);
        commit = (TextView) this.findViewById(R.id.backImage);
        forgetPsw = (TextView) this.findViewById(R.id.txtForget);

        edtOld = (CyEditText) this.findViewById(R.id.edtOld);
        edtNew = (CyEditText) this.findViewById(R.id.edtNew);
        edtNewRe = (CyEditText) this.findViewById(R.id.edtNewRes);

        titleName.setText("修改密码");
        backImage.setOnClickListener(this);
        commit.setOnClickListener(this);
        forgetPsw.setOnClickListener(this);
        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub

        switch (v.getId())
        {
        case R.id.btnBack:
        {

            closeSelf(PswManagentActivity.this);
        }
            break;
        case R.id.backImage:
        {

            saveNewPsw();
        }
            break;
        case R.id.txtForget:
        {

            losePsw();
        }
            break;
        case R.id.backtext:
        {
            closeSelf(PswManagentActivity.this);
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

    private void saveNewPsw()
    {
        // 判断数据完整性
        if (canModifyPsw())
        {
            String url = Constant.MODIFYPSW_INTEFACE;
            new ModifyPswAsyncTask().execute(url);
        }
    }

    private void losePsw()
    {
        ActivityUtils.getInstance().skipActivity(PswManagentActivity.this,
                ForgetActivity.class);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            // finish自身
            PswManagentActivity.this.finish();
            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 
     * @方法描述：判断数据完整性
     * @方法名：canModifyPsw
     * @参数：@return
     * @返回：boolean
     * @exception
     * @since
     */
    private boolean canModifyPsw()
    {
        if (TextUtils.isEmpty(edtOld.getText()))
        {
            edtOld.setError("原密码不能为空！");
            return false;
        } else if (TextUtils.isEmpty(edtNew.getText()))
        {
            edtNew.setError("新密码不能为空！");
            return false;
        } else if (TextUtils.isEmpty(edtNewRe.getText()))
        {
            edtNewRe.setError("新密码确认不能为空！");
            return false;
        } else if (!edtNew.getText().toString()
                .equals(edtNewRe.getText().toString()))
        {
            edtNewRe.setError("两次熟的密码不同！");
            return false;
        } else
        {
            return true;
        }
    }

    class ModifyPswAsyncTask extends AsyncTask<String, Void, FMModifyPsw>
    {

        Map<String, String> paramMap = null;
        

        @Override
        protected FMModifyPsw doInBackground(String... params)
        {
            // TODO Auto-generated method stub
            FMModifyPsw modifyPswBean = new FMModifyPsw();
            JSONUtil<FMModifyPsw> jsonUtil = new JSONUtil<FMModifyPsw>();
            String url = params[0];
            String json =  HttpUtil.getInstance().doPost(url, paramMap);
            try
            {
                modifyPswBean =  jsonUtil.toBean(json, modifyPswBean);
            }
            catch(JsonSyntaxException e)
            {
               LogUtil.e("JSON_ERROR", e.getMessage());
               modifyPswBean.setResultCode(0);
               modifyPswBean.setResultDescription("解析json出错");
            }
            return modifyPswBean;
        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
            // 封转参数
            ObtainParamsMap obtainMap = new ObtainParamsMap(
                    PswManagentActivity.this);
            paramMap = obtainMap.obtainMap();

            // 注册是POST提交
            paramMap.put(
                    "password",
                    EncryptUtil.getInstance().encryptMd532(
                            edtOld.getText().toString()));
            paramMap.put(
                    "newPassword",
                    EncryptUtil.getInstance().encryptMd532(
                            edtNew.getText().toString()));
            // 封装sign
            String signStr = obtainMap.getSign(paramMap);
            paramMap.put("sign", signStr);
        }

        @Override
        protected void onPostExecute(FMModifyPsw result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            
            if (1 == result.getResultCode())
            {
                
                // 弹出注册成功提示框
                noticeDialog = new NoticeDialog(PswManagentActivity.this,
                        R.style.NoticeDialog, "修改密码", "修改成功",
                        new NoticeDialog.LeaveMyDialogListener()
                        {

                            @Override
                            public void onClick(View view)
                            {
                                // TODO Auto-generated method stub
                                noticeDialog.dismiss();
                                noticeDialog = null;
                                closeSelf(PswManagentActivity.this);
                            }
                        });
                noticeDialog.show();
            } else
            {
                // 弹出注册失败提示框
                noticeDialog = new NoticeDialog(PswManagentActivity.this,
                        R.style.NoticeDialog, "修改密码", "修改失败",
                        new NoticeDialog.LeaveMyDialogListener()
                        {

                            @Override
                            public void onClick(View view)
                            {
                                // TODO Auto-generated method stub
                                noticeDialog.dismiss();
                                noticeDialog = null;
                            }
                        });
                noticeDialog.show();
            }
        }

    }
}
