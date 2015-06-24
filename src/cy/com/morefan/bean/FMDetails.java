package cy.com.morefan.bean;

import java.util.List;

public class FMDetails extends BaseBaseBean
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = 7870377231850223331L;
    
    private InnerData resultData;

    public InnerData  getResultData()
    {
        return resultData;
    }

    public void setResultData( InnerData resultData)
    {
        this.resultData = resultData;
    }
    
    public class InnerData
    {
        private List<Detail> details;

        public List<Detail> getDetails()
        {
            return details;
        }

        public void setDetails(List<Detail> details)
        {
            this.details = details;
        }     
    }
    
    

}
