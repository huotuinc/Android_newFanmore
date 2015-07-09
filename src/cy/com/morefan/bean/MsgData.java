package cy.com.morefan.bean;

public class MsgData extends BaseData implements IBaseData
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = 724479087115202438L;
    
    private String msgType;//消息类型
    private String msgTime;//消息时间
    private String msgTitle;//消息标题
    private String msgCon;//消息内容
    public String getMsgType()
    {
        return msgType;
    }
    public void setMsgType(String msgType)
    {
        this.msgType = msgType;
    }
    public String getMsgTime()
    {
        return msgTime;
    }
    public void setMsgTime(String msgTime)
    {
        this.msgTime = msgTime;
    }
    public String getMsgTitle()
    {
        return msgTitle;
    }
    public void setMsgTitle(String msgTitle)
    {
        this.msgTitle = msgTitle;
    }
    public String getMsgCon()
    {
        return msgCon;
    }
    public void setMsgCon(String msgCon)
    {
        this.msgCon = msgCon;
    }
    
    
    

}
