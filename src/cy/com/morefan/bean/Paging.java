package cy.com.morefan.bean;

public class Paging
{
    private Integer pagingSize;
    private String pagingTag;
    
    public String getPagingTag()
    {
        return pagingTag;
    }
    public void setPagingTag(String pagingTag)
    {
        this.pagingTag = pagingTag;
    }
    public Integer getPagingSize()
    {
        return pagingSize;
    }
    public void setPagingSize(Integer pagingSize)
    {
        this.pagingSize = pagingSize;
    }
    
}
