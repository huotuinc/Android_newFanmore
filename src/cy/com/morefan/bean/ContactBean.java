package cy.com.morefan.bean;

import java.io.Serializable;

public class ContactBean implements Serializable
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = 1002363158076791378L;
    
    private String fanmorePicUrl; // 联系人logo
    private String originMobile; // 联系人手机号码
    private String originIdentify; //用户提交的联系人唯一识别码
    private String fanmoreUsername ; //该联系人电话号码所关联的粉猫用户名 通常是一个手机号码 如果没有找到则返回null
    private Float fanmoreBalance; //在粉猫平台的剩余流量
    private String fanmoreTele; //运营商
    private Float teleBalance; //运营商当月剩余流量 无法查询则为null
    private int fanmoreSex;//性别 0男1女
    private String sortKey; // 排序用的
    private String originName; //联系人姓名
    
    public String getSortKey()
    {
        return sortKey;
    }
    public void setSortKey(String sortKey)
    {
        this.sortKey = sortKey;
    }
    public String getFanmorePicUrl()
    {
        return fanmorePicUrl;
    }
    public void setFanmorePicUrl(String fanmorePicUrl)
    {
        this.fanmorePicUrl = fanmorePicUrl;
    }
    public String getOriginMobile()
    {
        return originMobile;
    }
    public void setOriginMobile(String originMobile)
    {
        this.originMobile = originMobile;
    }
    public String getOriginIdentify()
    {
        return originIdentify;
    }
    public void setOriginIdentify(String originIdentify)
    {
        this.originIdentify = originIdentify;
    }
    public String getFanmoreUsername()
    {
        return fanmoreUsername;
    }
    public void setFanmoreUsername(String fanmoreUsername)
    {
        this.fanmoreUsername = fanmoreUsername;
    }
    public Float getFanmoreBalance()
    {
        return fanmoreBalance;
    }
    public void setFanmoreBalance(Float fanmoreBalance)
    {
        this.fanmoreBalance = fanmoreBalance;
    }
    public String getFanmoreTele()
    {
        return fanmoreTele;
    }
    public void setFanmoreTele(String fanmoreTele)
    {
        this.fanmoreTele = fanmoreTele;
    }
    public Float getTeleBalance()
    {
        return teleBalance;
    }
    public void setTeleBalance(Float teleBalance)
    {
        this.teleBalance = teleBalance;
    }
    public int getFanmoreSex()
    {
        return fanmoreSex;
    }
    public void setFanmoreSex(int fanmoreSex)
    {
        this.fanmoreSex = fanmoreSex;
    }

    public void setOriginName(String originName){this.originName=originName;}
    public String getOriginName(){return this.originName;}
}
