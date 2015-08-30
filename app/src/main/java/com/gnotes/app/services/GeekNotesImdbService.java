package com.gnotes.app.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.gnotes.app.ItemArticleFragment;
import com.gnotes.app.data.GeekNotesContract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class GeekNotesImdbService extends IntentService {

    private static final String TAG = "GeekNotes IMDB Service";

    private String engTranlation;

    public GeekNotesImdbService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String itemTitle = intent.getStringExtra("ITEM_TITLE");

        engTranlation = itemTitle;

        String lingvoJsonStr = connectToWiki(itemTitle);
        getWikiTranslation(itemTitle, lingvoJsonStr);

        String imdbJsonStr = connectToImdb(engTranlation);
        getImdbStuff(itemTitle, imdbJsonStr);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ItemArticleFragment.ImdbReceiver.ACTION_IMDB_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(broadcastIntent);
    }


    private String connectToWiki(String itemTitle) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        StringBuilder buffer = new StringBuilder();

        String format = "json";
        String action = "query";
        String prop = "langlinks";
        String lllang = "en";
        String lllimit = "100";

        try {
            final String WIKI_BASE_URL_RU = "https://ru.wikipedia.org/w/api.php?";

            final String WIKI_PARAM_FORMAT = "format";
            final String WIKI_PARAM_ACTION = "action";
            final String WIKI_PARAM_PROP = "prop";
            final String WIKI_PARAM_LANG = "lllang";
            final String WIKI_PARAM_LIMIT = "lllimit";
            final String WIKI_PARAM_TITLES = "titles";

            Uri wikiURI = Uri.parse(WIKI_BASE_URL_RU)
                    .buildUpon()
                    .appendQueryParameter(WIKI_PARAM_FORMAT, format)
                    .appendQueryParameter(WIKI_PARAM_ACTION, action)
                    .appendQueryParameter(WIKI_PARAM_PROP, prop)
                    .appendQueryParameter(WIKI_PARAM_LANG, lllang)
                    .appendQueryParameter(WIKI_PARAM_LIMIT, lllimit)
                    .appendQueryParameter(WIKI_PARAM_TITLES, itemTitle)
                    .build();

            URL url = new URL(wikiURI.toString());

            Log.w(TAG, wikiURI.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                return "";
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                return "";
            }
        } catch (IOException exc) {
            Log.e(TAG, "Terrible I/O exception occured", exc);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException exc) {
                    Log.e(TAG, "Error closing URL connection", exc);
                }
            }
        }
        return buffer.toString();
    }

    private void getWikiTranslation(String originalTitle, String wikiArticleJsonStr) {
        final String GN_QUERY = "query";
        final String GN_PAGES = "pages";
        final String GN_PAGEID = "pageid";
        final String GN_TRANSLATIONS = "langlinks";
        final String GN_ENG = "*";

        try {
            JSONObject jsonObject = new JSONObject(wikiArticleJsonStr);

            JSONObject query = jsonObject.getJSONObject(GN_QUERY);
            JSONObject pages = query.getJSONObject(GN_PAGES);
            JSONObject nestedObject = null;

            Iterator keys = pages.keys(); // iterate through wiki-pages ids

            while (keys.hasNext()) {
                String currentDynamicKey = (String) keys.next();
                nestedObject = pages.getJSONObject(currentDynamicKey);
                if (nestedObject.has(GN_PAGEID))
                    break;
            }

            if (nestedObject != null) {
                JSONArray translations = nestedObject.getJSONArray(GN_TRANSLATIONS);
                JSONObject eng = translations.getJSONObject(0);
                engTranlation = eng.getString(GN_ENG);
            }

            Log.w(TAG, engTranlation);

            StringBuilder modifiedTranslation = new StringBuilder(engTranlation);

            if (engTranlation.contains(" (")) { // necessary for search query in imdb not to break
                modifiedTranslation.replace(engTranlation.indexOf(" ("), engTranlation.length(), "");
                Log.w("Translation service", modifiedTranslation.toString());
            }

            engTranlation = modifiedTranslation.toString();

            ContentValues noteValues = new ContentValues();
            noteValues.put(GeekNotesContract.GeekEntry.COLUMN_TRANSLATION, engTranlation);

            getContentResolver().update(GeekNotesContract.GeekEntry.CONTENT_URI, noteValues,
                    GeekNotesContract.GeekEntry.COLUMN_TITLE + "=?", new String[] {originalTitle});
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private String connectToImdb(String itemTitle) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        StringBuilder buffer = new StringBuilder();

        String plot = "full";
        String format = "json";

        try {
            final String IMDB_BASE_URL = "http://www.omdbapi.com/?";

            final String IMDB_PARAM_TITLE = "t";
            final String IMDB_PARAM_YEAR = "y";
            final String IMDB_PARAM_PLOT = "plot";
            final String IMDB_PARAM_FORMAT = "r";

            Uri imdbURI = Uri.parse(IMDB_BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(IMDB_PARAM_TITLE, itemTitle)
                    .appendQueryParameter(IMDB_PARAM_YEAR, "")
                    .appendQueryParameter(IMDB_PARAM_PLOT, plot)
                    .appendQueryParameter(IMDB_PARAM_FORMAT, format)
                    .build();

            URL url = new URL(imdbURI.toString());

            Log.w("Taaaaaaag IMDB", imdbURI.toString());

            Log.w(TAG, imdbURI.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            if (inputStream == null) {
                return "";
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                return "";
            }

        } catch (IOException exc) {
            Log.e(TAG, "Terrible I/O exception occured", exc);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException exc) {
                    Log.e(TAG, "Error closing URL connection", exc);
                }
            }
        }
        return buffer.toString();
    }

    private void getImdbStuff(String originalTitle, String imdbJsonStr) {

        final String IMDB_RATING = "imdbRating";
        final String IMDB_PLOT = "Plot";
        final String IMDB_POSTER = "Poster";
        final String IMDB_YEAR = "Year";
        final String IMDB_DIRECTOR = "Director";

        try {
            JSONObject jsonObject = new JSONObject(imdbJsonStr);
            String rating = jsonObject.getString(IMDB_RATING);
            String plot = jsonObject.getString(IMDB_PLOT);
            String poster = jsonObject.getString(IMDB_POSTER);
            String year = jsonObject.getString(IMDB_YEAR);
            String director = jsonObject.getString(IMDB_DIRECTOR);

            String dbPlot = ((plot) != null && !plot.equals("")) ? plot : "";
            String dbRating = ((rating) != null && !rating.equals("N/A")) ? rating : "";
            String dbPoster = ((poster) != null && !poster.equals("") ? poster : "");
            String dbYear = ((year) != null && !year.equals("") ? year : "");
            String dbDirector = ((director) != null && !director.equals("") && !director.equals("N/A") ? director : "");

            Log.w("TAAAAAAAAAAAAAAAG Plot", plot);

            ContentValues noteValues = new ContentValues();
            noteValues.put(GeekNotesContract.GeekEntry.COLUMN_ARTICLE_IMDB_INFO, dbPlot);
            noteValues.put(GeekNotesContract.GeekEntry.COLUMN_ARTICLE_RANK, dbRating);
            noteValues.put(GeekNotesContract.GeekEntry.COLUMN_ARTICLE_POSTERLINK, dbPoster);
            noteValues.put(GeekNotesContract.GeekEntry.COLUMN_ARTICLE_IMDB_YEAR, dbYear);
            noteValues.put(GeekNotesContract.GeekEntry.COLUMN_INFO, dbDirector);

            getContentResolver().update(GeekNotesContract.GeekEntry.CONTENT_URI, noteValues,
                    GeekNotesContract.GeekEntry.COLUMN_TITLE + "=?", new String[] {originalTitle});
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
