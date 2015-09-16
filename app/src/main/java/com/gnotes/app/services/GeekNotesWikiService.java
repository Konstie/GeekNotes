package com.gnotes.app.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.gnotes.app.ui.ItemArticleFragment;
import com.gnotes.app.R;
import com.gnotes.app.database.GeekNotesContract;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class GeekNotesWikiService extends IntentService {

    private static final String TAG = "GeekNotes Wiki Service";

    private String plot = "";

    public GeekNotesWikiService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String itemTitle = intent.getStringExtra("ITEM_TITLE");

        getWikiArticle(itemTitle, getJsonInfoFromWiki(itemTitle));

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ItemArticleFragment.ImdbReceiver.ACTION_IMDB_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(broadcastIntent);
    }

    private String getJsonInfoFromWiki(String itemTitle) {
        String format = "json";
        String action = "query";
        String prop = "extracts";

        final String WIKI_BASE_URL_RU = getString(R.string.url_wikipedia);

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

        String jsonInfo = ConnectionUtils.connect(wikiURI);

        return jsonInfo;
    }

    private void getWikiArticle(String originalTitle, String wikiArticleJsonStr) {
        final String GN_QUERY = "query";
        final String GN_PAGES = "pages";
        final String GN_PAGEID = "pageid";
        final String GN_EXTRACT = "extract";

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
                String tempPlot = nestedObject.getString(GN_EXTRACT);
                plot = ((tempPlot) != null && !tempPlot.equals("")) ? tempPlot
                        : "";
            }

            Log.w("Wikipedia Plot", plot);

            ContentValues noteValues = new ContentValues();
            noteValues.put(GeekNotesContract.GeekEntry.COLUMN_ARTICLE_INFO, plot);

            getContentResolver().update(GeekNotesContract.GeekEntry.CONTENT_URI, noteValues,
                    GeekNotesContract.GeekEntry.COLUMN_TITLE + "=?", new String[] {originalTitle});
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
