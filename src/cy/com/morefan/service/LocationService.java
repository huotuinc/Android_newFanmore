package cy.com.morefan.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import cy.com.morefan.MyApplication;

public class LocationService extends Service
{

    /**
     * 纬度值
     */
    public static String latitude = null;

    /**
     * 经度值
     */
    public static String Longitude = null;

    /**
     * 当前城市
     */
    public static String city = null;
    /**
     * 地址
     */
    public static String address = null;
    /**
     * 当前城市码
     */
    public static String cityCode = null;
    
    /**
     * 高精度模式
     */
    private LocationMode mode = LocationMode.Hight_Accuracy;
    /**
     * 定位客户端
     */
    private LocationClient mLocationClient = null;
    
    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        mLocationClient = ((MyApplication)getApplication()).mLocationClient;
        
        //设置定位参数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(mode);
        option.setCoorType("bd09ll"); //设置坐标类型为bd09ll
        option.setOpenGps(true); //打开gps
        option.setScanSpan(5000); //定时定位，每隔一分钟定位一次，刷新一次数据。
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(true);
        mLocationClient.setLocOption(option);
        
        mLocationClient.start();
        
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        mLocationClient.stop();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
    }
    
    

}
