package cy.com.morefan.bean;

import java.math.BigDecimal;
import java.util.List;

/**
 * 准备兑现 接口返回的 数据类
 * 
 * @类名称：FMPrepareCheckout
 * @类描述：
 * @创建人：jinxiangdong
 * @修改人：
 * @修改时间：2015年6月9日 上午10:42:18
 * @修改备注：
 * @version:
 */
public class FMPrepareCheckout extends BaseBaseBean
{

    /**
     * @field:serialVersionUID:TODO
     * @since
     */

    private static final long serialVersionUID = 3891932182572295664L;

    private InnerData resultData =new InnerData();

    public InnerData getResultData()
    {
        return resultData;
    }

    public void setResultData(InnerData resultData)
    {
        this.resultData = resultData;
    }

    public class InnerData
    {
        /**
         * 当前流量余额
         */
        private BigDecimal currentBalance;

        /**
         * 可供兑现的不同流量数据 单位m
         */
        private List<BigDecimal> targets;

        public BigDecimal getCurrentBalance()
        {
            return currentBalance;
        }

        public void setCurrentBalance(BigDecimal currentBalance)
        {
            this.currentBalance = currentBalance;
        }

        public List<BigDecimal> getTargets()
        {
            return targets;
        }

        public void setTargets(List<BigDecimal> targets)
        {
            this.targets = targets;
        }
    }

}
