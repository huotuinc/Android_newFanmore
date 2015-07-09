package cy.com.morefan.bean;

/**
 * 
 * @类名称：GBBaseData
 * @类描述：初始化接口使用的json替代类
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月2日 下午1:51:06
 * @修改备注：
 * @version:
 */
public class GBBaseData extends BaseBaseBean
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = -8801666195249696664L;
    
    private GlobalGroup resultData;//公共信息结构
    
    public GlobalGroup getResultData()
    {
        return resultData;
    }
    public void setResultData(GlobalGroup resultData)
    {
        this.resultData = resultData;
    }
    
}
