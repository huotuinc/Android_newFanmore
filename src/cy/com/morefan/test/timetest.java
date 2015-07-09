package cy.com.morefan.test;

import junit.framework.TestCase;

public class timetest extends TestCase
{
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
}
