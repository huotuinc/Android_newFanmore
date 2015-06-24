package cy.com.morefan.bean;

import java.util.List;

public class FMPreTaskData extends BaseBaseBean
{    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = 8396398044663148174L;
    
    private InnerData resultData;    

    public InnerData getResultData()
    {
        return resultData;
    }

    public void setResultData(InnerData resultData)
    {
        this.resultData = resultData;
    }

    public class InnerData{
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
