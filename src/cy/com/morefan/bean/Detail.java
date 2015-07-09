package cy.com.morefan.bean;

import java.io.Serializable;

public class Detail implements Serializable
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = 742819393203071123L;
    
    private long detailId;
    private String title;
    private float vary;
    private Long date;
    private Long detailOrder;
    
    public long getDetailId()
    {
        return detailId;
    }
    public void setDetailId(long detailId)
    {
        this.detailId = detailId;
    }
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public float getVary()
    {
        return vary;
    }
    public void setVary(float vary)
    {
        this.vary = vary;
    }
    public Long getDate()
    {
        return date;
    }
    public void setDate(Long date)
    {
        this.date = date;
    }
    public Long getDetailOrder()
    {
        return detailOrder;
    }
    public void setDetailOrder(Long detailOrder)
    {
        this.detailOrder = detailOrder;
    }

}
