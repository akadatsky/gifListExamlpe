package com.example.testGiphy;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

import java.util.ArrayList;
import java.util.List;

public class GifAdapter extends BaseAdapter implements ListPreloader.PreloadModelProvider<Api.GifResult> {

    private static final String TAG = "GifAdapter";

    private static final Api.GifResult[] EMPTY_RESULTS = new Api.GifResult[0];

    private final Activity activity;
    private DrawableRequestBuilder<Api.GifResult> requestBuilder;
    private ViewPreloadSizeProvider<Api.GifResult> preloadSizeProvider;

    private Api.GifResult[] results = EMPTY_RESULTS;

    public GifAdapter(Activity activity, DrawableRequestBuilder<Api.GifResult> requestBuilder,
                      ViewPreloadSizeProvider<Api.GifResult> preloadSizeProvider) {
        this.activity = activity;
        this.requestBuilder = requestBuilder;
        this.preloadSizeProvider = preloadSizeProvider;
    }

    public void setResults(Api.GifResult[] results) {
        if (results != null) {
            this.results = results;
        } else {
            this.results = EMPTY_RESULTS;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return results.length;
    }

    @Override
    public Api.GifResult getItem(int i) {
        return results[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.gif_list_item, parent, false);
        }

        final Api.GifResult result = results[position];
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "load result: " + result);
        }
        final ImageView gifView = (ImageView) convertView.findViewById(R.id.gif_view);
        gifView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("giphy_url", result.images.fixed_height_downsampled.url);
                clipboard.setPrimaryClip(clip);

                Intent fullscreenIntent = FullscreenActivity.getIntent(activity, result);
                activity.startActivity(fullscreenIntent);
            }
        });

        requestBuilder
                .load(result)
                .into(gifView);

        preloadSizeProvider.setView(gifView);

        return convertView;
    }

    @Override
    public List<Api.GifResult> getPreloadItems(int position) {
        List<Api.GifResult> items = new ArrayList<Api.GifResult>(1);
        items.add(getItem(position));
        return items;
    }

    @Override
    public GenericRequestBuilder getPreloadRequestBuilder(Api.GifResult item) {
        return requestBuilder.load(item);
    }
}