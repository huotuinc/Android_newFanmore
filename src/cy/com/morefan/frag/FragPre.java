package cy.com.morefan.frag;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.FMPreTaskData;
import cy.com.morefan.bean.IBaseData;
import cy.com.morefan.bean.Paging;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.frag.FragPre.PreTaskAdapter.ViewHolder;
import cy.com.morefan.listener.DataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.BitmapLoader;
import cy.com.morefan.util.DateUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.KJLoger;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.StringUtils;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.KJListView;
import cy.com.morefan.view.KJRefreshListener;

/**
 * 今日预告
 * 
 * @author cy
 *
 */
public class FragPre extends BaseFragment implements DataListener, Callback
{

    private KJListView listView;

    private List<TaskData> datas;

    private PreTaskAdapter adapter;

    private Handler mHandler = new Handler(this);

    private ViewHolder holder = null;

    private AlarmManager alarm;

    private RefreshReceiver refreshReceiver;

    //public static final String REFRESH_TASK_STATUS = "cy.com.morefan.pre.status";
    // 列表刷新提示
    private TextView listNotice;
    public MyApplication application;
    

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setRetainInstance(true);
        datas = new ArrayList<TaskData>();
        alarm = (AlarmManager) getActivity().getSystemService(
                Context.ALARM_SERVICE);
        application = (MyApplication) getActivity().getApplication();
        List<Integer> taskids = application.taskList;
        if(null == taskids || taskids.isEmpty())
        {
            String str = MyApplication.readString(getActivity(), Constant.TASK_ALARM_INFO, Constant.TASK_ALARM_INFO_ID);
            if(null == str || "".equals(str))
            {
                //tasks = new ArrayList<Integer>();
            } 
            else
            {
                //读取文件中的taskid
                List<Integer> is = new ArrayList<Integer>();
                JSONUtil<InnerClass> jsonUtil = new JSONUtil<InnerClass>();
                InnerClass inner = new InnerClass();
                try
                {
                    inner = jsonUtil.toBean(str, inner);
                    application.taskList = inner.getTaskIds();
                }
                catch (JsonSyntaxException e)
                {
                    LogUtil.e("JSON_ERROR", e.getMessage());
                    //tasks = new ArrayList<Integer>();
                }
                
            }
            
            
        }
        // 注册刷新列表广播
       registerFreshReceiver(); // used for receive msg
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
    }
    
    

    @Override
    public void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();
        //getActivity().unregisterReceiver(refreshReceiver);
    }

    // 注册刷新列表广播
    private void registerFreshReceiver()
    {
        if(null == refreshReceiver)
        {
            refreshReceiver = new RefreshReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction( MyBroadcastReceiver.REFRESH_TASK_STATUS);
        getActivity().registerReceiver(refreshReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_pre, container, false);
        listView = (KJListView) rootView.findViewById(R.id.announceListView);
        listView.setPullLoadEnable(true);
        listNotice = (TextView) rootView.findViewById(R.id.listNotice);
        adapter = new PreTaskAdapter();
        listView.setAdapter(adapter);
        initList();
        listView.setRefreshTime(
                DateUtils.formatDate(System.currentTimeMillis()), getActivity());
       
//        new PreTaskAsyncTask(Constant.REFRESH)
//                .execute(Constant.PREVIEW_TASK_LIST);

        return rootView;
    }

    
    @Override
    public void onDestroyView()
    {
        // TODO Auto-generated method stub
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        getActivity().unregisterReceiver(refreshReceiver);
        //任务编号写文件
        InnerClass innerClass = new InnerClass();
        innerClass.setTaskIds(application.taskList);
        JSONUtil<InnerClass> jsonUtil = new JSONUtil<InnerClass>();
        String taskJson = jsonUtil.toJson(innerClass);
        application.writeString(getActivity(), Constant.TASK_ALARM_INFO, Constant.TASK_ALARM_INFO_ID, taskJson);
    }
    

    private void initList()
    {
        listView.setOnRefreshListener(new KJRefreshListener()
        {

            @Override
            public void onRefresh()
            {
                // TODO Auto-generated method stub
                listView.setRefreshTime(
                        DateUtils.formatDate(System.currentTimeMillis()),
                        getActivity());
                new PreTaskAsyncTask(Constant.REFRESH)
                        .execute(Constant.PREVIEW_TASK_LIST);
                listView.stopRefreshData();
            }

            @Override
            public void onLoadMore()
            {
                // TODO Auto-generated method stub
                listView.setRefreshTime(
                        DateUtils.formatDate(System.currentTimeMillis()),
                        getActivity());
                new PreTaskAsyncTask(Constant.LOAD_MORE)
                        .execute(Constant.PREVIEW_TASK_LIST);
                listView.stopRefreshData();
            }
        });
    }

    @Override
    public void onReshow()
    {
        // TODO Auto-generated method stub
        registerFreshReceiver(); // used for receive msg
        
        new PreTaskAsyncTask(Constant.REFRESH).execute(Constant.PREVIEW_TASK_LIST);
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
    public boolean handleMessage(Message msg)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onDataFinish(int type, String msg, Bundle extra,
            IBaseData... data)
    {
        // TODO Auto-generated method stub
        mHandler.obtainMessage(type, data).sendToTarget();
    }

    @Override
    public void onDataFail(int type, String msg, Bundle extra)
    {
        // TODO Auto-generated method stub
        mHandler.obtainMessage(type, msg).sendToTarget();
    }

    public class PreTaskAdapter extends BaseAdapter
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
        public View getView(final int position, View convertView,
                ViewGroup parent)
        {
            // TODO Auto-generated method stub
            Resources res = getActivity().getResources();
            if (convertView == null)
            {
                holder = new ViewHolder();
                convertView = View.inflate(getActivity(),
                        R.layout.item_announce, null);
                holder.preimg = (NetworkImageView) convertView
                        .findViewById(R.id.preimg);
                holder.pretitle = (TextView) convertView
                        .findViewById(R.id.pretitle);
                holder.predate = (TextView) convertView
                        .findViewById(R.id.predate);
                holder.presummary = (TextView) convertView
                        .findViewById(R.id.presummary);
                holder.pretips = (TextView) convertView
                        .findViewById(R.id.pretips);
                holder.alarmBtn1 = (Button) convertView
                        .findViewById(R.id.alarmBtn1);

                holder.prestatus = (ImageView) convertView
                        .findViewById(R.id.prestatus);
                convertView.setTag(holder);
            } else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            // 加载图片
            if (datas.size() > 0)
            {
                BitmapLoader.create().displayUrl(getActivity(), holder.preimg,
                        datas.get(position).getPictureURL());
                holder.pretitle.setText(datas.get(position).getTitle());

                String dateString = "";
                boolean isToday = DateUtils.isToday(datas.get(position)
                        .getPublishDate());
                if (isToday)
                {
                    String timeStr = DateUtils.formatDate(datas.get(position)
                            .getPublishDate(), Constant.ONLYTIME_FORMAT);
                    dateString = "今日" + timeStr;
                } else
                {
                    dateString = DateUtils.formatDate(datas.get(position)
                            .getPublishDate());
                }

                holder.predate.setText("上线时间：" + dateString);
                String merchant = datas.get(position).getMerchantTitle();
                if (StringUtils.isEmpty(merchant))
                {
                    holder.presummary.setText("由【  】提供");
                } else
                {
                    holder.presummary.setText("由【"
                            + datas.get(position).getMerchantTitle() + "】提供");
                }
                float flow = datas.get(position).getReward();
                
                holder.pretips.setText("免费领取" + Util.decimalFloat(datas.get(position).getMaxBonus(), Constant.ACCURACY_1)+ "M");
                        
                if (datas.get(position).isOnline())
                {
                    // 任务已上线，隐藏按钮，显示状态图标
                    holder.prestatus.setVisibility(View.VISIBLE);
                    holder.alarmBtn1.setVisibility(View.GONE);
                } else
                {
                    // 任务未上线，隐藏状态图标，显示按钮
                    holder.prestatus.setVisibility(View.GONE);
                    holder.alarmBtn1.setVisibility(View.VISIBLE);
                    if (datas.get(position).isAlarm())
                    {
                        holder.alarmBtn1.setText("设置提醒");
                        holder.alarmBtn1
                                .setBackgroundResource(R.drawable.roundbuttonshape);
                        holder.alarmBtn1.setTextColor(res
                                .getColor(R.color.pre_btn_text_color));
                    } else
                    {
                        holder.alarmBtn1.setText("取消设置");
                        holder.alarmBtn1
                                .setBackgroundResource(R.drawable.btn_cancel_alarm);
                        holder.alarmBtn1.setTextColor(res
                                .getColor(R.color.frist_page_flow_color));

                    }

                    holder.alarmBtn1.setOnClickListener(new OnClickListener()
                    {

                        PendingIntent alarmPI;
                        @SuppressLint("NewApi")
                        @Override
                        public void onClick(View v)
                        {
                            // TODO Auto-generated method stub
                            TaskData taskData = null;
                            taskData = datas.get(position);
                            boolean tag = taskData.isAlarm();
                            if (tag)
                            {
                                // 如果未设置闹钟
                                Intent alarmI = new Intent(
                                        Constant.ALARM_ACTION);
                                // 添加任务的下下标
                                Bundle bundle = new Bundle();
                                bundle.putInt("index", position);
                                //记录设置过闹钟的任务
                                application.taskList.add(taskData.getTaskId());
                                int taskid = taskData.getTaskId();
                                
                                bundle.putSerializable("task", taskData);
                                alarmI.putExtras(bundle);
                                alarmPI = PendingIntent
                                        .getBroadcast(
                                                getActivity(),
                                                taskid ,
                                                alarmI,
                                                PendingIntent.FLAG_UPDATE_CURRENT);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(taskData
                                        .getPublishDate());
                                //calendar.add(Calendar.SECOND, -10);
                                //alarm.set(AlarmManager.RTC_WAKEUP,
                                //        calendar.getTimeInMillis(), alarmPI);
                                
                                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ){
                                    //ToastUtils.showLongToast(getActivity(), "sdk="+Build.VERSION.SDK_INT);
                                    alarm.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() , alarmPI);
                                }else
                                {
                                    //ToastUtils.showLongToast(getActivity(), "LLLLLsdk="+Build.VERSION.SDK_INT);
                                    alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() , alarmPI);
                                }

                                ToastUtils.showLongToast(getActivity(), "设置成功");
                            } else
                            {
                                // 取消闹钟
                                //alarm.cancel(null);                               
                                int taskid = taskData.getTaskId();
                                Intent intent = new Intent(Constant.ALARM_ACTION);
                                PendingIntent pi = PendingIntent.getBroadcast( getActivity() , taskid , intent, PendingIntent.FLAG_UPDATE_CURRENT);                                                                                                                 
                                alarm.cancel( pi );
                                application.taskList.remove(Integer.valueOf(taskData.getTaskId()));
                            }
                            taskData.setAlarm(!tag);
                            notifyDataSetChanged();
                        }
                    });
                }

            }
            return convertView;
        }

        class ViewHolder
        {
            NetworkImageView preimg;// 任务图片

            TextView pretitle;// 任务标题

            TextView predate;// 发布时间

            TextView presummary;// 简介

            TextView pretips;// 免费领取100M

            ImageView prestatus;// 任务状态

            Button alarmBtn1;// 定时按钮
        }

    }

    public class PreTaskAsyncTask extends
            AsyncTask<String, Void, FMPreTaskData>
    {

        private int loadType;// 0：下拉，1：上拉

        public PreTaskAsyncTask(int loadType)
        {
            // TODO Auto-generated constructor stub
            this.loadType = loadType;
        }

        int size;

        @Override
        protected FMPreTaskData doInBackground(String... params)
        {
            // TODO Auto-generated method stub
            Paging paging = new Paging();
            paging.setPagingSize(Constant.PAGES_TASK);
            if (Constant.REFRESH == loadType)
            {
                paging.setPagingTag("");
            } else if (Constant.LOAD_MORE == loadType)
            {
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

            String url = params[0];
            FMPreTaskData preTaskDatas = new FMPreTaskData();
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

                //Log.i("FragPre", url);

            } catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                KJLoger.errorLog(e.getMessage());
            }
            JSONUtil<FMPreTaskData> jsonUtil = new JSONUtil<FMPreTaskData>();
            //
            String json = HttpUtil.getInstance().doGet(url);
            try
            {
                preTaskDatas = jsonUtil.toBean(json, preTaskDatas);
            } catch (JsonSyntaxException e)
            {
                LogUtil.e("JSON_ERROR", e.getMessage());
                preTaskDatas.setResultCode(0);
                preTaskDatas.setResultDescription("解析json出错");
            }
            return preTaskDatas;
        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(final FMPreTaskData result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            AlphaAnimation anima = new AlphaAnimation(0.0f, 5.0f);
            anima.setDuration(1000);// 设置动画显示时间
            listNotice.setAnimation(anima);

            if (Constant.IS_PRODUCTION_ENVIRONMENT)
            {
                if(Constant.REFRESH == loadType)
                {
                    if (1 == result.getResultCode())
                    {
                        datas.clear();
                        // 刷新列表
                        datas = filter(result.getResultData().getTask());
                        adapter.notifyDataSetChanged();
                        anima.setAnimationListener(new AnimationListener()
                        {

                            @Override
                            public void onAnimationStart(Animation animation)
                            {
                                // TODO Auto-generated method stub
                                listNotice.setVisibility(View.VISIBLE);
                                if(result.getResultData().getTask().isEmpty())
                                {
                                    listNotice.setText("系统暂无任务");
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
                    else if(5000 == result.getResultCode())
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
                                listNotice.setText("没有可刷新的数据");
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
                    else if (Constant.TOKEN_OVERDUE == result.getResultCode())
                    {
                        // 提示账号异地登陆，强制用户退出
                        // 并跳转到登录界面
                        ToastUtils.showLongToast(getActivity(), "账户登录过期，请重新登录");
                        Handler mHandler = new Handler();
                        mHandler.postDelayed(new Runnable()
                        {

                            @Override
                            public void run()
                            {
                                // TODO Auto-generated method stub
                                ActivityUtils.getInstance().loginOutInFragment(
                                        (Activity) getActivity());
                            }
                        }, 2000);
                    }
                }
                else if 
                (Constant.LOAD_MORE == loadType)
                {
                    if (1 == result.getResultCode())
                    {
                        if(!result.getResultData().getTask().isEmpty())
                        {
                            //datas.clear();
                            size = result.getResultData().getTask().size();
                            // 刷新列表
                            //datas = filter(result.getResultData().getTask());
                            datas.addAll( filter(result.getResultData().getTask()) );
                            
                            adapter.notifyDataSetChanged();
                        }
                        anima.setAnimationListener(new AnimationListener()
                        {

                            @Override
                            public void onAnimationStart(Animation animation)
                            {
                                // TODO Auto-generated method stub
                                listNotice.setVisibility(View.VISIBLE);
                                if(!result.getResultData().getTask().isEmpty())
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
                    else if (5000 == result.getResultCode())
                    {
                        // 刷新列表
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
                    else if (Constant.TOKEN_OVERDUE == result.getResultCode())
                    {
                        // 提示账号异地登陆，强制用户退出
                        // 并跳转到登录界面
                        ToastUtils.showLongToast(getActivity(), "账户登录过期，请重新登录");
                        Handler mHandler = new Handler();
                        mHandler.postDelayed(new Runnable()
                        {

                            @Override
                            public void run()
                            {
                                // TODO Auto-generated method stub
                                ActivityUtils.getInstance().loginOutInFragment(
                                        (Activity) getActivity());
                            }
                        }, 2000);
                    }
                }
            } else
            {
            }
        }

    }

    public class RefreshReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            Bundle bundle = intent.getExtras();
            if ( MyBroadcastReceiver.REFRESH_TASK_STATUS.equals(intent.getAction()))
            {
                // 刷新列表项
                // 预告任务下标
                int index =0;
                if( bundle.containsKey("index"))
                {
                    index = bundle.getInt("index");           
                    if( index>=0 ){
                        datas.get(index).setOnline(true);
                    }
                }                
                
                new PreTaskAsyncTask(Constant.REFRESH).execute(Constant.PREVIEW_TASK_LIST);
                
                adapter.notifyDataSetChanged();
            }
        }
    }
    
    public class InnerClass
    {
        private List<Integer> taskIds;

        public List<Integer> getTaskIds()
        {
            return taskIds;
        }

        public void setTaskIds(List<Integer> taskIds)
        {
            this.taskIds = taskIds;
        }
    }
    
    /**
     * 
     *@方法描述：过滤数据，判断：1、是否设置过闹钟  2、是否上线
     *@方法名：filter
     *@参数：@param datas
     *@参数：@return
     *@返回：List<TaskData>
     *@exception 
     *@since
     */
    private List<TaskData> filter(List<TaskData> datas)
    {
        List<TaskData> taskList = new ArrayList<TaskData>();
        for(TaskData task:datas)
        {
            //判断是否设置过闹钟
            if(application.taskList.contains(task.getTaskId()))
            {
                //设置过闹钟
                task.setAlarm(false);
            }
            else
            {
                task.setAlarm(true);
            }
            //判断是上线
            if(System.currentTimeMillis() < task.getPublishDate())
            {
                //未上线
                task.setOnline(false);
            }
            else
            {
                task.setOnline(true);
            }
            taskList.add(task);
        }
        
        return taskList;
    }

}
