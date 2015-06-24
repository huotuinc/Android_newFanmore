package cy.com.morefan.bean;

public class UserData extends BaseData implements IBaseData {
	
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = -1121112389112863716L;

    private String logoUrl;//用户头像
    private int errorCode;//登录时返回的错误码:::0：成功 、 其他失败
    private String phoneNumber;//手机号
    private String userName;//用户名（默认为手机号）
    private transient String password;//用户密码
    private String accountName;//登录人实名
    private String birthday;//生日
    private String job;//职业
    private String invitationCode;//邀请码
    private String verificationCode;//验证码
    public String getVerificationCode()
    {
        return verificationCode;
    }
    public void setVerificationCode(String verificationCode)
    {
        this.verificationCode = verificationCode;
    }
    public String getInvitationCode()
    {
        return invitationCode;
    }
    public void setInvitationCode(String invitationCode)
    {
        this.invitationCode = invitationCode;
    }
    public String getPassword()
    {
        return password;
    }
    public int getErrorCode()
    {
        return errorCode;
    }
    public void setErrorCode(int errorCode)
    {
        this.errorCode = errorCode;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    public String getHaveFlow()
    {
        return haveFlow;
    }
    public void setHaveFlow(String haveFlow)
    {
        this.haveFlow = haveFlow;
    }
    private String income;//收入
    private String hobby;//爱好
    private String location;//所在地
    private String registrationTime;//注册时间
    private String accountStatus;//账户状态
    private String haveFlow;//拥有的流量
    
	public boolean isLogin;
	public String signInfo;//签到信息

    public boolean isLogin()
    {
        return isLogin;
    }
    public String getLogoUrl()
    {
        return logoUrl;
    }
    public void setLogoUrl(String logoUrl)
    {
        this.logoUrl = logoUrl;
    }
    public String getPhoneNumber()
    {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }
    public String getUserName()
    {
        return userName;
    }
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    public String getAccountName()
    {
        return accountName;
    }
    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }
    public String getBirthday()
    {
        return birthday;
    }
    public void setBirthday(String birthday)
    {
        this.birthday = birthday;
    }
    public String getJob()
    {
        return job;
    }
    public void setJob(String job)
    {
        this.job = job;
    }
    public String getIncome()
    {
        return income;
    }
    public void setIncome(String income)
    {
        this.income = income;
    }
    public String getHobby()
    {
        return hobby;
    }
    public void setHobby(String hobby)
    {
        this.hobby = hobby;
    }
    public String getLocation()
    {
        return location;
    }
    public void setLocation(String location)
    {
        this.location = location;
    }
    public String getRegistrationTime()
    {
        return registrationTime;
    }
    public void setRegistrationTime(String registrationTime)
    {
        this.registrationTime = registrationTime;
    }
    public String getAccountStatus()
    {
        return accountStatus;
    }
    public void setAccountStatus(String accountStatus)
    {
        this.accountStatus = accountStatus;
    }
    public void setLogin(boolean isLogin)
    {
        this.isLogin = isLogin;
    }
    public String getSignInfo()
    {
        return signInfo;
    }
    public void setSignInfo(String signInfo)
    {
        this.signInfo = signInfo;
    }
	
	
}
