package cy.com.morefan.bean;

import java.io.Serializable;

public class GlobalGroup implements Serializable
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = -6063552365591809849L;
    
    private GlobalData global;
    private FMUserData user;
    private UpdateData update;
    public GlobalData getGlobal()
    {
        return global;
    }
    public void setGlobal(GlobalData global)
    {
        this.global = global;
    }
    public FMUserData getUser()
    {
        return user;
    }
    public void setUser(FMUserData user)
    {
        this.user = user;
    }
    public UpdateData getUpdate()
    {
        return update;
    }
    public void setUpdate(UpdateData update)
    {
        this.update = update;
    }
    
    

}
