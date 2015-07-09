package cy.com.morefan.frag;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cy.com.morefan.R;
import cy.com.morefan.adapter.DsisciplesAdapter;
import cy.com.morefan.bean.App;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.task.GetDiscipleAsyncTask;
import cy.com.morefan.util.DateUtils;
import cy.com.morefan.view.KJListView;
import cy.com.morefan.view.KJRefreshListener;

public class FraDisciplesTime extends Fragment
{
    
    private KJListView list;
    private List<App> datas;
    private DsisciplesAdapter adapter;
    // 列表刷新提示
    private TextView listNotice;
    
    public FraDisciplesTime(TextView listNotice)
    {
        // TODO Auto-generated constructor stub
        this.listNotice = listNotice;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        datas = new ArrayList<App>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        View parent = inflater.inflate(R.layout.dsisciples_time_ui, container, false);
        list = (KJListView) parent.findViewById(R.id.dsisciplesTime);
        list.setPullLoadEnable(true);
        adapter = new DsisciplesAdapter(getActivity(), datas);
        list.setAdapter(adapter);
        initList();
        list.setRefreshTime(DateUtils.formatDate(System.currentTimeMillis()), getActivity());
        new GetDiscipleAsyncTask(Constant.REFRESH, getActivity(), adapter, datas, listNotice).execute(0);
        return parent;
    }
    
    private void initList()
    {
        list.setOnRefreshListener(new KJRefreshListener()
        {

            @Override
            public void onRefresh()
            {
                // TODO Auto-generated method stub
                // 加载数据
                list.setRefreshTime(DateUtils.formatDate(System.currentTimeMillis()), getActivity());
                new GetDiscipleAsyncTask(Constant.REFRESH, getActivity(), adapter, datas, listNotice).execute(0);
                list.stopRefreshData();
            }

            @Override
            public void onLoadMore()
            {
                // TODO Auto-generated method stub
                list.setRefreshTime(DateUtils.formatDate(System.currentTimeMillis()), getActivity());
                new GetDiscipleAsyncTask(Constant.LOAD_MORE, getActivity(), adapter, datas, listNotice).execute(0);
                list.stopRefreshData();
            }
        });
    }
    
}
