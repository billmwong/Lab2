package com.example.bill.photostreamapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
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

//    private ArrayList<String> imageURLs = new ArrayList<>();
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
        // saveButton
        deleteButton = (Button) view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).removeImage(currentImagePos);
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
                // Don't go out of bounds if we're at the end of the list
                ArrayList<String> savedImages = ((MainActivity) getActivity()).getSavedImages();
                if (currentImagePos < savedImages.size()-1) {
                    currentImagePos++;
                }
                updateWebView();
            }
        });

        return view;
    }

    public void updateWebView() {
        // Retrieve the savedImages from the MainActivity
        ArrayList<String> savedImages = ((MainActivity) getActivity()).getSavedImages();
        if (savedImages.isEmpty()) {
            webView.loadUrl("about:blank");
            currentImagePos = 0;
        }
        else {
            if (currentImagePos > savedImages.size()-1) {
                // If the last image was just deleted, go back a position
                currentImagePos--;
            }
            webView.loadUrl(savedImages.get(currentImagePos));
        }
    }
}
