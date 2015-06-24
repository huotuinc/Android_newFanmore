package cy.com.morefan.ui.ambitus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import android.graphics.pdf.PdfDocument.PageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.bean.Detail;
import cy.com.morefan.bean.FMDetails;
import cy.com.morefan.bean.Paging;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.Constant;
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
        OnClickListener
{

    private KJListView detailList;

    private CyButton backImage;

    private TextView title;

    private TextView functionBtn;

    private List<Detail> datas;

    private DetailDataAdapter adapter;

    // 返回文字事件
    private TextView backText;

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
        detailList.setRefreshTime(
                DateUtils.formatDate(System.currentTimeMillis()),
                DetailsActivity.this);
        new GetDetailAsyncTask(Constant.REFRESH).execute();
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
            paging.setPagingSize(Constant.PAGES);
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
        protected void onPostExecute(FMDetails result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if (null == result)
            {
                ToastUtils.showLongToast(DetailsActivity.this, "请求失败。");
                return;
            }
            if (1 != result.getSystemResultCode())
            {
                ToastUtils.showLongToast(DetailsActivity.this,
                        result.getSystemResultDescription());
                return;
            }

            if (1 != result.getResultCode())
            {
                ToastUtils.showLongToast(DetailsActivity.this,
                        result.getResultDescription());
                return;
            }

            if (result.getResultData() == null
                    || result.getResultData().getDetails() == null
                    || result.getResultData().getDetails().size() < 1)
            {
                ToastUtils.showLongToast(DetailsActivity.this, "没有更多的数据加载");
                return;
            }

            // 下拉刷新和上拉加载分开处理
            if (Constant.REFRESH == loadType)
            {
                // 下拉刷新
                // 重新加载数据
                datas.clear();
                datas.addAll(result.getResultData().getDetails());
                adapter.notifyDataSetChanged();
            } else if (Constant.LOAD_MORE == loadType)
            {
                datas.addAll(result.getResultData().getDetails());
                adapter.notifyDataSetChanged();
            }
        }
    }
}
