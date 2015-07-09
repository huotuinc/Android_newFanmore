package cy.com.morefan.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

public class SystemTools
{

    public static byte[] readInputStream(InputStream inStream) {  
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[2048];  
        int len = 0;  
        try
        {
            while( (len=inStream.read(buffer)) != -1 ){  
                outStream.write(buffer, 0, len);
            }
            return outStream.toByteArray();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            KJLoger.errorLog(e.getMessage());
        }
        finally
        {
            try
            {
                inStream.close();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                KJLoger.errorLog(e.getMessage());
            }
        }
        return null;        
    }
    
    /**
     * 指定格式返回当前系统时间
     */
    public static String getDataTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }
    
    /**
    *
    * @param time yyy-MM-dd HH:mm
    * @return
    */
   public static long getLongTime(String time){
       SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm", Locale.CHINA);
       Date date;
       try {
           date = sdf.parse(time);
           Calendar c = Calendar.getInstance();
           TimeZone tz = TimeZone.getTimeZone("GMT");
           c.setTimeZone(tz);
           c.setTime(date);

           return c.getTimeInMillis();
       } catch (ParseException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
       return 0;
   }
    
    /**
     * 获取手机系统SDK版本
     * 
     * @return 如API 17 则返回 17
     */
    public static int getSDKVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获取系统版本
     * 
     * @return 形如2.3.3
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }
    
    @SuppressLint("NewApi")
    public static void loadBackground(TextView view, Drawable drawable, String text)
    {
        if(SystemTools.getSDKVersion() >= 16)
        {
            view.setText(text);
            view.setBackground(drawable);
        }
        else
        {
            view.setText(text);
            view.setBackgroundDrawable(drawable);
        }
    }
    
    @SuppressLint("NewApi")
    public static void loadBackground(View view, Drawable drawable)
    {
        if(SystemTools.getSDKVersion() >= 16)
        {
            view.setBackground(drawable);
        }
        else
        {
            view.setBackgroundDrawable(drawable);
        }
    }
    
}
