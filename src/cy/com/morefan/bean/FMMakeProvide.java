package cy.com.morefan.bean;

import com.sina.weibo.sdk.api.share.Base;

/**
 * Created by Administrator on 2015/7/27.
 */
public class FMMakeProvide extends BaseBaseBean {
    public InnerClass getResultData() {
        return resultData;
    }

    public void setResultData(InnerClass resultData) {
        this.resultData = resultData;
    }

    private InnerClass resultData;

    public class InnerClass{
        public String getSmsContent() {
            return smsContent;
        }

        public void setSmsContent(String smsContent) {
            this.smsContent = smsContent;
        }

        private String smsContent;

    }

}
