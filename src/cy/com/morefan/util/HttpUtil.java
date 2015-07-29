package cy.com.morefan.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.conn.ConnectTimeoutException;

import android.util.Log;

public class HttpUtil
{

    private static class Holder
    {
        private static final HttpUtil instance = new HttpUtil();
    }

    private HttpUtil()
    {

    }

    public static final HttpUtil getInstance()
    {
        return Holder.instance;
    }

    /**
     * 
     * @方法描述：post请求
     * @方法名：doPost
     * @参数：@param url
     * @参数：@param params
     * @参数：@return
     * @返回：InputStream
     * @exception
     * @since
     */
    public String doPost(String url, final Map<String, String> params)
    {
        // POST方式
        URL post_url;
        String jsonStr = null;
        HttpURLConnection conn = null;
        InputStream inStream = null;
        OutputStream os = null;
        try
        {
            post_url = new URL(url);
            conn = (HttpURLConnection) post_url.openConnection();
            conn.setRequestMethod("POST");

            // 准备数据
            String data = this.potParams(params);
            byte[] data_bytes = data.getBytes();
            
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;");
            conn.setRequestProperty("Content-Length", data_bytes.length + "");
            // POST方式：浏览器将数据以流的方式写入服务器
            conn.setDoOutput(true);// 允许向外部写入数据
            conn.setDoInput(true);
            conn.setUseCaches(true);

            os = conn.getOutputStream();
            os.write(data_bytes);
            conn.setConnectTimeout(10000);
            int statusCode = conn.getResponseCode();
            if (200 == statusCode )
            {
                inStream = conn.getInputStream();
                byte[] dataByte = SystemTools.readInputStream(inStream);
                jsonStr = new String(dataByte);

                Log.i("HttpUtil Post",url);
                Log.i("HttpUtil Post", jsonStr);
            } else
            {
                // 获取数据失败
                jsonStr = "{\"resultCode\":50601,\"systemResultCode\":1}";
            }
        } catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            KJLoger.errorLog(e.getMessage());
            // 服务无响应
            jsonStr = "{\"resultCode\":50001,\"systemResultCode\":1}";
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            KJLoger.errorLog(e.getMessage());
            // 服务无响应
            jsonStr = "{\"resultCode\":50001,\"systemResultCode\":1}";
        } finally
        {

            try
            {
                if (null != os)
                {
                    os.close();
                    if (null != inStream)
                    {
                        inStream.close();
                    }
                }
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (null != conn)
            {
                conn.disconnect();
            }

        }

        return jsonStr;
    }

    /**
     * 
     * @方法描述：get请求
     * @方法名：getByHttpConnection
     * @参数：@param url
     * @参数：@return
     * @返回：InputStream
     * @exception
     * @since
     */
    public String doGet(String url)
    {
        HttpURLConnection conn = null;
        InputStream inStream = null;
        String jsonStr = null;
        URL get_url;
        try
        {                   
            get_url = new URL(url);
            conn = (HttpURLConnection) get_url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(10000);
            int statusCode = conn.getResponseCode();
            if (200 == statusCode )
            {
                inStream = conn.getInputStream();
                byte[] dataByte = SystemTools.readInputStream(inStream);
                jsonStr = new String(dataByte);
                
                //Log.i("HttpUtil",url);
                //Log.i("HttpUtil", jsonStr);
            } else
            {
                // 获取数据失败
                jsonStr = "{\"resultCode\":50601,\"systemResultCode\":1}";
            }
        }catch( ConnectTimeoutException ctimeoutex){            
            jsonStr = "{\"resultCode\":50001,\"resultDescription\":\"网络请求超时，请稍后重试\",\"systemResultCode\":1}";
        }catch (SocketTimeoutException stimeoutex) {
            jsonStr = "{\"resultCode\":50001,\"resultDescription\":\"网络请求超时，请稍后重试\",\"systemResultCode\":1}";
        }
        catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            KJLoger.errorLog(e.getMessage());
            // 服务无响应
            jsonStr = "{\"resultCode\":50001,\"resultDescription\":\"系统请求失败\",\"systemResultCode\":1}";
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            KJLoger.errorLog(e.getMessage());
            // 服务无响应
            jsonStr = "{\"resultCode\":50001,\"resultDescription\":\"系统请求失败\",\"systemResultCode\":1}";
        } finally
        {
            try
            {
                if (null != inStream)
                {
                    inStream.close();
                }
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (null != conn)
            {
                conn.disconnect();
            }
        }

        return jsonStr;
    }

    private String potParams(Map<String, String> map)
    {
        StringBuffer buffer = new StringBuffer();
        Iterator mapI = map.entrySet().iterator();
        while (mapI.hasNext())
        {
            Map.Entry entry = (Map.Entry) mapI.next();

//            buffer.append("&" + entry.getKey() + "=" + entry.getValue());
            try
            {
               String eee = URLEncoder.encode(entry.getValue().toString() , "UTF-8");
                buffer.append("&" + entry.getKey() +"=" + eee );
                
                //Log.i("dedd", eee);
                
            } catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        return buffer.toString().substring(1, buffer.length());
    }

}
