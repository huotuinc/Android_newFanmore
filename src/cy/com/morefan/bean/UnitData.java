package cy.com.morefan.bean;

public class UnitData extends BaseData implements IBaseData{

	
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = 4901688661647131473L;
    public String taskId;
	public String canGetMastFlow;// Number 最高可获取奖励流量
	public String getFlow;//Number 已获取流量
	public int type;// 任务类别
	public int status;// 任务状态
	public String luckies;// Number 多少人获取了流量
	public String last;//Number 剩余流量
	public String contextURL;
	public String shareURL;//分享出去时所用的URL，应当有所区别
    public String getTaskId()
    {
        return taskId;
    }
    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }
    public String getCanGetMastFlow()
    {
        return canGetMastFlow;
    }
    public void setCanGetMastFlow(String canGetMastFlow)
    {
        this.canGetMastFlow = canGetMastFlow;
    }
    public String getGetFlow()
    {
        return getFlow;
    }
    public void setGetFlow(String getFlow)
    {
        this.getFlow = getFlow;
    }
    public int getType()
    {
        return type;
    }
    public void setType(int type)
    {
        this.type = type;
    }
    public int getStatus()
    {
        return status;
    }
    public void setStatus(int status)
    {
        this.status = status;
    }
    public String getLuckies()
    {
        return luckies;
    }
    public void setLuckies(String luckies)
    {
        this.luckies = luckies;
    }
    public String getLast()
    {
        return last;
    }
    public void setLast(String last)
    {
        this.last = last;
    }
    public String getContextURL()
    {
        return contextURL;
    }
    public void setContextURL(String contextURL)
    {
        this.contextURL = contextURL;
    }
    public String getShareURL()
    {
        return shareURL;
    }
    public void setShareURL(String shareURL)
    {
        this.shareURL = shareURL;
    }
	
	

}
