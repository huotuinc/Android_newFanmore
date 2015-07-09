package cy.com.morefan.ui.flow;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.ContactBean;
import cy.com.morefan.bean.FMContact;
import cy.com.morefan.util.BitmapLoader;
import cy.com.morefan.util.DateUtils;
import cy.com.morefan.util.SystemTools;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.view.KJListView;
import cy.com.morefan.view.KJRefreshListener;

/**
 * 
 * @类名称：FriendsResActivity
 * @类描述：好友求流量界面
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月13日 上午11:31:09
 * @修改备注：
 * @version:
 */
public class FriendsResActivity extends BaseActivity implements
        OnClickListener, Callback, OnItemClickListener
{
    public Handler mHandler = new Handler(this);

    private CyButton backImage;

    private TextView title;

    private TextView functionBtn;

    private KJListView friendsList;

    public MyApplication application;

    private List<ContactBean> contacts;

    private FriendtAdapter adapter;
    //返回文字事件
    private TextView backText;

    @Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        application = (MyApplication) FriendsResActivity.this.getApplication();
        contacts = new ArrayList<ContactBean>();
        this.setContentView(R.layout.friends_res_ui);
        this.initView();
        friendsList.setPullLoadEnable(false);
        adapter = new FriendtAdapter();
        friendsList.setAdapter(adapter);
        initList();
        friendsList.setOnItemClickListener(this);
        friendsList.setRefreshTime(DateUtils.formatDate(System.currentTimeMillis()), FriendsResActivity.this);
        new LoadFriendsAsyncTask().execute();
        
    }

    private void initList()
    {
        friendsList.setOnRefreshListener(new KJRefreshListener()
        {

            @Override
            public void onRefresh()
            {
                // TODO Auto-generated method stub
                friendsList.setRefreshTime(DateUtils.formatDate(System.currentTimeMillis()), FriendsResActivity.this);
                friendsList.stopRefreshData();
            }

            @Override
            public void onLoadMore()
            {
                // TODO Auto-generated method stub
                friendsList.setRefreshTime(DateUtils.formatDate(System.currentTimeMillis()), FriendsResActivity.this);
                friendsList.stopRefreshData();
            }
        });
    }

    private void initView()
    {
        backImage = (CyButton) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(this);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("好友请求列表");
        functionBtn = (TextView) this.findViewById(R.id.functionBtn);
        functionBtn.setVisibility(View.VISIBLE);
        functionBtn.setText("清空列表");
        functionBtn.setOnClickListener(this);
        friendsList = (KJListView) this.findViewById(R.id.friendsList);
        backText = (TextView) this.findViewById(R.id.backtext);
        backText.setOnClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            // finish自身
            FriendsResActivity.this.finish();
            return true;
        }
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean handleMessage(Message msg)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        switch (v.getId())
        {
        case R.id.backImage:
        {
            closeSelf(FriendsResActivity.this);
        }
            break;
        case R.id.functionBtn:
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(FriendsResActivity.this);
            dialog.setTitle("好友求流量");
            dialog.setMessage("确认清空列表信息？");
            dialog.setPositiveButton("确认", new DialogInterface.OnClickListener()
            {
                
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // TODO Auto-generated method stub
                    // 先删除服务端数据，服务端删除成功后删除本地数据
                    //模拟删除
                    //new ClearInfoAsyncTask().execute();
                 // 清空列表信息
                    mHandler.post(new Runnable()
                    {
                        
                        @Override
                        public void run()
                        {
                            // TODO Auto-generated method stub
                            contacts.clear();
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener()
            {
                
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // TODO Auto-generated method stub
                    
                }
            });
            
            dialog.show();
        }
            break;
        case R.id.backtext:
        {
            closeSelf(FriendsResActivity.this);
        }
            break;
        default:
            break;
        }
    }

    /**
     * 
     * @类名称：FriendtAdapter
     * @类描述：好友求流量适配
     * @创建人：aaron
     * @修改人：
     * @修改时间：2015年6月13日 上午11:52:33
     * @修改备注：
     * @version:
     */
    public class FriendtAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            // TODO Auto-generated method stub
            return contacts.size();
        }

        @Override
        public Object getItem(int position)
        {
            // TODO Auto-generated method stub
            return contacts.get(position);
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
            Resources res = FriendsResActivity.this.getResources();
            if (convertView == null)
            {
                holder = new ViewHolder();
                convertView = View.inflate(FriendsResActivity.this,
                        R.layout.contact_item_ui, null);
                holder.img = (NetworkImageView) convertView
                        .findViewById(R.id.img);
                holder.phoneNumber = (TextView) convertView
                        .findViewById(R.id.phoneNumber);
                holder.contactName = (TextView) convertView
                        .findViewById(R.id.contactName);
                holder.account = (TextView) convertView
                        .findViewById(R.id.account);
                holder.flows = (TextView) convertView.findViewById(R.id.flows);
                holder.operator = (TextView) convertView
                        .findViewById(R.id.operator);
                holder.label = (TextView) convertView
                        .findViewById(R.id.label);
                convertView.setTag(holder);
            } else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            // 加载图片
            if (contacts.size() > 0)
            {
                BitmapLoader.create().displayUrl(FriendsResActivity.this,
                        holder.img, contacts.get(position).getFanmorePicUrl());
                holder.phoneNumber.setText(contacts.get(position)
                        .getOriginMobile());
                String userName = contacts.get(position).getFanmoreUsername();
                if (null != userName)
                {
                    holder.contactName.setText(userName);
                    holder.account.setText("");
                } else
                {
                    holder.account.setText("");
                }

                holder.flows.setText(""+ contacts.get(position).getFanmoreBalance() + "M");
                holder.flows.setTextSize(res
                        .getDimension(R.dimen.friends_accpet_text));
                holder.flows.setTextColor(res.getColor(R.color.accpet_flow_color));
                holder.operator
                        .setText(contacts.get(position).getFanmoreTele());
                holder.label.setVisibility(View.VISIBLE);
                holder.label.setText("向你求");
                int sex = contacts.get(position).getFanmoreSex();
                // 男
                if (0 == sex)
                {
                    SystemTools.loadBackground(holder.account, res.getDrawable(R.drawable.friends_sex_male));
                } else if (1 == sex)
                {
                    SystemTools.loadBackground(holder.account, res.getDrawable(R.drawable.friends_sex_female));
                }
            }
            return convertView;
        }

        class ViewHolder
        {
            NetworkImageView img;// 联系人图片

            TextView phoneNumber;// 联系人手机

            TextView contactName;// 联系人

            TextView account;// 是否是粉猫用户

            TextView flows;// 求流量
            TextView label;//标识
            TextView operator;// 运营商
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        // TODO Auto-generated method stub
        ContactBean contact = contacts.get(position-1);
        AlertDialog.Builder dialog = new AlertDialog.Builder(FriendsResActivity.this);
        dialog.setTitle("好友求流量");
        if(0 == contact.getFanmoreSex())
        {
            dialog.setMessage("送他" + contact.getFanmoreBalance() + "M流量。");
        }
        if(1 == contact.getFanmoreSex())
        {
            dialog.setMessage("送她" + contact.getFanmoreBalance() + "M流量。");
        }
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // TODO Auto-generated method stub
                // 送流量接口
                new SendFlowAsyncTask().execute();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // TODO Auto-generated method stub
                
            }
        });
        
        dialog.show();
    }

    public class LoadFriendsAsyncTask extends AsyncTask<Void, Void, FMContact>
    {

        @Override
        protected FMContact doInBackground(Void... params)
        {
            // TODO Auto-generated method stub
            
            //初始化数据
            ContactBean contact = new ContactBean();
            contact.setFanmoreUsername("Aaron");
            contact.setFanmorePicUrl("http://img4.imgtn.bdimg.com/it/u=3309848897,132969131&fm=21&gp=0.jpg");
            contact.setFanmoreBalance(100);
            contact.setOriginMobile("13588741728");
            contact.setFanmoreTele("移动");
            contact.setFanmoreSex(1);
            contact.setOriginIdentify("123");
            contact.setTeleBalance(700);
            contacts.add(contact);
            return null;
        }

    }
    
    
    public class SendFlowAsyncTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params)
        {
            // TODO Auto-generated method stub
            return null;
        }
        
    }

}
