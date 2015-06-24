package cy.com.morefan.task;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import android.content.Context;
import android.os.AsyncTask;
import cy.com.morefan.adapter.DsisciplesAdapter;
import cy.com.morefan.bean.App;
import cy.com.morefan.bean.FMApp;
import cy.com.morefan.bean.Paging;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.Constant;
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
    private int loadType;// 0：下拉，1：上拉
    
    public GetDiscipleAsyncTask(int loadType, Context context, DsisciplesAdapter adapter, List<App> datas)
    {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.adapter = adapter;
        this.datas = datas;
        this.loadType = loadType;
    }
    
    @Override
    protected FMApp doInBackground(Integer... params)
    {
        // TODO Auto-generated method stub
        orderBy = params[0];
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
                App app = datas.get(datas.size() - 1);
                if(0 == orderBy)
                {
                    //按时间
                    paging.setPagingTag(String.valueOf(app.getDate()));
                }
                else if(1 == orderBy)
                {
                    //按贡献
                    paging.setPagingTag(String.valueOf(app.getM()));
                }
                
            }
            else if(datas != null && datas.size() == 0)
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
            url = url
                    + "?orderBy="
                    + URLEncoder.encode(String.valueOf(orderBy),
                            "UTF-8");
            url += paramMap;
            url += "&sign=" + URLEncoder.encode(signStr, "UTF-8");
            url += "&pagingTag="
                    + URLEncoder.encode(paging.getPagingTag(), "UTF-8");
            url += "&pagingSize="
                    + URLEncoder.encode(paging.getPagingSize().toString(),
                            "UTF-8");
            // 根据返回的json封装新的user数据集
            //切换到测试环境
            if(Constant.IS_PRODUCTION_ENVIRONMENT)
            {
                String jsonStr = HttpUtil.getInstance().doGet(url);
                try
                {
                    appBean = jsonUtil.toBean(jsonStr, appBean);
                }
                catch(JsonSyntaxException e)
                {
                   LogUtil.e("JSON_ERROR", e.getMessage());
                   appBean.setResultCode(0);
                   appBean.setResultDescription("解析json出错");
                }
            }
            else
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
        
    }

    @Override
    protected void onPostExecute(FMApp result)
    {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        if (1 == result.getResultCode())
        {
            List<App> apps = result.getResultData().getApps();
            if (Constant.REFRESH == loadType)
            {
                datas.clear();
                if (apps.size() > 0)
                {
                    datas.addAll(apps);
                    adapter.notifyDataSetChanged();
                } else
                {
                    ToastUtils.showLongToast(context, "没有需要更新的数据");
                }
            } else if (Constant.LOAD_MORE == loadType)
            {

                if (apps.size() > 0)
                {
                    datas.addAll(apps);
                    adapter.notifyDataSetChanged();
                } else
                {
                    ToastUtils.showLongToast(context, "没有需要更新的数据");
                }
            }
        } else
        {
            ToastUtils.showLongToast(context, "徒弟列表加载失败");
        }
    }
    
    

}
