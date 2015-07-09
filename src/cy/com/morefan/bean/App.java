package cy.com.morefan.bean;

import java.io.Serializable;

public class App implements Serializable
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = 3359438659557472255L;
    
    private String picUrl ;
    private int appid;
    private String showName;
    private float m;
    private Long date;
    private int countOfApp;
    private String appOrder;
    
    public int getCountOfApp()
    {
        return countOfApp;
    }
    public String getAppOrder()
    {
        return appOrder;
    }
    public void setAppOrder(String appOrder)
    {
        this.appOrder = appOrder;
    }
    public void setCountOfApp(int countOfApp)
    {
        this.countOfApp = countOfApp;
    }
    public String getPicUrl()
    {
        return picUrl;
    }
    public void setPicUrl(String picUrl)
    {
        this.picUrl = picUrl;
    }
    public int getAppid()
    {
        return appid;
    }
    public void setAppid(int appid)
    {
        this.appid = appid;
    }
    public String getShowName()
    {
        return showName;
    }
    public void setShowName(String showName)
    {
        this.showName = showName;
    }
    public float getM()
    {
        return m;
    }
    public void setM(float m)
    {
        this.m = m;
    }
    public Long getDate()
    {
        return date;
    }
    public void setDate(Long date)
    {
        this.date = date;
    }
    
    

}
