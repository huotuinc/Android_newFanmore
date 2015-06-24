package cy.com.morefan.bean;

import java.util.List;

public class FMContact extends BaseBaseBean
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = -346101131706163891L;

    private List<ContactBean> resultData;

    public List<ContactBean> getResultData()
    {
        return resultData;
    }

    public void setResultData(List<ContactBean> resultData)
    {
        this.resultData = resultData;
    }

    
}
