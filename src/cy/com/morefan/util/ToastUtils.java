package cy.com.morefan.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtils
{

    public static void showShortToast(Context context, String showMsg)
    {
        Toast t = Toast.makeText(context, showMsg, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER, 0,0);
        t.show();       
    }

    public static void showLongToast(Context context, String showMsg)
    {
        Toast t = Toast.makeText(context, showMsg, Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER,0,0);
        t.show();
    }
}
