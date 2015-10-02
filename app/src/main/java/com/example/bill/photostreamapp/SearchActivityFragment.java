package com.example.bill.photostreamapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment {

    private Button searchButton;
    private Button prevButton;
    private Button nextButton;
    private Button saveButton;
    private EditText inputText;
    private WebView webView;
    private ArrayList<String> imageURLs = new ArrayList<>();
    // Build the base url string without the search query
    private String baseURL = "https://www.googleapis.com/customsearch/v1" +
                             "?key=AIzaSyDZB0aUx8X9QuCBBYvKg3KJXodV0no2YIs" +
                             "&cx=001581967375476266624:g-yjubqpbso" +
                             "&searchType=image" +
                             "&q=";
    private int currentImagePos = 0;

    public SearchActivityFragment() {
    }

//    public interface OnDataPass {
//        public void onDataPass(String data)
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Setup the input text box
        inputText = (EditText) view.findViewById(R.id.searchInput);

        // Setup the WebView
        webView = (WebView) view.findViewById(R.id.webView);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        // Setup the buttons
        // searchButton
        searchButton = (Button) view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageURLs.clear();
                performSearch(inputText.getText().toString());
            }
        });
        // saveButton
        saveButton = (Button) view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add the current imageURL to MainActivity's SavedImages list
                String URLToSave = imageURLs.get(currentImagePos);
                ((MainActivity) getActivity()).addToSavedImages(URLToSave);
                ((MainActivity) getActivity()).updateFeed();
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
                if (currentImagePos < imageURLs.size()-1) {
                    currentImagePos++;
                }
                updateWebView();
            }
        });

        return view;
    }

    private void performSearch(String query) {
        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Handle spaces in the query
        if (query.contains(" ")) {
            query = query.replace(" ","%20");
        }

        String url = baseURL + query;

        // Request a JSON response from the url
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray images = null;
                        try {
                            // Get the JSON array of the images
                            images = response.getJSONArray("items");
                            // Add the urls of the images to imageURLs arraylist
                            for (int i = 0; i < images.length(); i++) {
                                imageURLs.add(images.getJSONObject(i).get("link").toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("Items in the JSON: ",imageURLs.toString());

                        // Update the WebView
//                        webView.loadUrl(imageURLs.get(currentImagePos));
                        updateWebView();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley Error: ","An error occurred in Volley");
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }

    private void updateWebView() {
        webView.loadUrl(imageURLs.get(currentImagePos));
    }
}
