package com.doloop.www.util;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    //private final int PAGES = 3;
    private ArrayList<Fragment> mFragmentlist;


    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    
    public ViewPagerAdapter(FragmentManager fm,ArrayList<Fragment> Fragmentlist) {
        super(fm);
        this.mFragmentlist = Fragmentlist;
    }
    

    @Override
    public Fragment getItem(int position) {
        	 return mFragmentlist.get(position);
/*        switch (position) {
            case 0:
//            	if(usrAppsFrg == null)
//            	{
//            		usrAppsFrg = new UserAppsTabFragment();
//            		return usrAppsFrg;
//            	}
//            	else
//            	{
//            		return usrAppsFrg;
//            	}
            	//return new LoaderCustomSupport.AppListFragment();
            case 1:
            	if(sysAppsFrg == null)
            	{
            		sysAppsFrg = new SysAppsTabFragment();
            		return sysAppsFrg;
            	}
            	else
            	{
            		return sysAppsFrg;
            	}
            case 2:
            	if(allAppsFrg == null)
            	{
            		allAppsFrg = new LoaderCustomSupport.AppListFragment();
            		return allAppsFrg;
            	}
            	else
            	{
            		return allAppsFrg;
            	}
            	//return new LoaderCustomSupport.AppListFragment();
            default:
                throw new IllegalArgumentException("The item position should be less or equal to:" + PAGES);
        }*/
    }

  
    
    @Override
    public int getCount() {
        //return PAGES;
    	return mFragmentlist.size();
    }
}
