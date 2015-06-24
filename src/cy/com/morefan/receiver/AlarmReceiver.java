package cy.com.morefan.receiver;

import java.io.Serializable;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cy.com.morefan.R;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.frag.FragPre;
import cy.com.morefan.ui.answer.AnswerActivity;

public class AlarmReceiver extends BroadcastReceiver
{
    
    private NotificationManager notificationManager;
    private Notification notification;
    private static int Notification_ID_BASE = 110;

    @Override
    public void onReceive(Context context, Intent intent)
    {        
        // TODO Auto-generated method stub
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        //预告任务下标
        int index = bundle.getInt("index");
        if (Constant.ALARM_ACTION.equals(action))
        {
            // 拦截到闹钟，设置提醒Notice
            notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notification = new Notification();
            notification.when = System.currentTimeMillis();
            notification.tickerText = "粉猫今日预告提醒";
            notification.icon = R.drawable.ic_launcher;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            Intent noticeI = new Intent(context, AnswerActivity.class);     
            if( null != bundle && bundle.containsKey("task")){
                TaskData task=(TaskData)bundle.getSerializable("task");
                noticeI.putExtra("task", (Serializable) task);
            }
            
            PendingIntent noticePI = PendingIntent.getActivity(context, 0, noticeI, PendingIntent.FLAG_UPDATE_CURRENT );//获得PendingIntent
            notification.setLatestEventInfo(context, "粉猫今日预告提醒","一个今日预告定时已经到期，请关注", noticePI);//设置事件信息
            notificationManager.notify(Notification_ID_BASE, notification);
            
            //返回刷新listview列表，刷新该条记录，去掉设置提醒按钮，添加三角标示符
            Intent freshIntent = new Intent(FragPre.REFRESH_TASK_STATUS);
            freshIntent.putExtras(bundle);
            context.sendBroadcast(freshIntent);
        }

    }

}
