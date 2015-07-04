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
import java.util.Iterator;

public class GeekNotesWikiService extends IntentService {

    private static final String TAG = "GeekNotes Wiki Service";

    String itemTitle;

    public GeekNotesWikiService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        itemTitle = intent.getStringExtra("ITEM_TITLE");

        String wikiArticleJsonStr = connectToWiki(itemTitle);
        try {
            getWikiArticle(wikiArticleJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String connectToWiki(String itemTitle) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();

        String format = "json";
        String action = "query";
        String prop = "extracts";

        try {
            final String WIKI_BASE_URL_RU = "https://ru.wikipedia.org/w/api.php?";
            final String WIKI_BASE_URL_EN = "https://en.wikipedia.org/w/api.php?";

            final String WIKI_PARAM_FORMAT = "format";
            final String WIKI_PARAM_ACTION = "action";
            final String WIKI_PARAM_PROP = "prop";
            final String WIKI_PARAM_EXINTRO = "exintro";
            final String WIKI_PARAM_EXPLAINTEXT = "explaintext";
            final String WIKI_PARAM_TITLES = "titles";

            Uri wikiURI = Uri.parse(WIKI_BASE_URL_RU)
                    .buildUpon()
                    .appendQueryParameter(WIKI_PARAM_FORMAT, format)
                    .appendQueryParameter(WIKI_PARAM_ACTION, action)
                    .appendQueryParameter(WIKI_PARAM_PROP, prop)
                    .appendQueryParameter(WIKI_PARAM_EXINTRO, "")
                    .appendQueryParameter(WIKI_PARAM_EXPLAINTEXT, "")
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
                buffer.append(line + "\n");
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


    private void getWikiArticle(String wikiArticleJsonStr) throws JSONException {
        final String GN_QUERY = "query";
        final String GN_PAGES = "pages";
        final String GN_PAGEID = "pageid";
        final String GN_EXTRACT = "extract";

        try {
            JSONObject jsonObject = new JSONObject(wikiArticleJsonStr);

            JSONObject query = jsonObject.getJSONObject(GN_QUERY);
            JSONObject pages = query.getJSONObject(GN_PAGES);
            JSONObject nestedObject = null;

            String plot = "";

            Iterator keys = pages.keys();

            while (keys.hasNext()) {
                String currentDynamicKey = (String) keys.next();
                nestedObject = pages.getJSONObject(currentDynamicKey);
                if (nestedObject.has(GN_PAGEID))
                    break;
            }

            if (nestedObject != null) {
                String tempPlot = nestedObject.getString(GN_EXTRACT);
                plot = ((tempPlot) != null && !tempPlot.equals("")) ? tempPlot
                        : "К сожалению, на Википедии ничего об этом не написано. Попробуйте" +
                        " воспользоваться функцией поиска в гугле или изменить название.";
            }

            Log.w("TAAAAAAAAAAAAAAAG Plot", plot);

            ContentValues noteValues = new ContentValues();
            noteValues.put(GeekNotesContract.GeekEntry.COLUMN_ARTICLE_INFO, plot);

            getContentResolver().update(GeekNotesContract.GeekEntry.CONTENT_URI, noteValues,
                    GeekNotesContract.GeekEntry.COLUMN_TITLE + "=?", new String[] {itemTitle});
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
