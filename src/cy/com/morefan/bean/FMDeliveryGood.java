package cy.com.morefan.bean;

import com.sina.weibo.sdk.api.share.Base;

/**
 * Created by Administrator on 2015/7/27.
 */
public class FMDeliveryGood extends BaseBaseBean {

    /**
     * @field:serialVersionUID:TODO
     * @since
     */
    private static final long serialVersionUID = 1L;


    public InnerClass getResultData() {
        return resultData;
    }

    public void setResultData(InnerClass resultData) {
        this.resultData = resultData;
    }

    private InnerClass resultData;

    class InnerClass{

    }

}
