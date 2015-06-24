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

        public List<Question> getTaskDetail()
        {
            return taskDetail;
        }

        public void setTaskDetail(List<Question> taskDetail)
        {
            this.taskDetail = taskDetail;
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
