package cy.com.morefan.ui.flow;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.BaseBaseBean;
import cy.com.morefan.bean.FCBean;
import cy.com.morefan.bean.FMFCList;
import cy.com.morefan.bean.Paging;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.task.DeleteRequestFCAsyncTask;
import cy.com.morefan.task.MakeProvideAsyncTask;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.BitmapLoader;
import cy.com.morefan.util.DateUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.SystemTools;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.view.KJListView;
import cy.com.morefan.view.KJRefreshListener;


/**
 * @类名称：FriendsResActivity
 * @类描述：好友求流量界面
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月13日 上午11:31:09
 * @修改备注：
 * @version:
 */
public class FriendsResActivity extends BaseActivity implements
        OnClickListener, Callback, OnItemClickListener , MyBroadcastReceiver.BroadcastListener{
    public Handler mHandler = new Handler(this);

    private CyButton backImage;

    private TextView title;

    private TextView functionBtn;

    private KJListView friendsList;

    public MyApplication application;

    private List<FCBean> reqFMlist;

    private FriendtAdapter adapter;
    //返回文字事件
    private TextView backText;
    private TextView listNotice;

    private int infoId;

    private MyBroadcastReceiver myBroadcastReceiver=null;


    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);

        application = (MyApplication) FriendsResActivity.this.getApplication();
        reqFMlist = new ArrayList<FCBean>();
        this.setContentView(R.layout.friends_res_ui);
        this.initView();
        friendsList.setPullLoadEnable(true);
        adapter = new FriendtAdapter();
        friendsList.setAdapter(adapter);
        initList();
        friendsList.setOnItemClickListener(this);
        friendsList.setRefreshTime(DateUtils.formatDate(System.currentTimeMillis()), FriendsResActivity.this);

        myBroadcastReceiver = new MyBroadcastReceiver(this, this,
                MyBroadcastReceiver.ACTION_REQUESTFLOW );

    }

    @Override
    protected void onResume() {
        super.onResume();
        new LoadFriendsAsyncTask(Constant.REFRESH).execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myBroadcastReceiver!=null){
            myBroadcastReceiver.unregisterReceiver();
        }
    }

    private void initList() {
        friendsList.setOnRefreshListener(new KJRefreshListener() {


            public void onRefresh() {
                // TODO Auto-generated method stub
                // 加载数据
                friendsList.setBackgroundColor(Color.TRANSPARENT);
                friendsList.setRefreshTime(
                        DateUtils.formatDate(System.currentTimeMillis()),
                        FriendsResActivity.this);
                new LoadFriendsAsyncTask(Constant.REFRESH)
                        .execute();
                friendsList.stopRefreshData();
                friendsList.stopLoadMore();
            }

            @Override
            public void onLoadMore() {
                // TODO Auto-generated method stub

                friendsList.setBackgroundColor(Color.TRANSPARENT);

                friendsList.setRefreshTime(
                        DateUtils.formatDate(System.currentTimeMillis()),
                        FriendsResActivity.this);
                new LoadFriendsAsyncTask(Constant.LOAD_MORE)
                        .execute();
                friendsList.stopRefreshData();
                friendsList.setRefreshTime(DateUtils.formatDate(System.currentTimeMillis()), FriendsResActivity.this);
                friendsList.stopRefreshData();
            }


        });
    }

    private void initView() {
        backImage = (CyButton) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(this);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("好友请求列表");
        functionBtn = (TextView) this.findViewById(R.id.functionBtn);
        functionBtn.setVisibility(View.VISIBLE);
        functionBtn.setText("清空列表");
        functionBtn.setOnClickListener(this);
        friendsList = (KJListView) this.findViewById(R.id.friendsList);
        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);
        listNotice = (TextView) this.findViewById(R.id.listNotice);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            // finish自身
            FriendsResActivity.this.finish();
            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean handleMessage(Message msg) {
        // TODO Auto-generated method stub
        switch (msg.what) {
            case MakeProvideAsyncTask.FAIL:
                ToastUtils.showLongToast(FriendsResActivity.this, msg.obj.toString());
                break;
            case MakeProvideAsyncTask.SUCCESS:
                ToastUtils.showLongToast(FriendsResActivity.this, msg.obj.toString());
                new DeleteRequestFCAsyncTask(FriendsResActivity.this, mHandler, infoId).execute();
                break;
            case DeleteRequestFCAsyncTask.SUCCESS:
                new LoadFriendsAsyncTask(Constant.REFRESH ).execute();
                break;
            case DeleteRequestFCAsyncTask.FAIL:
                ToastUtils.showLongToast(FriendsResActivity.this, msg.obj.toString());
                break;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.backImage: {
                closeSelf(FriendsResActivity.this);
            }
            break;
            case R.id.functionBtn: {
                AlertDialog.Builder dialog = new AlertDialog.Builder(FriendsResActivity.this);
                dialog.setTitle("好友求流量");
                dialog.setMessage("确认清空列表信息？");
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        // 先删除服务端数据，服务端删除成功后删除本地数据
                        //cleanRequestFC();
                        //模拟删除
                        new ClearInfoAsyncTask().execute();

                        // 清空列表信息
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                reqFMlist.clear();
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                    private void cleanRequestFC() {
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
            break;
            case R.id.backtext: {
                closeSelf(FriendsResActivity.this);
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void onFinishReceiver(MyBroadcastReceiver.ReceiverType type, Object msg) {
        new LoadFriendsAsyncTask(Constant.REFRESH).execute();
    }


    /**
     * @类名称：FriendtAdapter
     * @类描述：好友求流量适配
     * @创建人：aaron
     * @修改人：
     * @修改时间：2015年6月13日 上午11:52:33
     * @修改备注：
     * @version:
     */
    public class FriendtAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return reqFMlist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return reqFMlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = null;
            Resources res = FriendsResActivity.this.getResources();
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(FriendsResActivity.this,
                        R.layout.friend_item_ui, null);
                holder.img = (NetworkImageView) convertView
                        .findViewById(R.id.img);
                holder.msg = (TextView) convertView.findViewById(R.id.msg);
                holder.phoneNumber = (TextView) convertView
                        .findViewById(R.id.phoneNumber);
                holder.contactName = (TextView) convertView
                        .findViewById(R.id.contactName);
                holder.account = (TextView) convertView
                        .findViewById(R.id.account);
                holder.flows = (TextView) convertView.findViewById(R.id.flows);
                holder.operator = (TextView) convertView
                        .findViewById(R.id.operator);
                holder.label = (TextView) convertView
                        .findViewById(R.id.label);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // 加载图片
            if (reqFMlist.size() > 0) {
                BitmapLoader.create().displayUrl(FriendsResActivity.this,
                        holder.img, reqFMlist.get(position).getFromPicUrl());
                holder.phoneNumber.setText("(" + reqFMlist.get(position)
                        .getFrom() + ")");
                holder.msg.setText(reqFMlist.get(position).getMessage());
                String userName = reqFMlist.get(position).getFromName();
                if (null != userName) {
                    holder.contactName.setText(userName);
                    holder.account.setText("");
                } else {
                    holder.account.setText("");
                }

                Number temp = Util.decimalFloat(reqFMlist.get(position).getFee() ,"0.##");

                holder.flows.setText("" +temp + "M");
                holder.flows.setTextSize(res
                        .getDimension(R.dimen.friends_accpet_text));
                holder.flows.setTextColor(res.getColor(R.color.accpet_flow_color));

                holder.label.setVisibility(View.VISIBLE);
                holder.label.setText("");
                int sex = reqFMlist.get(position).getFromSex();
                // 男
                if (0 == sex) {
                    SystemTools.loadBackground(holder.account, res.getDrawable(R.drawable.friends_sex_male));
                } else if (1 == sex) {
                    SystemTools.loadBackground(holder.account, res.getDrawable(R.drawable.friends_sex_female));
                }
            }
            return convertView;
        }

        class ViewHolder {
            NetworkImageView img;// 联系人图片

            TextView phoneNumber;// 联系人手机
            TextView msg;//留言信息

            TextView contactName;// 联系人

            TextView account;// 是否是粉猫用户

            TextView flows;// 求流量
            TextView label;//标识
            TextView operator;// 运营商
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub


        int idx = position - 1;
        if (idx < 0 || idx >= reqFMlist.size())
            return;

        final FCBean fcbean = reqFMlist.get(idx);

        infoId = fcbean.getInfoId();

        AlertDialog.Builder dialog = new AlertDialog.Builder(FriendsResActivity.this);
        dialog.setTitle("好友求流量");
        if (0 == fcbean.getFromSex()) {
            dialog.setMessage("送他" + fcbean.getFee() + "M流量。");
        }
        if (1 == fcbean.getFromSex()) {
            dialog.setMessage("送她" + fcbean.getFee() + "M流量。");
        }
        dialog.setNeutralButton("拒绝", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        // 删除接口

                        new DeleteRequestFCAsyncTask(FriendsResActivity.this, mHandler, infoId).execute();
                    }
                }
        );
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                // 送流量接口
                String msg = "朕赏你点流量,还不谢恩";
                new MakeProvideAsyncTask(FriendsResActivity.this, mHandler, fcbean.getFrom(), String.valueOf(fcbean.getFee()), msg).execute();
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

    public class LoadFriendsAsyncTask extends AsyncTask<Void, Void, FMFCList> {
        private int loadType;// 0：下拉，1：上拉

        public LoadFriendsAsyncTask(int loadType) {
            // TODO Auto-generated constructor stub
            this.loadType = loadType;
        }

        int size;

        @Override
        protected FMFCList doInBackground(Void... params) {
            FMFCList list;
            Paging paging = new Paging();
            paging.setPagingSize(Constant.PAGES_TASK);
            // 判断是下拉刷新还是上拉加载数据
            if (Constant.REFRESH == loadType) {
                // 下拉
                paging.setPagingTag("");
            } else if (Constant.LOAD_MORE == loadType) {
                // 上拉
                if (reqFMlist != null && reqFMlist.size() > 0) {
                    FCBean lastFC = reqFMlist.get(reqFMlist.size() - 1);
                    paging.setPagingTag(String.valueOf(lastFC.getInfoId()));

                } else if (reqFMlist != null && reqFMlist.size() == 0) {
                    paging.setPagingTag("");
                }
            }
            String url = Constant.TAKE_FRIEMDS_LIST;
            ObtainParamsMap obtainMap = new ObtainParamsMap(FriendsResActivity.this);
            String paraString = obtainMap.getMap();
            Map<String, String> signMap = new HashMap<String, String>();
            signMap.put("pagingTag", paging.getPagingTag());
            signMap.put("pagingSize", paging.getPagingSize().toString());
            String sign = obtainMap.getSign(signMap);

            try {
                url += "?sign=" + URLEncoder.encode(sign, "UTF-8");
                url += paraString;
                url += "&pagingTag="
                        + URLEncoder.encode(paging.getPagingTag(), "UTF-8");
                url += "&pagingSize="
                        + URLEncoder.encode(paging.getPagingSize().toString(),
                        "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            JSONUtil<FMFCList> jsonUtil = new JSONUtil<FMFCList>();
            //
            String json = HttpUtil.getInstance().doGet(url);
            list = new FMFCList();
            try {
                list = jsonUtil.toBean(json, list);
            } catch (JsonSyntaxException e) {
                LogUtil.e("JSON_ERROR", e.getMessage());
                list = new FMFCList();
                list.setResultCode(0);
                list.setResultDescription("解析json出错");
            }


            return list;

        }


        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            FriendsResActivity.this.showProgress();

        }

        @Override
        protected void onPostExecute(final FMFCList result) {
            super.onPostExecute(result);

            FriendsResActivity.this.dismissProgress();

            AlphaAnimation anima = new AlphaAnimation(0.0f, 5.0f);
            anima.setDuration(1000);// 设置动画显示时间
            listNotice.setAnimation(anima);
            if (Constant.IS_PRODUCTION_ENVIRONMENT) {
                // 下拉刷新和上拉加载分开处理
                if (Constant.REFRESH == loadType) {
                    // 下拉刷新
                    if (1 == result.getResultCode()) {
                        //设置求流量消息为空
                        String username = MyApplication.readUserName(FriendsResActivity.this);
                        MyApplication.writeBoolean(FriendsResActivity.this, Constant.LOGIN_USER_INFO, username,false);

                        // 重新加载数据
                        reqFMlist.clear();
                        if (result.getResultData().getRequests() != null) {
                            reqFMlist.addAll(result.getResultData().getRequests());
                        }
                        adapter.notifyDataSetChanged();
                        anima.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                // TODO Auto-generated method stub
                                listNotice.setVisibility(View.VISIBLE);
                                if (result.getResultData().getRequests().isEmpty()) {
                                    listNotice.setText("系统暂无任务");
                                    friendsList.setBackgroundResource(R.drawable.nodata);
                                    friendsList.stopLoadMore();
                                } else {
                                    listNotice.setText("数据已经刷新");
                                    //friendsList.setBackgroundColor(R.color.activity_bg);
                                }
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                // TODO Auto-generated method stub
                                listNotice.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                                // TODO Auto-generated method stub

                            }

                        });

                    } else if (5000 == result.getResultCode()) {
                        reqFMlist.clear();
                        adapter.notifyDataSetChanged();
                        // 刷新列表
                        anima.setAnimationListener(new Animation.AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {
                                // TODO Auto-generated method stub
                                listNotice.setVisibility(View.VISIBLE);
                                listNotice.setText("系统暂无请求信息");
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                // TODO Auto-generated method stub
                                listNotice.setVisibility(View.GONE);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                                // TODO Auto-generated method stub

                            }

                        });
                    } else if (Constant.TOKEN_OVERDUE == result.getResultCode()) {
                        // 提示账号异地登陆，强制用户退出
                        // 并跳转到登录界面
                        ToastUtils.showLongToast(FriendsResActivity.this, "账户登录过期，请重新登录");
                        Handler mHandler = new Handler();
                        mHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                ActivityUtils.getInstance().loginOutInFragment(
                                        FriendsResActivity.this);
                            }
                        }, 2000);
                    }
                } else if (Constant.LOAD_MORE == loadType) {
                    // 上拉加载
                    if (1 == result.getResultCode()) {

                        //设置求流量消息为空
                        String username = MyApplication.readUserName(FriendsResActivity.this);
                        MyApplication.writeBoolean(FriendsResActivity.this, Constant.LOGIN_USER_INFO, username,false);


                        if (!result.getResultData().getRequests().isEmpty()) {
                            // 重新加载数据
                            //datas.clear();
                            size = result.getResultData().getRequests().size();
                            // 刷新列表
                            reqFMlist.addAll(result.getResultData().getRequests());
                            adapter.notifyDataSetChanged();
                        }
                        anima.setAnimationListener(new Animation.AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {
                                // TODO Auto-generated method stub
                                listNotice.setVisibility(View.VISIBLE);
                                if (!result.getResultData().getRequests().isEmpty()) {
                                    listNotice.setText("加载了" + size + "条数据");
                                } else {
                                    listNotice.setText("没有可加载的数据");
                                    friendsList.setBackgroundResource(R.drawable.nodata);
                                    friendsList.stopLoadMore();
                                }
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                // TODO Auto-generated method stub
                                listNotice.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                                // TODO Auto-generated method stub

                            }

                        });
                    } else if (5000 == result.getResultCode()) {
                        anima.setAnimationListener(new Animation.AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {
                                // TODO Auto-generated method stub
                                listNotice.setVisibility(View.VISIBLE);
                                listNotice.setText("没有可加载的数据");
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                // TODO Auto-generated method stub
                                listNotice.setVisibility(View.GONE);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                                // TODO Auto-generated method stub

                            }

                        });

                    } else if (Constant.TOKEN_OVERDUE == result.getResultCode()) {
                        // 提示账号异地登陆，强制用户退出
                        // 并跳转到登录界面
                        ToastUtils.showLongToast(FriendsResActivity.this, "账户登录过期，请重新登录");
                        Handler mHandler = new Handler();
                        mHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                ActivityUtils.getInstance().loginOutInFragment(
                                        FriendsResActivity.this
                                );
                            }
                        }, 2000);
                    }
                }
            } else {
                //测试代码
            }
        }


    }

    public class ClearInfoAsyncTask extends AsyncTask<Void, Void, BaseBaseBean> {
        protected BaseBaseBean doInBackground(Void... params) {
            String url = Constant.DELETE_LIST;
            ObtainParamsMap obtainMap = new ObtainParamsMap(FriendsResActivity.this);
            String paraString = obtainMap.getMap();
            Map<String, String> signMap = new HashMap<String, String>();
            String sign = obtainMap.getSign(signMap);
            try {
                url += "?sign=" + URLEncoder.encode(sign, "UTF-8") + paraString;


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String responseStr = HttpUtil.getInstance().doGet(url);
            JSONUtil<BaseBaseBean> jsonUtil = new JSONUtil<BaseBaseBean>();
            BaseBaseBean result = new BaseBaseBean();
            try {
                result = jsonUtil.toBean(responseStr, result);
            } catch (JsonSyntaxException e) {
                LogUtil.e("JSON_ERROR", e.getMessage());
                result.setResultCode(0);
                result.setResultDescription("解析json出错");
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            FriendsResActivity.this.showProgress();
        }

        @Override
        protected void onPostExecute(BaseBaseBean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            FriendsResActivity.this.dismissProgress();
            // 清空列表信息
            new LoadFriendsAsyncTask(Constant.REFRESH ).execute();
        }
    }

}
