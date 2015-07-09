package cy.com.morefan.util;

public class StringUtils
{

    public static boolean isEmpty(String str)
    {
        if(null == str || "".equals(str.trim()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
