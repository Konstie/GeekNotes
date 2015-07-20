package com.gnotes.app;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.gnotes.app.data.GeekNotesDbHelper;
import com.gnotes.app.services.GeekNotesImdbService;
import com.gnotes.app.services.GeekNotesWikiService;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;

public class ItemArticleFragment extends Fragment
        implements ObservableScrollViewCallbacks {

    static final int COL_GEEKNOTE_ID = 0;
    static final int COL_GEEKNOTE_TITLE = 1;
    static final int COL_GEEKNOTE_CATEGORY = 2;
    static final int COL_GEEKNOTE_INFOTYPE = 3;
    static final int COL_GEEKNOTE_INFO = 4;
    static final int COL_GEEKNOTE_ARTICLE_INFO = 5;
    static final int COL_GEEKNOTE_ARTICLE_RANK = 6;
    static final int COL_GEEKNOTE_ARTICLE_IMDB_INFO = 7;
    static final int COL_GEEKNOTE_POSTERLINK = 8;

    private View mToolbarView;
    private ImageButton searchButton;
    private int mParallaxImageHeight;

    private String mToolbarTitle = "";
    private String mCategory;
    private String mPlot;
    private String mImdbPlot;
    private String mImdbRating;
    private String mImdbPosterLink;

    private TextView tvInfo;
    private TextView tvRating;
    private TextView tvImdbPlot;
    private ImageView imgThumbnail;

    private WikiReceiver wikiReceiver;
    private ImdbReceiver imdbReceiver;
    private GeekNotesDbHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        dbHelper = new GeekNotesDbHelper(getActivity());

        Intent intent = getActivity().getIntent();
        mToolbarTitle = intent.getStringExtra("ITEM_TITLE");
        mCategory = intent.getStringExtra("ITEM_CAT");

        IntentFilter wikiFilter = new IntentFilter(WikiReceiver.ACTION_WIKI_RESP);
        wikiFilter.addCategory(Intent.CATEGORY_DEFAULT);
        wikiReceiver = new WikiReceiver();
        getActivity().registerReceiver(wikiReceiver, wikiFilter);

        IntentFilter imdbFilter = new IntentFilter(ImdbReceiver.ACTION_IMDB_RESP);
        imdbFilter.addCategory(Intent.CATEGORY_DEFAULT);
        imdbReceiver = new ImdbReceiver();
        getActivity().registerReceiver(imdbReceiver, imdbFilter);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup parent,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_article_fragment, parent, false);

        ((ItemArticleActivity) getActivity()).setSupportActionBar((Toolbar) rootView.findViewById(R.id.toolbar_article));
        ActionBar actionBar = ((ItemArticleActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            // actionBar.setLogo(getResources().getDrawable(R.drawable.tardis_icon));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mToolbarTitle);
        }

        imgThumbnail = (ImageView) rootView.findViewById(R.id.image);
        imgThumbnail.bringToFront();
        imgThumbnail.setImageResource(R.drawable.bg_tardis);

        mToolbarView = rootView.findViewById(R.id.toolbar_article);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.primary)));

        ObservableScrollView mScrollView = (ObservableScrollView) rootView.findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

        tvInfo = (TextView) rootView.findViewById(R.id.wikiInfo);
        tvRating = (TextView) rootView.findViewById(R.id.imdbRank);
        tvImdbPlot = (TextView) rootView.findViewById(R.id.imdbPlot);

        searchButton = (ImageButton) rootView.findViewById(R.id.fab);
        searchButton.invalidate();
        searchButton.bringToFront();

        return rootView;
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) { // handling header scroll
        int baseColor = getResources().getColor(R.color.primary);
        float alpha = 1 - (float) Math.max(0, mParallaxImageHeight - scrollY) / mParallaxImageHeight;
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(imgThumbnail, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {
        // nothing to do here
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        // nothing to do here
    }

    @Override
    public void onStart() {
        super.onStart();

        startFetchingInfo();
    }

    public class WikiReceiver extends BroadcastReceiver {
        public static final String ACTION_WIKI_RESP = "com.gnotes.app.action.IMDB_READ";

        @Override
        public void onReceive(Context context, Intent intent) {
            Cursor cursor = dbHelper.getSpecificNote(mToolbarTitle);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    mPlot = (cursor.getString(COL_GEEKNOTE_ARTICLE_INFO) != null) ?
                            cursor.getString(COL_GEEKNOTE_ARTICLE_INFO) : "";

                    if (!mPlot.equals("") && mPlot != null) {
                        tvInfo.setVisibility(View.VISIBLE);
                        tvInfo.setText(mPlot);
                    }
                }
            }
        }
    }

    public class ImdbReceiver extends BroadcastReceiver {
        public static final String ACTION_IMDB_RESP = "com.gnotes.app.action.IMDB_READ";

        @Override
        public void onReceive(Context context, Intent intent) {
            Cursor cursor = dbHelper.getSpecificNote(mToolbarTitle);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    mImdbPlot = (cursor.getString(COL_GEEKNOTE_ARTICLE_IMDB_INFO) != null)
                            ? cursor.getString(COL_GEEKNOTE_ARTICLE_IMDB_INFO) : "";
                    mImdbRating = (cursor.getString(COL_GEEKNOTE_ARTICLE_RANK) != null)
                            ? cursor.getString(COL_GEEKNOTE_ARTICLE_RANK) : "";
                    mImdbPosterLink = (cursor.getString(COL_GEEKNOTE_POSTERLINK) != null)
                            ? cursor.getString(COL_GEEKNOTE_POSTERLINK) : "";

                    if (!mImdbPlot.equals("") && mImdbPlot != null) {
                        tvImdbPlot.setVisibility(View.VISIBLE);
                        tvImdbPlot.setText("Сюжет (англ.): " + mImdbPlot);
                        tvRating.setVisibility(View.VISIBLE);
                        tvRating.setText("Рейтинг IMDB: " + mImdbRating);
                    }
                }
            }

            if (!(mImdbPosterLink.equals("") || mImdbPosterLink == null)) {
                Log.w("IMDB Tag", mImdbPosterLink);
                Picasso.with(getActivity())
                        .load(mImdbPosterLink)
                        .into(imgThumbnail);
            } else {
                imgThumbnail.setImageResource(R.drawable.bg_tardis);
            }
        }
    }

    private void updateWikiInfo() { // starts connection to wikipedia
        Intent wikiArticleIntent = new Intent(getActivity(), GeekNotesWikiService.class);
        wikiArticleIntent.putExtra("ITEM_TITLE", mToolbarTitle);
        getActivity().startService(wikiArticleIntent);
    }

    private void updateImdbInfo() { // starts connection to IMDB servers
        Intent imdbIntent = new Intent(getActivity(), GeekNotesImdbService.class);
        imdbIntent.putExtra("ITEM_TITLE", mToolbarTitle);
        getActivity().startService(imdbIntent);
    }

    private void startFetchingInfo() {
        if ((mCategory.equals("Фильм") || mCategory.equals("Сериал") ||
                mCategory.equals("Мультсериал") || mCategory.equals("Мультфильм") ||
                mCategory.equals("Аниме"))) {
            if (mImdbPlot == null) {
                updateImdbInfo();
            }

            if (mPlot == null) {
                updateWikiInfo();
            }
        } else if ((mPlot == null || !mPlot.equals("")) && (mCategory.equals("Книга") || mCategory.equals("Комикс") ||
                mCategory.equals("Муз. исполнитель") || mCategory.equals("Игра"))) {
            if (mPlot == null) {
                updateWikiInfo();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse("http://www.google.com.my/search?q=" + mToolbarTitle);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(wikiReceiver);
        getActivity().unregisterReceiver(imdbReceiver);

        super.onDestroy();
    }
}