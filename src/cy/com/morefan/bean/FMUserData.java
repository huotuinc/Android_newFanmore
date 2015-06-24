package cy.com.morefan.bean;

import java.io.Serializable;

public class FMUserData implements Serializable
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = -8167964043869271607L;
    

    private float balance;//剩余流量
    private String pictureURL;//头像图片
    private String invCode;//邀请码
    private int signInfo;//签到信息
    private float signtoday;//今日获取流量
    private String name;//
    private int sex;//0男，1女
    private Long birthDate;//生日
    private String mobile;//手机
    private int career;//职业索引
    private int incoming;//收入
    private String favs;//爱好
    private String area;//定位区域
    private Long regDate;//注册时间
    private int invalidCode;
    private String realName;
    private String welcomeTip;
    
    public int getSex()
    {
        return sex;
    }
    public void setSex(int sex)
    {
        this.sex = sex;
    }
    private String token;//用户持有的令牌
    
    public String getToken()
    {
        return token;
    }
    public String getRealName()
    {
        return realName;
    }
    public void setRealName(String realName)
    {
        this.realName = realName;
    }
    public void setToken(String token)
    {
        this.token = token;
    }

    public float getBalance()
    {
        return balance;
    }
    
    public void setBalance(float balance)
    {
        this.balance = balance;
    }
    public String getPictureURL()
    {
        return pictureURL;
    }
    public void setPictureURL(String pictureURL)
    {
        this.pictureURL = pictureURL;
    }
    public String getInvCode()
    {
        return invCode;
    }
    public void setInvCode(String invCode)
    {
        this.invCode = invCode;
    }
    public int getSignInfo()
    {
        return signInfo;
    }
    public void setSignInfo(int signInfo)
    {
        this.signInfo = signInfo;
    }
    public String getName()
    {
        return name;
    }
    public float getSigntoday()
    {
        return signtoday;
    }
    public void setSigntoday(float signtoday)
    {
        this.signtoday = signtoday;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public Long getBirthDate()
    {
        return birthDate;
    }
    public void setBirthDate(Long birthDate)
    {
        this.birthDate = birthDate;
    }
    public String getMobile()
    {
        return mobile;
    }
    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }
    public int getCareer()
    {
        return career;
    }
    public void setCareer(int career)
    {
        this.career = career;
    }
    public int getIncoming()
    {
        return incoming;
    }
    public void setIncoming(int incoming)
    {
        this.incoming = incoming;
    }
    public String getFavs()
    {
        return favs;
    }
    public void setFavs(String favs)
    {
        this.favs = favs;
    }
    public String getArea()
    {
        return area;
    }
    public void setArea(String area)
    {
        this.area = area;
    }
    public Long getRegDate()
    {
        return regDate;
    }
    public void setRegDate(Long regDate)
    {
        this.regDate = regDate;
    }
    public int getInvalidCode()
    {
        return invalidCode;
    }
    public void setInvalidCode(int invalidCode)
    {
        this.invalidCode = invalidCode;
    }
    public String getWelcomeTip()
    {
        return welcomeTip;
    }
    public void setWelcomeTip(String welcomeTip)
    {
        this.welcomeTip = welcomeTip;
    }
    
    

}
