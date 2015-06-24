package cy.com.morefan.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import cy.com.morefan.MyApplication;
import cy.com.morefan.bean.AnnounceBean;
import cy.com.morefan.bean.Answers;
import cy.com.morefan.bean.GBBaseData;
import cy.com.morefan.bean.MasterData;
import cy.com.morefan.bean.MoreSettingData;
import cy.com.morefan.bean.MsgData;
import cy.com.morefan.bean.Question;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.DataListener;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.KJLoger;
import cy.com.morefan.util.ObtainParamsMap;

public class ApiService extends BaseService
{

    private Context context;

    public ApiService(DataListener listener, Context context)
    {
        super(listener);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public void getAnnounceData()
    {
        ThreadPoolManager.getInstance().addTask(new Runnable()
        {

            @Override
            public void run()
            {
                // get data from ser

                // test data
                if (Constant.USE_TEST_DATA)
                {
                    try
                    {
                        Thread.sleep(3000);
                    } catch (InterruptedException e)
                    {
                        // TODO Auto-generated catch block
                        KJLoger.errorLog(e.getMessage());
                    }
                    AnnounceBean[] datas = new AnnounceBean[100];
                    for (int i = 0; i < 100; i++)
                    {
                        datas[i] = new AnnounceBean();
                    }

                    listener.onDataFinish(DataListener.DONE_UNIT_LIST, null,
                            null, datas);
                }

            }
        });
    }

    public void getTaskList()
    {
        /*
         * ThreadPoolManager.getInstance().addTask(new Runnable() {
         * 
         * @Override public void run() { // get data from ser
         * 
         * // test data if (Constant.USE_TEST_DATA) { try { Thread.sleep(3000);
         * } catch (InterruptedException e) { // TODO Auto-generated catch block
         * KJLoger.errorLog(e.getMessage()); } UnitData[] datas = new
         * UnitData[100]; for (int i = 0; i < 100; i++) { datas[i] = new
         * UnitData(); }
         * 
         * listener.onDataFinish(DataListener.DONE_UNIT_LIST, null, null,
         * datas); }
         * 
         * } });
         */

        // 获取任务数据
        //new GetTaskListAsyncTask().execute(Constant.TASK_DATA_INTEFACE);
        
        try
        {
            Thread.sleep(3000);
        } catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            KJLoger.errorLog(e.getMessage());
        }
        TaskData[] datas = new TaskData[100];
        for (int i = 0; i < 100; i++)
        {
            datas[i] = new TaskData();
            //datas[i].setType("1"); 
            int r = i%4;                   
            datas[i].setType( String.valueOf(r));
            
            List<Question> questions = new ArrayList<Question>();
            for( int k=1;k<=2;k++){
                Question q=new Question();
                q.setQid(k);                
                q.setContext("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet. Proin gravida dolor sit amet lacus accumsan");
                List<Answers> answers=new ArrayList<>();
                Answers answer = new Answers();
                answer.setAid(1);
                answer.setName("答案A");
                answers.add(answer);
                answer = new Answers();
                answer.setAid(2);
                answer.setName("答案B");
                answers.add(answer);
                answer = new Answers();
                answer.setAid(3);
                answer.setName("答案C");
                answers.add(answer);
                answer = new Answers();
                answer.setAid(4);
                answer.setName("答案D");
                answers.add(answer);
                q.setAnswers(answers);
                q.setCorrentAid("2");
                questions.add(q);
            }
            for(int k=3;k<=4;k++){
                Question q=new Question();
                q.setQid(k);
                q.setContext("如何打乱一个顺序的数组，其实集合的帮助类Collection就有现成的方法可用，而且效率还蛮高的，总比自定义随机数等等方法要好很多");
                List<Answers> answers=new ArrayList<>();
                Answers answer = new Answers();
                answer.setAid(1);
                answer.setName("答案A");
                answers.add(answer);
                answer = new Answers();
                answer.setAid(2);
                answer.setName("答案B");
                answers.add(answer);
                answer = new Answers();
                answer.setAid(3);
                answer.setName("答案C");
                answers.add(answer);
                answer = new Answers();
                answer.setAid(4);
                answer.setName("答案D");
                answers.add(answer);
                q.setAnswers(answers);
                q.setCorrentAid("1");
                questions.add(q);
            }
            for(int k=5;k<=6;k++){
                Question q=new Question();
                q.setQid(k);
                q.setContext("展示答案的顺序 app端需要进行乱序；只提交答案的ID而非序号;只有需要提供选择才会有这个字段");
                List<Answers> answers=new ArrayList<>();
                Answers answer = new Answers();
                answer.setAid(1);
                answer.setName("答案A");
                answers.add(answer);
                answer = new Answers();
                answer.setAid(2);
                answer.setName("答案B");
                answers.add(answer);
                answer = new Answers();
                answer.setAid(3);
                answer.setName("答案C");
                answers.add(answer);
                answer = new Answers();
                answer.setAid(4);
                answer.setName("答案D");
                answers.add(answer);
                q.setAnswers(answers);
                q.setCorrentAid("3");
                questions.add(q);
            }
            
            datas[i].setQuestions(questions);
        }

       // listener.onDataFinish(DataListener.DONE_UNIT_LIST, null, null, datas);
        
        
    }

