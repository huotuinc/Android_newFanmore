package cy.com.morefan.bean;

/**
 * 
 * @类名称：FMLogin
 * @类描述：处理登录时的数据
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月4日 下午1:39:41
 * @修改备注：
 * @version:
 */
public class FMLogin extends BaseBaseBean
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = -1348721346570085816L;
    
    private InnerUser resultData;//注册时返回的用户信息
    
    public class InnerUser
    {
        private int requireMobile;
        private FMUserData user;
        public int getRequireMobile()
        {
            return requireMobile;
        }
        public void setRequireMobile(int requireMobile)
        {
            this.requireMobile = requireMobile;
        }
        public FMUserData getUser()
        {
            return user;
        }
        public void setUser(FMUserData user)
        {
            this.user = user;
        }
        
        
    }

    public InnerUser getResultData()
    {
        return resultData;
    }

    public void setResultData(InnerUser resultData)
    {
        this.resultData = resultData;
    }
    
    

}
