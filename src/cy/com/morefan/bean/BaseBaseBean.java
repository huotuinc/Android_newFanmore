package cy.com.morefan.bean;

import java.io.Serializable;

/**
 * 
 * @类名称：BaseBaseBean
 * @类描述：http返回json格式4个状态码
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月2日 下午1:00:29
 * @修改备注：
 * @version:
 */
public class BaseBaseBean implements Serializable
{

    /**
     * @field:serialVersionUID:TODO
     * @since
     */

    private static final long serialVersionUID = 1673193772947147006L;

    private int systemResultCode;// 系统状态返回：1，成功;0，失败

    private String systemResultDescription;// 成功/失败描述

    private int resultCode;// 逻辑状态返回 ：1成功,0 失败

    private String resultDescription;// 逻辑状态描述

    public int getSystemResultCode()
    {
        return systemResultCode;
    }

    public void setSystemResultCode(int systemResultCode)
    {
        this.systemResultCode = systemResultCode;
    }

    public String getSystemResultDescription()
    {
        return systemResultDescription;
    }

    public void setSystemResultDescription(String systemResultDescription)
    {
        this.systemResultDescription = systemResultDescription;
    }

    public int getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(int resultCode)
    {
        this.resultCode = resultCode;
    }

    public String getResultDescription()
    {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription)
    {
        this.resultDescription = resultDescription;
    }

}
