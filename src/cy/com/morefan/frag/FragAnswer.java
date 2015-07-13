package cy.com.morefan.frag;

import java.io.Serializable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cy.com.morefan.ui.answer.AnswerActivity;
import cy.com.morefan.ui.user.LoginActivity;
import cy.com.morefan.util.VolleyUtil;
/*
 * 答题类/问卷类
 * @类名称：FragAnswer
 * @类描述：
 * @创建人：jinxiangdong
 * @修改人：
 * @修改时间：2015年6月1日 下午3:47:44
 * @修改备注：
 * @version:
 */
public class FragAnswer extends FragAnswerBase
{            

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);                  
    }   

    @Override
    public void onFragPasue()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == android.app.Activity.RESULT_OK)
        {                       
            //new TaskDetailAsyncTask().execute(taskData.getTaskId());
            startNextFragment();
        }
    }
     
    
    @Override
    protected void startNextFragment()
    {
        BaseFragment frag = new FragAnswerList();
        Bundle bd = new Bundle();
        bd.putSerializable("questions",
                (Serializable) taskData.getQuestions());
        bd.putInt("taskid", taskData.getTaskId());
        frag.setArguments(bd);
        ((AnswerActivity) getActivity()).switchFragment(frag,
                "answerlist");
    }
        
    @Override
    public void onClick(View view)
    {                  
        saveReadTask( taskData.getTaskId() );
        
        if (view == btnAnswer)
        {                                     
            if (false == app.isLogin(getActivity()))
            {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, 1000);             
            } else
            {
                startNextFragment();
              //new TaskDetailAsyncTask().execute(taskData.getTaskId());
            }
        }
    }
}
