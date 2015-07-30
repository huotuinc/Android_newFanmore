package cy.com.morefan.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import cy.com.morefan.MyApplication;
import cy.com.morefan.constant.Constant;

public class ObtainParamsMap
{

    private Context context;

    private  String timestamp;

//    static
//    {
//        timestamp = String.valueOf(System.currentTimeMillis());
//    }

    public ObtainParamsMap(Context context)
    {
        // TODO Auto-generated constructor stub
        this.context = context;
        
        timestamp = String.valueOf(System.currentTimeMillis());
    }



    /**
     * 
     * @方法描述：获取post数据
     * @方法名：obtainMap
     * @参数：@return
     * @返回：Map<String,String>
     * @exception
     * @since
     */
    public Map<String, String> obtainMap()
    {
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("appKey", Constant.APPKEY);
        if (null != PreferenceHelper.readString(context, Constant.LOCATION_INFO,
                "latitude"))
        {
            paramsMap.put("lat", PreferenceHelper.readString(context,
                    Constant.LOCATION_INFO, "latitude"));
        } else
        {
            paramsMap.put("lat", "");
        }
        if (null != PreferenceHelper.readString(context, Constant.LOCATION_INFO,
                "Longitude"))
        {
            paramsMap.put("lng", PreferenceHelper.readString(context,
                    Constant.LOCATION_INFO, "Longitude"));
        } else
        {
            paramsMap.put("lng", "");
        }
        if (null != PreferenceHelper.readString(context, Constant.LOCATION_INFO,
                "cityCode"))
        {
            paramsMap.put("cityCode", PreferenceHelper.readString(context,
                    Constant.LOCATION_INFO, "cityCode"));
        } else
        {
            paramsMap.put("cityCode", "");
        }
        paramsMap.put("timestamp", timestamp);
        paramsMap.put("operation", Constant.OPERATION_CODE);
        paramsMap.put("version",
                MyApplication.getAppVersion(context.getApplicationContext()));
        if (null != PreferenceHelper.readString(
                context.getApplicationContext(), Constant.LOGIN_AUTH_INFO,
                Constant.LOGIN_AUTH_TOKEN))
        {
            paramsMap.put("token", PreferenceHelper.readString(
                    context.getApplicationContext(), Constant.LOGIN_AUTH_INFO,
                    Constant.LOGIN_AUTH_TOKEN));
        } else
        {
            paramsMap.put("token", "");
        }
        paramsMap.put("imei",
                MyApplication.getPhoneIMEI(context.getApplicationContext()));
        paramsMap.put("cpaCode", Constant.CAP_CODE);
        return paramsMap;
    }

