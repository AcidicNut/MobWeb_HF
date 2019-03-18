package hu.bme.aut.siris.placelist.placedetails.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import hu.bme.aut.siris.R;
import hu.bme.aut.siris.placelist.placedetails.fragments.NotePlaceFragment;
import hu.bme.aut.siris.placelist.placedetails.fragments.MainPlaceFragment;

public class PlacePagerAdapter extends FragmentPagerAdapter {
    private int NUM_PAGES = 2;
    private String TAG = "PlacePagerAdapter";
    private Context context;
    public PlacePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int i) {
        Log.d(TAG, "getItem");
        switch (i) {
            case 0:
                return new MainPlaceFragment();
            case 1:
                return new NotePlaceFragment();
            default:
                return new MainPlaceFragment();
        }
    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount");
        return NUM_PAGES;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch (position) {
            case 0:
                title = context.getString(R.string.details);
                break;
            case 1:
                title = context.getString(R.string.notes) ;
                break;
            default:
                title = "";
        }
        return title;
    }
}