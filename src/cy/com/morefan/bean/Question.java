package cy.com.morefan.bean;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable
{

    
     /**
     * @field:serialVersionUID:TODO
     * @since
     */
    
    private static final long serialVersionUID = 619139761366966799L;
    
    private int qid;
    /*
     * 题目正文
     */
    private String context;
    /*
     * 展示答案的顺序 app端需要进行乱序；只提交答案的ID而非序号;只有需要提供选择才会有这个字段
     */
    private List<Answers> answers;
    /*
     * 正确答案
     */
    private String correntAid;
    /*
     * 报名类专用字段名，比如电话，姓名
     */
    private String fieldName;
    /*
     * 报名类专用字段正则表达式，只有通过正
     */
    private String fieldPattern;
    /*
     * 答案外取类(游戏类):游戏URL,答题 报名类 无效字段
     */
    private String relexUrl;
    /*
     * 相关图片URL
     */
    private String imageUrl;
    
    public int getQid()
    {
        return qid;
    }
    public void setQid(int qid)
    {
        this.qid = qid;
    }
    public String getContext()
    {
        return context;
    }
    public void setContext(String context)
    {
        this.context = context;
    }
    public List<Answers> getAnswers()
    {
        return answers;
    }
    public void setAnswers(List<Answers> answers)
    {
        this.answers = answers;
    }
    
    public String getCorrentAid()
    {
        return correntAid;
    }
    public void setCorrentAid(String correntAid)
    {
        this.correntAid=correntAid;
    }   
    
    public String getFieldName(){
        return this.fieldName;
    }
    public void setFieldName(String fieldName){
        this.fieldName=fieldName;
    }
    public String getFieldPattern()
    {
        return fieldPattern;
    }
    public void setFieldPattern(String fieldPattern)
    {
        this.fieldPattern = fieldPattern;
    }
    public String getRelexUrl()
    {
        return relexUrl;
    }
    public void setRelexUrl(String relexUrl)
    {
        this.relexUrl = relexUrl;
    }
    public String getImageUrl()
    {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }
   
}