    /**
     * 
     * @方法描述：获取get数据
     * @方法名：getMap
     * @参数：@return
     * @返回：String
     * @exception
     * @since
     */
    public String getMap()
    {
        StringBuffer buffer = new StringBuffer();
        try
        {
            buffer.append("&appKey=");
            buffer.append(URLEncoder.encode(Constant.APPKEY, "UTF-8"));
            if (null != PreferenceHelper.readString(context, Constant.LOCATION_INFO,
                    "latitude"))
            {
                buffer.append("&lat=");
                buffer.append(URLEncoder.encode(PreferenceHelper.readString(
                        context, Constant.LOCATION_INFO, "latitude"), "UTF-8"));
            } else
            {
                buffer.append("&lat=");
                buffer.append("");
            }
            if (null != PreferenceHelper.readString(context, Constant.LOCATION_INFO,
                    "Longitude"))
            {
                buffer.append("&lng=");
                buffer.append(URLEncoder.encode(PreferenceHelper.readString(
                        context, Constant.LOCATION_INFO, "Longitude"), "UTF-8"));
            } else
            {
                buffer.append("&lng=");
                buffer.append("");
            }
            if (null != PreferenceHelper.readString(context, Constant.LOCATION_INFO,
                    "cityCode"))
            {
                buffer.append("&cityCode=");
                buffer.append(URLEncoder.encode(PreferenceHelper.readString(
                        context, Constant.LOCATION_INFO, "cityCode"), "UTF-8"));
            } else
            {
                buffer.append("&cityCode=");
                buffer.append("");
            }
            buffer.append("&timestamp=");
            buffer.append(URLEncoder.encode(timestamp, "UTF-8"));
            buffer.append("&operation=");
            buffer.append(URLEncoder.encode(Constant.OPERATION_CODE, "UTF-8"));
            buffer.append("&version=");
            buffer.append(URLEncoder.encode(MyApplication.getAppVersion(context
                    .getApplicationContext()), "UTF-8"));
            if (null != PreferenceHelper.readString(
                    context.getApplicationContext(), Constant.LOGIN_AUTH_INFO,
                    Constant.LOGIN_AUTH_TOKEN))
            {
                buffer.append("&token=");
                buffer.append(URLEncoder.encode(PreferenceHelper.readString(
                        context.getApplicationContext(), Constant.LOGIN_AUTH_INFO,
                        Constant.LOGIN_AUTH_TOKEN), "UTF-8"));
            } else
            {
                buffer.append("&token=");
                buffer.append("");
            }

            buffer.append("&imei=");
            buffer.append(URLEncoder.encode(
                    MyApplication.getPhoneIMEI(context.getApplicationContext()),
                    "UTF-8"));
            buffer.append("&cpaCode=");
            buffer.append(URLEncoder.encode(Constant.CAP_CODE, "UTF-8"));
            return buffer.toString();
        } catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            KJLoger.errorLog(e.getMessage());
            return null;
        }

    }

    /**
     * 
     * @方法描述：分装sign码第一步：获取所有参数的map对象
     * @方法名：packMap
     * @参数：@return
     * @返回：Map<String,String>
     * @exception
     * @since
     */
    public Map<String, String> packMap(Map<String, String> exceptionParams)
    {
        Map<String, String> mapResult;
        if (null == exceptionParams)
        {
            mapResult = obtainMap();

            return mapResult;
        } else
        {
            mapResult = obtainMap();

            Iterator iMap = exceptionParams.entrySet().iterator();
            while (iMap.hasNext())
            {
                Map.Entry entry = (Map.Entry) iMap.next();
                mapResult.put((String) entry.getKey(),
                        (String) entry.getValue());
            }

            return mapResult;

        }
    }

    /**
     * 
     * @方法描述：获取sign码第三步：加密
     * @方法名：getSign
     * @参数：@param map
     * @参数：@return
     * @返回：String
     * @exception
     * @since
     */
    public String getSign(Map<String, String> map)
    {

        /*
         * try {
         */
        String values = this.doSort(map);
        Log.i("sign", values);
        // values = URLEncoder.encode(values);
        //String signHex =DigestUtils.md5DigestAsHex(values.toString().getBytes("UTF-8")).toLowerCase();
        String signHex = EncryptUtil.getInstance().encryptMd532(values);
        Log.i("signHex", signHex);
        return signHex;
        /*
         * } catch (UnsupportedEncodingException e) { // TODO Auto-generated
         * catch block KJLoger.errorLog(e.getMessage()); return null; }
         */
    }

    /**
     * 
     * @方法描述：获取sign码第二步：参数排序
     * @方法名：doSort
     * @参数：@param map
     * @参数：@return
     * @返回：String
     * @exception
     * @since
     */
    private String doSort(Map<String, String> map)
    {
        Map<String, String> resultMap = packMap(map);
        resultMap.put("appSecret", Constant.APP_SECRET);
        StringBuffer buffer = new StringBuffer();
        List arrayList = new ArrayList(resultMap.entrySet());

        Collections.sort(arrayList, new Comparator()
        {
            public int compare(Object arg1, Object arg2)
            {
                Map.Entry obj1 = (Map.Entry) arg1;
                Map.Entry obj2 = (Map.Entry) arg2;
                return (obj1.getKey()).toString().compareTo(
                        (String) obj2.getKey());
            }
        });

        //
        for (Iterator iter = arrayList.iterator(); iter.hasNext();)
        {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            // Log.i("key", key);
            // Log.i("value", resultMap.get(key));
            buffer.append(resultMap.get(key));
        }

        return buffer.toString();
    }
}
