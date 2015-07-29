package cy.com.morefan.bean;

import java.util.List;

/**
 * Created by Administrator on 2015/7/29.
 */
public class FMFCList extends BaseBaseBean {

    private InnerData resultData;

    public InnerData  getResultData()
    {
        return resultData;
    }

    public void setResultData( InnerData resultData)
    {
        this.resultData = resultData;
    }

    public class InnerData
    {
        public List<FCBean> getRequests() {
            return requests;
        }

        public void setRequests(List<FCBean> list) {
            this.requests = list;
        }

        private List<FCBean> requests;
    }
}
