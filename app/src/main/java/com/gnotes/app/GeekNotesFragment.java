package com.gnotes.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.gnotes.app.data.GeekNotesContract;
import com.gnotes.app.data.GeekNotesDbHelper;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class GeekNotesFragment extends Fragment {

    private GeekNotesDbHelper dbHelper;
    private GeekNotesAdapter adapter;

    private ActionBar toolbar;
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

        toolbar = ((GeekNotesActivity) getActivity()).getSupportActionBar();

        listView = (ListView) rootView.findViewById(R.id.items_list);

        setTargetFragment(GeekNotesFragment.this, 0);
        listView.setAdapter(adapter);

        listView.setDividerHeight(0);
        listView.setDivider(null);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        final SwipeToDismissTouchListener<ListViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new ListViewAdapter(listView),
                        new SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>() {
                            @Override
                            public boolean canDismiss(int i) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListViewAdapter listViewAdapter, int i) {
                                dbHelper.deleteByID(i);
                            }
                        }
                );

        listView.setOnTouchListener(touchListener);
        listView.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = ((TextView) view.findViewById(R.id.name)).getText().toString();
                if (touchListener.existPendingDismisses()) {
                    touchListener.undoPendingDismiss();
                } else {
                    Toast.makeText(getActivity(), title + " removed!", Toast.LENGTH_SHORT).show();
                }
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

    private class ToolbarSpinnerAdapter extends BaseAdapter {

        private List<String> mItems = new ArrayList<>();

        public void addItems(List<String> catItems) {
            mItems.addAll(catItems);
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getDropDownView(int position, View view, ViewGroup viewGroup) {
            if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
                view = getActivity().getLayoutInflater().inflate(R.layout.toolbar_main_spinner_item_dropdown, viewGroup, false);
                view.setTag("DROPDOWN");
            }
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getTitle(position));

            return view;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
                view = getActivity().getLayoutInflater().inflate(R.layout.
                        toolbar_main_spinner_item, parent, false);
                view.setTag("NON_DROPDOWN");
            }
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getTitle(position));
            return view;
        }

        private String getTitle(int position) {
            return position >= 0 && position < mItems.size() ? mItems.get(position) : "Все";
        }
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