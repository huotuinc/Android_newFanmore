package cy.com.morefan.bean;

public class FMCheckIn extends BaseBaseBean
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = -6935287550855596570L;
    
    private InnerUser resultData;

    public InnerUser getResultData()
    {
        return resultData;
    }

    public void setResultData(InnerUser resultData)
    {
        this.resultData = resultData;
    }
    
    public class InnerUser
    {
        private FMUserData user;

        public FMUserData getUser()
        {
            return user;
        }

        public void setUser(FMUserData user)
        {
            this.user = user;
        }
        
        
    }
    
    

}
