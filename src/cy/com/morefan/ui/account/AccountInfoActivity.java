package cy.com.morefan.ui.account;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMRegisterBean;
import cy.com.morefan.bean.FMRegisterBean.InnerUser;
import cy.com.morefan.bean.FMUserData;
import cy.com.morefan.bean.GlobalData.Value;
import cy.com.morefan.bean.UserSelectData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.util.BitmapLoader;
import cy.com.morefan.util.DateUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.StringUtils;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.util.UserInfoView;
import cy.com.morefan.util.UserInfoView.OnUserInfoBackListener;
import cy.com.morefan.util.UserInfoView.Type;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.CropperView;
import cy.com.morefan.view.CropperView.OnCropperBackListener;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.view.PhotoSelectView;
import cy.com.morefan.view.PhotoSelectView.OnPhotoSelectBackListener;
import cy.com.morefan.view.PhotoSelectView.SelectType;
import cy.com.morefan.view.PopWheelView;
import cy.com.morefan.view.PopWheelView.OnDateBackListener;

/**
 * 
 * @类名称：AccountInfoActivity
 * @类描述：修改用户资料界面
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月10日 上午9:58:17
 * @修改备注：
 * @version:
 */
public class AccountInfoActivity extends BaseActivity implements Callback,
        OnClickListener, OnUserInfoBackListener, OnPhotoSelectBackListener,
        OnDateBackListener, OnCropperBackListener
{

    public Handler mHandler = new Handler(this);

    private TextView titleName;

    private String[] YEAR;

    private CyButton backImage;

    private PopWheelView popWheelView;

    // 完成修改功能按钮
    private TextView functionBtn;

    private PhotoSelectView pop;

    private UserInfoView userInfoView;

    private CropperView cropperView;

    private Account account;

    // 界面信息
    // 头像
    private LinearLayout layImg;

    private NetworkImageView img;

    // 姓名
    private LinearLayout layName;

    public static TextView txtName;

    // 性别
    private LinearLayout laySex;

    public static TextView txtSex;

    // 生日
    private LinearLayout layAge;

    public static TextView txtAge;

    // 职业
    private LinearLayout layJob;

    public static TextView txtJob;

    // 收入
    private LinearLayout layIncome;

    public static TextView txtIncome;

    // 爱好
    private LinearLayout layFav;

    public static TextView txtFav;

    // 返回文字事件
    private TextView backText;

    // 所在地址
    private TextView txtArea;

    // 注册时间
    private TextView txtTime;

    @Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        this.setContentView(R.layout.account_info_ui);
        initData();
        initView();
        userInfoView = new UserInfoView(this, application);
        userInfoView.setOnUserInfoBackListener(this);

    }

    private void initData()
    {
        account = new Account();
        account.setBrithday(application.readString(AccountInfoActivity.this,
                Constant.LOGIN_USER_INFO, Constant.LOGIN_USER_BRITHDAY));
        String faqsIndex = application.readString(AccountInfoActivity.this,
                Constant.LOGIN_USER_INFO, Constant.LOGIN_USER_FAVS);
        account.setLocation(application.readString(AccountInfoActivity.this,
                Constant.LOCATION_INFO, Constant.LOCATION_ADDRESS));
        account.setLogo(application.readString(AccountInfoActivity.this,
                Constant.LOGIN_USER_INFO, Constant.LOGIN_USER_LOGO));
        account.setName(application.readString(AccountInfoActivity.this,
                Constant.LOGIN_USER_INFO, Constant.LOGIN_USER_ACCOUNT));
        account.setRegTime(application.readString(AccountInfoActivity.this,
                Constant.LOGIN_USER_INFO, Constant.LOGIN_USER_REG_DATE));
        int sexI = application.readInt(AccountInfoActivity.this,
                Constant.LOGIN_USER_INFO, Constant.LOGIN_USER_SEX);
        if (0 == sexI)
        {
            account.setSex("男");
        } else if (1 == sexI)
        {
            account.setSex("女");
        }
        // 收入列表
        Value[] incomings = application.globalData.getIncomings();
        ArrayList incomeList = new ArrayList<UserSelectData>();
        for (Value value : incomings)
        {
            incomeList.add(new UserSelectData(value.getName(), String
                    .valueOf(value.getValue())));
        }

        // 职位列表
        Value[] jobs = application.globalData.getCareer();
        ArrayList indutryList = new ArrayList<UserSelectData>();
        for (Value value : jobs)
        {
            indutryList.add(new UserSelectData(value.getName(), String
                    .valueOf(value.getValue())));
        }

        // 爱好列表
        Value[] fars = application.globalData.getFavs();
        ArrayList favoriteList = new ArrayList<UserSelectData>();
        for (Value value : fars)
        {
            favoriteList.add(new UserSelectData(value.getName(), String
                    .valueOf(value.getValue())));
        }

        int comingI = application.readInt(AccountInfoActivity.this,
                Constant.LOGIN_USER_INFO, Constant.LOGIN_USER_INCOMING);
        int jobI = application.readInt(AccountInfoActivity.this,
                Constant.LOGIN_USER_INFO, Constant.LOGIN_USER_JOB);
        String coming = doChangeData(comingI, incomeList);
        String job = doChangeData(jobI, indutryList);
        account.setComing(coming);
        account.setFavs(doFar(faqsIndex, favoriteList));
        account.setFavoriteList(favoriteList);
        account.setIncomeList(incomeList);
        account.setIndutryList(indutryList);
        account.setJob(job);

    }

    private void initView()
    {
        titleName = (TextView) this.findViewById(R.id.title);
        titleName.setText("用户信息");
        backImage = (CyButton) this.findViewById(R.id.backImage);
        functionBtn = (TextView) this.findViewById(R.id.functionBtn);
        functionBtn.setVisibility(View.GONE);
        backImage.setOnClickListener(this);

        layImg = (LinearLayout) this.findViewById(R.id.layImg);
        img = (NetworkImageView) this.findViewById(R.id.img);
        if (StringUtils.isEmpty(account.getLogo()))
        {
            BitmapLoader
                    .create()
                    .displayUrl(
                            AccountInfoActivity.this,
                            img,
                            "http://img4.imgtn.bdimg.com/it/u=3309848897,132969131&fm=21&gp=0.jpg",
                            R.drawable.ic_login_username,
                            R.drawable.ic_login_username);
        } else
        {
            BitmapLoader.create().displayUrl(AccountInfoActivity.this, img,
                    account.getLogo(), R.drawable.ic_login_username,
                    R.drawable.ic_login_username);
        }
        layName = (LinearLayout) this.findViewById(R.id.layName);
        txtName = (TextView) this.findViewById(R.id.txtName);
        txtName.setText(account.getName());
        laySex = (LinearLayout) this.findViewById(R.id.laySex);
        txtSex = (TextView) this.findViewById(R.id.txtSex);
        txtSex.setText(account.getSex());
        layAge = (LinearLayout) this.findViewById(R.id.layAge);
        txtAge = (TextView) this.findViewById(R.id.txtAge);
        txtAge.setText(account.getBrithday());
        layJob = (LinearLayout) this.findViewById(R.id.layJob);
        txtJob = (TextView) this.findViewById(R.id.txtJob);
        txtJob.setText(account.getJob());
        layIncome = (LinearLayout) this.findViewById(R.id.layIncome);
        txtIncome = (TextView) this.findViewById(R.id.txtIncome);
        txtIncome.setText(account.getComing());
        layFav = (LinearLayout) this.findViewById(R.id.layFav);
        txtFav = (TextView) this.findViewById(R.id.txtFav);
        txtFav.setText(account.getFavs());
        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);
        txtArea = (TextView) this.findViewById(R.id.txtArea);
        txtArea.setText(account.getLocation());
        txtTime = (TextView) this.findViewById(R.id.txtTime);
        txtTime.setText(account.getRegTime());

    }

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub

        switch (v.getId())
        {
        case R.id.backImage:
        {
            // 关闭当前登录界面、
            closeSelf(AccountInfoActivity.this);
        }
            break;
        case R.id.functionBtn:
        {
            // 提交修改资料信息
            this.modifyProfile();
        }
            break;
        case R.id.layImg:
        {
            // 提交修改资料信息
            if (null == pop)
                pop = new PhotoSelectView(this, this);
            pop.show();
        }
            break;
        case R.id.layName:
        {
            userInfoView.show(Type.Name, null, txtName.getText().toString());
        }
            break;
        case R.id.laySex:
        {

            List<UserSelectData> sexDatas = new ArrayList<UserSelectData>();
            txtSex.setTag(application.readInt(AccountInfoActivity.this,
                    Constant.LOGIN_USER_INFO, Constant.LOGIN_USER_SEX));
            // 1男2女
            sexDatas.add(new UserSelectData("男", "0"));
            sexDatas.add(new UserSelectData("女", "1"));
            userInfoView.show(Type.Sex, sexDatas, txtSex.getTag().toString());
            // mainZoomOut(layAll);
        }
            break;
        case R.id.layJob:
        {
            txtJob.setTag(application.readInt(AccountInfoActivity.this,
                    Constant.LOGIN_USER_INFO, Constant.LOGIN_USER_JOB));
            userInfoView.show(Type.Job, account.getIndutryList(), txtJob
                    .getTag().toString());
            // mainZoomOut(layAll);
        }
            break;
        case R.id.layFav:
        {
            // 获取爱好的索引
            String favIndex = application.readString(AccountInfoActivity.this,
                    Constant.LOGIN_USER_INFO, Constant.LOGIN_USER_FAVS);
            String tag = doTag(favIndex);
            txtFav.setTag(tag);
            userInfoView.show(Type.Fav, account.getFavoriteList(), txtFav
                    .getTag().toString());
            // mainZoomOut(layAll);
        }
            break;
        case R.id.layIncome:
        {
            txtIncome.setTag(application.readInt(AccountInfoActivity.this,
                    Constant.LOGIN_USER_INFO, Constant.LOGIN_USER_INCOMING));
            userInfoView.show(Type.Income, account.getIncomeList(), txtIncome
                    .getTag().toString());
            // mainZoomOut(layAll);
        }
            break;
        case R.id.layAge:
        {
            if (YEAR == null)
                initYears();
            if (popWheelView == null)
            {
                popWheelView = new PopWheelView(this, application);
                popWheelView.setOnDateBackListener(this);
            }

            // mainZoomOut(layAll);
            popWheelView.show(txtAge.getText().toString().trim());
            // userInfoView.show(Type.Age, YEAR);
        }
            break;
        case R.id.backtext:
        {
            closeSelf(AccountInfoActivity.this);
        }
            break;
        default:
            break;
        }
    }

    private void initYears()
    {
        YEAR = new String[60];
        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR) - 10;
        for (int i = 0; i < 60; i++)
        {
            YEAR[i] = curYear - i + "";

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
            AccountInfoActivity.this.finish();
            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

    private void modifyProfile()
    {

    }

    @Override
    public void onUserInfoBack(Type type, UserSelectData data)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPhotoSelectBack(SelectType type)
    {
        // TODO Auto-generated method stub
        if (null == type)
            return;
        getPhotoByType(type);
    }

    private void getPhotoByType(SelectType type)
    {
        switch (type)
        {
        case Camera:
            getPhotoByCamera();
            break;
        case File:
            getPhotoByFile();
            break;

        default:
            break;
        }
    }

    private String imgPath;

    public void getPhotoByCamera()
    {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED))
        { // 检测sd是否可用
            Log.v("TestFile", "SD card is not avaiable/writeable right now.");
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss",
                Locale.CHINA);
        String imageName = "fm" + sdf.format(date) + ".jpg";
        imgPath = Environment.getExternalStorageDirectory() + "/" + imageName;
        File out = new File(imgPath);
        Uri uri = Uri.fromFile(out);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("fileName", imageName);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 0);
    }

    public void getPhotoByFile()
    {
        Intent intent2 = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent2, 1);
    }

    @Override
    public void onDateBack(String date)
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        if (requestCode == 0)
        {// camera back
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            if (bitmap == null)
            {
                ToastUtils.showLongToast(AccountInfoActivity.this, "未获取到图片!");
                return;
            }
            if (null == cropperView)
                cropperView = new CropperView(this, this);
            cropperView.cropper(bitmap);
        } else if (requestCode == 1)
        {// file back
            if (data != null)
            {
                Uri uri = data.getData();
                String path = null;
                String[] pojo =
                { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(uri, pojo, null,
                        null, null);
                // managedQuery(uri, pojo, null, null, null);
                if (cursor != null)
                {
                    // ContentResolver cr = this.getContentResolver();
                    int colunm_index = cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    path = cursor.getString(colunm_index);

                }
                Bitmap bitmap = Util.readBitmapByPath(path);
                if (bitmap == null)
                {
                    ToastUtils.showLongToast(AccountInfoActivity.this,
                            "未获取到图片!");
                    return;
                }
                if (null == cropperView)
                    cropperView = new CropperView(this, this);
                cropperView.cropper(bitmap);
            }

        }

    }

    private String doFar(String index, List<UserSelectData> list)
    {
        if (null == index)
        {
            return null;
        } else
        {

            // index 以逗号隔开的索引串
            String[] indexs = index.split(",");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < indexs.length; i++)
            {
                for (UserSelectData data : list)
                {
                    if (indexs[i].equals(data.id))
                    {
                        builder.append(data.name);
                        builder.append(",");
                    } else
                    {
                        continue;
                    }
                }
            }
            if (builder.length() > 0)
            {
                return builder.toString().substring(0,
                        (builder.toString().length() - 1));
            } else
            {
                return null;
            }

        }
    }

    private String doChangeData(int index, List<UserSelectData> list)
    {
        for (UserSelectData data : list)
        {
            if (String.valueOf(index).equals(data.id))
            {
                return data.name;
            } else
            {
                continue;
            }
        }
        return null;
    }

    private Bitmap cropBitmap;

    @Override
    public void OnCropperBack(Bitmap bitmap)
    {
        // TODO Auto-generated method stub
        if (null == bitmap)
            return;
        cropBitmap = bitmap;

        // 上传头像
        new UserLogoAsyncTask().execute();
    }

    public class UserLogoAsyncTask extends
            AsyncTask<Void, Void, FMRegisterBean>
    {

        private int profileType;

        private Object profileData;

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
            profileType = 0;
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            cropBitmap.compress(Bitmap.CompressFormat.PNG, 90, bao);
            byte[] buffer = bao.toByteArray();
            String imgStr = Base64.encodeToString(buffer, 0, buffer.length,
                    Base64.DEFAULT);
            profileData = imgStr;
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
                if (null != userData
                        && (!"".equals(userData.getToken()) && null != userData
                                .getToken()))
                {
                    // 刷新token
                    application.writeTokenToLocal(AccountInfoActivity.this,
                            result.getResultData().getUser().getToken(),
                            Constant.TOKEN_ADD);
                    // 保存用户信息
                    application.writeUserInfoToLocal(AccountInfoActivity.this,
                            userData.getName(), userData.getBalance(), userData
                                    .getPictureURL(), userData.getSignInfo(),
                            userData.getInvCode(), userData.getSigntoday(),
                            DateUtils.formatDate(userData.getBirthDate(),
                                    Constant.DATE_FORMAT), userData.getMobile(),
                            userData.getCareer(), userData.getIncoming(),
                            userData.getFavs(), userData.getArea(), DateUtils
                                    .formatDate(userData.getRegDate(), Constant.DATE_FORMAT),
                            userData.getWelcomeTip(),
                            userData.getInvalidCode(), userData.getSex(),
                            userData.getRealName());
                }
                BitmapLoader.create().displayUrl(AccountInfoActivity.this, img,
                        userData.getPictureURL(), R.drawable.ic_login_username,
                        R.drawable.ic_login_username);
            } else
            {
                ToastUtils.showLongToast(AccountInfoActivity.this, "上传头像失败！");
            }
        }

        @Override
        protected FMRegisterBean doInBackground(Void... params)
        {
            // TODO Auto-generated method stub
            FMRegisterBean registerBean = new FMRegisterBean();
            JSONUtil<FMRegisterBean> jsonUtil = new JSONUtil<FMRegisterBean>();
            String url;
            ObtainParamsMap obtainMap = new ObtainParamsMap(
                    AccountInfoActivity.this);
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
                user.setPictureURL("http://img4.imgtn.bdimg.com/it/u=3309848897,132969131&fm=21&gp=0.jpg");
                innerUser.setUser(user);
                registerBean.setResultData(innerUser);
            }

            return registerBean;
        }

    }

    /**
     * 
     * @类名称：InnerUser
     * @类描述：内部封转数据信息
     * @创建人：aaron
     * @修改人：
     * @修改时间：2015年6月16日 上午10:13:23
     * @修改备注：
     * @version:
     */
    public class Account
    {
        private String logo;// 头像地址

        private String name;// 用户名称

        private String sex;// 性别

        private String brithday;// 生日

        private String job;// 职业

        private String coming;// 收入

        private String favs;// 爱好

        private String location;// 区域

        private String regTime;// 注册时间

        // 职业列表
        private List<UserSelectData> indutryList;

        // 爱好数据列表
        private List<UserSelectData> favoriteList;

        // 收入数据列表
        private List<UserSelectData> incomeList;

        public String getLogo()
        {
            return logo;
        }

        public void setLogo(String logo)
        {
            this.logo = logo;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getSex()
        {
            return sex;
        }

        public void setSex(String sex)
        {
            this.sex = sex;
        }

        public String getBrithday()
        {
            return brithday;
        }

        public void setBrithday(String brithday)
        {
            this.brithday = brithday;
        }

        public String getJob()
        {
            return job;
        }

        public void setJob(String job)
        {
            this.job = job;
        }

        public String getComing()
        {
            return coming;
        }

        public void setComing(String coming)
        {
            this.coming = coming;
        }

        public String getFavs()
        {
            return favs;
        }

        public void setFavs(String favs)
        {
            this.favs = favs;
        }

        public String getLocation()
        {
            return location;
        }

        public void setLocation(String location)
        {
            this.location = location;
        }

        public String getRegTime()
        {
            return regTime;
        }

        public void setRegTime(String regTime)
        {
            this.regTime = regTime;
        }

        public List<UserSelectData> getIndutryList()
        {
            return indutryList;
        }

        public void setIndutryList(List<UserSelectData> indutryList)
        {
            this.indutryList = indutryList;
        }

        public List<UserSelectData> getFavoriteList()
        {
            return favoriteList;
        }

        public void setFavoriteList(List<UserSelectData> favoriteList)
        {
            this.favoriteList = favoriteList;
        }

        public List<UserSelectData> getIncomeList()
        {
            return incomeList;
        }

        public void setIncomeList(List<UserSelectData> incomeList)
        {
            this.incomeList = incomeList;
        }

    }

    /**
     * 
     *@方法描述：封装了选中的爱好的索引
     *@方法名：doTag
     *@参数：@param favIndex
     *@参数：@return
     *@返回：String
     *@exception 
     *@since
     */
    private String doTag(String favIndex)
    {
        StringBuilder builder = new StringBuilder();
        String[] indexs = favIndex.split(",");
        for (String index : indexs)
        {
            builder.append(index);
        }
        return builder.toString();

    }

}
