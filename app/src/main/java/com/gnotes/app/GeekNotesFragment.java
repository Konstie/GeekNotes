package com.gnotes.app;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.gnotes.app.data.GeekNotesDbHelper;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeekNotesFragment extends Fragment {

    private static final ArrayList<String> filterCats = new ArrayList<>(
            Arrays.asList("Все", "Книга", "Фильм", "Сериал",
                    "Мультсериал", "Мультфильм", "Муз. исполнитель",
                    "Игра", "Комикс", "Аниме")
    );

    private GeekNotesDbHelper dbHelper;
    private GeekNotesAdapter adapter;

    private Toolbar toolbar;
    private Spinner filterSpinner;
    private SwipeMenuListView listView;

    private TextView mTextField;
    private EditText mEditTitle;
    private EditText mEditInfo;

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup parent,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_geeknotes, parent, false);

        toolbar = (Toolbar) (getActivity()).findViewById(R.id.toolbar);
        ((GeekNotesActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setLogo(R.drawable.tardis_icon);

        View spinnerContainer = LayoutInflater.from(getActivity()).inflate(R.layout.toolbar_main_spinner, toolbar, false);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        toolbar.addView(spinnerContainer, lp);
        ToolbarSpinnerAdapter spinnerAdapter = new ToolbarSpinnerAdapter();
        spinnerAdapter.addItems(filterCats);

        filterSpinner = (Spinner) spinnerContainer.findViewById(R.id.toolbar_spinner);
        filterSpinner.setAdapter(spinnerAdapter);

        listView = (SwipeMenuListView) rootView.findViewById(R.id.items_list);

        setTargetFragment(GeekNotesFragment.this, 0);

        listView.setDividerHeight(0);
        listView.setDivider(null);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        dbHelper = new GeekNotesDbHelper(getActivity());
        adapter = new GeekNotesAdapter(getActivity(), dbHelper.getAllData(), 0);
        adapter.changeCursor(dbHelper.getAllData());
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String chosenCategory = filterSpinner.getSelectedItem().toString();
                if (chosenCategory.equals("Все")) {
                    adapter = new GeekNotesAdapter(getActivity(), dbHelper.getAllData(), 0);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new GeekNotesAdapter(getActivity(), dbHelper.getItemsByCategory(chosenCategory), 0);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
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
                Intent intent = new Intent(getActivity(), ItemArticleActivity.class);
                intent.putExtra("ITEM_TITLE", title);
                startActivity(intent);
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu swipeMenu) {
                SwipeMenuItem infoItem = new SwipeMenuItem(getActivity().getApplicationContext());
                infoItem.setBackground(new ColorDrawable(Color.YELLOW));
                infoItem.setWidth(100);
                infoItem.setIcon(R.drawable.ic_action_info);
                swipeMenu.addMenuItem(infoItem);

                SwipeMenuItem editItem = new SwipeMenuItem(getActivity().getApplicationContext());
                editItem.setBackground(new ColorDrawable(Color.rgb(0xFF, 0xA5, 0x00)));
                editItem.setWidth(100);
                editItem.setIcon(R.drawable.ic_action_edit);
                swipeMenu.addMenuItem(editItem);

                SwipeMenuItem removeItem = new SwipeMenuItem(
                        getActivity().getApplicationContext()
                );
                removeItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                removeItem.setWidth(100);
                removeItem.setIcon(R.drawable.ic_action_delete);
                swipeMenu.addMenuItem(removeItem);
            }
        };

        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int i, SwipeMenu swipeMenu, int index) {
                TextView itemText = (TextView) listView.getChildAt(i).findViewById(R.id.name);
                final String itemTitle = itemText.getText().toString();
                switch (index) {
                    case 0:
                        listView.performItemClick(listView.getChildAt(i), i, listView.getItemIdAtPosition(i));
                        break;
                    case 1:
                        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                                .title("Изменить заметку")
                                .customView(R.layout.change_item_dialog, true)
                                .positiveText("Сохранить")
                                .negativeText("Отменить")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        String title, info;
                                        title = mEditTitle.getText().toString();
                                        info = mEditInfo.getText().toString();
                                        dbHelper.updateData(itemTitle, title, info);

                                        if (filterSpinner.getSelectedItem().toString().equals("Все")) {
                                            adapter.changeCursor(dbHelper.getAllData());
                                        } else {
                                            adapter.changeCursor(dbHelper.getItemsByCategory(filterSpinner.getSelectedItem().toString()));
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
                        TextView itemExtraField = (TextView) listView.getChildAt(i).findViewById(R.id.extra_type);
                        String extraField = itemExtraField.getText().toString();
                        if (extraField.equals("")) {
                            mTextField.setText("");
                        } else {
                            mTextField.setText(extraField);
                        }

                        String extraValue = ((TextView) listView.getChildAt(i).findViewById(R.id.extra_info))
                                .getText().toString();

                        mEditInfo = (EditText) dialog.getCustomView().findViewById(R.id.edit_info);
                        mEditInfo.setText(extraValue);

                        dialog.show();
                        break;
                    case 2:
//                        TextView itemText = (TextView) listView.getChildAt(i).findViewById(R.id.name);
//                        String itemTitle = itemText.getText().toString();
                        dbHelper.deleteByTitle(itemTitle);
                        Toast.makeText(getActivity(), "ID: " + itemTitle, Toast.LENGTH_SHORT).show();

                        if (filterSpinner.getSelectedItem().toString().equals("Все")) {
                            adapter.changeCursor(dbHelper.getAllData());
                        } else {
                            adapter.changeCursor(dbHelper.getItemsByCategory(filterSpinner.getSelectedItem().toString()));
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
                Intent newNoteIntent = new Intent(getActivity(), NewNoteActivity.class);
                startActivity(newNoteIntent);
            }
        });
        return rootView;
    }

    private class ToolbarSpinnerAdapter extends BaseAdapter {

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
            return position >= 0 && position < mItems.size() ? mItems.get(position) : "Все";
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Cursor c = dbHelper.getAllData();
        c.requery();
        adapter.changeCursor(dbHelper.getAllData());
        adapter.notifyDataSetChanged();
    }
}