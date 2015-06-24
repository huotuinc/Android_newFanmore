package cy.com.morefan.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
/**
 * 
 * @类名称：MenuListView
 * @类描述：为了解决listView scrollView冲突
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年5月25日 下午7:23:24
 * @修改备注：
 * @version:
 */
public class MenuListView extends ListView
{

    public MenuListView(Context context)
    {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public MenuListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MenuListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    
}
