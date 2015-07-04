package com.gnotes.app;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.gnotes.app.data.GeekNotesContract;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeekNotesImdbService extends IntentService {

    private static final String TAG = "GeekNotes Wiki Service";

    public GeekNotesImdbService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String itemTitle = intent.getStringExtra("ITEM_TITLE");

        String imdbJsonStr = connectToImdb(itemTitle);
        getImdbStuff(itemTitle, imdbJsonStr);
    }

    private String connectToImdb(String itemTitle) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        StringBuilder buffer = new StringBuilder();

        String plot = "short";
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

        try {
            JSONObject jsonObject = new JSONObject(imdbJsonStr);
            String rating = jsonObject.getString(IMDB_RATING);
            String plot = jsonObject.getString(IMDB_PLOT);
            String poster = jsonObject.getString(IMDB_POSTER);

            String dbPlot = ((plot) != null && !plot.equals("")) ? plot : "";
            String dbRating = ((rating) != null && !rating.equals("N/A")) ? rating : "";
            String dbPoster = ((poster) != null && !poster.equals("") ? poster : "");

            Log.w("TAAAAAAAAAAAAAAAG Plot", plot);

            ContentValues noteValues = new ContentValues();
            noteValues.put(GeekNotesContract.GeekEntry.COLUMN_ARTICLE_IMDB_INFO, dbPlot);
            noteValues.put(GeekNotesContract.GeekEntry.COLUMN_ARTICLE_RANK, dbRating);
            noteValues.put(GeekNotesContract.GeekEntry.COLUMN_ARTICLE_POSTERLINK, dbPoster);

            getContentResolver().update(GeekNotesContract.GeekEntry.CONTENT_URI, noteValues,
                    GeekNotesContract.GeekEntry.COLUMN_TITLE + "=?", new String[] {originalTitle});
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String title = intent.getStringExtra("ITEM_TITLE");
            Intent sendIntent = new Intent(context, GeekNotesWikiService.class);
            sendIntent.putExtra("ITEM_TITLE", title);
            context.startService(sendIntent);
        }
    }
}
