package cy.com.morefan.bean;

import java.io.Serializable;

public class HttpCode implements Serializable
{

    /**
     * @field:serialVersionUID:TODO
     * @since
     */

    private static final long serialVersionUID = -2104824751163027730L;

    private String httpGetCode;

    private String httpGetMsg;

    private String httpPostCode;

    private String httpPostMsg;
    
    //

    public String getHttpGetCode()
    {
        return httpGetCode;
    }

    public void setHttpGetCode(String httpGetCode)
    {
        this.httpGetCode = httpGetCode;
    }

    public String getHttpGetMsg()
    {
        return httpGetMsg;
    }

    public void setHttpGetMsg(String httpGetMsg)
    {
        this.httpGetMsg = httpGetMsg;
    }

    public String getHttpPostCode()
    {
        return httpPostCode;
    }

    public void setHttpPostCode(String httpPostCode)
    {
        this.httpPostCode = httpPostCode;
    }

    public String getHttpPostMsg()
    {
        return httpPostMsg;
    }

    public void setHttpPostMsg(String httpPostMsg)
    {
        this.httpPostMsg = httpPostMsg;
    }

}
