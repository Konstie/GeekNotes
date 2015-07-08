package com.gnotes.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GeekNotesAdapter extends CursorAdapter {
    private static final int VIEW_TYPE_WITHOUT_EXTRA = 0;
    private static final int VIEW_TYPE_WITH_EXTRA = 1;

    private final int[] catIcons = {R.drawable.cat_book, R.drawable.cat_cartoon, R.drawable.cat_comics, R.drawable.cat_game,
            R.drawable.cat_music, R.drawable.cat_series, R.drawable.cat_movie};

    public GeekNotesAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    String extrainfo = "";

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

        extrainfo = cursor.getString(GeekNotesFragment.COL_GEEKNOTE_INFO);

        int viewType = getItemViewType(cursor.getPosition());

        switch (viewType) {
            case VIEW_TYPE_WITHOUT_EXTRA:
                //viewHolder.extraField.setTextSize(0);
                //viewHolder.extraInfo.setTextSize(0);
                //viewHolder.extraField.setText("");
                //viewHolder.extraInfo.setText("");
                viewHolder.extraInfo.setVisibility(View.GONE);
                viewHolder.extraField.setVisibility(View.GONE);
                break;
            case VIEW_TYPE_WITH_EXTRA:
                viewHolder.extraField.setText(cursor.getString(GeekNotesFragment.COL_GEEKNOTE_INFOTYPE));
                viewHolder.extraInfo.setText(cursor.getString(GeekNotesFragment.COL_GEEKNOTE_INFO));
                viewHolder.extraField.setVisibility(View.VISIBLE);
                viewHolder.extraField.setVisibility(View.VISIBLE);
                break;
        }

        String title = cursor.getString(GeekNotesFragment.COL_GEEKNOTE_TITLE);
        viewHolder.title.setText(title);

        String category = cursor.getString(GeekNotesFragment.COL_GEEKNOTE_CATEGORY);
        viewHolder.category.setText(category);

        String extraInfoType = cursor.getString(GeekNotesFragment.COL_GEEKNOTE_INFOTYPE);
        viewHolder.extraField.setText(extraInfoType + ":");

        extrainfo = cursor.getString(GeekNotesFragment.COL_GEEKNOTE_INFO);
        viewHolder.extraInfo.setText(this.extrainfo);

        if (category.equals("Книга")) {
            viewHolder.catIcon.setImageResource(catIcons[0]);
        } else if (category.equals("Мультфильм") || viewHolder.category.getText().equals("Мультсериал")
                || viewHolder.category.getText().equals("Аниме")) {
            viewHolder.catIcon.setImageResource(catIcons[1]);
        } else if (category.equals("Комикс")) {
            viewHolder.catIcon.setImageResource(catIcons[2]);
        } else if (category.equals("Игра")) {
            viewHolder.catIcon.setImageResource(catIcons[3]);
        } else if (category.equals("Муз. исполнитель")) {
            viewHolder.catIcon.setImageResource(catIcons[4]);
        } else if (category.equals("Сериал")) {
            viewHolder.catIcon.setImageResource(catIcons[5]);
        }  else if (category.equals("Фильм")) {
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
