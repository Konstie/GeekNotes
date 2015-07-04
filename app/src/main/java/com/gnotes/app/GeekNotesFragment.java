package com.gnotes.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.gnotes.app.data.GeekNotesContract;
import com.gnotes.app.data.GeekNotesDbHelper;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

public class GeekNotesFragment extends Fragment {

    static final int COL_GEEKNOTE_ID = 0;
    static final int COL_GEEKNOTE_TITLE = 1;
    static final int COL_GEEKNOTE_CATEGORY = 2;
    static final int COL_GEEKNOTE_INFOTYPE = 3;
    static final int COL_GEEKNOTE_INFO = 4;
    static final int COL_GEEKNOTE_ARTICLE_INFO = 5;
    static final int COL_GEEKNOTE_ARTICLE_RANK = 6;
    static final int COL_GEEKNOTE_ARTICLE_IMDB_INFO = 7;

    private GeekNotesDbHelper dbHelper;
    private GeekNotesAdapter adapter;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup parent,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_geeknotes, parent, false);

        // ((GeekNotesActivity) getActivity()).getSupportActionBar().setTitle("Hehehe");

        listView = (ListView) rootView.findViewById(R.id.items_list);

        setTargetFragment(GeekNotesFragment.this, 0);
        listView.setAdapter(adapter);

        listView.setDividerHeight(0);
        listView.setDivider(null);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                // TODO: open new activity with details fetched from the Internet
                String title = ((TextView) view.findViewById(R.id.name)).getText().toString();
                Intent intent = new Intent(getActivity(), ItemArticleActivity.class);
                intent.putExtra("ITEM_TITLE", title);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.attachToListView(listView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newNoteIntent = new Intent(getActivity(), NewNoteActivity.class);
                startActivity(newNoteIntent);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        dbHelper = new GeekNotesDbHelper(getActivity());
        adapter = new GeekNotesAdapter(getActivity(), dbHelper.getAllData(), 0);
        adapter.changeCursor(dbHelper.getAllData());
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }
}