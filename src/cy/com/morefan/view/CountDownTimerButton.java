package cy.com.morefan.view;

import android.os.CountDownTimer;
import android.widget.TextView;
import cy.com.morefan.R;

public class CountDownTimerButton extends CountDownTimer
{
    public interface CountDownFinishListener{
        void finish();
    }
    
    TextView view;
    String txt;
    String formatTxt;
    CountDownFinishListener finishListener=null;
    
    
    public CountDownTimerButton( TextView view , String formatTxt , String txt , long millisInFuture , CountDownFinishListener listener ) {                   
        super(millisInFuture, 1000 ); 
        this.view= view;
        this.formatTxt = formatTxt;
        this.txt = txt;
        this.view.setText(txt);
        this.view.setClickable(false);           
        //this.view.setBackgroundColor(Color.parseColor("#999999"));
        this.view.setBackgroundResource(R.drawable.btn_mark_gray);
        finishListener = listener;
    }

    @Override
    public void onTick(long millisUntilFinished)
    {
        // TODO Auto-generated method stub        
        String content = String.format( formatTxt , millisUntilFinished / 1000 );
        view.setText( content );    
    }

    @Override
    public void onFinish()
    {
        // TODO Auto-generated method stub
        view.setClickable(true);
        view.setText(txt); 
        //view.setBackgroundColor(Color.parseColor("#0096FF"));
        view.setBackgroundResource(R.drawable.btn_red_sel);
        if( finishListener!=null){
            finishListener.finish();
        }
    }
    
    public void Stop(){
        this.cancel();
    }

}
