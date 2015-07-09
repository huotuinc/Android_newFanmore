package cy.com.morefan.bean;

import java.io.Serializable;

public class BaseCode implements Serializable, IBaseCode
{

    /**
     * @field:serialVersionUID:TODO
     * @since
     */

    private static final long serialVersionUID = -680550449096462438L;

    private int resultCode;

    private String description;

    private int status;

    private String tip;

    private String resultData;

    public int getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(int resultCode)
    {
        this.resultCode = resultCode;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getTip()
    {
        return tip;
    }

    public void setTip(String tip)
    {
        this.tip = tip;
    }

    public String getResultData()
    {
        return resultData;
    }

    public void setResultData(String resultData)
    {
        this.resultData = resultData;
    }

}
