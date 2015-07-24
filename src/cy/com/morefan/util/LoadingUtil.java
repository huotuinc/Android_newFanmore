package cy.com.morefan.util;

import cy.com.morefan.view.WindowProgress;
import android.app.Activity;
import android.os.Handler;

/**
 * 
 * @类名称：LoadingUtil
 * @类描述：加载界面工具类
 * @创建人：aaron
 * @修改人：
 * @修改时间：2015年7月22日 下午8:22:25
 * @修改备注：
 * @version:
 */
public class LoadingUtil
{

    private Activity aty;
    private WindowProgress progress;
    public Handler handler = new Handler();
    
    public LoadingUtil(Activity aty)
    {
        // TODO Auto-generated constructor stub
        this.aty = aty;
    }
    
    public void showProgress() {
        //网络访问前先检测网络是否可用
        if(!Util.isConnect(aty)){
            ToastUtils.showLongToast(aty , "无网络或当前网络不可用!");
            return;
        }

        if(progress == null){
            progress = new WindowProgress(aty);
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                if(!aty.isFinishing())
                    try {
                        progress.showProgress();
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }

            }
        });

    }
    public void dismissProgress(){
        if(progress == null)
            return;

        handler.post(new Runnable() {
            @Override
            public void run() {
                progress.dismissProgress();
            }
        });


    }
}
