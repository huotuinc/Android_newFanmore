package cy.com.morefan.bean;

import java.io.Serializable;
import java.util.List;

public class TaskData implements Serializable
{
    /**
     * @field:serialVersionUID:TODO
     * @since
     */

    private static final long serialVersionUID = -1353199148164456981L;

    private int taskId;

    private String title;

    private boolean isAlarm;

    private Long publishDate;

    private String pictureURL;

    private float maxBonus;

    private float reward;

    private String desc;

    private String type;

    private String status;

    private int luckies;

    private float last;

    private String contextURL;

    private String shareURL;

    private List<Question> questions;

    private long taskOrder;

    private int backTime;

    private String merchantTitle;

    /*
     * 距离上线的时间（秒）
     */
    private int timeToStart;

    /**
     * 判断预告任务是否已经上线啦，默认为false true为已经到期，上线啦
     */
    private boolean online;

    public boolean isOnline()
    {
        return online;
    }

    public void setOnline(boolean online)
    {
        this.online = online;
    }

    public int getTaskId()
    {
        return taskId;
    }

    public void setTaskId(int taskId)
    {
        this.taskId = taskId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public boolean isAlarm()
    {
        return isAlarm;
    }

    public void setAlarm(boolean isAlarm)
    {
        this.isAlarm = isAlarm;
    }

    public Long getPublishDate()
    {
        return publishDate;
    }

    public void setPublishDate(Long publishDate)
    {
        this.publishDate = publishDate;
    }

    public String getPictureURL()
    {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL)
    {
        this.pictureURL = pictureURL;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public int getLuckies()
    {
        return luckies;
    }

    public void setLuckies(int luckies)
    {
        this.luckies = luckies;
    }

    public String getMerchantTitle()
    {
        return merchantTitle;
    }

    public void setMerchantTitle(String merchantTitle)
    {
        this.merchantTitle = merchantTitle;
    }

    public String getContextURL()
    {
        return contextURL;
    }

    public float getMaxBonus()
    {
        return maxBonus;
    }

    public void setMaxBonus(float maxBonus)
    {
        this.maxBonus = maxBonus;
    }

    public float getReward()
    {
        return reward;
    }

    public void setReward(float reward)
    {
        this.reward = reward;
    }

    public float getLast()
    {
        return last;
    }

    public void setLast(float last)
    {
        this.last = last;
    }

    public void setContextURL(String contextURL)
    {
        this.contextURL = contextURL;
    }

    public String getShareURL()
    {
        return shareURL;
    }

    public void setShareURL(String shareURL)
    {
        this.shareURL = shareURL;
    }

    public List<Question> getQuestions()
    {
        return questions;
    }

    public void setQuestions(List<Question> questions)
    {
        this.questions = questions;
    }

    public long getTaskOrder()
    {
        return taskOrder;
    }

    public void setTaskOrder(long taskOrder)
    {
        this.taskOrder = taskOrder;
    }

    public int getBackTime()
    {
        return backTime;
    }

    public void setBackTime(int backTime)
    {
        this.backTime = backTime;
    }

    public int getTimeToStart()
    {
        return timeToStart;
    }

    public void setTimeToStart(int timeToStart)
    {
        this.timeToStart = timeToStart;
    }

}
