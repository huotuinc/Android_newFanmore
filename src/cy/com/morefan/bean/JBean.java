package cy.com.morefan.bean;

import java.io.Serializable;

public class JBean implements Serializable
{

    /**
     * @field:serialVersionUID:TODO
     * @since
     */

    private static final long serialVersionUID = 8137753696453182798L;

    private String title;

    private String type;

    private String data;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }

}
