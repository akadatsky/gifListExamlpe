package com.example.testGiphy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

/**
 * The primary activity in the Giphy sample that allows users to view trending animated GIFs from Giphy's api.
 */
public class MainActivity extends Activity implements Api.Monitor {

    private GifAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageView giphyLogoView = (ImageView) findViewById(R.id.giphy_logo_view);
        Glide.with(this)
                .load(R.raw.large_giphy_logo)
                .fitCenter()
                .into(giphyLogoView);

        ListView gifList = (ListView) findViewById(R.id.gif_list);

        DrawableRequestBuilder<Api.GifResult> gifItemRequest = Glide.with(this)
                .from(Api.GifResult.class)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .fitCenter();

        ViewPreloadSizeProvider<Api.GifResult> preloadSizeProvider = new ViewPreloadSizeProvider<Api.GifResult>();
        adapter = new GifAdapter(this, gifItemRequest, preloadSizeProvider);
        gifList.setAdapter(adapter);
        ListPreloader<Api.GifResult> preloader = new ListPreloader<Api.GifResult>(adapter, preloadSizeProvider, 2);
        gifList.setOnScrollListener(preloader);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Api.get().addMonitor(this);
        if (adapter.getCount() == 0) {
            Api.get().getTrending();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Api.get().removeMonitor(this);
    }

    @Override
    public void onSearchComplete(Api.SearchResult result) {
        adapter.setResults(result.data);
    }

}
