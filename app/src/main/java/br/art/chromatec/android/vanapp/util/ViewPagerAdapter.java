package br.art.chromatec.android.vanapp.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chromatec on 15/12/2016.
 */
public class ViewPagerAdapter
        extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList;
    private final List<String> mFragmentTitleList;

    public ViewPagerAdapter(FragmentManager paramFragmentManager) {
        super(paramFragmentManager);
        this.mFragmentList = new ArrayList();
        this.mFragmentTitleList = new ArrayList();
    }

    public void addFragment(Fragment paramFragment, String paramString) {
        this.mFragmentList.add(paramFragment);
        this.mFragmentTitleList.add(paramString);
    }

    public int getCount() {
        return this.mFragmentList.size();
    }

    public Fragment getItem(int paramInt) {
        return this.mFragmentList.get(paramInt);
    }

    public CharSequence getPageTitle(int position) {
        return this.mFragmentTitleList.get(position);
    }
}
