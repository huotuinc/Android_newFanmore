package cy.com.morefan.bean;

public class MasterData extends BaseData implements IBaseData
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = 7548599812102931926L;
    
    private String discipleName;//徒弟名称
    private String discipleDevotion;//徒弟贡献
    private String yestodayDevotion;//昨日师傅贡献
    public String getDiscipleName()
    {
        return discipleName;
    }
    public void setDiscipleName(String discipleName)
    {
        this.discipleName = discipleName;
    }
    public String getDiscipleDevotion()
    {
        return discipleDevotion;
    }
    public void setDiscipleDevotion(String discipleDevotion)
    {
        this.discipleDevotion = discipleDevotion;
    }
    public String getYestodayDevotion()
    {
        return yestodayDevotion;
    }
    public void setYestodayDevotion(String yestodayDevotion)
    {
        this.yestodayDevotion = yestodayDevotion;
    }
    
    

}
