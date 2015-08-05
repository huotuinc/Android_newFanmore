package cy.com.morefan.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.adapter.UserInfoAdapter;
import cy.com.morefan.bean.FMRegisterBean;
import cy.com.morefan.bean.FMRegisterBean.InnerUser;
import cy.com.morefan.bean.FMUserData;
import cy.com.morefan.bean.GlobalData.Value;
import cy.com.morefan.bean.UserSelectData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.ui.account.AccountInfoActivity;
import cy.com.morefan.ui.account.MsgCenterActivity;

public class UserInfoView
{

    public enum Type
    {
        Name, Sex, Age, Job, Income, Fav
    }

    public HashMap<Type, String> titleNames = new HashMap<UserInfoView.Type, String>();

    public interface OnUserInfoBackListener
    {
        void onUserInfoBack(Type type, UserSelectData data);
    }

    private OnUserInfoBackListener listener;

    private View mainView;
    private MyApplication application;

    private TextView txtTitle;

    private ListView listView;

    private Context mContext;

    private TextView btnSure;

    private Dialog dialog;

    private LinearLayout layMain;

    private EditText edtName;

    public UserInfoView(Context context, MyApplication application)
    {
        this.mContext = context;
        this.application = application;
        initView(context);
    }

    public void setOnUserInfoBackListener(OnUserInfoBackListener listener)
    {
        this.listener = listener;
    }

    private void initView(Context context)
    {
        if (dialog == null)
        {
            mainView = LayoutInflater.from(context).inflate(
                    R.layout.pop_userinfo, null);
            dialog = new Dialog(context, R.style.PopDialog);
            dialog.setContentView(mainView);
            Window window = dialog.getWindow();
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.AnimationPop); // 添加动画

            // 设置视图充满屏幕宽度
            WindowManager.LayoutParams lp = window.getAttributes();
            int[] size = DensityUtils.getSize(mContext);
            lp.width = size[0]; // 设置宽度
            // lp.height = (int) (size[1]*0.8);
            window.setAttributes(lp);
        }
        titleNames.put(Type.Name, "姓名");
        titleNames.put(Type.Sex, "性别");
        titleNames.put(Type.Age, "出生年份");
        titleNames.put(Type.Job, "职业");
        titleNames.put(Type.Income, "收入");
        titleNames.put(Type.Fav, "爱好");
        edtName = (EditText) mainView.findViewById(R.id.edtName);
        txtTitle = (TextView) mainView.findViewById(R.id.txtTitle);
        listView = (ListView) mainView.findViewById(R.id.listView);

