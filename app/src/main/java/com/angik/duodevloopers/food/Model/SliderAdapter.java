package com.angik.duodevloopers.food.Model;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.angik.duodevloopers.food.InfoFragment;
import com.angik.duodevloopers.food.InputOTPFragment;
import com.angik.duodevloopers.food.LogInFragment;
import com.angik.duodevloopers.food.SendOTPFragment;

public class SliderAdapter extends FragmentPagerAdapter {
    @SuppressWarnings({"FieldCanBeLocal", "CanBeFinal"})
    private static int NUM_ITEMS = 3;

    public SliderAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return new SendOTPFragment();
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return new InputOTPFragment();
            case 2: // Fragment # 1 - This will show SecondFragment
                return new InfoFragment();
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }

}
