package com.example.bill.photostreamapp;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 2;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    // The arraylist of urls for the saved images
    private ArrayList<String> savedImages = new ArrayList<String>();

    private FeedActivityFragment feedActivityFragment = new FeedActivityFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    public ArrayList<String> getSavedImages() {
        return savedImages;
    }

//    public void setSavedImages(ArrayList<String> newList) {
//        savedImages = newList;
//    }

    public void removeImage(int position) {
        savedImages.remove(position);
        updateFeed();
    }

    public void addToSavedImages(String newImage) {
        savedImages.add(newImage);
        Log.d("NEW IMAGE: ", newImage);
    }

    public void updateFeed() {
        feedActivityFragment.updateWebView();
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents the SearchActivityFragment and FeedActivityFragment
     * objects, in sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // Search Fragment is the left one, Feed Fragment is the right one
            if (position == 0) {
                return new SearchActivityFragment();
            }
            return feedActivityFragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}