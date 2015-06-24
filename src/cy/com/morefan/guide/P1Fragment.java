package cy.com.morefan.guide;

import cy.com.morefan.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class P1Fragment extends Fragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.guide_item_ui_page1, container, false);
        return view;
    }

    
}
