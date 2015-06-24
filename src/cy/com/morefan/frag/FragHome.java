package cy.com.morefan.frag;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMTaskData;
import cy.com.morefan.bean.IBaseData;
import cy.com.morefan.bean.Paging;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.DataListener;
import cy.com.morefan.ui.answer.AnswerActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.BitmapLoader;
import cy.com.morefan.util.DateUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.KJLoger;
import cy.com.morefan.util.L;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.StringUtils;
import cy.com.morefan.view.KJListView;
import cy.com.morefan.view.KJRefreshListener;

/**
 * 首页
 * 
 * @author cy
 *
 */
public class FragHome extends BaseFragment implements DataListener, Callback,
        OnItemClickListener
{

    private final String Tag = FragHome.class.getName();

    private KJListView listView;

    public MyApplication application;

    private List<TaskData> datas;

    // 列表刷新提示
    private TextView listNotice;

    private TaskDataAdapter adapter;

    private Handler mHandler = new Handler(this);

    @Override
    public boolean handleMessage(Message msg)
    {

        return false;

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
        datas = new ArrayList<TaskData>();
        application = (MyApplication) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_home, container, false);
        listView = (KJListView) rootView.findViewById(R.id.listView);
        listView.setPullLoadEnable(true);
        adapter = new TaskDataAdapter();
        listView.setAdapter(adapter);

        initList();
        listView.setRefreshTime(
                DateUtils.formatDate(System.currentTimeMillis()), getActivity());
        new GetTaskListAsyncTask(Constant.REFRESH).execute(Constant.TASK_DATA_INTEFACE);
        listNotice = (TextView) rootView.findViewById(R.id.listNotice);
        listView.setOnItemClickListener(this);

        return rootView;
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
                        getActivity());
                new GetTaskListAsyncTask(Constant.REFRESH)
                        .execute(Constant.TASK_DATA_INTEFACE);
                listView.stopRefreshData();
            }

            @Override
            public void onLoadMore()
            {
                // TODO Auto-generated method stub
                listView.setRefreshTime(
                        DateUtils.formatDate(System.currentTimeMillis()),
                        getActivity());
                new GetTaskListAsyncTask(Constant.LOAD_MORE)
                        .execute(Constant.TASK_DATA_INTEFACE);
                listView.stopRefreshData();
            }
        });
    }

    @Override
    public void onReshow()
    {
        L.i(">>>>onReshow");

    }

    @Override
    public void onFragPasue()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View view)
    {
        // TODO Auto-generated method stub

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
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        int idx = position - 1;
        if (idx < 0 || idx >= datas.size())
            return;

        TaskData item = (TaskData) datas.get(idx);
        ActivityUtils.getInstance().showActivity(getActivity(),
                AnswerActivity.class, "task", (Serializable) item);
    }

    class GetTaskListAsyncTask extends AsyncTask<String, Void, FMTaskData>
    {
        private int loadType;// 0：下拉，1：上拉

        public GetTaskListAsyncTask(int loadType)
        {
            // TODO Auto-generated constructor stub
            this.loadType = loadType;
        }
        int size;

        @Override
        protected FMTaskData doInBackground(String... params)
        {
            // TODO Auto-generated method stub
            Paging paging = new Paging();
            paging.setPagingSize(Constant.PAGES);
            // 判断是下拉刷新还是上拉加载数据
            if (Constant.REFRESH == loadType)
            {
                // 下拉
                paging.setPagingTag("");
            } else if (Constant.LOAD_MORE == loadType)
            {
                // 上拉
                if (datas != null && datas.size() > 0)
                {
                    TaskData lastTask = datas.get(datas.size() - 1);
                    paging.setPagingTag(String.valueOf(lastTask.getTaskOrder()));
                }
                else if(datas != null && datas.size() == 0)
                {
                    paging.setPagingTag("");
                }
            }
            // JSONUtil<Paging> paingUtil=new JSONUtil<Paging>();
            // String pagingString = paingUtil.toJson(paging);

            String url = params[0];
            FMTaskData taskDatas = new FMTaskData();
            ObtainParamsMap obtainMap = new ObtainParamsMap(getActivity());
            //
            String paramMap = obtainMap.getMap();
            Map<String, String> signMap = new HashMap<String, String>();
            signMap.put("pagingTag", paging.getPagingTag());
            signMap.put("pagingSize", paging.getPagingSize().toString());

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

                Log.i(Tag, url);

            } catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                KJLoger.errorLog(e.getMessage());
            }
            if (Constant.IS_PRODUCTION_ENVIRONMENT)
            {
                JSONUtil<FMTaskData> jsonUtil = new JSONUtil<FMTaskData>();
                //
                String json = HttpUtil.getInstance().doGet(url);
                try
                {
                    taskDatas = jsonUtil.toBean(json, taskDatas);
                } catch (JsonSyntaxException e)
                {
                    LogUtil.e("JSON_ERROR", e.getMessage());
                    taskDatas.setResultCode(0);
                    taskDatas.setResultDescription("解析json出错");
                }
            } else
            {
                taskDatas.setResultCode(0);
            }

            return taskDatas;
        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(FMTaskData result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            AlphaAnimation anima = new AlphaAnimation(0.0f, 5.0f);
            anima.setDuration(1000);// 设置动画显示时间
            listNotice.setAnimation(anima);
            if (Constant.IS_PRODUCTION_ENVIRONMENT)
            {

                // 下拉刷新和上拉加载分开处理
                if (Constant.REFRESH == loadType)
                {
                    // 下拉刷新
                    if (1 == result.getResultCode())
                    {
                        //重新加载数据
                        datas.clear();
                        datas.addAll(result.getResultData().getTask());
                        adapter.notifyDataSetChanged();
                        anima.setAnimationListener(new AnimationListener()
                        {

                            @Override
                            public void onAnimationStart(Animation animation)
                            {
                                // TODO Auto-generated method stub
                                listNotice.setVisibility(View.VISIBLE);
                                listNotice.setText("数据已经刷新");
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
                } else if (Constant.LOAD_MORE == loadType)
                {
                    // 上拉加载
                    if (1 == result.getResultCode())
                    {
                        size = result.getResultData().getTask().size();
                        // 刷新列表
                        datas.addAll(result.getResultData().getTask());
                        adapter.notifyDataSetChanged();
                        anima.setAnimationListener(new AnimationListener()
                        {

                            @Override
                            public void onAnimationStart(Animation animation)
                            {
                                // TODO Auto-generated method stub
                                listNotice.setVisibility(View.VISIBLE);
                                listNotice.setText("加载了" + size + "条数据");
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
                    } else if (5000 == result.getResultCode())
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
            } else
            {
                datas.addAll(initData());
                adapter.notifyDataSetChanged();
            }

        }

    }

    private List<TaskData> initData()
    {
        List<TaskData> datas = new ArrayList<TaskData>();
        TaskData data = new TaskData();
        data.setPictureURL("");
        data.setStatus("1");
        data.setTaskId(0);
        data.setTitle("测试");
        datas.add(data);
        return datas;
    }

    public class TaskDataAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            // TODO Auto-generated method stub
            return (datas.size() > Constant.PAGES) ? Constant.PAGES : datas
                    .size();

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
            Resources res = getActivity().getResources();
            if (convertView == null)
            {
                holder = new ViewHolder();
                convertView = View.inflate(getActivity(), R.layout.item_unit,
                        null);
                holder.img = (NetworkImageView) convertView
                        .findViewById(R.id.img);
                holder.txtName = (TextView) convertView
                        .findViewById(R.id.txtName);
                holder.txtDate = (TextView) convertView
                        .findViewById(R.id.txtDate);
                holder.txtDes = (TextView) convertView
                        .findViewById(R.id.txtDes);
                holder.txtFlow = (TextView) convertView
                        .findViewById(R.id.txtFlow);
                holder.txtCount = (TextView) convertView
                        .findViewById(R.id.txtCount);
                holder.imgStatus = (ImageView) convertView
                        .findViewById(R.id.imgStatus);
                holder.fp_flow = (TextView) convertView
                        .findViewById(R.id.fp_flow);
                holder.txtCountL = (TextView) convertView
                        .findViewById(R.id.txtCountL);
                holder.txtCountR = (TextView) convertView
                        .findViewById(R.id.txtCountR);
                convertView.setTag(holder);
            } else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            // 加载图片
            if (datas.size() > 0)
            {
                BitmapLoader.create().displayUrl(getActivity(), holder.img,
                        datas.get(position).getPictureURL());

                holder.txtName.setText(datas.get(position).getTitle());
                // holder.txtDate.setText(DateUtils.formatDate(datas.get(position)
                // .getPublishDate()));

                boolean isToday = DateUtils.isToday(datas.get(position)
                        .getPublishDate());
                String publishDateStr = "";

                if (isToday)
                {
                    publishDateStr = "今日";
                } else
                {
                    publishDateStr = DateUtils.formatDate(datas.get(position)
                            .getPublishDate(), Constant.DATE_FORMAT_COMPACT);
                }
                holder.txtDate.setText(publishDateStr);
                String merchant = datas.get(position).getMerchantTitle();
                if (StringUtils.isEmpty(merchant))
                {
                    holder.txtDes.setText("由【  】提供");
                } else
                {
                    holder.txtDes.setText("由【"
                            + datas.get(position).getMerchantTitle() + "】提供");
                }

                holder.txtFlow.setText("免费获得");
                holder.fp_flow.setText(datas.get(position).getMaxBonus() + "M");
                holder.txtCountL.setText("已有");
                holder.txtCount.setText("" + datas.get(position).getLuckies()
                        + "");
                holder.txtCountR.setText("人获取");
                
                holder.imgStatus.setImageDrawable(null);
                
                if( datas.get(position).getLast() == 0 ){
                    holder.imgStatus.setImageDrawable( res.getDrawable( R.drawable.unit_status_over));
                }else
                {
                    if( null != datas.get(position)  && datas.get(position).getReward() >0 ){
                        holder.imgStatus.setImageDrawable( res.getDrawable( R.drawable.unit_status_get ));
                    }
                }
                    
                
//                if (Constant.TASK_STATE_PRE_RELEASE.equals(datas.get(position)
//                        .getStatus()))
//                {
//                    // 预发布
//                    holder.imgStatus.setImageDrawable(res
//                            .getDrawable(R.drawable.unit_status_online));
//                } else if (Constant.TASK_STATE_RELEASE.equals(datas.get(
//                        position).getStatus()))
//                {
//                    // 已发布
//                    holder.imgStatus.setImageDrawable(res
//                            .getDrawable(R.drawable.unit_status_get));
//                } else
//                {
//                    // 已结束
//                    holder.imgStatus.setImageDrawable(res
//                            .getDrawable(R.drawable.unit_status_over));
//                }
            }
            return convertView;
        }

        class ViewHolder
        {
            NetworkImageView img;// 任务图片

            TextView txtName;// 任务标题

            TextView txtDate;// 发布时间

            TextView txtDes;// 详细信息

            TextView txtFlow;// 免费领取100M

            TextView txtCount;// 已有300人领取

            ImageView imgStatus;// 任务状态

            TextView fp_flow;// 可领取流量
            TextView txtCountL;//已有
            TextView txtCountR;//人领取
        }

    }

}
