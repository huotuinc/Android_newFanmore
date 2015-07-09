package cy.com.morefan.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import cy.com.morefan.R;
import cy.com.morefan.util.DensityUtils;

public class PhotoSelectView
{

    public enum SelectType{
        Camera, File
    }
    public interface OnPhotoSelectBackListener{
        void onPhotoSelectBack(SelectType type);
    }
    private OnPhotoSelectBackListener listener;
    private Dialog dialog;
    private View mainView;
    public PhotoSelectView(Context context, OnPhotoSelectBackListener listener){
        initView(context);
        this.listener = listener;
    }
    public void setOnPhotoSelectBackListener(OnPhotoSelectBackListener listener){
        this.listener = listener;
    }
    private void initView(Context context) {
        if(dialog == null){
            mainView = LayoutInflater.from(context).inflate(R.layout.pop_photo_select, null);
            dialog = new Dialog(context, R.style.PopDialog);
            dialog.setContentView(mainView);
             Window window = dialog.getWindow();
             window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
             window.setWindowAnimations(R.style.AnimationPop);  //添加动画

             //设置视图充满屏幕宽度
             WindowManager.LayoutParams lp = window.getAttributes();
             int[] size  = DensityUtils.getSize(context);
             lp.width = size[0]; //设置宽度
            // lp.height = (int) (size[1]*0.8);
             window.setAttributes(lp);
        }



        mainView.setFocusableInTouchMode(true);
        mainView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK ){
                     if(listener != null)
                         listener.onPhotoSelectBack(null);
                     dialog.dismiss();
                }
                return false;
            }
        });
        mainView.findViewById(R.id.btnCamera).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onPhotoSelectBack(SelectType.Camera);
                dialog.dismiss();
            }
        });
        mainView.findViewById(R.id.btnFile).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onPhotoSelectBack(SelectType.File);
                dialog.dismiss();
            }
        });
        mainView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onPhotoSelectBack(null);
                dialog.dismiss();
            }
        });
//      mainView.findViewById(R.id.layMain).setOnClickListener(new View.OnClickListener() {
//
//          @Override
//          public void onClick(View v) {
//              if(listener != null)
//                  listener.onPhotoSelectBack(null);
//              dialog.dismiss();
//          }
//      });


    }

    public void show(){
        dialog.show();
    }


}
