package cy.com.morefan.bean;

public class MarkData extends BaseData implements IBaseData{
	
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = 7367108210052257914L;
    
    public enum WEEK_NAME{
		周一, 周二, 周三, 周四, 周五, 周六, 周日
	}
	public int index;
	public int status;//0：未签到  1：已签到 2：漏签
	
	public String name;
    public int getIndex()
    {
        return index;
    }
    public void setIndex(int index)
    {
        this.index = index;
    }
    public int getStatus()
    {
        return status;
    }
    public void setStatus(int status)
    {
        this.status = status;
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
