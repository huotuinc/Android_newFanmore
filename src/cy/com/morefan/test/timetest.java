package cy.com.morefan.test;

import android.app.Application;
import android.test.ApplicationTestCase;

import junit.framework.TestCase;

import cy.com.morefan.bean.FMDeliveryGood;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.Util;

public class timetest extends ApplicationTestCase<Application>//TestCase
{
    public timetest() {
        super(Application.class);
    }

    public void test(){
        int t=60*60*24+3*60*60+8*60+56;
        
        String time="";
       
        int days = t/60/60/24;
        int remain =  t % ( 60*60*24);
        int hours = remain / (60*60);
        remain = remain % (60*60);
        int minute = remain / ( 60);
        int second = remain %60;
        if( days>0){
            time = days+"天";
        }
        if(hours>0){
            time += hours+"小时";
        }
        if(minute>0){
            time +=minute+"分";
        }
        if(second>0){
            time += second+"秒";
        }
        
        String tipText =String .format( "%s", time);
        
        assertEquals("1天3小时8分56秒", time);
        
    }
    
    
    public void testfloat()
    {
        float a = 0.00001f;
        float b = 0.000002f;
        //assertEquals("a==b::::", a == b );
       
        //assertEquals(" a> b ::::", a>b);
        //assertEquals("math(a-b)>0", Math.abs(a-b)>0 );
    }

    public void testjson(){
        String jsonStr= " {\"systemResultCode\":1,\"systemResultDescription\":null,\"resultCode\":1,\"resultDescription\":\"操作成功\",\"resultData\":null}";
        JSONUtil<FMDeliveryGood> jsonUtil=new JSONUtil<>();
        FMDeliveryGood result=new FMDeliveryGood();
        result= jsonUtil.toBean(jsonStr,result);

        assertEquals("tetet:", result.getResultCode()==1);

    }

    public void testSign(){
        //int signCount = Util.calSignIn( 56);
        //assertEquals( "sign=3" , signCount== 0);

        //int signCount = Util.calSignIn( 62 );
        //assertEquals(signCount== 4 , "sign=4"  );

        int signCount = Util.calSignIn(71);
        //assertEquals(signCount==2 ,"sign=2");

        signCount = Util.calSignIn(127);
        //assertEquals(signCount==7 ,"sign=7");

        signCount = Util.calSignIn(65);
    }
}
