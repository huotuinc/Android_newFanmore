package cy.com.morefan.bean;

import java.util.List;

public class FMTaskDetail extends BaseBaseBean
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = -7870809841791151204L;
    
    private InnerData resultData;
       
    public class  InnerData
    {
        private List<Question> taskDetail;
        
        private TaskData task;
        
        private Long secondToStart;

        public List<Question> getTaskDetail()
        {
            return taskDetail;
        }

        public void setTaskDetail(List<Question> taskDetail)
        {
            this.taskDetail = taskDetail;
        }

        public Long getSecondToStart()
        {
            return secondToStart;
        }

        public void setSecondToStart(Long secondToStart)
        {
            this.secondToStart = secondToStart;
        }

        public TaskData getTask()
        {
            return task;
        }

        public void setTask(TaskData task)
        {
            this.task = task;
        }
        
    }



    public InnerData getResultData()
    {
        return resultData;
    }



    public void setResultData(InnerData resultData)
    {
        this.resultData = resultData;
    }
    
}
