package cy.com.morefan.guide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import cy.com.morefan.MainActivity;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.Util;

public class P4Fragment extends Fragment
{
    public MyApplication application;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        application = (MyApplication) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.guide_item_ui_page4, container, false);

        return view;
    }
}
