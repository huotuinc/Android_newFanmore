package cy.com.morefan.frag;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import cy.com.morefan.util.DateUtils;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.LoadingUtil;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.SystemTools;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.KJListView;
import cy.com.morefan.view.KJRefreshListener;
import cy.com.morefan.view.MyLetterListView;
import cy.com.morefan.view.QuickAlphabeticBar;
import com.huotu.android.library.libedittext.EditText;

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
    private EditText etSearch;
    // 内容列表
    private KJListView contactList;

    private MyLetterListView alpha;

    public MyApplication application;

    private List<ContactBean> contacts;

    private ContactAdapter adapter;

    // 搜索
    private List<ContactBean> copyContacts;

    private HashMap<String, Integer> alphaIndexer;// 保存每个索引在list中的位置【#-0，A-4，B-10】

    private String[] sections;// 每个分组的索引表【A,B,C,F...】

    private LoadingUtil loadingUtil =null;

    private TextView overlay;

    private OverlayThread overlayThread;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setRetainInstance(true);

        super.onCreate(savedInstanceState);
        application = (MyApplication) getActivity().getApplication();
        contacts = new ArrayList<ContactBean>();
        loadingUtil=new LoadingUtil(getActivity());

        overlayThread = new OverlayThread();
    }

    private void initView(View rootView)
    {
        etSearch = (EditText) rootView.findViewById(R.id.etSearch);
        contactList = (KJListView) rootView.findViewById(R.id.contactList);
        contactList.setPullLoadEnable(false);
        contactList.setOnRefreshListener(new KJRefreshListener() {
            @Override
            public void onRefresh() {
                contactList.setRefreshTime(
                        DateUtils.formatDate(System.currentTimeMillis()), getActivity());
                new LoadDataAsyncTask().execute();
                contactList.stopRefreshData();
            }

            @Override
            public void onLoadMore() {

            }
        });

        //alpha = (QuickAlphabeticBar) rootView.findViewById(R.id.fast_scroller);

        alpha = (MyLetterListView)rootView.findViewById(R.id.fast_scroller);
        alpha.setOnTouchingLetterChangedListener(new LetterListViewListener());

        etSearch.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count)
            {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                String filterText = etSearch.getText().toString();
                doFilter(filterText);
            }
        });
    }

    //初始化汉语拼音首字母弹出提示框
    private void initOverlay() {
        LayoutInflater inflater = LayoutInflater.from(this.getActivity());
        overlay = (TextView) inflater.inflate(R.layout.overlay, null);
        overlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager windowManager = (WindowManager) this.getActivity().getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(overlay, lp);
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
                contacts.clear();
                List<ContactBean> points = new ArrayList<ContactBean>();
                for (ContactBean contact : copyContacts)
                {
                    if (contact.getOriginMobile().contains(filter) || contact.getSortKey().contains(filter.toUpperCase()))
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
        initOverlay();

        new LoadDataAsyncTask().execute();
        adapter = new ContactAdapter();
        contactList.setAdapter(adapter);
        contactList.setOnItemClickListener(this);
        return rootView;
    }

    class LoadDataAsyncTask extends AsyncTask<Void, Void, FMContact>
    {
        private List<InnerContact> contactss;
        ContentResolver cr = getActivity().getContentResolver();

        @Override
        protected FMContact doInBackground(Void... params) {
            FMContact contactBean = null;
            try {
                getContractData2();

                contactBean = new FMContact();
                JSONUtil<FMContact> jsonUtil = new JSONUtil<FMContact>();
                String url;
                ObtainParamsMap obtainMap = new ObtainParamsMap(getActivity());
                Map<String, String> paramMap = obtainMap.obtainMap();

                url = Constant.CONTACTINFO;

                String contractString = getContracts();
                // 参数位
                paramMap.put("contacts", contractString);
                // 封装sign
                String signStr = obtainMap.getSign(paramMap);
                paramMap.put("sign", signStr);

                String jsonStr = HttpUtil.getInstance().doPost(url, paramMap);

                contactBean = jsonUtil.toBean(jsonStr, contactBean);

                if( contactBean !=null && contactBean.getSystemResultCode() ==1
                        && contactBean.getResultCode() ==1 ) {
                    List<ContactBean> data = changeContracts(contactBean.getResultData().getContactInfo());
                    contactBean.getResultData().setContactInfo(data);
                }
            } catch (JsonSyntaxException e) {
                LogUtil.e("JSON_ERROR", e.getMessage());
                contactBean =new FMContact();
                contactBean.setResultCode(0);
                contactBean.setResultDescription("解析json出错");
            }
            catch (Exception ex){
                Log.e("error:",ex.getMessage());
                contactBean =new FMContact();
                contactBean.setResultCode(0);
                contactBean.setResultDescription(ex.getMessage());
            }

            return contactBean;
        }


        protected String getContracts(){
            if(contactss==null || contactss.size()<1 ) return "";
            String contractString ="";
            for( InnerContact item : contactss){
                String str = item.getContactId()+"\r"+item.getPhone();
                if(contractString.length()>0) {
                    contractString += "\t";
                }
                contractString+= str;
            }

            return contractString;
        }

        protected ContactBean findContact( InnerContact model , List<ContactBean> fanContracts){

            for( ContactBean item : fanContracts){
                if( item.getOriginIdentify().equals( model.getContactId() )){
                    ContactBean newInst=new ContactBean();
                    newInst.setFanmoreBalance(item.getFanmoreBalance());
                    newInst.setFanmorePicUrl(item.getFanmorePicUrl());
                    newInst.setFanmoreSex(item.getFanmoreSex());
                    newInst.setFanmoreTele(item.getFanmoreTele());
                    newInst.setFanmoreUsername(item.getFanmoreUsername());
                    newInst.setOriginIdentify(item.getOriginIdentify());
                    newInst.setOriginMobile(item.getOriginMobile());
                    newInst.setSortKey(model.getSortKey());
                    newInst.setTeleBalance(item.getTeleBalance());
                    newInst.setOriginName(model.getName());
                    return newInst;
                }
            }
            return  null;
        }

        protected List<ContactBean> changeContracts( List<ContactBean> fanContracts){
            List<ContactBean> data=new ArrayList<>();
            for( InnerContact item : contactss){
                ContactBean bean = findContact(item , fanContracts);
                if( bean==null){
                    continue;
//                    bean=new ContactBean();
//                    bean.setTeleBalance(null);
//                    bean.setSortKey(item.getSortKey());
//                    bean.setOriginMobile(item.getPhone());
//                    bean.setOriginIdentify(item.getContactId());
//                    bean.setFanmoreBalance(null);
//                    bean.setFanmorePicUrl("");
//                    bean.setFanmoreSex(-1);
//                    bean.setFanmoreTele("");
//                    bean.setFanmoreUsername(null);
//                    bean.setOriginName(item.getName());
                }
                data.add(bean);
            }
            return data;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            loadingUtil.showProgressNotPost();

            //getContractData();

//            Cursor cursor = null;
//            // 获取本地通讯录
//            // 采用provider获取本地联系方式
//            contactss = new ArrayList<InnerContact>();
//            try
//            {
//                cursor = getActivity().getContentResolver()
//                        .query(ContactsContract.Contacts.CONTENT_URI,
//                                new String[]
//                                { "_id", "display_name", "has_phone_number",
//                                        "sort_key" }, null, null,
//                                "sort_key COLLATE LOCALIZED ASC");
//                if (cursor != null)
//                {
//                    if (cursor.moveToFirst())
//                    {
//                        InnerContact contact;
//                        do
//                        {
//                            contact = new InnerContact();
//                            // 获得联系人的ID号
//                            String contactId = cursor.getString(cursor
//                                    .getColumnIndex("_id"));
//
//                            // 获得联系人姓名
//                            String name = cursor.getString(cursor
//                                    .getColumnIndex("display_name"));
//                            // 查看该联系人有多少个电话号码。如果没有这返回值为0
//                            int phoneCount = cursor.getInt(cursor
//                                    .getColumnIndex("has_phone_number"));
//                            String number = null;
//                            String sortKey = cursor.getString(cursor
//                                    .getColumnIndex("sort_key"));
//                            if (phoneCount > 0)
//                            {
//                                // 获得联系人的电话号码
//                                Cursor phones = getActivity()
//                                        .getContentResolver()
//                                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                                                null,
//                                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
//                                                        + " = " + contactId,
//                                                null, null);
//                                if (phones.moveToFirst())
//                                {
//                                    number = phones
//                                            .getString(phones
//                                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                                }
//                                phones.close();
//
//                                if( null== number){
//                                    continue;
//                                }
//
//                                contact.setContactId(contactId);
//                                contact.setName(name);
//                                contact.setPhone(number);
//                                contact.setSortKey(sortKey);
//                            }
//                            contactss.add(contact);
//                        } while (cursor.moveToNext());
//                    }
//                }
//            } finally
//            {
//                cursor.close();
//            }

        }

        protected void getContractData(){
            contactss = new ArrayList<InnerContact>();


            InnerContact contact;
            Cursor cursor = cr
                    .query(ContactsContract.Contacts.CONTENT_URI,
                            new String[]{ContactsContract.Contacts._ID,
                                    ContactsContract.Contacts.DISPLAY_NAME,
                                    ContactsContract.Contacts.HAS_PHONE_NUMBER,
                                    ContactsContract.Contacts.SORT_KEY_PRIMARY}, null, null,
                            "sort_key COLLATE LOCALIZED asc");

            String contactId = "";
            String name = "";
            String sort_key = "";
            String phoneNumber = "";
            String has_phoneNumber = "0";
            int idx_hasnumber=cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
            int idx_displayname=cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int idx_id= cursor.getColumnIndex(ContactsContract.Contacts._ID);
            int idx_sortkey = cursor.getColumnIndex(ContactsContract.Contacts.SORT_KEY_PRIMARY);

            while (cursor.moveToNext()) {
                has_phoneNumber = cursor.getString(idx_hasnumber);
                if (has_phoneNumber.equals("1")) {
                    name = cursor.getString(idx_displayname);
                    if (name != null) {
                        contactId = cursor.getString(idx_id);
                        sort_key = cursor.getString(idx_sortkey);

                        contact = new InnerContact();
                        contact.setName(name);
                        contact.setContactId(contactId);
                        contact.setPhone(phoneNumber);
                        contact.setSortKey(sort_key);
                        contact.setHas_phonenumber(has_phoneNumber);

                        contactss.add(contact);
                    }
                }
            }

            cursor.close();

            Pad_FirstPhone(contactss);
        }

        protected void getContractData2(){
            contactss = new ArrayList<InnerContact>();
            InnerContact contact;
            Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]{ContactsContract.CommonDataKinds.Phone._ID,
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY}, null, null,
                            "sort_key COLLATE LOCALIZED asc");

            String contactId = "";
            String name = "";
            String sort_key = "";
            String phoneNumber = "";
            int idx_number=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int idx_displayname=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int idx_id= cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
            int idx_sortkey = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY);

            while (cursor.moveToNext()) {
                    contactId = cursor.getString(idx_id);
                    name = cursor.getString(idx_displayname);
                if( name==null||name.length()<1){
                    continue;
                }

                    //if (name != null) {
                        sort_key = cursor.getString(idx_sortkey);
                phoneNumber=cursor.getString(idx_number);

                        contact = new InnerContact();
                        contact.setName(name);
                        contact.setContactId(contactId);
                        contact.setPhone(phoneNumber);
                        contact.setSortKey(sort_key);
                        //contact.setHas_phonenumber(has_phoneNumber);

                        contactss.add(contact);
                    //}

            }

            cursor.close();

        }

        private void Pad_FirstPhone(List<InnerContact> data ) {

            Cursor phones = cr
                    .query(ContactsContract.Data.CONTENT_URI,
                            new String[]{ContactsContract.Data.CONTACT_ID,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER},
                            ContactsContract.Contacts.Data.MIMETYPE
                                    + "='"
                                    + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                                    + "'", null, null);

            String contactId = "";
            String phoneNumber = "";
            int idx_contact_id = phones.getColumnIndex(ContactsContract.Data.CONTACT_ID);
            int idx_phonenumber=phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            while (phones.moveToNext()) {
                contactId = phones.getString(idx_contact_id);
                phoneNumber = phones.getString(idx_phonenumber);

                for (InnerContact showData : data ) {

                    if (showData.getPhone().length() == 0) {

                        if (showData.getContactId().equals(contactId)) {

//                            phoneNumber = phones
//                                    .getString(phones
//                                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phoneNumber = phoneNumber == null ? "" : phoneNumber;

                            showData.setPhone(phoneNumber);
                            break;
                        }
                    }
                }
            }

            phones.close();
        }


        @Override
        protected void onPostExecute(FMContact result)
        {
            super.onPostExecute(result);

            loadingUtil.dismissProgressNotPost();

            if( result==null){
                ToastUtils.showLongToast(getActivity(),"请求失败");
                return;
            }
            if( 1 != result.getSystemResultCode()){
                ToastUtils.showLongToast(getActivity(), result.getSystemResultDescription());
                return;
            }

            if (1 == result.getResultCode())
            {
                // 添加数据集备份
                copyContacts = result.getResultData().getContactInfo();
                contacts.clear();

                if( copyContacts !=null) {
                    contacts.addAll(result.getResultData().getContactInfo());
                }

                alphaIndexer = new HashMap<String, Integer>();
                sections = new String[contacts.size()];

                for (int i = 0; i < contacts.size(); i++)
                {
                    String name = getAlpha(contacts.get(i).getSortKey());
                    if (!alphaIndexer.containsKey(name))
                    {// 只记录在list中首次出现的位置
                        alphaIndexer.put(name, i);

                        sections[i] = name;
                    }
                }
//                Set<String> sectionLetters = alphaIndexer.keySet();
//                ArrayList<String> sectionList = new ArrayList<String>(
//                        sectionLetters);
//                Collections.sort(sectionList);
//                sections = new String[sectionList.size()];
//                sectionList.toArray(sections);

//                alpha.setAlphaIndexer(alphaIndexer);
//                alpha.init(getActivity());
//                alpha.setListView(contactList);
//                alpha.setHight(alpha.getHeight());
//                alpha.setVisibility(View.VISIBLE);

                String key= etSearch.getText().toString().trim();
                if(key.length()>0) {
                    doFilter(key);
                }else {
                    adapter.notifyDataSetChanged();
                }
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
        //new LoadDataAsyncTask().execute();

        //String key = etSearch.getText().toString().trim();
        //doFilter(key);
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
        int idx = position - 1;
        if (idx < 0 || idx >= contacts.size())
            return;

        // 跳转到赠送界面
        ContactBean contact = contacts.get(idx);
        Bundle bundle = new Bundle();
        bundle.putString("fanmoreTele", contact.getFanmoreTele());
        bundle.putString("originMobile", contact.getOriginMobile());
        bundle.putString("fanmoreUsername", contact.getFanmoreUsername());
        bundle.putString("fanmorePicUrl", contact.getFanmorePicUrl());
        bundle.putFloat("fanmoreBalance", contact.getFanmoreBalance()==null?0:contact.getFanmoreBalance());
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
        }

        @Override
        public int getCount()
        {
            return contacts.size();
        }

        @Override
        public Object getItem(int position)
        {
            return contacts.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
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

                holder.rlAlphaL=(RelativeLayout)convertView.findViewById(R.id.alphaL);
                holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
                convertView.setTag(holder);
            } else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            // 加载图片
            if (contacts.size() > 0)
            {
                //holder.img.setImageUrl("",null);
                //holder.img.setBackgroundResource(R.drawable.mrtou);
                BitmapLoader.create().displayUrl(getActivity(), holder.img,
                        contacts.get(position).getFanmorePicUrl(), R.drawable.mrtou, R.drawable.mrtou);
                holder.phoneNumber.setText(contacts.get(position)
                        .getOriginMobile());
                String userName = contacts.get(position).getFanmoreUsername();
                if (null != userName)
                {
                    holder.contactName.setText( contacts.get(position).getOriginName());
                    holder.account.setText("");
                } else
                {//非粉猫用户，则显示联系人姓名
                    holder.account.setText("");
                    holder.contactName.setText(contacts.get(position).getOriginName());
                }

                if(null != contacts.get(position).getFanmoreBalance()) {

                    //Number temp = Util.decimalFloat( contacts.get(position).getFanmoreBalance(),"0.###");

                    BigDecimal balance = new BigDecimal(contacts.get(position).getFanmoreBalance());
                    balance = balance.setScale(0, RoundingMode.HALF_DOWN);


                    holder.flows.setText( balance + "M");
                }else{
                    holder.flows.setText("");
                }
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
                }else{
                    SystemTools.loadBackground(holder.account,null);
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
                    holder.rlAlphaL.setVisibility(View.VISIBLE);
                } else
                {
                    holder.rlAlphaL.setVisibility(View.GONE);
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

            RelativeLayout rlAlphaL;
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

        private String has_phonenumber;

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

        public String getHas_phonenumber() {
            return has_phonenumber;
        }

        public void setHas_phonenumber(String has_phonenumber) {
            this.has_phonenumber = has_phonenumber;
        }
    }



    private class LetterListViewListener implements MyLetterListView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            if(alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);
                contactList.setSelection(position);
                overlay.setText(sections[position]);
                overlay.setVisibility(View.VISIBLE);
                mHandler.removeCallbacks(overlayThread);
                //延迟一秒后执行，让overlay为不可见
                mHandler.postDelayed(overlayThread, 1500);
            }
        }
    }

    //设置overlay不可见
    private class OverlayThread implements Runnable {

        @Override
        public void run() {
            overlay.setVisibility(View.GONE);
        }

    }
}
