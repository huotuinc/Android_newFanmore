package cy.com.morefan.bean;

public class FMTaskTurnedNotify extends BaseBaseBean
{
    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = -6434331027060614108L;
    private InnerData resultData;
    
    
    public class InnerData
    {
        private int illgel;
        private float reward;
        public int getIllgel()
        {
            return illgel;
        }
        public void setIllgel(int illgel)
        {
            this.illgel = illgel;
        }
        public float getReward()
        {
            return reward;
        }
        public void setReward(float reward)
        {
            this.reward = reward;
        }
        
        
    }


    public InnerData getResultData()
    {
        return resultData;
    }


    public void setResultData(InnerData resultData)
    {
        this.resultData = resultData;
    }
}
