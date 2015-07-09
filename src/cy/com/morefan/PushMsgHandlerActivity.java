package cy.com.morefan;

import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.ui.account.MsgCenterActivity;
import cy.com.morefan.ui.answer.AnswerActivity;
import cy.com.morefan.util.Util;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PushMsgHandlerActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);        
        
        operationAlarm();       
        
        finish();
    }
    
    private boolean operationAlarm() {
        if(null != getIntent().getExtras()){
            final boolean isLoaded = Util.isActivityLoaded(this);
            
             System.out.println(">>>>isRunning:" + isLoaded);
             
            int type = 0;
            if( getIntent().hasExtra("type")){
                type = getIntent().getIntExtra( "type", 0);
            }
           
            if( type == Constant.MESSAGE_TYPE_SYSTEMMESSAGE){
                //如果是系统消息，则跳转到消息界面
                Intent intentmsg =null;
                if(isLoaded){
                     intentmsg = new Intent(this, MsgCenterActivity.class);    
                 }else{
                     intentmsg = new Intent(this, LoadingActivity.class);
                     intentmsg.putExtra("type",  type);
                 }
                startActivity(intentmsg);
                return true;
            }
            
            
             
           TaskData taskData  = null;
           if( getIntent().hasExtra("task")){
               taskData = (TaskData)getIntent().getSerializableExtra("task");
           }      
           
            if(taskData != null ){
                 Intent intent1 = null;
                 if(isLoaded){
                        intent1 = new Intent(this, AnswerActivity.class);    
                        intent1.putExtra("task", taskData);
                     }else{
                        intent1 = new Intent(this, LoadingActivity.class);
                        intent1.putExtra("task", taskData);
                     }
                 startActivity(intent1);
                 return true;
            }
        }
        return false;
    }

}
