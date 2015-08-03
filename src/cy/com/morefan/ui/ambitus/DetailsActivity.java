package cy.com.morefan.ui.ambitus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
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
import cy.com.morefan.R;
import cy.com.morefan.bean.Detail;
import cy.com.morefan.bean.FMDetails;
import cy.com.morefan.bean.Paging;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.ui.account.MsgCenterActivity;
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
 * @类名称：DetailsActivity
 * @类描述：流量明细界面
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月10日 上午9:59:08
 * @修改备注：
 * @version:
 */
public class DetailsActivity extends BaseActivity implements Callback,
        OnClickListener,MyBroadcastReceiver.BroadcastListener
{

    private KJListView detailList;

    private CyButton backImage;

    private TextView title;

    private TextView functionBtn;

    private List<Detail> datas;

    private DetailDataAdapter adapter;

    // 返回文字事件
    private TextView backText;
    // 列表刷新提示
    private TextView listNotice;

    private MyBroadcastReceiver myBroadcastReceiver;

    @Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        this.setContentView(R.layout.details_ui);
        datas = new ArrayList<Detail>();
        this.initView();

        // detailList.setPullLoadEnable(false);
        adapter = new DetailDataAdapter();
        detailList.setAdapter(adapter);

        initList();

        myBroadcastReceiver=new MyBroadcastReceiver(this,this,MyBroadcastReceiver.ACTION_SENDFLOW);
    }

    @Override
    protected void onResume() {
        super.onResume();

        detailList.setRefreshTime(
                DateUtils.formatDate(System.currentTimeMillis()),
                DetailsActivity.this);
        new GetDetailAsyncTask(Constant.REFRESH).execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myBroadcastReceiver!=null){
            myBroadcastReceiver.unregisterReceiver();
        }
    }

    private void initList()
    {
        detailList.setOnRefreshListener(new KJRefreshListener()
        {

            @Override
            public void onRefresh()
            {
                // TODO Auto-generated method stub
                // 加载数据
                detailList.setRefreshTime(
                        DateUtils.formatDate(System.currentTimeMillis()),
                        DetailsActivity.this);
                new GetDetailAsyncTask(Constant.REFRESH).execute();
                detailList.stopRefreshData();
            }

            @Override
            public void onLoadMore()
            {
                // TODO Auto-generated method stub
                detailList.setRefreshTime(
                        DateUtils.formatDate(System.currentTimeMillis()),
                        DetailsActivity.this);
                new GetDetailAsyncTask(Constant.LOAD_MORE).execute();
                detailList.stopRefreshData();
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
            closeSelf(DetailsActivity.this);
        }

            break;
        case R.id.functionBtn:
        {
            reflush();
        }

            break;
        case R.id.backtext:
        {
            closeSelf(DetailsActivity.this);
        }
            break;
        default:
            break;
        }
    }

    private void initView()
    {
        detailList = (KJListView) this.findViewById(R.id.detailList);
        detailList.setPullLoadEnable(true);

        backImage = (CyButton) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(this);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("流量明细");
        functionBtn = (TextView) this.findViewById(R.id.functionBtn);
        functionBtn.setVisibility(View.VISIBLE);
        functionBtn.setText("刷新");
        functionBtn.setOnClickListener(this);
        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);
        listNotice = (TextView) this.findViewById(R.id.detailListNotice);
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
            DetailsActivity.this.finish();
            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

    private void reflush()
    {

    }

    @Override
    public void onFinishReceiver(MyBroadcastReceiver.ReceiverType type, Object msg) {
        new GetDetailAsyncTask(Constant.REFRESH).execute();
    }

    class DetailDataAdapter extends BaseAdapter
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
                convertView = View.inflate(DetailsActivity.this,
                        R.layout.detail_item_ui, null);
                holder.detailTitle = (TextView) convertView
                        .findViewById(R.id.detailTitle);
                holder.detailDate = (TextView) convertView
                        .findViewById(R.id.detailDate);
                holder.flow = (TextView) convertView.findViewById(R.id.flow);
                convertView.setTag(holder);
            } else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.detailTitle.setText(datas.get(position).getTitle());
            holder.detailDate.setText(DateUtils.formatDate(datas.get(position)
                    .getDate()));
            float flow = datas.get(position).getVary();
            if (flow >= 0)
            {
                holder.flow.setText("+" + datas.get(position).getVary() + "M");
                holder.flow.setTextColor(DetailsActivity.this.getResources()
                        .getColor(R.color.flow_detail_color));
            } else
            {
                holder.flow.setTextColor(DetailsActivity.this.getResources()
                        .getColor(R.color.flow_detail_color_red));
                holder.flow.setText( datas.get(position).getVary() + "M");
            }
            return convertView;
        }

        class ViewHolder
        {
            TextView detailTitle;

            TextView detailDate;

            TextView flow;
        }

    }

    class GetDetailAsyncTask extends AsyncTask<Void, Void, FMDetails>
    {
        int loadType = Constant.REFRESH;
        int size;
        
        public GetDetailAsyncTask(int type)
        {
            loadType = type;
        }

        @Override
        protected FMDetails doInBackground(Void... params)
        {
            // TODO Auto-generated method stub
            FMDetails detailBean = new FMDetails();
            JSONUtil<FMDetails> jsonUtil = new JSONUtil<FMDetails>();
            ObtainParamsMap obtainMap = new ObtainParamsMap(
                    DetailsActivity.this);
            String paramMap = obtainMap.getMap();
            // 封装sign
            Map<String, String> signMap = new HashMap<String, String>();
            // String signStr = obtainMap.getSign(null);
            String url = Constant.DETAILS;

            Paging paging = new Paging();
            paging.setPagingSize(Constant.PAGES_COMMON);
            if (loadType == Constant.REFRESH)
            {
                paging.setPagingTag("");
            } else
            {
                // 上拉
                if (datas != null && datas.size() > 0)
                {
                    Detail detail = datas.get(datas.size() - 1);
                    paging.setPagingTag(String.valueOf(detail.getDetailOrder()));
                } else if (datas != null && datas.size() == 0)
                {
                    paging.setPagingTag("");
                }
            }

            signMap.put("pagingTag", paging.getPagingTag());
            signMap.put("pagingSize", String.valueOf(paging.getPagingSize()));
            String signStr = obtainMap.getSign(signMap);

            try
            {
                url += "?sign=" + URLEncoder.encode(signStr, "UTF-8");
                url += paramMap;
                url += "&pagingTag="
                        + URLEncoder.encode(paging.getPagingTag(), "UTF-8");
                url += "&pagingSize="
                        + URLEncoder.encode(paging.getPagingSize().toString(),
                                "UTF-8");

                // 根据返回的json封装新的user数据集
                // 切换到测试环境
                if (Constant.IS_PRODUCTION_ENVIRONMENT)
                {
                    String jsonStr = HttpUtil.getInstance().doGet(url);
                    try
                    {
                        detailBean = jsonUtil.toBean(jsonStr, detailBean);
                    } catch (JsonSyntaxException e)
                    {
                        LogUtil.e("JSON_ERROR", e.getMessage());
                        detailBean.setResultCode(0);
                        detailBean.setResultDescription("解析json出错");
                    }
                } else
                {
                    detailBean.setResultCode(1);
                    List<Detail> details = new ArrayList<Detail>();
                    Detail detail = new Detail();
                    detail.setDate(System.currentTimeMillis());
                    detail.setDetailId(1);
                    detail.setTitle("明细1");
                    detail.setVary(100);
                    details.add(detail);
                    detail = new Detail();
                    detail.setDate(System.currentTimeMillis());
                    detail.setDetailId(1);
                    detail.setTitle("明细2");
                    detail.setVary(-100);
                    details.add(detail);
                    // detailBean.set.setResultData(details);
                }

            } catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                KJLoger.errorLog(e.getMessage());
            }
            return detailBean;
        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(final FMDetails result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            AlphaAnimation anima = new AlphaAnimation(0.0f, 5.0f);
            anima.setDuration(1000);// 设置动画显示时间
            listNotice.setAnimation(anima);
            if(1 == result.getResultCode())
            {
                // 下拉刷新和上拉加载分开处理
                if (Constant.REFRESH == loadType)
                {
                    // 下拉刷新
                    datas.clear();
                    datas.addAll(result.getResultData().getDetails());
                    adapter.notifyDataSetChanged();
                    anima.setAnimationListener(new AnimationListener()
                    {

                        @Override
                        public void onAnimationStart(Animation animation)
                        {
                            // TODO Auto-generated method stub
                            listNotice.setVisibility(View.VISIBLE);
                            if(result.getResultData().getDetails().isEmpty())
                            {
                                listNotice.setText("系统暂无流量明细");
                            }
                            else
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
                } 
                else if (Constant.LOAD_MORE == loadType)
                {
                    if(!result.getResultData().getDetails().isEmpty())
                    {
                        datas.clear();
                        size = result.getResultData().getDetails().size();
                        datas.addAll(result.getResultData().getDetails());
                        adapter.notifyDataSetChanged();
                    }
                    anima.setAnimationListener(new AnimationListener()
                    {

                        @Override
                        public void onAnimationStart(Animation animation)
                        {
                            // TODO Auto-generated method stub
                            listNotice.setVisibility(View.VISIBLE);
                            if(!result.getResultData().getDetails().isEmpty())
                            {
                                listNotice.setText("加载了" + size + "条数据");
                            }
                            else
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
            }
            else if (5000 == result.getResultCode())
            {
                // 下拉刷新和上拉加载分开处理
                if (Constant.REFRESH == loadType)
                {
                    //刷新列表
                    datas.clear();
                    adapter.notifyDataSetChanged();
                    anima.setAnimationListener(new AnimationListener()
                    {

                        @Override
                        public void onAnimationStart(Animation animation)
                        {
                            // TODO Auto-generated method stub
                            listNotice.setVisibility(View.VISIBLE);
                            listNotice.setText("系统暂无流量明细");
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
                else if (Constant.LOAD_MORE == loadType)
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
            }
            else if (Constant.TOKEN_OVERDUE == result.getResultCode())
            {
                // 提示账号异地登陆，强制用户退出
                // 并跳转到登录界面
                ToastUtils.showLongToast(DetailsActivity.this, "账户登录过期，请重新登录");
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        ActivityUtils.getInstance().loginOutInActivity(
                                (Activity) DetailsActivity.this);
                    }
                }, 2000);
            }

        }
    }
}
