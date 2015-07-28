package cy.com.morefan.bean;

import java.util.List;

public class FMContact extends BaseBaseBean
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = -346101131706163891L;

    private InnerClass resultData;

    public InnerClass getResultData()
    {
        return resultData;
    }

    public void setResultData(InnerClass resultData)
    {
        this.resultData = resultData;
    }

    public class InnerClass{
        public List<ContactBean> getContactInfo() {
            return contactInfo;
        }

        public void setContactInfo(List<ContactBean> contactInfo) {
            this.contactInfo = contactInfo;
        }

        private List<ContactBean> contactInfo;

    }
}
