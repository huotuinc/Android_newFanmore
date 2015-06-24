package cy.com.morefan.bean;

public class FMGetVC extends BaseBaseBean
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = -777518764822405933L;
    
    private InnerClass resultData;
    
    public InnerClass getResultData()
    {
        return resultData;
    }

    public void setResultData(InnerClass resultData)
    {
        this.resultData = resultData;
    }


    public class InnerClass
    {
        private boolean voiceAble;

        public boolean isVoiceAble()
        {
            return voiceAble;
        }

        public void setVoiceAble(boolean voiceAble)
        {
            this.voiceAble = voiceAble;
        }
        
        
    }

}
