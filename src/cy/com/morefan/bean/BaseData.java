package cy.com.morefan.bean;

import java.io.Serializable;

public class BaseData implements Serializable
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = 4647600782044792543L;
    
    /**任务列表**/
    public String title;// 标题
    public String pictureURL;// 图片连接
    public String desc;// String 简述
    public String canGetFlow;//可领取的流量
    public Long publishDate;//标准时间 发布时间
    
    /**师徒联盟**/
    public String myCode;//我的邀请码

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getPictureURL()
    {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL)
    {
        this.pictureURL = pictureURL;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public String getCanGetFlow()
    {
        return canGetFlow;
    }

    public void setCanGetFlow(String canGetFlow)
    {
        this.canGetFlow = canGetFlow;
    }

    

    public Long getPublishDate()
    {
        return publishDate;
    }

    public void setPublishDate(Long publishDate)
    {
        this.publishDate = publishDate;
    }

    public String getMyCode()
    {
        return myCode;
    }

    public void setMyCode(String myCode)
    {
        this.myCode = myCode;
    }
    
    

}
