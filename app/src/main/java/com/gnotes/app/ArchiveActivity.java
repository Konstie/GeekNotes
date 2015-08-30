package com.gnotes.app;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.gnotes.app.adapters.GeekNotesAdapter;
import com.gnotes.app.data.GeekNotesDbHelper;

public class ArchiveActivity extends AppCompatActivity {
    private static final boolean ARCHIVE_STATE_FLAG = true;

    private Resources resources;
    private GeekNotesDbHelper dbHelper;
    private GeekNotesAdapter adapter;

    private SwipeMenuListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        resources = getResources();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(resources.getColor(R.color.primary_dark_archive));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(resources.getString(R.string.cats_archive));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listView = (SwipeMenuListView) findViewById(R.id.archive_list);

        listView.setDividerHeight(0);
        listView.setDivider(null);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        dbHelper = new GeekNotesDbHelper(this);
        adapter = new GeekNotesAdapter(this, dbHelper.getAllData(ARCHIVE_STATE_FLAG), 0);
        adapter.changeCursor(dbHelper.getAllData(ARCHIVE_STATE_FLAG));
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu swipeMenu) {
                SwipeMenuItem editItem = new SwipeMenuItem(getApplicationContext());
                editItem.setBackground(new ColorDrawable(Color.rgb(0xFF, 0xD1, 0x2A)));
                editItem.setWidth(100);
                editItem.setIcon(R.mipmap.ic_action_regenerate);
                swipeMenu.addMenuItem(editItem);

                SwipeMenuItem removeItem = new SwipeMenuItem(
                        getApplicationContext()
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
                    case 0: // regenerate item
                        dbHelper.makeArchived(itemTitle, false);
                        Snackbar.make(listView, String.format(resources.getString(R.string.msg_note_restored),
                                itemTitle), Snackbar.LENGTH_SHORT).show();
                        adapter.changeCursor(dbHelper.getAllData(ARCHIVE_STATE_FLAG));
                        adapter.notifyDataSetChanged();
                        break;
                    case 1: // remove item option
                        dbHelper.deleteByTitle(itemTitle);
                        Snackbar.make(listView, String.format(resources.getString(R.string.msg_note_destroyed),
                                itemTitle), Snackbar.LENGTH_SHORT).show();

                        adapter.changeCursor(dbHelper.getAllData(ARCHIVE_STATE_FLAG));
                        adapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_archive, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_wiki:
                dbHelper.clearArchive(ARCHIVE_STATE_FLAG);
                adapter.changeCursor(dbHelper.getAllData(ARCHIVE_STATE_FLAG));
                adapter.notifyDataSetChanged();
                Snackbar.make(listView, getResources().getString(R.string.archive_clean), Snackbar.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }
}
