package cy.com.morefan.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cy.com.morefan.R;

public class NoticeDialog extends Dialog implements OnClickListener
{

    private Context context;

    private TextView dialogTileView;

    private TextView dialogNoticeView;

    private String dialogTile;

    private String dialogNotice;

    private ImageView operationBtn;

    private LeaveMyDialogListener listener;

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub

        listener.onClick(v);
    }

    public NoticeDialog(Context context)
    {
        super(context);
        this.context = context;
    }

    public NoticeDialog(Context context, int theme)
    {
        super(context, theme);
        this.context = context;
    }

    public NoticeDialog(Context context, int theme, String dialogTile,
            String dialogNotice, LeaveMyDialogListener listener)
    {
        super(context, theme);
        this.context = context;
        this.dialogTile = dialogTile;
        this.dialogNotice = dialogNotice;
        this.listener = listener;
    }

    public interface LeaveMyDialogListener
    {
        public void onClick(View view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_self_ui);

        dialogTileView = (TextView) this.findViewById(R.id.titleText);
        dialogNoticeView = (TextView) this.findViewById(R.id.dialogText);

        dialogTileView.setText(dialogTile);
        dialogNoticeView.setText(dialogNotice);

        operationBtn = (ImageView) this.findViewById(R.id.operationBtn);

        operationBtn.setOnClickListener(this);
    }

}
