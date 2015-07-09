package cy.com.morefan.bean;

import java.util.List;

/**
 * 
 * @类名称：FMApp
 * @类描述：获取徒弟列表数据
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月3日 下午4:23:32
 * @修改备注：
 * @version:
 */
public class FMApp extends BaseBaseBean
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = 6716268278322523725L;
    private InnerClass resultData;
    
    public class InnerClass
    {
        private List<App> apps;

        public List<App> getApps()
        {
            return apps;
        }

        public void setApps(List<App> apps)
        {
            this.apps = apps;
        }
        
    }

    public InnerClass getResultData()
    {
        return resultData;
    }

    public void setResultData(InnerClass resultData)
    {
        this.resultData = resultData;
    }
    
    

}