        btnSure = (TextView) mainView.findViewById(R.id.btnSure);
        btnSure.setOnClickListener(new View.OnClickListener()
        {

            // 在确定后逐个提交修改信息
            @Override
            public void onClick(View v)
            {
                if(Util.isConnect(mContext) ==false ){
                    ToastUtils.showLongToast(mContext , "无网络或当前网络不可用!");
                    return;
                }


                if (curType == Type.Name)
                {
                    /*
                     * if(listener != null){ listener.onUserInfoBack(Type.Name,
                     * new UserSelectData(edtName.getText().toString(), "0"));
                     * 
                     * }
                     */
                    new modifyUserInfoAsyncTask().execute();

                } else if (curType == Type.Sex)
                {
                    /*
                     * List<UserSelectData> result = adapter.getSelectData();
                     * 
                     * 
                     * StringBuffer tag = new StringBuffer(); StringBuffer name
                     * = new StringBuffer(); int length = result.size(); for
                     * (int i = 0; i < length; i++) {
                     * tag.append(result.get(i).id);
                     * name.append(result.get(i).name); if(i != length -1){
                     * tag.append(","); name.append(","); }
                     * 
                     * } if(listener != null){ listener.onUserInfoBack(Type.Fav,
                     * new UserSelectData(name.toString(), tag.toString())); }
                     */
                    new modifyUserInfoAsyncTask().execute();
                } else if (curType == Type.Job)
                {
                    new modifyUserInfoAsyncTask().execute();
                } else if (curType == Type.Income)
                {
                    new modifyUserInfoAsyncTask().execute();
                } else if (curType == Type.Fav)
                {
                    new modifyUserInfoAsyncTask().execute();
                }

            }
        });
        // init support
        mainView.findViewById(R.id.btnCancel).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (listener != null)
                            listener.onUserInfoBack(null, null);
                        dialog.dismiss();
                    }
                });

        mainView.setFocusableInTouchMode(true);
        mainView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    if (listener != null)
                        listener.onUserInfoBack(null, null);
                    dialog.dismiss();
                }
                return false;
            }
        });
        layMain = (LinearLayout) mainView.findViewById(R.id.layMain);

    }

    private Handler handler = new Handler();

    private UserInfoAdapter adapter;

    private Type curType;

    public void show(final Type type, final List<UserSelectData> datas,
            String selectIds)
    {
        curType = type;
        txtTitle.setText(titleNames.get(type));
        // btnSure.setVisibility(type == Type.Fav || type == Type.Name ?
        // View.VISIBLE : View.GONE);
        edtName.setVisibility(type == Type.Name ? View.VISIBLE : View.GONE);
        listView.setVisibility(type == Type.Name ? View.GONE : View.VISIBLE);
        dialog.show();
        if (type == Type.Name)
        {
            edtName.requestFocus();
            edtName.requestFocusFromTouch();

            final InputMethodManager imm = (InputMethodManager) mContext
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }, 10);
            edtName.setText(selectIds);

        } else
        {
            adapter = new UserInfoAdapter(mContext, datas);
            listView.setAdapter(adapter);
            if (!TextUtils.isEmpty(selectIds))
            {
                // boolean[] tags = new boolean[datas.size()];
                for (int i = 0, length = datas.size(); i < length; i++)
                {
                    if (selectIds.contains(datas.get(i).id))
                        adapter.setSelect(i);
                }
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                        int arg2, long arg3)
                {
                    if (type == Type.Sex || type == Type.Job
                            || type == Type.Income)
                    {
                        adapter.setSelectOne(arg2);
                        if (listener != null)
                        {
                            listener.onUserInfoBack(type, datas.get(arg2));
                        }
                        // dialog.dismiss();
                    } else if (type == Type.Fav)
                    {
                        adapter.setSelect(arg2);
                    }

                }
            });
        }

        // set height
        LinearLayout.LayoutParams params = (LayoutParams) layMain
                .getLayoutParams();
        // reset
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;// ownHeight >
                                                               // height ?
                                                               // height
                                                               // :ownHeight;
        layMain.setLayoutParams(params);

        ViewTreeObserver vto2 = layMain.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                layMain.getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);
                LinearLayout.LayoutParams params = (LayoutParams) layMain
                        .getLayoutParams();
                int ownHeight = layMain.getHeight();
                int height = (int) (DensityUtils.getSize(mContext)[1] * 0.75);
                params.height = ownHeight > height ? height : ownHeight;
                layMain.setLayoutParams(params);
            }
        });

    }

    /**
     * 
     * @类名称：modifyUserInfoActivity
     * @类描述：修改资料提交
     * @创建人：aaron
     * @修改人：
     * @修改时间：2015年6月9日 上午11:08:55
     * @修改备注：
     * @version:
     */
    public class modifyUserInfoAsyncTask extends
            AsyncTask<Void, Void, FMRegisterBean>
    {

        private int profileType;
        private Object profileData;

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
            if (curType == Type.Name)
            {
                // 封装提交姓名参数
                profileType = 1;
                profileData = edtName.getText().toString();
            } else if (curType == Type.Job)
            {
                profileType = 3;
                if( adapter.getSelectData().size()>0) {
                    profileData = adapter.getSelectData().get(0).id;
                }
            } else if (curType == Type.Income)
            {
                profileType = 4;
                if( adapter.getSelectData().size()>0) {
                    profileData = adapter.getSelectData().get(0).id;
                }
            } else if (curType == Type.Fav)
            {
                profileType = 5;
                if( adapter.getSelectData().size()>0) {
                    StringBuilder builder = new StringBuilder();
                    for (UserSelectData data : adapter.getSelectData()) {
                        builder.append(data.id);
                        builder.append(",");
                    }
                    profileData = builder.toString().subSequence(0,
                            (builder.toString().length() - 1));
                }
            } else if (curType == Type.Sex)
            {
                profileType = 6;
                StringBuilder builder = new StringBuilder();
                if( adapter.getSelectData().size()>0) {
                    profileData = adapter.getSelectData().get(0).id;
                }
            }

        }

        @Override
        protected void onPostExecute(FMRegisterBean result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (1 == result.getResultCode())
            {
                FMUserData userData = result.getResultData().getUser();
                application.personal = userData;
                if(null != userData && (!"".equals(userData.getToken()) && null != userData.getToken()))
                {
                  //刷新token
                    MyApplication.writeTokenToLocal(mContext, result
                            .getResultData().getUser().getToken(), Constant.TOKEN_ADD);
                 // 保存用户信息
                    MyApplication.writeUserInfoToLocal(mContext,
                            userData.getName(), userData.getBalance(),
                            userData.getPictureURL(), userData.getSignInfo(),
                            userData.getInvCode(), userData.getSigntoday(),
                            DateUtils.formatDate(userData.getBirthDate(), Constant.DATE_FORMAT), userData.getMobile(),
                            userData.getCareer(), userData.getIncoming(),
                            userData.getFavs(), userData.getArea(),
                            DateUtils.formatDate(userData.getRegDate(), Constant.DATE_FORMAT), userData.getWelcomeTip(),
                            userData.getInvalidCode(),userData.getSex(),userData.getRealName());
                }
                
                dialog.dismiss();
                // 修改名称
                if (curType == Type.Name)
                {
                    AccountInfoActivity.txtName.setText(result.getResultData()
                            .getUser().getRealName());
                } else if (curType == Type.Job)
                {
                    AccountInfoActivity.txtJob.setText(obtainUserData(
                            String.valueOf(result.getResultData().getUser()
                                    .getCareer()), "job"));
                } else if (curType == Type.Fav)
                {
                    AccountInfoActivity.txtFav.setText(obtainUserData(
                            String.valueOf(result.getResultData().getUser()
                                    .getFavs()), "Fav"));
                    
                } else if (curType == Type.Income)
                {

                    AccountInfoActivity.txtIncome.setText(obtainUserData(
                            String.valueOf(result.getResultData().getUser()
                                    .getIncoming()), "Income"));
                } else if (curType == Type.Sex)
                {
                    AccountInfoActivity.txtSex.setText(obtainUserData(
                            String.valueOf(result.getResultData().getUser()
                                    .getSex()), "Sex"));
                }

            } 
            else if (Constant.TOKEN_OVERDUE == result.getResultCode())
            {
                // 提示账号异地登陆，强制用户退出
                // 并跳转到登录界面
                ToastUtils.showLongToast(mContext, "账户登录过期，请重新登录");
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        ActivityUtils.getInstance().loginOutInActivity(
                                (Activity) mContext);
                    }
                }, 2000);
            }
            else
            {
                
                dialog.dismiss();
                ToastUtils.showLongToast(mContext, "信息修改错误！");
            }

        }

        @Override
        protected FMRegisterBean doInBackground(Void... params)
        {
            // TODO Auto-generated method stub
            FMRegisterBean registerBean = new FMRegisterBean();
            JSONUtil<FMRegisterBean> jsonUtil = new JSONUtil<FMRegisterBean>();
            String url;
            ObtainParamsMap obtainMap = new ObtainParamsMap(mContext);
            Map<String, String> paramMap = obtainMap.obtainMap();

            // 拼接注册url
            url = Constant.UPDATE_PROFILE;
            // 注册是POST提交
            paramMap.put("profileType", String.valueOf(profileType));
            paramMap.put("profileData", String.valueOf(profileData));
            // 封装sign
            String signStr = obtainMap.getSign(paramMap);
            paramMap.put("sign", signStr);
            if (Constant.IS_PRODUCTION_ENVIRONMENT)
            {
                String jsonStr = HttpUtil.getInstance().doPost(url, paramMap);
                try
                {
                    registerBean = jsonUtil.toBean(jsonStr, registerBean);
                }
                catch(JsonSyntaxException e)
                {
                   LogUtil.e("JSON_ERROR", e.getMessage());
                   registerBean.setResultCode(0);
                   registerBean.setResultDescription("解析json出错");
                }
            } else
            {
                registerBean.setResultCode(1);
                InnerUser innerUser = registerBean.new InnerUser();
                FMUserData user = new FMUserData();
                user.setName("Aaron");
                user.setSex(1);
                user.setIncoming(2);
                user.setCareer(3);
                user.setFavs("1,2,3");

                innerUser.setUser(user);
                registerBean.setResultData(innerUser);
            }

            return registerBean;
        }

    }

    public String obtainUserData(String key, String type)
    {
        String result = null;
        if ("job".equals(type))
        {
            // 从application中获取职业信息，初始化接口获取
            // 匹配key获取职业
            Value[] jobs = application.globalData.getCareer();
            for(Value job:jobs)
            {
                if(String.valueOf(job.getValue()).equals(key))
                {
                    result = job.getName();
                }
            }
            
        } else if ("Fav".equals(type))
        {
            // 从application中获取爱好信息，初始化接口获取
            // 匹配key获取爱好
            Value[] favs = application.globalData.getFavs();
            StringBuilder builder = new StringBuilder();
            String[] keys = key.split(",");
            for(int i=0; i<keys.length; i++)
            {
                for(Value fav:favs)
                {
                    if(String.valueOf(fav.getValue()).equals(keys[i]))
                    {
                        builder.append(fav.getName());
                        builder.append(",");
                    }
                }
            }
            result = builder.toString().substring(0, (builder.toString().length()-1));

        } else if ("Income".equals(type))
        {
            // 从application中获取收入信息，初始化接口获取
            // 匹配key获取收入
            Value[] incomings = application.globalData.getIncomings();
            for(Value incoming:incomings)
            {
                if(String.valueOf(incoming.getValue()).equals(key))
                {
                    result = incoming.getName();
                }
            }
        } else if ("Sex".equals(type))
        {
            if ("0".equals(key))
            {
                result = "男";
            } else if ("1".equals(key))
            {
                result = "女";
            }
        }

        return result;
    }
}
