package com.gnotes.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
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
import com.gnotes.app.data.GeekNotesContract;
import com.gnotes.app.data.GeekNotesDbHelper;
import com.gnotes.app.services.GeekNotesImdbService;
import com.gnotes.app.services.GeekNotesWikiService;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ItemArticleFragment extends Fragment
        implements ObservableScrollViewCallbacks {

    private static final String[] GEEKNOTES_COLUMNS = {
            GeekNotesContract.GeekEntry.TABLE_NAME + "." + GeekNotesContract.GeekEntry._ID,
            GeekNotesContract.GeekEntry.COLUMN_TITLE,
            GeekNotesContract.GeekEntry.COLUMN_CATEGORY,
            GeekNotesContract.GeekEntry.COLUMN_INFOTYPE,
            GeekNotesContract.GeekEntry.COLUMN_INFO,
            GeekNotesContract.GeekEntry.COLUMN_ARTICLE_INFO,
            GeekNotesContract.GeekEntry.COLUMN_ARTICLE_RANK,
            GeekNotesContract.GeekEntry.COLUMN_ARTICLE_IMDB_INFO,
            GeekNotesContract.GeekEntry.COLUMN_ARTICLE_POSTERLINK
    };

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
    private ObservableScrollView mScrollView;
    private ImageButton searchButton;
    private int mParallaxImageHeight;

    private String toolbarTitle = "";
    private String category, plot, imdbPlot, imdbRating, imdbPosterLink;

    private TextView tvInfo;
    private TextView tvRating;
    private TextView tvImdbPlot;
    private ImageView imgThumbnail;
    private GeekNotesDbHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        dbHelper = new GeekNotesDbHelper(getActivity());

        Intent intent = getActivity().getIntent();
        toolbarTitle = intent.getStringExtra("ITEM_TITLE");
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup parent,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_article_fragment, parent, false);

                ((ItemArticleActivity) getActivity()).setSupportActionBar((Toolbar) rootView.findViewById(R.id.toolbar_article));
        // ((ItemArticleActivity) getActivity()).getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.tardis_icon));
        ((ItemArticleActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ItemArticleActivity) getActivity()).getSupportActionBar().setTitle(toolbarTitle);

        imgThumbnail = (ImageView) rootView.findViewById(R.id.image);
        imgThumbnail.bringToFront();
        imgThumbnail.setImageResource(R.drawable.bg_tardis);

        mToolbarView = rootView.findViewById(R.id.toolbar_article);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.primary)));

        mScrollView = (ObservableScrollView) rootView.findViewById(R.id.scroll);
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
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.primary);
        float alpha = 1 - (float) Math.max(0, mParallaxImageHeight - scrollY) / mParallaxImageHeight;
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(imgThumbnail, scrollY / 2);
    }

    public void updateWikiInfo() {
        Log.w("Wiki articles service", "Updating info...");
        Intent alarmIntent = new Intent(getActivity(), GeekNotesWikiService.AlarmReceiver.class);
        alarmIntent.putExtra("ITEM_TITLE", toolbarTitle);
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pi);
    }

    public void updateImdbInfo() {
        Log.w("IMDB Service", "IMDB Service launched!");
        Intent alarmIntent = new Intent(getActivity(), GeekNotesImdbService.AlarmReceiver.class);
        alarmIntent.putExtra("ITEM_TITLE", toolbarTitle);
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pi);
    }

    @Override
    public void onResume() {
        super.onResume();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse("");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });

        Cursor cursor = dbHelper.getSpecificNote(toolbarTitle);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                plot = cursor.getString(COL_GEEKNOTE_ARTICLE_INFO);
                imdbPlot = (cursor.getString(COL_GEEKNOTE_ARTICLE_IMDB_INFO) != null)
                        ? cursor.getString(COL_GEEKNOTE_ARTICLE_IMDB_INFO) : "";
                imdbRating = (cursor.getString(COL_GEEKNOTE_ARTICLE_RANK) != null)
                        ? cursor.getString(COL_GEEKNOTE_ARTICLE_RANK) : "";
                imdbPosterLink = (cursor.getString(COL_GEEKNOTE_POSTERLINK) != null)
                        ? cursor.getString(COL_GEEKNOTE_POSTERLINK) : "";
            }
        }

        if (!imdbPosterLink.equals("")) {
            Log.w("IMDB Tag", imdbPosterLink);
            Picasso.with(getActivity())
                    .load(imdbPosterLink)
                    .into(imgThumbnail);
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                plot = cursor.getString(COL_GEEKNOTE_ARTICLE_INFO);
                category = cursor.getString(COL_GEEKNOTE_CATEGORY);
                if ((plot == null || !plot.equals("")) && (category.equals("Фильм") || category.equals("Сериал") ||
                        category.equals("Мультсериал") || category.equals("Мультфильм") ||
                        category.equals("Аниме"))) {
                    updateWikiInfo();
                    updateImdbInfo();
                } else if ((plot == null || !plot.equals("")) && (category.equals("Книга") || category.equals("Комикс") ||
                        category.equals("Муз. исполнитель") || category.equals("Игра"))) {
                    updateWikiInfo();
                }
            }
        }

        if (plot != null && !plot.equals("")) {
            tvInfo.setVisibility(View.VISIBLE);
            tvInfo.setText(plot);
        }

        if (!imdbPlot.equals("") && !imdbRating.equals("")) {
            tvImdbPlot.setVisibility(View.VISIBLE);
            tvRating.setVisibility(View.VISIBLE);
            tvRating.append(imdbRating);
            tvImdbPlot.append(imdbPlot);
        } else {
            tvImdbPlot.setVisibility(View.GONE);
            tvRating.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

}
