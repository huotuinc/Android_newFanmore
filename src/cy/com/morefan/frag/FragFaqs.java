package cy.com.morefan.frag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.ContactBean;
import cy.com.morefan.bean.FMContact;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.ui.flow.SendFlowActivity;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.BitmapLoader;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.SystemTools;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.view.QuickAlphabeticBar;
import cy.lib.edittext.CyEditText;

/**
 * 
 * @类名称：FragFaqs
 * @类描述：送流量界面
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月12日 上午9:14:40
 * @修改备注：
 * @version:
 */
public class FragFaqs extends BaseFragment implements Callback,
        OnItemClickListener
{

    private Handler mHandler = new Handler(this);

    // 搜索框
    private CyEditText etSearch;

    // 内容列表
    private ListView contactList;

    private QuickAlphabeticBar alpha;

    public MyApplication application;

    private List<ContactBean> contacts;

    private ContactAdapter adapter;

    // 搜索
    private List<ContactBean> copyContacts;

    private HashMap<String, Integer> alphaIndexer;// 保存每个索引在list中的位置【#-0，A-4，B-10】

    private String[] sections;// 每个分组的索引表【A,B,C,F...】

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setRetainInstance(true);
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        application = (MyApplication) getActivity().getApplication();
        contacts = new ArrayList<ContactBean>();
    }

    private void initView(View rootView)
    {
        etSearch = (CyEditText) rootView.findViewById(R.id.etSearch);
        contactList = (ListView) rootView.findViewById(R.id.contactList);
        alpha = (QuickAlphabeticBar) rootView.findViewById(R.id.fast_scroller);

        etSearch.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count)
            {
                // TODO Auto-generated method stub
                // 根据输入过滤列表

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after)
            {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                // TODO Auto-generated method stub
                String filterText = etSearch.getText().toString();
                doFilter(filterText);
            }
        });

    }

    private void doFilter(final String filter)
    {
        /*
         * List<ContactBean> deleteContacts = new ArrayList<ContactBean>(); for
         * (ContactBean contact : contacts) { if
         * (!contact.getMobileNumber().contains(filter)) {
         * deleteContacts.add(contact); } } contacts.removeAll(deleteContacts);
         * adapter.notifyDataSetChanged();
         */
        mHandler.post(new Runnable()
        {

            @Override
            public void run()
            {
                // TODO Auto-generated method stub
                contacts.clear();
                List<ContactBean> points = new ArrayList<ContactBean>();
                for (ContactBean contact : copyContacts)
                {
                    if (contact.getOriginMobile().contains(filter))
                    {
                        points.add(contact);
                    }
                }
                contacts.addAll(points);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_faqs, container, false);
        initView(rootView);
        // new LoadDataAsyncTask().execute();
        adapter = new ContactAdapter();
        contactList.setAdapter(adapter);
        contactList.setOnItemClickListener(this);
        return rootView;
    }

    class LoadDataAsyncTask extends AsyncTask<Void, Void, FMContact>
    {
        private List<InnerContact> contactss;

        @Override
        protected FMContact doInBackground(Void... params)
        {
            // TODO Auto-generated method stub
            
            FMContact tes=new FMContact();
            ContactBean b1=new ContactBean();
            b1.setFanmoreBalance(100);
            b1.setFanmorePicUrl("");
            b1.setFanmoreSex(1);
            b1.setFanmoreTele("13757193476");
            b1.setFanmoreUsername("jxd");
            b1.setOriginIdentify("1111");
            //b1.setOriginMobile("13757193476");
            b1.setSortKey("j");
            b1.setTeleBalance(22);
            List<ContactBean> ls=new ArrayList<>();
            ls.add(b1);
            
            b1=new ContactBean();
            b1.setFanmoreBalance(33);
            b1.setFanmorePicUrl("");
            b1.setFanmoreSex(0);
            b1.setFanmoreTele("18857155121");
            b1.setFanmoreUsername("wlf");
            b1.setOriginIdentify("2222");
          //  b1.setOriginMobile("18857155121");
            b1.setSortKey("x");
            b1.setTeleBalance(44);
            ls.add(b1);
            
            tes.setResultData(ls);
            tes.setResultCode(1);
            return tes;
            
            
            // 请求接口
//            FMContact contactBean = new FMContact();
//            JSONUtil<FMContact> jsonUtil = new JSONUtil<FMContact>();
//            String url;
//            ObtainParamsMap obtainMap = new ObtainParamsMap(getActivity());
//            Map<String, String> paramMap = obtainMap.obtainMap();
//
//            url = Constant.SENT_FLOW;
//            // 预留参数位
//            paramMap.put("params", "");
//            // 封装sign
//            String signStr = obtainMap.getSign(paramMap);
//            paramMap.put("sign", signStr);
//
//            String jsonStr = HttpUtil.getInstance().doPost(url, paramMap);
//            try
//            {
//                contactBean = jsonUtil.toBean(jsonStr, contactBean);
//            } catch (JsonSyntaxException e)
//            {
//                LogUtil.e("JSON_ERROR", e.getMessage());
//                contactBean.setResultCode(0);
//                contactBean.setResultDescription("解析json出错");
//            }
//
//            return contactBean;
        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
            Cursor cursor = null;
            // 获取本地通讯录
            // 采用provider获取本地联系方式
            contactss = new ArrayList<InnerContact>();
            try
            {
                cursor = getActivity().getContentResolver()
                        .query(ContactsContract.Contacts.CONTENT_URI,
                                new String[]
                                { "_id", "display_name", "has_phone_number",
                                        "sort_key" }, null, null,
                                "sort_key COLLATE LOCALIZED ASC");
                if (cursor != null)
                {
                    if (cursor.moveToFirst())
                    {
                        InnerContact contact;
                        do
                        {
                            contact = new InnerContact();
                            // 获得联系人的ID号
                            String contactId = cursor.getString(cursor
                                    .getColumnIndex("_id"));

                            // 获得联系人姓名
                            String name = cursor.getString(cursor
                                    .getColumnIndex("display_name"));
                            // 查看该联系人有多少个电话号码。如果没有这返回值为0
                            int phoneCount = cursor.getInt(cursor
                                    .getColumnIndex("has_phone_number"));
                            String number = null;
                            String sortKey = cursor.getString(cursor
                                    .getColumnIndex("sort_key"));
                            if (phoneCount > 0)
                            {
                                // 获得联系人的电话号码
                                Cursor phones = getActivity()
                                        .getContentResolver()
                                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                                null,
                                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                        + " = " + contactId,
                                                null, null);
                                if (phones.moveToFirst())
                                {
                                    number = phones
                                            .getString(phones
                                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                }
                                phones.close();
                                contact.setContactId(contactId);
                                contact.setName(name);
                                contact.setPhone(number);
                                contact.setSortKey(sortKey);
                            }
                            contactss.add(contact);
                        } while (cursor.moveToNext());
                    }
                }
            } finally
            {
                cursor.close();
            }

        }

        @Override
        protected void onPostExecute(FMContact result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (1 == result.getResultCode())
            {
                // 添加数据集备份
                copyContacts = result.getResultData();
                contacts.addAll(result.getResultData());
                alphaIndexer = new HashMap<String, Integer>();
                sections = new String[contacts.size()];

                for (int i = 0; i < contacts.size(); i++)
                {
                    String name = getAlpha(contacts.get(i).getSortKey());
                    if (!alphaIndexer.containsKey(name))
                    {// 只记录在list中首次出现的位置
                        alphaIndexer.put(name, i);
                    }
                }
                Set<String> sectionLetters = alphaIndexer.keySet();
                ArrayList<String> sectionList = new ArrayList<String>(
                        sectionLetters);
                Collections.sort(sectionList);
                sections = new String[sectionList.size()];
                sectionList.toArray(sections);

                alpha.setAlphaIndexer(alphaIndexer);
                alpha.init(getActivity());
                alpha.setListView(contactList);
                alpha.setHight(alpha.getHeight());
                alpha.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            } else if (Constant.TOKEN_OVERDUE == result.getResultCode())
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
                        ActivityUtils.getInstance().loginOutInActivity(
                                (Activity) getActivity());
                    }
                }, 2000);
            } else
            {
                ToastUtils.showLongToast(getActivity(), "加载通讯录失败！");
            }

        }

    }

    @Override
    public void onReshow()
    {
        // TODO Auto-generated method stub
        new LoadDataAsyncTask().execute();
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
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        // TODO Auto-generated method stub

        // 跳转到赠送界面
        ContactBean contact = contacts.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("fanmoreTele", contact.getFanmoreTele());
        bundle.putString("originMobile", contact.getOriginMobile());
        bundle.putString("fanmoreUsername", contact.getFanmoreUsername());
        bundle.putString("fanmorePicUrl", contact.getFanmorePicUrl());
        bundle.putInt("fanmoreBalance", contact.getFanmoreBalance());
        ActivityUtils.getInstance().showActivity(getActivity(),
                SendFlowActivity.class, bundle);

    }

    /**
     * 
     * @类名称：ContactAdapter
     * @类描述：
     * @创建人：aaron
     * @修改人：
     * @修改时间：2015年6月12日 上午11:28:08
     * @修改备注：
     * @version:
     */
    public class ContactAdapter extends BaseAdapter
    {

        public ContactAdapter()
        {
            // TODO Auto-generated constructor stub

        }

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
            Resources res = getActivity().getResources();
            if (convertView == null)
            {
                holder = new ViewHolder();
                convertView = View.inflate(getActivity(),
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
                holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
                convertView.setTag(holder);
            } else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            // 加载图片
            if (contacts.size() > 0)
            {
                BitmapLoader.create().displayUrl(getActivity(), holder.img,
                        contacts.get(position).getFanmorePicUrl());
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

                holder.flows.setText(contacts.get(position).getFanmoreBalance()
                        + "M");
                holder.operator
                        .setText(contacts.get(position).getFanmoreTele());
                int sex = contacts.get(position).getFanmoreSex();
                // 男
                if (0 == sex)
                {
                    SystemTools.loadBackground(holder.account,
                            res.getDrawable(R.drawable.friends_sex_male));
                } else if (1 == sex)
                {
                    SystemTools.loadBackground(holder.account,
                            res.getDrawable(R.drawable.friends_sex_female));
                }

                // 当前联系人的sortKey
                String currentStr = getAlpha(contacts.get(position)
                        .getSortKey());
                // 上一个联系人的sortKey
                String previewStr = (position - 1) >= 0 ? getAlpha(contacts
                        .get(position - 1).getSortKey()) : " ";
                /**
                 * 判断显示#、A-Z的TextView隐藏与可见
                 */
                if (!previewStr.equals(currentStr))
                { // 当前联系人的sortKey！=上一个联系人的sortKey，说明当前联系人是新组。
                    holder.alpha.setVisibility(View.VISIBLE);
                    holder.alpha.setText(currentStr);
                } else
                {
                    holder.alpha.setVisibility(View.GONE);
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

            TextView flows;// 流量

            TextView operator;// 运营商

            TextView alpha;
        }

    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     * 
     * @param str
     * @return
     */
    private String getAlpha(String str)
    {
        if (str == null)
        {
            return "#";
        }

        if (str.trim().length() == 0)
        {
            return "#";
        }

        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches())
        {
            return (c + "").toUpperCase(); // 大写输出
        } else
        {
            return "#";
        }
    }

    public class InnerContact
    {
        private String name;

        private String phone;

        private String contactId;

        private String sortKey;

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getPhone()
        {
            return phone;
        }

        public void setPhone(String phone)
        {
            this.phone = phone;
        }

        public String getContactId()
        {
            return contactId;
        }

        public String getSortKey()
        {
            return sortKey;
        }

        public void setSortKey(String sortKey)
        {
            this.sortKey = sortKey;
        }

        public void setContactId(String contactId)
        {
            this.contactId = contactId;
        }

    }

}
