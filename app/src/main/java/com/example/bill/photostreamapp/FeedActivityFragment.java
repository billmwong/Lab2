package com.example.bill.photostreamapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class FeedActivityFragment extends Fragment {

    private Button prevButton;
    private Button nextButton;
    private Button deleteButton;
    private WebView webView;

    private int currentImagePos = 0;


    public FeedActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        // Setup the WebView
        webView = (WebView) view.findViewById(R.id.webView);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        // Setup the Buttons
        // deleteButton
        deleteButton = (Button) view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper helper = ((MainActivity) getActivity()).getmDbHelper();
                ((MainActivity) getActivity()).deleteImageFromDb(getCurrentURL(helper));
            }
        });
        // prevButton
        prevButton = (Button) view.findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Don't go out of bounds if we're at the beginning of the list
                if (currentImagePos > 0) {
                    currentImagePos--;
                }
                updateWebView();
            }
        });
        // nextButton
        nextButton = (Button) view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentImagePos++;
                updateWebView();
            }
        });

        return view;
    }

    private void updateWebView() {
        updateFromDb(((MainActivity) getActivity()).getmDbHelper());
    }

    private String getCurrentURL(DatabaseHelper helper) {
        SQLiteDatabase db = helper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                DatabaseHelper.FeedEntry._ID,
                DatabaseHelper.FeedEntry.COLUMN_NAME_URL
        };

        Cursor c = db.query(
                DatabaseHelper.FeedEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        if (c.getCount() == 0) {
            // No images saved, so show a blank page
            currentImagePos = 0;
            return "about:blank";
        }
        else {
            if (currentImagePos >= c.getCount()) {
                // If the position is out of range, set it as the last position
                currentImagePos = c.getCount() - 1;
            }
            // Return the URL
            c.moveToPosition(currentImagePos);
            return c.getString(1);
        }
    }

    // Updates the image feed by querying the SQL database and then displaying the proper image.
    public void updateFromDb(DatabaseHelper helper) {
        String currentImageURL = getCurrentURL(helper);
        webView.loadUrl(currentImageURL);
    }
}
