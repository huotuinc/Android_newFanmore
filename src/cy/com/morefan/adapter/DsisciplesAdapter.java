package cy.com.morefan.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import cy.com.morefan.R;
import cy.com.morefan.bean.App;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.util.BitmapLoader;
import cy.com.morefan.util.DateUtils;
import cy.com.morefan.util.Util;

public class DsisciplesAdapter extends BaseAdapter
{

    private Context context;

    private List<App> datas;

    public DsisciplesAdapter(Context context, List<App> datas)
    {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.datas = datas;
    }

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
        Resources res = context.getResources();
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.disciple_item, null);
            holder.disciplesLogo = (NetworkImageView) convertView
                    .findViewById(R.id.disciplesLogo);
            holder.showName = (TextView) convertView
                    .findViewById(R.id.showName);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.m = (TextView) convertView.findViewById(R.id.m);
            holder.countOfApp = (TextView) convertView
                    .findViewById(R.id.countOfApp);
            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        // 加载图片
        if (datas.size() > 0)
        {
            BitmapLoader.create().displayUrl(context, holder.disciplesLogo,
                    datas.get(position).getPicUrl());
            holder.showName.setText(datas.get(position).getShowName());
            holder.date.setText(DateUtils.formatDate(datas.get(position).getDate(), Constant.DATE_FORMAT_COMPACT));
            Number n1 = Util.decimalFloat(datas.get(position).getM(), Constant.ACCURACY_1);
            holder.m.setText(n1+"M");
            holder.countOfApp.setText("徒孙" + datas.get(position).getCountOfApp() +"人");
        }
        return convertView;
    }

    class ViewHolder
    {
        NetworkImageView disciplesLogo;

        TextView showName;// 一般是手机号码

        TextView date;

        TextView m;

        TextView countOfApp;
    }

}
