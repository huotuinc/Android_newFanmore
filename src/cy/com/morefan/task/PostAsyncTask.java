package cy.com.morefan.task;

import android.os.AsyncTask;
import cy.com.morefan.bean.HttpCode;
/**
 * 
 * @类名称：PostAsyncTask
 * @类描述：统一处理post请求
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年6月1日 下午3:16:08
 * @修改备注：
 * @version:
 */
public class PostAsyncTask extends AsyncTask<String, Void, HttpCode>
{

    @Override
    protected HttpCode doInBackground(String... params)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void onPreExecute()
    {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(HttpCode result)
    {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
    }
    

}
