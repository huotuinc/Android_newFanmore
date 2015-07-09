package cy.com.morefan.bean;

import java.util.List;

public class FMMessage extends BaseBaseBean
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = 3628717194862506915L;
    
    private InnerClass resultData;


    public class InnerClass
    {
        private List<Message> messages;

        public List<Message> getMessages()
        {
            return messages;
        }

        public void setMessages(List<Message> messages)
        {
            this.messages = messages;
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
