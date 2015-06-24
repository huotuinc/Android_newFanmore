package cy.com.morefan.bean;
/**
 * 
 * @类名称：FMRegisterBean
 * @类描述：注册接口使用的json替代类
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月2日 下午1:52:27
 * @修改备注：
 * @version:
 */
public class FMRegisterBean extends BaseBaseBean
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = -2948598629475552628L;
    
    private InnerUser resultData;//注册时返回的用户信息

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
