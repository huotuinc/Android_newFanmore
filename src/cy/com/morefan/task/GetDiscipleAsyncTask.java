package cy.com.morefan.task;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.adapter.DsisciplesAdapter;
import cy.com.morefan.bean.App;
import cy.com.morefan.bean.FMApp;
import cy.com.morefan.bean.Paging;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.KJLoger;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;

public class GetDiscipleAsyncTask extends AsyncTask<Integer, Void, FMApp>
{

    private int orderBy;

    private Context context;

    private DsisciplesAdapter adapter;

    private List<App> datas;
    // 列表刷新提示
    private TextView listNotice;

    private int loadType;// 0：下拉，1：上拉

    public GetDiscipleAsyncTask(int loadType, Context context,
            DsisciplesAdapter adapter, List<App> datas, TextView listNotice)
    {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.adapter = adapter;
        this.datas = datas;
        this.loadType = loadType;
        this.listNotice = listNotice;
    }

    Paging paging = null;
    
    @Override
    protected FMApp doInBackground(Integer... params)
    {
        // TODO Auto-generated method stub
        orderBy = params[0];
         
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
               App app = datas.get(datas.size() - 1);
               paging.setPagingTag(app.getAppOrder());

            } else if (datas != null && datas.size() == 0)
            {
                paging.setPagingTag("");
            }
        }

        FMApp appBean = new FMApp();
        JSONUtil<FMApp> jsonUtil = new JSONUtil<FMApp>();
        ObtainParamsMap obtainMap = new ObtainParamsMap(context);
        String paramMap = obtainMap.getMap();
        // 封装sign
        Map<String, String> signMap = new HashMap<String, String>();
        signMap.put("orderBy", String.valueOf(orderBy));
        signMap.put("pagingTag", paging.getPagingTag());
        signMap.put("pagingSize", paging.getPagingSize().toString());
        String signStr = obtainMap.getSign(signMap);
        String url = Constant.DISCIPLE_LIST;
        try
        {
            url = url + "?orderBy="
                    + URLEncoder.encode(String.valueOf(orderBy), "UTF-8");
            url += paramMap;
            url += "&sign=" + URLEncoder.encode(signStr, "UTF-8");
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
                    appBean = jsonUtil.toBean(jsonStr, appBean);
                } catch (JsonSyntaxException e)
                {
                    LogUtil.e("JSON_ERROR", e.getMessage());
                    appBean.setResultCode(0);
                    appBean.setResultDescription("解析json出错");
                }
            } else
            {
            }

        } catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            KJLoger.errorLog(e.getMessage());
        }
        return appBean;
    }

    @Override
    protected void onPreExecute()
    {
        // TODO Auto-generated method stub
        super.onPreExecute();
        paging = new Paging();
        paging.setPagingSize(Constant.PAGES_COMMON);
        paging.setPagingTag("");
        
    }

    @Override
    protected void onPostExecute(FMApp result)
    {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        AlphaAnimation anima = new AlphaAnimation(0.0f, 5.0f);
        anima.setDuration(1000);// 设置动画显示时间
        listNotice.setAnimation(anima);
        if (1 == result.getResultCode())
        {
            final List<App> apps = result.getResultData().getApps();
            if (Constant.REFRESH == loadType)
            {
                datas.clear();
                datas.addAll(apps);
                adapter.notifyDataSetChanged();
                anima.setAnimationListener(new AnimationListener()
                {

                    @Override
                    public void onAnimationStart(Animation animation)
                    {
                        // TODO Auto-generated method stub
                        listNotice.setVisibility(View.VISIBLE);
                        if(apps.isEmpty())
                        {
                            listNotice.setText("你没有徒弟");
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
            } else if (Constant.LOAD_MORE == loadType)
            {

                if (apps.size() > 0)
                {
                    datas.clear();
                    datas.addAll(apps);
                    adapter.notifyDataSetChanged();
                }
                anima.setAnimationListener(new AnimationListener()
                {

                    @Override
                    public void onAnimationStart(Animation animation)
                    {
                        // TODO Auto-generated method stub
                        listNotice.setVisibility(View.VISIBLE);
                        if(!apps.isEmpty())
                        {
                            listNotice.setText("加载了" + apps.size() + "条数据");
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
            List<App> apps = result.getResultData().getApps();
            if (Constant.REFRESH == loadType)
            {
                datas.clear();
                adapter.notifyDataSetChanged();
            }
            anima.setAnimationListener(new AnimationListener()
            {

                @Override
                public void onAnimationStart(Animation animation)
                {
                    // TODO Auto-generated method stub
                    listNotice.setVisibility(View.VISIBLE);
                    if(Constant.REFRESH == loadType)
                    {
                        listNotice.setText("你没有徒弟");
                    }
                    else if (Constant.LOAD_MORE == loadType)
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
        else if (Constant.TOKEN_OVERDUE == result.getResultCode())
        {
            // 提示账号异地登陆，强制用户退出
            // 并跳转到登录界面
            ToastUtils.showLongToast(context, "该账户在其他地方登录，请重新登录");
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable()
            {

                @Override
                public void run()
                {
                    // TODO Auto-generated method stub
                    ActivityUtils.getInstance().loginOutInActivity(
                            (Activity) context);
                }
            }, 2000);
        }
    }

}
