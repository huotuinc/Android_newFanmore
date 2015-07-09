package cy.com.morefan.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cy.com.morefan.R;
import cy.com.morefan.bean.AnnounceBean;
import cy.com.morefan.bean.IBaseData;
import cy.com.morefan.bean.MarkData;
import cy.com.morefan.bean.MasterData;
import cy.com.morefan.bean.MoreSettingData;
import cy.com.morefan.bean.MsgData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.UnitData;
import cy.com.morefan.util.ViewHolderUtil;

/**
 * 1.首页列表数据adapter
 * 
 * @author cy
 *
 */
public class DataListAdapter extends BaseAdapter
{

    private List<IBaseData> datas;

    private Context mContext;

    public DataListAdapter(Context context, List<IBaseData> datas)
    {
        this.mContext = context;
        this.datas = datas;
    }

    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return datas.size();
    }

    @Override
    public IBaseData getItem(int position)
    {
        // TODO Auto-generated method stub
        return datas.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        convertView = getConvertView(position, convertView, getItem(position));
        return convertView;
    }

    public View getConvertView(int positon, View convertView, IBaseData item)
    {
        if (item instanceof UnitData)
        {
            //UnitData data = (UnitData) item;
            if (null == convertView)
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_unit, null);
        } else if (item instanceof TaskData)
        {
            TaskData data = (TaskData) item;
            if (null == convertView)
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_unit, null);
        } else if (item instanceof MarkData)
        {
            MarkData data = (MarkData) item;
            if (null == convertView)
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_mark, null);
            TextView txt = ViewHolderUtil.get(convertView, R.id.txt);

            txt.setText(data.name);
            if (1 == data.status)
            {
                txt.setTextColor(mContext.getResources().getColor(R.color.white));
                txt.setBackgroundResource(R.drawable.mark_blue_bg);
            } else if (0 == data.status)
            {
                txt.setTextColor(mContext.getResources().getColor(R.color.gray_94));
                txt.setBackgroundResource(R.drawable.mark_gray_bg);
            } else if (2 == data.status)
            {
                txt.setTextColor(mContext.getResources().getColor(R.color.theme_color));
                txt.setBackgroundResource(R.drawable.mark_yellow_bg);
            }

        } else if (item instanceof AnnounceBean)
        {
            AnnounceBean announce = (AnnounceBean) item;
            if (null == convertView)
            {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_announce, null);
            }

        } else if (item instanceof MasterData)
        {
            MasterData masterData = (MasterData) item;
            if (null == convertView)
            {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.disciple_item, null);
            }
        } else if (item instanceof MoreSettingData)
        {
            MoreSettingData moreSetting = (MoreSettingData) item;
            MoreSettingHolder holder = null;
            if (null == convertView)
            {
                /*
                 * holder = new MoreSettingHolder(); convertView =
                 * LayoutInflater
                 * .from(mContext).inflate(R.layout.more_setting_item, null);
                 * 
                 * holder.settingName = (TextView)
                 * convertView.findViewById(R.id.settingName); holder.data =
                 * (TextView) convertView.findViewById(R.id.settingData);
                 * holder.settingImage = (ImageView)
                 * convertView.findViewById(R.id.settingImage);
                 * 
                 * 
                 * holder.settingName.setLineSpacing(2.0f, 2.0f);
                 * holder.settingName.setText(moreSetting.getSettingName());
                 * if("ICON".equals(moreSetting.getData())) {
                 * holder.data.setVisibility(View.GONE);
                 * holder.settingImage.setImageResource
                 * (R.drawable.arrows_right); } else {
                 * holder.settingImage.setVisibility(View.GONE);
                 * holder.data.setText(moreSetting.getData()); }
                 */

            }
        } else if (item instanceof MsgData)
        {
            MsgData msg = (MsgData) item;
            MsgHolder holder = null;
            if (null == convertView)
            {
                /*
                 * holder = new MsgHolder(); convertView =
                 * LayoutInflater.from(mContext
                 * ).inflate(R.layout.msg_center_item, null);
                 * 
                 * holder.msgType = (TextView)
                 * convertView.findViewById(R.id.msgType); holder.msgTitle =
                 * (TextView) convertView.findViewById(R.id.msgTitle);
                 * holder.msgTime = (TextView)
                 * convertView.findViewById(R.id.msgTime); holder.skipIamge =
                 * (ImageView) convertView.findViewById(R.id.skipImage);
                 * 
                 * holder.msgType.setText("【" + msg.getMsgType() + "】");
                 * holder.msgTitle.setText(msg.getMsgTitle());
                 * holder.msgTime.setText(msg.getMsgTime());
                 */
                holder.skipIamge.setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        // TODO Auto-generated method stub

                    }
                });

            }
        }
        return convertView;

    }

    static class MoreSettingHolder
    {
        TextView settingName;

        TextView data;

        ImageView settingImage;
    }

    static class MsgHolder
    {
        TextView msgType;

        TextView msgTitle;

        TextView msgTime;

        ImageView skipIamge;
    }

}
