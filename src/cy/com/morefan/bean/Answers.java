package cy.com.morefan.bean;

import java.io.Serializable;

public class Answers implements Serializable
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = -4801937172812164525L;
    
    private Integer aid;
    private String name;
    public Integer getAid()
    {
        return aid;
    }
    public void setAid(int aid)
    {
        this.aid = aid;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    
    

}
