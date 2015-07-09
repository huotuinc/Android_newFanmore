package cy.com.morefan.bean;

import java.io.Serializable;

public class Message implements Serializable
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = 4700302435348994093L;
    
    private int messageid;
    private long messageOrder;
    private String context;
    private long date;
    public int getMessageid()
    {
        return messageid;
    }
    public void setMessageid(int messageid)
    {
        this.messageid = messageid;
    }
   
    public String getContext()
    {
        return context;
    }
    public void setContext(String context)
    {
        this.context = context;
    }
    public long getDate()
    {
        return date;
    }
    public void setDate(long date)
    {
        this.date = date;
    }
    public long getMessageOrder()
    {
        return messageOrder;
    }
    public void setMessageOrder(long messageOrder)
    {
        this.messageOrder = messageOrder;
    }

}
