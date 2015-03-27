package com.botty.theme.next.blue.Fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.astuetz.PagerSlidingTabStrip;
import com.botty.theme.next.blue.R;

/**
 * Created by BottyIvan on 27/03/15.
 */
public class Fragment_Swiper extends Fragment{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    public PagerSlidingTabStrip tabs;

    private Drawable oldBackground = null;
    private int currentColor = 0xffffffff;

    public Toolbar toolbar;

    public Fragment_Swiper() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_swiper, container,
                false);


        tabs=(PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mViewPager=(ViewPager) view.findViewById(R.id.pager);
        mSectionsPagerAdapter=new SectionsPagerAdapter(getActivity().getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mViewPager.setPageMargin(pageMargin);

        tabs.setViewPager(mViewPager);
        tabs.setIndicatorColor(currentColor);
        tabs.setTextColor(currentColor);
        tabs.setIndicatorHeight(5);

        return view;
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private String[] titles={getActivity().getString(R.string.All_icons_tab),getActivity().getString(R.string.request_icons_tab)};
        private final int PAGES = titles.length ;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new iconpack();
                case 1:
                    return new IconHelp();
                default:
                    throw new IllegalArgumentException("The item position should be less or equal to:" + PAGES);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return PAGES;
        }
    }
}
