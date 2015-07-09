package cy.com.morefan.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class Purchase implements Serializable
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = -547435186517370044L;
    
    private Integer purchaseid;
    private int m;
    private BigDecimal price;
    private String msg;
    public Integer getPurchaseid()
    {
        return purchaseid;
    }
    public void setPurchaseid(int purchaseid)
    {
        this.purchaseid = purchaseid;
    }
    public int getM()
    {
        return m;
    }
    public void setM(int m)
    {
        this.m = m;
    }
    public BigDecimal getPrice()
    {
        return price;
    }
    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }
    public String getMsg()
    {
        return msg;
    }
    public void setMsg(String msg)
    {
        this.msg = msg;
    }
    
    

}
