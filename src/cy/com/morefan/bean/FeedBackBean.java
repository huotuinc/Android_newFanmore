package cy.com.morefan.bean;

import java.io.Serializable;

public class FeedBackBean implements Serializable
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = -3797173214608220305L;
    
    private String contact;//联系人
    private String content;//反馈内容
    private Long createTime;//创建时间
    private Long doTime;//提交时间（处理时间）
    private int id;//回馈单编号
    private int isDo;//是否处理
    private String name;//用户名称
    private String remark;//备注
    private String turnUserId;//用户Id
    public String getContact()
    {
        return contact;
    }
    public void setContact(String contact)
    {
        this.contact = contact;
    }
    public String getContent()
    {
        return content;
    }
    public void setContent(String content)
    {
        this.content = content;
    }
    public Long getCreateTime()
    {
        return createTime;
    }
    public void setCreateTime(Long createTime)
    {
        this.createTime = createTime;
    }
    public Long getDoTime()
    {
        return doTime;
    }
    public void setDoTime(Long doTime)
    {
        this.doTime = doTime;
    }
    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }
    public int getIsDo()
    {
        return isDo;
    }
    public void setIsDo(int isDo)
    {
        this.isDo = isDo;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getRemark()
    {
        return remark;
    }
    public void setRemark(String remark)
    {
        this.remark = remark;
    }
    public String getTurnUserId()
    {
        return turnUserId;
    }
    public void setTurnUserId(String turnUserId)
    {
        this.turnUserId = turnUserId;
    }
    
    

}
