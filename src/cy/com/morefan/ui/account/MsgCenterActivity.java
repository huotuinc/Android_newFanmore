package cy.com.morefan.ui.account;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
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
import android.text.Html;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMMessage;
import cy.com.morefan.bean.IBaseData;
import cy.com.morefan.bean.Paging;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.DataListener;
import cy.com.morefan.ui.user.LoginActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.DateUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.KJLoger;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.view.KJListView;
import cy.com.morefan.view.KJRefreshListener;

/**
 * 
 * @类名称：MsgCenterActivity
 * @类描述：消息中心界面
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月10日 上午9:58:38
 * @修改备注：
 * @version:
 */
public class MsgCenterActivity extends BaseActivity implements Callback,
        OnClickListener, DataListener
{

    private TextView titleName;

    private CyButton titleBack;

    private List<cy.com.morefan.bean.Message> datas;

    private KJListView listView;

    private MsgAdapter adapter;

    // 列表刷新提示
    private TextView listNotice;

    Handler mHandler = new Handler(this);

    // 返回文字事件
    private TextView backText;

    private MyApplication app = null;

    @Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);

        app = (MyApplication) this.getApplication();

        this.setContentView(R.layout.msg_center_ui);
        initView();
        datas = new ArrayList<cy.com.morefan.bean.Message>();
        listView.setPullLoadEnable(true);
        adapter = new MsgAdapter();
        listView.setAdapter(adapter);
        // new MsgListAsyncTask(Constant.REFRESH).execute();
        initList();
        listView.setRefreshTime(
                DateUtils.formatDate(System.currentTimeMillis()),
                MsgCenterActivity.this);
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();

        if (false == app.isLogin(MsgCenterActivity.this))
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 1101);
        } else
        {
            new MsgListAsyncTask(Constant.REFRESH).execute();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1101 && resultCode == RESULT_OK)
        {
            new MsgListAsyncTask(Constant.REFRESH).execute();
        } else
        {
            this.closeSelf(this);
        }
    }

    private void initList()
    {
        listView.setOnRefreshListener(new KJRefreshListener()
        {

            @Override
            public void onRefresh()
            {
                // TODO Auto-generated method stub
                // 加载数据
                listView.setRefreshTime(
                        DateUtils.formatDate(System.currentTimeMillis()),
                        MsgCenterActivity.this);
                new MsgListAsyncTask(Constant.REFRESH).execute();
                listView.stopRefreshData();
            }

            @Override
            public void onLoadMore()
            {
                // TODO Auto-generated method stub
                listView.setRefreshTime(
                        DateUtils.formatDate(System.currentTimeMillis()),
                        MsgCenterActivity.this);
                new MsgListAsyncTask(Constant.LOAD_MORE).execute();
                listView.stopRefreshData();
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
            closeSelf(MsgCenterActivity.this);
            break;
        case R.id.backtext:
        {
            closeSelf(MsgCenterActivity.this);
        }
            break;
        default:
            break;
        }
    }

    private void initView()
    {

        titleName = (TextView) this.findViewById(R.id.title);
        titleName.setText("消息中心");
        titleBack = (CyButton) this.findViewById(R.id.backImage);
        titleBack.setOnClickListener(this);
        listView = (KJListView) this.findViewById(R.id.msgList);
        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);
        listNotice = (TextView) this.findViewById(R.id.msglistNotice);
    }

    @Override
    public boolean handleMessage(Message msg)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onDataFinish(int type, String msg, Bundle extra,
            IBaseData... data)
    {
        mHandler.obtainMessage(type, data).sendToTarget();

    }

    @Override
    public void onDataFail(int type, String msg, Bundle extra)
    {
        mHandler.obtainMessage(type, msg).sendToTarget();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            // finish自身
            MsgCenterActivity.this.finish();
            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 
     * @类名称：MsgAdapter
     * @类描述：
     * @创建人：aaron
     * @修改人：
     * @修改时间：2015年6月9日 下午4:18:58
     * @修改备注：
     * @version:
     */
    public class MsgAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            // TODO Auto-generated method stub
            return datas.size();
        }

        @Override
        public Object getItem(int position)
        {
            // TODO Auto-generated method stub
            return datas.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            // TODO Auto-generated method stub
            ViewHolder holder = null;
            if (convertView == null)
            {
                holder = new ViewHolder();
                convertView = View.inflate(MsgCenterActivity.this,
                        R.layout.msg_center_item, null);
                holder.msgTime = (TextView) convertView
                        .findViewById(R.id.msgTime);
                holder.msgCon = (TextView) convertView
                        .findViewById(R.id.msgCon);
                holder.msgCon.setAutoLinkMask(Linkify.ALL);
                convertView.setTag(holder);
            } else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            if (datas.size() > 0)
            {
                holder.msgTime.setText(DateUtils.formatDate(datas.get(position)
                        .getDate(), Constant.DATE_FORMAT_COMPACT));
                holder.msgCon.setText(Html.fromHtml(datas.get(position)
                        .getContext()));
            }
            return convertView;
        }

        class ViewHolder
        {
            TextView msgTime;// 消息时间

            TextView msgCon;// 消息内容
        }

    }

    /**
     * 
     * @类名称：MsgListAsyncTask
     * @类描述：请求消息信息
     * @创建人：aaron
     * @修改人：
     * @修改时间：2015年6月10日 上午9:26:42
     * @修改备注：
     * @version:
     */
    public class MsgListAsyncTask extends AsyncTask<Void, Void, FMMessage>
    {

        private String url;

        private int loadType;// 0：下拉，1：上拉

        int size;

        public MsgListAsyncTask(int loadType)
        {
            // TODO Auto-generated constructor stub
            this.loadType = loadType;
        }

        @Override
        protected FMMessage doInBackground(Void... params)
        {
            FMMessage messageBean = null;
            try
            {
                messageBean = new FMMessage();
                JSONUtil<FMMessage> jsonUtil = new JSONUtil<FMMessage>();
                String jsonStr = HttpUtil.getInstance().doGet(url);
                messageBean = jsonUtil.toBean(jsonStr, messageBean);
            } catch (JsonSyntaxException e)
            {
                LogUtil.e("JSON_ERROR", e.getMessage());
                messageBean.setResultCode(0);
                messageBean.setResultDescription("解析json出错");
            } catch (Exception ex)
            {
                // TODO: handle exception
                return null;
            }
            return messageBean;
        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
            ObtainParamsMap obtainMap = new ObtainParamsMap(
                    MsgCenterActivity.this);
            Paging paging = new Paging();
            paging.setPagingSize(Constant.PAGES_COMMON);
            if (Constant.REFRESH == loadType)
            {
                // 下拉
                paging.setPagingTag("");
            } else if (Constant.LOAD_MORE == loadType)
            {
                // 上拉
                if (datas != null && datas.size() > 0)
                {
                    cy.com.morefan.bean.Message message = datas.get(datas
                            .size() - 1);
                    paging.setPagingTag(String.valueOf(message
                            .getMessageOrder()));
                } else if (datas != null && datas.size() == 0)
                {
                    paging.setPagingTag("");
                }
            }
            //
            String paramMap = obtainMap.getMap();
            Map<String, String> signMap = new HashMap<String, String>();
            signMap.put("pagingTag", paging.getPagingTag());
            signMap.put("pagingSize", paging.getPagingSize().toString());

            String signStr = obtainMap.getSign(signMap);
            url = Constant.MESSAGE;
            try
            {
                url += "?sign=" + URLEncoder.encode(signStr, "UTF-8");
                url += paramMap;
                url += "&pagingTag="
                        + URLEncoder.encode(paging.getPagingTag(), "UTF-8");
                url += "&pagingSize="
                        + URLEncoder.encode(paging.getPagingSize().toString(),
                                "UTF-8");

            } catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                KJLoger.errorLog(e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(final FMMessage result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            
            if( result ==null ){
                ToastUtils.showLongToast(MsgCenterActivity.this , "网络请求发生异常，请重试");
                return;
            }
            if( 1 != result.getSystemResultCode()){
                String errorMsg = result.getSystemResultDescription();
                if( null == errorMsg|| errorMsg.length()<1){
                    errorMsg = "服务器发生" + result.getSystemResultCode()+"错误";
                }
                ToastUtils.showLongToast(MsgCenterActivity.this,  errorMsg );
                return;
            }
            
            AlphaAnimation anima = new AlphaAnimation(0.0f, 5.0f);
            anima.setDuration(1000);// 设置动画显示时间
            listNotice.setAnimation(anima);
            if (1 == result.getResultCode())
            {
                if (Constant.REFRESH == loadType)
                {
                    // 刷新列表
                    datas.clear();
                    datas.addAll(result.getResultData().getMessages());
                    adapter.notifyDataSetChanged();
                    anima.setAnimationListener(new AnimationListener()
                    {

                        @Override
                        public void onAnimationStart(Animation animation)
                        {
                            // TODO Auto-generated method stub
                            listNotice.setVisibility(View.VISIBLE);
                            if (result.getResultData().getMessages().isEmpty())
                            {
                                listNotice.setText("系统暂无消息");
                            } else
                            {
                                listNotice.setText("数据已经刷新");
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animation animation)
                        {
                            // TODO Auto-generated method stub
                            listNotice.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation)
                        {
                            // TODO Auto-generated method stub

                        }

                    });

                } else if (Constant.LOAD_MORE == loadType)
                {
                    if (!result.getResultData().getMessages().isEmpty())
                    {
                        datas.clear();
                        size = result.getResultData().getMessages().size();
                        datas.addAll(result.getResultData().getMessages());
                        adapter.notifyDataSetChanged();
                    }
                    anima.setAnimationListener(new AnimationListener()
                    {

                        @Override
                        public void onAnimationStart(Animation animation)
                        {
                            // TODO Auto-generated method stub
                            listNotice.setVisibility(View.VISIBLE);
                            if (!result.getResultData().getMessages().isEmpty())
                            {
                                listNotice.setText("加载了" + size + "条数据");
                            } else
                            {
                                listNotice.setText("没有可加载的数据");
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animation animation)
                        {
                            // TODO Auto-generated method stub
                            listNotice.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation)
                        {
                            // TODO Auto-generated method stub

                        }

                    });

                }

            } else if (5000 == result.getResultCode())
            {
                if (Constant.REFRESH == loadType)
                {
                    // 刷新列表
                    datas.clear();
                    adapter.notifyDataSetChanged();
                    anima.setAnimationListener(new AnimationListener()
                    {

                        @Override
                        public void onAnimationStart(Animation animation)
                        {
                            // TODO Auto-generated method stub
                            listNotice.setVisibility(View.VISIBLE);
                            listNotice.setText("系统暂无消息");
                        }

                        @Override
                        public void onAnimationEnd(Animation animation)
                        {
                            // TODO Auto-generated method stub
                            listNotice.setVisibility(View.GONE);

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation)
                        {
                            // TODO Auto-generated method stub

                        }

                    });
                } else if (Constant.LOAD_MORE == loadType)
                {
                    anima.setAnimationListener(new AnimationListener()
                    {

                        @Override
                        public void onAnimationStart(Animation animation)
                        {
                            // TODO Auto-generated method stub
                            listNotice.setVisibility(View.VISIBLE);
                            listNotice.setText("没有可加载的数据");
                        }

                        @Override
                        public void onAnimationEnd(Animation animation)
                        {
                            // TODO Auto-generated method stub
                            listNotice.setVisibility(View.GONE);

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation)
                        {
                            // TODO Auto-generated method stub

                        }

                    });
                }

            } else if (Constant.TOKEN_OVERDUE == result.getResultCode())
            {
                // 提示账号异地登陆，强制用户退出
                // 并跳转到登录界面
                ToastUtils
                        .showLongToast(MsgCenterActivity.this, "账户登录过期，请重新登录");
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        ActivityUtils.getInstance().loginOutInActivity(
                                (Activity) MsgCenterActivity.this);
                    }
                }, 2000);
            }
        }

    }

    /**
     * 
     * @类名称：DeleteMsgAsyncTask
     * @类描述：删除消息请求
     * @创建人：aaron
     * @修改人：
     * @修改时间：2015年6月10日 上午9:26:27
     * @修改备注：
     * @version:
     */
    public class DeleteMsgAsyncTask extends AsyncTask<Integer, Void, FMMessage>
    {

        int messageId;

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(FMMessage result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (1 == result.getResultCode())
            {
                // 删除消息成功，删除本地消息并同步
                cy.com.morefan.bean.Message deleteMsg = (cy.com.morefan.bean.Message) adapter
                        .getItem(messageId - 1);
                datas.remove(deleteMsg);
                adapter.notifyDataSetChanged();

            } else
            {
                ToastUtils.showLongToast(MsgCenterActivity.this, "消息删除失败！");
            }
        }

        @Override
        protected FMMessage doInBackground(Integer... params)
        {
            // TODO Auto-generated method stub
            messageId = params[0];
            FMMessage messageBean = new FMMessage();
            JSONUtil<FMMessage> jsonUtil = new JSONUtil<FMMessage>();
            String url;
            ObtainParamsMap obtainMap = new ObtainParamsMap(
                    MsgCenterActivity.this);
            Map<String, String> paramMap = obtainMap.obtainMap();

            // 拼接注册url
            url = Constant.DELETE_MESSAGE;
            paramMap.put("messageId", String.valueOf(messageId));
            // 封装sign
            String signStr = obtainMap.getSign(paramMap);
            paramMap.put("sign", signStr);
            if (Constant.IS_PRODUCTION_ENVIRONMENT)
            {
                String jsonStr = HttpUtil.getInstance().doPost(url, paramMap);
                KJLoger.i("删除消息", jsonStr);
                try
                {
                    messageBean = jsonUtil.toBean(jsonStr, messageBean);
                } catch (JsonSyntaxException e)
                {
                    LogUtil.e("JSON_ERROR", e.getMessage());
                    messageBean.setResultCode(0);
                    messageBean.setResultDescription("解析json出错");
                }
            } else
            {
            }
            return messageBean;
        }

    }

}
