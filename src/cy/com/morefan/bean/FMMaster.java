package cy.com.morefan.bean;

import java.io.Serializable;
/**
 * 
 * @类名称：FMMaster
 * @类描述：师徒联盟实体类
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月8日 下午5:03:40
 * @修改备注：
 * @version:
 */
public class FMMaster implements Serializable
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = -188172636671450460L;
    
    private float totalM;//总贡献流量
    private float yestodayM;//昨日贡献流量
    private String about;//规则介绍
    private int apprNum;//徒弟数量
    private String shareURL;//师徒分享url
    private String shareDescription;//
    
    public String getAbout()
    {
        return about;
    }
    public String getShareURL()
    {
        return shareURL;
    }
    public void setShareURL(String shareURL)
    {
        this.shareURL = shareURL;
    }
    public float getTotalM()
    {
        return totalM;
    }
    public void setTotalM(float totalM)
    {
        this.totalM = totalM;
    }
    public float getYestodayM()
    {
        return yestodayM;
    }
    public void setYestodayM(float yestodayM)
    {
        this.yestodayM = yestodayM;
    }
    public int getApprNum()
    {
        return apprNum;
    }
    public void setApprNum(int apprNum)
    {
        this.apprNum = apprNum;
    }
    public void setAbout(String about)
    {
        this.about = about;
    }
    public String getShareDescription()
    {
        return shareDescription;
    }
    public void setShareDescription(String shareDescription)
    {
        this.shareDescription = shareDescription;
    }

}
