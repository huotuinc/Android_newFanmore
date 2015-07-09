package cy.com.morefan.guide;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import cy.com.morefan.R;
import cy.com.morefan.adapter.ViewPagerAdapter;

public class GuideActivity extends FragmentActivity
{

    private ViewPager mVPActivity;
    private P1Fragment mFragment1;
    private P2Fragment mFragment2;
    private P3Fragment mFragment3;
    private List<Fragment> mListFragment = new ArrayList<Fragment>();
    private PagerAdapter mPgAdapter;
    @Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        this.setContentView(R.layout.guide_ui);
        initView();
    }
    
    private void initView() {
        mVPActivity = (ViewPager) findViewById(R.id.vp_activity);
        mFragment1 = new P1Fragment();
        mFragment2 = new P2Fragment();
        mFragment3 = new P3Fragment();
        mListFragment.add(mFragment1);
        mListFragment.add(mFragment2);
        mListFragment.add(mFragment3);
        mPgAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                mListFragment);
        mVPActivity.setAdapter(mPgAdapter);
    }
    
}
