package cy.com.morefan.bean;

import java.io.Serializable;

/**
 * 
 * @类名称：GlobalData
 * @类描述：公共信息
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年5月28日 下午1:26:17
 * @修改备注：
 * @version:
 */
public class GlobalData implements Serializable
{

    /**
     * @field:serialVersionUID:TODO
     * @since
     */

    private static final long serialVersionUID = -2518292892471579263L;

    private int amountToCheckout;// 流量充值最小值
    private int lessReadSeconds;//最少阅读时间单位秒
    private String signMsg;// 连续签到每日可增加0.1M
    private String aboutURL;// 关于我们的连接
    private String helpURL;// 帮助的连接
    private Value[] incomings;// 收入
    private String privacyPoliciesURL;//隐私策略
    private String customerServicePhone;//客服电话
    private Value[] career;// 职业
    private Value[] favs;// 爱好
    private String serviceURL;//投放指南
    private String ruleURL;//规则说明
    private boolean voiceSupported;//验证码是否支持语音播报
    
    
    public class Value
    {
        private String name;
        private int value;
        public String getName()
        {
            return name;
        }
        public void setName(String name)
        {
            this.name = name;
        }
        public int getValue()
        {
            return value;
        }
        public void setValue(int value)
        {
            this.value = value;
        }
        
    }


    public String getServiceURL()
    {
        return serviceURL;
    }


    public void setServiceURL(String serviceURL)
    {
        this.serviceURL = serviceURL;
    }


    public String getRuleURL()
    {
        return ruleURL;
    }


    public void setRuleURL(String ruleURL)
    {
        this.ruleURL = ruleURL;
    }


    public int getAmountToCheckout()
    {
        return amountToCheckout;
    }


    public void setAmountToCheckout(int amountToCheckout)
    {
        this.amountToCheckout = amountToCheckout;
    }


    public int getLessReadSeconds()
    {
        return lessReadSeconds;
    }

    public String getPrivacyPoliciesURL()
    {
        return privacyPoliciesURL;
    }


    public void setPrivacyPoliciesURL(String privacyPoliciesURL)
    {
        this.privacyPoliciesURL = privacyPoliciesURL;
    }


    public String getCustomerServicePhone()
    {
        return customerServicePhone;
    }


    public void setCustomerServicePhone(String customerServicePhone)
    {
        this.customerServicePhone = customerServicePhone;
    }


    public boolean isVoiceSupported()
    {
        return voiceSupported;
    }


    public void setVoiceSupported(boolean voiceSupported)
    {
        this.voiceSupported = voiceSupported;
    }


    public void setLessReadSeconds(int lessReadSeconds)
    {
        this.lessReadSeconds = lessReadSeconds;
    }


    public String getSignMsg()
    {
        return signMsg;
    }


    public void setSignMsg(String signMsg)
    {
        this.signMsg = signMsg;
    }


    public String getAboutURL()
    {
        return aboutURL;
    }


    public void setAboutURL(String aboutURL)
    {
        this.aboutURL = aboutURL;
    }


    public String getHelpURL()
    {
        return helpURL;
    }


    public void setHelpURL(String helpURL)
    {
        this.helpURL = helpURL;
    }


    public Value[] getIncomings()
    {
        return incomings;
    }


    public void setIncomings(Value[] incomings)
    {
        this.incomings = incomings;
    }


    public Value[] getCareer()
    {
        return career;
    }


    public void setCareer(Value[] career)
    {
        this.career = career;
    }


    public Value[] getFavs()
    {
        return favs;
    }


    public void setFavs(Value[] favs)
    {
        this.favs = favs;
    }
    
    
}
