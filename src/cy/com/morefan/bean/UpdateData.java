package cy.com.morefan.bean;

import java.io.Serializable;

public class UpdateData implements Serializable
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = 462423357090399441L;
    
    private String updateMD5;
    private String updateUrl;
    private String updateTips;
    private UpdateType updateType;
    
    public String getUpdateMD5()
    {
        return updateMD5;
    }
    public void setUpdateMD5(String updateMD5)
    {
        this.updateMD5 = updateMD5;
    }
    public String getUpdateUrl()
    {
        return updateUrl;
    }
    public void setUpdateUrl(String updateUrl)
    {
        this.updateUrl = updateUrl;
    }
    public String getUpdateTips()
    {
        return updateTips;
    }
    public void setUpdateTips(String updateTips)
    {
        this.updateTips = updateTips;
    }
    public UpdateType getUpdateType()
    {
        return updateType;
    }
    public void setUpdateType(UpdateType updateType)
    {
        this.updateType = updateType;
    }
    
    class UpdateType
    {
        String name;
        int value;
    }
    

}
