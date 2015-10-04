package com.example.bill.photostreamapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    // Number of screens to slide between
    private static final int NUM_PAGES = 2;

    // Screen slide initializers
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    private DatabaseHelper mDbHelper;

    private FeedActivityFragment feedActivityFragment = new FeedActivityFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        // Database
        mDbHelper = new DatabaseHelper(this);
    }

    public void updateFeed() {
        feedActivityFragment.updateFromDb(getmDbHelper());
    }

    public DatabaseHelper getmDbHelper() {
        return mDbHelper;
    }

    public void addImagetoDb(String url) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put(DatabaseHelper.FeedEntry.COLUMN_NAME_URL, url);

        Cursor cursor = db.rawQuery("Select * from " + DatabaseHelper.FeedEntry.TABLE_NAME +
                " where " + DatabaseHelper.FeedEntry.COLUMN_NAME_URL + " = \"" + url + "\"", null);
        if (cursor == null || cursor.getCount() <= 0) {
            // URL not already in database
            long newRowId;
            newRowId = db.insert(DatabaseHelper.FeedEntry.TABLE_NAME, null, vals);
            Log.d("Added", url);
        } else {
            Log.d("Already in database", url);
        }
        cursor.close();
        db.close();
    }

    public void deleteImageFromDb(String url) {
        Log.d("Deleted", url);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.FeedEntry.TABLE_NAME,
                DatabaseHelper.FeedEntry.COLUMN_NAME_URL + " = ?", new String[]{url});
        db.close();
        updateFeed();
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