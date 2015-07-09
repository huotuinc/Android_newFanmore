package cy.com.morefan.bean;

public class FMAnswer extends BaseBaseBean
{
    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */   
    
    private static final long serialVersionUID = -2839359772870347890L;
    
    private InnerData resultData;//答题返回的数据
    
    public InnerData getResultData()
    {
        return resultData;
    }

    public void setResultData(InnerData resultData)
    {
        this.resultData = resultData;
    }    

    public class InnerData {
        private Integer illgel;
        private float reward;
        private Integer chance;
        public Integer getIllgel()
        {
            return illgel;
        }
        public void setIllgel(Integer illgel)
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
        public Integer getChance()
        {
            return chance;
        }
        public void setChance(Integer chance)
        {
            this.chance = chance;
        }
        
    }

}
