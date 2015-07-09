package cy.com.morefan.bean;
/**
 * 
 * @类名称：FMQMaster
 * @类描述：师徒联盟接口相应数据类
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月8日 下午5:03:09
 * @修改备注：
 * @version:
 */
public class FMQMaster extends BaseBaseBean
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = 3003830896531541452L;
    
    private FMMaster resultData;

    public FMMaster getResultData()
    {
        return resultData;
    }

    public void setResultData(FMMaster resultData)
    {
        this.resultData = resultData;
    }
    
    
    

}
