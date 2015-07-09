package cy.com.morefan.bean;

import java.util.List;

public class FMTaskData extends BaseBaseBean
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = 7962890718283574790L;
    
    private InnerTask resultData;

    public InnerTask getResultData()
    {
        return resultData;
    }

    public void setResultData( InnerTask resultData)
    {
        this.resultData = resultData;
    }
    
    public class InnerTask{
        private List<TaskData> task;

        public List<TaskData> getTask()
        {
            return task;
        }

        public void setTask(List<TaskData> task)
        {
            this.task = task;
        }
    }
    

}
