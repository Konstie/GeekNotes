package com.gnotes.app;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.gnotes.app.adapters.GeekNotesAdapter;
import com.gnotes.app.data.GeekNotesDbHelper;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeekNotesFragment extends Fragment {

    private static final boolean ARCHIVE_STATE_FLAG = false;

    private Resources resources;
    private int listCurrentPosition;

    private List<String> filterCats;
    private String mCategory;

    private GeekNotesDbHelper dbHelper;
    private GeekNotesAdapter adapter;

    private Spinner filterSpinner;
    private SwipeMenuListView listView;

    private TextView mTextField;
    private EditText mEditTitle;
    private EditText mEditInfo;
    private Spinner mSpinnerEditCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        resources = getResources();
        mCategory = resources.getString(R.string.cats_all);

        filterCats = new ArrayList<>(Arrays.asList(resources.getStringArray(R.array.categories)));
        filterCats.add(0, resources.getString(R.string.cats_all));
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup parent,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_geeknotes, parent, false);

        final Toolbar toolbar = (Toolbar) (getActivity()).findViewById(R.id.toolbar);
        ((GeekNotesActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setLogo(R.mipmap.tardis_icon);

        View spinnerContainer = LayoutInflater.from(getActivity()).inflate(R.layout.toolbar_main_spinner, toolbar, false);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        toolbar.addView(spinnerContainer, lp);
        ToolbarSpinnerAdapter spinnerAdapter = new ToolbarSpinnerAdapter();
        spinnerAdapter.addItems(filterCats);

        if (savedInstanceState != null) {
            mCategory = savedInstanceState.getString("CATEGORY");
        }

        filterSpinner = (Spinner) spinnerContainer.findViewById(R.id.toolbar_spinner);
        filterSpinner.setAdapter(spinnerAdapter);

        listView = (SwipeMenuListView) rootView.findViewById(R.id.items_list);

        setTargetFragment(GeekNotesFragment.this, 0);

        listView.setDividerHeight(0);
        listView.setDivider(null);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        dbHelper = new GeekNotesDbHelper(getActivity());

        Cursor cursor;
        if (mCategory.equals(resources.getString(R.string.cats_all))) {
            cursor = dbHelper.getAllData(ARCHIVE_STATE_FLAG);
        } else {
            cursor = dbHelper.getItemsByCategory(mCategory, ARCHIVE_STATE_FLAG);
        }
        cursor.requery();

        adapter = new GeekNotesAdapter(getActivity(), cursor, 0);
        adapter.changeCursor(cursor);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCategory = filterSpinner.getSelectedItem().toString();
                if (mCategory.equals(filterCats.get(0))) {
                    adapter = new GeekNotesAdapter(getActivity(), dbHelper.getAllData(false), 0);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    toolbar.setBackgroundColor(getResources().getColor(R.color.primary_dark));
                } else {
                    adapter = new GeekNotesAdapter(getActivity(), dbHelper.getItemsByCategory(
                            mCategory, ARCHIVE_STATE_FLAG), 0
                    );
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    toolbar.setBackgroundColor(getResources().getColor(R.color.primary_dark));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = ((TextView) view.findViewById(R.id.name)).getText().toString();
                String category = ((TextView) view.findViewById(R.id.category)).getText().toString();
                Intent intent = new Intent(getActivity(), ItemArticleActivity.class);
                intent.putExtra("ITEM_TITLE", title);
                intent.putExtra("ITEM_CAT", category);
                startActivity(intent);
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu swipeMenu) {
                SwipeMenuItem editItem = new SwipeMenuItem(getActivity().getApplicationContext());
                editItem.setBackground(new ColorDrawable(Color.rgb(0xFF, 0xA5, 0x00)));
                editItem.setWidth(100);
                editItem.setIcon(R.mipmap.ic_action_edit);
                swipeMenu.addMenuItem(editItem);

                SwipeMenuItem removeItem = new SwipeMenuItem(
                        getActivity().getApplicationContext()
                );
                removeItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                removeItem.setWidth(100);
                removeItem.setIcon(R.mipmap.ic_action_delete);
                swipeMenu.addMenuItem(removeItem);
            }
        };

        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int i, SwipeMenu swipeMenu, int index) {
                TextView itemText = (TextView) listView.getChildAt(i - listView.getFirstVisiblePosition()).findViewById(R.id.name);
                final String itemTitle = itemText.getText().toString();
                switch (index) {
                    case 0: // item edit dialog call
                        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                                .title(resources.getString(R.string.dialog_change_note))
                                .customView(R.layout.change_item_dialog, true)
                                .positiveText(resources.getString(R.string.dialog_save))
                                .negativeText(resources.getString(R.string.dialog_cancel))
                                .positiveColor(Color.WHITE)
                                .negativeColor(Color.WHITE)
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        String title, info, category;
                                        title = mEditTitle.getText().toString();
                                        info = mEditInfo.getText().toString();
                                        category = mSpinnerEditCategory.getSelectedItem().toString();
                                        dbHelper.updateData(itemTitle, title, info, category);

                                        if (filterSpinner.getSelectedItem().toString().equals(filterCats.get(0))) {
                                            adapter.changeCursor(dbHelper.getAllData(ARCHIVE_STATE_FLAG));
                                        } else {
                                            adapter.changeCursor(dbHelper.getItemsByCategory(
                                                    filterSpinner.getSelectedItem().toString(), ARCHIVE_STATE_FLAG)
                                            );
                                        }
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {

                                    }
                                }).build();

                        mEditTitle = (EditText) dialog.getCustomView().findViewById(R.id.edit_title);
                        mEditTitle.setText(itemTitle);

                        mTextField = (TextView) dialog.getCustomView().findViewById(R.id.edit_fieldname);
                        TextView itemExtraField = (TextView) listView.getChildAt(i -
                                listView.getFirstVisiblePosition()).findViewById(R.id.extra_type);
                        String extraField = itemExtraField.getText().toString();
                        if (extraField.equals("")) {
                            mTextField.setText("");
                        } else {
                            mTextField.setText(extraField);
                        }

                        String extraValue = ((TextView) listView.getChildAt(i -
                                listView.getFirstVisiblePosition()).findViewById(R.id.extra_info))
                                .getText().toString();

                        mEditInfo = (EditText) dialog.getCustomView().findViewById(R.id.edit_info);
                        mEditInfo.setText(extraValue);

                        mSpinnerEditCategory = (Spinner) dialog.getCustomView().findViewById(R.id.spinner_category);
                        ArrayAdapter<CharSequence> dialogSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                                R.array.categories, android.R.layout.simple_spinner_item);
                        dialogSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                        mSpinnerEditCategory.setAdapter(dialogSpinnerAdapter);

                        String currentItemCategory = ((TextView) listView.getChildAt(i -
                                listView.getFirstVisiblePosition()).findViewById(R.id.category))
                                .getText().toString();

                        mSpinnerEditCategory.setSelection(filterCats.indexOf(currentItemCategory) - 1);

                        dialog.show();
                        break;
                    case 1: // remove item option
                        dbHelper.makeArchived(itemTitle, true);
                        Snackbar.make(getView(), String.format(resources.getString(R.string.msg_note_archived),
                                itemTitle), Snackbar.LENGTH_SHORT).show();

                        if (filterSpinner.getSelectedItem().toString().equals(resources.getString(R.string.cats_all))) {
                            adapter.changeCursor(dbHelper.getAllData(ARCHIVE_STATE_FLAG));
                        } else {
                            adapter.changeCursor(dbHelper.getItemsByCategory(
                                    filterSpinner.getSelectedItem().toString(), ARCHIVE_STATE_FLAG)
                            );
                        }
                        adapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.attachToListView(listView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newNoteIntent = new Intent(getActivity(), NewNoteActivity.class); // call new note activity
                startActivity(newNoteIntent);
            }
        });

        return rootView;
    }

    private class ToolbarSpinnerAdapter extends BaseAdapter { // filtering feature adapter

        private List<String> mItems = new ArrayList<>();

        public void addItems(List<String> itemsList) {
            mItems = itemsList;
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
            return position >= 0 && position < mItems.size() ? mItems.get(position) :
                    filterCats.get(0);
        }
    }

    @Override
    public void onPause() {
        listCurrentPosition = listView.getFirstVisiblePosition();
        super.onPause();
    }

    @Override
    public void onResume() { // update list elements immediately
        super.onResume();

        Cursor c;
        if (mCategory.equals(resources.getString(R.string.cats_all))) {
            c = dbHelper.getAllData(ARCHIVE_STATE_FLAG);
        } else {
            c = dbHelper.getItemsByCategory(mCategory, ARCHIVE_STATE_FLAG);
        }
        c.requery();
        adapter.changeCursor(c);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        listView.setSelection(listCurrentPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("CATEGORY", filterSpinner.getSelectedItem().toString());
    }
}