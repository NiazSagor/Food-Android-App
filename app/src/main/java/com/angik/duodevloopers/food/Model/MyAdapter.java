package com.angik.duodevloopers.food.Model;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.angik.duodevloopers.food.BurgerFragment;
import com.angik.duodevloopers.food.FastFoodFragment;
import com.angik.duodevloopers.food.RollFragment;
import com.angik.duodevloopers.food.SamusaFragment;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class MyAdapter extends FragmentPagerAdapter {

    private Context myContext;
    private int totalTabs;
    private ArrayList<String> mNames;//Names of the available items today sent from TodayActivity

    //Public default constructor
    public MyAdapter(Context context, FragmentManager fm, int totalTabs, ArrayList<String> names) {
        super(fm);
        this.myContext = context;
        this.totalTabs = totalTabs;
        this.mNames = names;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                /* Here we are using a different method to get the fragment that needs to be shown on that particular position
                 * That's why we are sending the position which is the current position of the view pager as argument
                 */
                return getFragment(position);
            case 1:
                return getFragment(position);
            case 2:
                return getFragment(position);
            case 3:
                return getFragment(position);
            case 4:
                return getFragment(position);
            case 5:
                return getFragment(position);
            case 6:
                return getFragment(position);
            case 7:
                return getFragment(position);
            default:
                return null;
        }
    }

    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }

    private Fragment getFragment(int position) {
        /* Here we are comparing the names by the positions
         * These names are in mNames ArrayList<String> and are in Alphabetical Order
         * Comparison suggests what names are available and which fragment needs to be shown, for every position
         * For example, if the 1st element in the ArrayList in Biscuits then we are returning the BiscuitFragment, not anything else
         * This way the fragments can be shown dynamically at run time
         */
        switch (mNames.get(position)) {
            case "Burger":
                return new BurgerFragment();
            case "Fast Foods":
                return new FastFoodFragment();
            case "Roll":
                return new RollFragment();
            case "Samusa":
                return new SamusaFragment();
            default:
                return null;
        }
    }
}