    public void mark(final MyApplication application, final int curWeekOfDay)
    {
        ThreadPoolManager.getInstance().addTask(new Runnable()
        {
            @Override
            public void run()
            {

                // test data
                if (Constant.USE_TEST_DATA)
                {
                    int sign = 0x1 << (7 - curWeekOfDay);
                    application.personal.setSignInfo(Integer.parseInt(
                            String.valueOf(application.personal.getSignInfo()),
                            2) | sign);
                    listener.onDataFinish(DataListener.DONE_MARK, null, null);
                }

            }
        });
    }

    public void getDiscipleList()
    {
        ThreadPoolManager.getInstance().addTask(new Runnable()
        {

            @Override
            public void run()
            {
                // get data from ser

                // test data
                if (Constant.USE_TEST_DATA)
                {
                    try
                    {
                        Thread.sleep(3000);
                    } catch (InterruptedException e)
                    {
                        // TODO Auto-generated catch block
                        KJLoger.errorLog(e.getMessage());
                    }
                    MasterData[] datas = new MasterData[100];
                    for (int i = 0; i < 100; i++)
                    {
                        datas[i] = new MasterData();
                    }

                    listener.onDataFinish(DataListener.DONE_UNIT_LIST, null,
                            null, datas);
                }

            }
        });
    }

    public void getMoreSettingList(String[] datas)
    {
        int length = datas.length;
        MoreSettingData[] sDatas = new MoreSettingData[length];

        for (int i = 0; i < length; i++)
        {
            sDatas[i] = new MoreSettingData();
            if ("意见反馈".equals(datas[i]) || "关于我们".equals(datas[i]))
            {
                sDatas[i].setSettingName(datas[i]);
                sDatas[i].setData("ICON");
            } else if ("清理缓存".equals(datas[i]))
            {
                sDatas[i].setSettingName(datas[i]);
                sDatas[i].setData("213KB");
            } else
            {
                sDatas[i].setSettingName(datas[i]);
                sDatas[i].setData("V21.1");
            }
        }

        listener.onDataFinish(DataListener.DONE_UNIT_LIST, null, null, sDatas);
    }

    public void getMsgList()
    {
        MsgData[] msgDatas = new MsgData[2];
        msgDatas[0] = new MsgData();
        msgDatas[0].setMsgType("系统消息");
        msgDatas[0].setMsgTitle("一条新的系统消息");
        msgDatas[0].setMsgTime("2015-05-26 13:12");
        msgDatas[0].setMsgCon(null);
        msgDatas[1] = new MsgData();
        msgDatas[1].setMsgType("个人消息");
        msgDatas[1].setMsgTitle("一条新的个人消息");
        msgDatas[1].setMsgTime("2015-05-26 13:12");
        msgDatas[1].setMsgCon(null);

        listener.onDataFinish(DataListener.DONE_UNIT_LIST, null, null, msgDatas);

    }

    class GetTaskListAsyncTask extends AsyncTask<String, Void, List<TaskData>>
    {

        @Override
        protected List<TaskData> doInBackground(String... params)
        {
            // TODO Auto-generated method stub
            String url = params[0];
            List<TaskData> taskDatas = new ArrayList<TaskData>();
            ObtainParamsMap obtainMap = new ObtainParamsMap(context);
            //
            String paramMap = obtainMap.getMap();
            String signStr = obtainMap.getSign(null);
            try
            {
                url += "?sign=" + URLEncoder.encode(signStr, "UTF-8");
                url += paramMap;

            } catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                KJLoger.errorLog(e.getMessage());
            }
            JSONUtil<TaskData> jsonUtil = new JSONUtil<TaskData>();
            GBBaseData globalData = new GBBaseData();
            //
            String json = HttpUtil.getInstance().doGet(url);
            // globalData = jsonUtil.toBean(json, globalData);
            return taskDatas;
        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<TaskData> result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
           /* //listener.onDataFinish(DataListener.DONE_UNIT_LIST, null, null,
                    (TaskData[]) result.toArray(new TaskData[result.size()]));*/
        }

    }
}
