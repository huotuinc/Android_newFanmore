package cy.com.morefan.bean;

public class MoreSettingData extends BaseData implements IBaseData
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = 5242325124201764043L;
    
    public String settingName;
    public String data;

    public String getSettingName()
    {
        return settingName;
    }

    public void setSettingName(String settingName)
    {
        this.settingName = settingName;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }
    
}
