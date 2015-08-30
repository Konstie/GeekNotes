package com.gnotes.app.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.gnotes.app.R;

import java.util.ArrayList;
import java.util.Arrays;

public class GeekNotesAdapter extends CursorAdapter {
    private static final int VIEW_TYPE_WITHOUT_EXTRA = 0;
    private static final int VIEW_TYPE_WITH_EXTRA = 1;

    static final int COL_GEEKNOTE_ID = 0;
    static final int COL_GEEKNOTE_TITLE = 1;
    static final int COL_GEEKNOTE_CATEGORY = 2;
    static final int COL_GEEKNOTE_INFOTYPE = 3;
    static final int COL_GEEKNOTE_INFO = 4;
    static final int COL_GEEKNOTE_ARTICLE_INFO = 5;
    static final int COL_GEEKNOTE_ARTICLE_RANK = 6;
    static final int COL_GEEKNOTE_ARTICLE_IMDB_INFO = 7;
    static final int COL_GEEKNOTE_POSTERLINK = 8;

    private final int[] catIcons = {R.mipmap.cat_book, R.mipmap.cat_cartoon, R.mipmap.cat_comics, R.mipmap.cat_game,
            R.mipmap.cat_music, R.mipmap.cat_series, R.mipmap.cat_movie};

    private String extrainfo = "";

    public GeekNotesAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.note_item_card;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        extrainfo = cursor.getString(COL_GEEKNOTE_INFO);

        int viewType = getItemViewType(cursor.getPosition());

        switch (viewType) {
            case VIEW_TYPE_WITHOUT_EXTRA:
                viewHolder.extraInfo.setVisibility(View.GONE);
                viewHolder.extraField.setVisibility(View.GONE);
                break;
            case VIEW_TYPE_WITH_EXTRA:
                viewHolder.extraField.setText(cursor.getString(COL_GEEKNOTE_INFOTYPE));
                viewHolder.extraInfo.setText(cursor.getString(COL_GEEKNOTE_INFO));
                viewHolder.extraField.setVisibility(View.VISIBLE);
                viewHolder.extraInfo.setVisibility(View.VISIBLE);
                break;
        }

        String title = cursor.getString(COL_GEEKNOTE_TITLE);
        viewHolder.title.setText(title);

        String category = cursor.getString(COL_GEEKNOTE_CATEGORY);
        viewHolder.category.setText(category);

        String extraInfoType = cursor.getString(COL_GEEKNOTE_INFOTYPE);
        viewHolder.extraField.setText(extraInfoType + ":");

        extrainfo = cursor.getString(COL_GEEKNOTE_INFO);
        viewHolder.extraInfo.setText(this.extrainfo);

        if (category.equals("Книга") || category.equals("Book")) {
            viewHolder.catIcon.setImageResource(catIcons[0]);
        } else if (category.equals("Мультфильм") || category.equals("Мультсериал") ||
                category.equals("Аниме") || category.equals("Cartoon") ||
                category.equals("Cartoon series") || category.equals("Anime")) {
            viewHolder.catIcon.setImageResource(catIcons[1]);
        } else if (category.equals("Комикс") || category.equals("Comics") ||
                category.equals("Манга") || category.equals("Manga")) {
            viewHolder.catIcon.setImageResource(catIcons[2]);
        } else if (category.equals("Игра") || category.equals("Game")) {
            viewHolder.catIcon.setImageResource(catIcons[3]);
        } else if (category.equals("Муз. исполнитель") || category.equals("Music artist")) {
            viewHolder.catIcon.setImageResource(catIcons[4]);
        } else if (category.equals("Сериал") || category.equalsIgnoreCase("TV series")) {
            viewHolder.catIcon.setImageResource(catIcons[5]);
        }  else if (category.equals("Фильм") || category.equals("Movie")) {
            viewHolder.catIcon.setImageResource(catIcons[6]);
        }
    }

    public static class ViewHolder {
        public final TextView title;
        public final TextView category;
        public final TextView extraField;
        public final TextView extraInfo;
        public final ImageView catIcon;

        public ViewHolder(View view) {
            title = (TextView) view.findViewById(R.id.name);
            category = (TextView) view.findViewById(R.id.category);
            extraField = (TextView) view.findViewById(R.id.extra_type);
            extraInfo = (TextView) view.findViewById(R.id.extra_info);
            catIcon = (ImageView) view.findViewById(R.id.imgCat);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (extrainfo.equals("")) ? VIEW_TYPE_WITHOUT_EXTRA : VIEW_TYPE_WITH_EXTRA;
    }
}
