package cy.com.morefan.bean;

public class FaqsData extends BaseData implements IBaseData
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = 6730204180339448277L;
    
    private String faqsId; //问题编号
    private String faqsTitle;//问题标题
    private String faqsImageUrl;//问题图片url地址
    private String faqsCon;//问题内容
    private String faqsAnswer;//问题的答案
    public String getFaqsId()
    {
        return faqsId;
    }
    public void setFaqsId(String faqsId)
    {
        this.faqsId = faqsId;
    }
    public String getFaqsTitle()
    {
        return faqsTitle;
    }
    public void setFaqsTitle(String faqsTitle)
    {
        this.faqsTitle = faqsTitle;
    }
    public String getFaqsImageUrl()
    {
        return faqsImageUrl;
    }
    public void setFaqsImageUrl(String faqsImageUrl)
    {
        this.faqsImageUrl = faqsImageUrl;
    }
    public String getFaqsCon()
    {
        return faqsCon;
    }
    public void setFaqsCon(String faqsCon)
    {
        this.faqsCon = faqsCon;
    }
    public String getFaqsAnswer()
    {
        return faqsAnswer;
    }
    public void setFaqsAnswer(String faqsAnswer)
    {
        this.faqsAnswer = faqsAnswer;
    }
    
    

}
