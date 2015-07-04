package com.gnotes.app;

import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.*;

import java.util.ArrayList;


public class GeekNotesActivity extends AppCompatActivity {

    private String menuTitles[] = {"Заметки", "Новая запись", "Настройки", "Выход"};
    private int icons[] = {android.R.drawable.ic_menu_slideshow, android.R.drawable.ic_menu_add,
            android.R.drawable.ic_menu_preferences, android.R.drawable.ic_menu_close_clear_cancel};

    private String name = "GeekNotes";
    private String slogan = "don't forget to widen your outlook";

    private static ArrayList<String> filterCats;

    private Toolbar toolbar;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DrawerLayout drawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geek_notes);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new DrawerAdapter(menuTitles, icons, name, slogan, this);

        mRecyclerView.setAdapter(mAdapter);

        final GestureDetector mGestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());

                if (child != null && mGestureDetector.onTouchEvent(e)) {
                    drawerLayout.closeDrawers();
                    handleDrawerItems(rv, e, child);
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                // здесь пока что царит пустота
            }
        });

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        //drawerLayout.bringToFront();
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_closed) {

            @Override
            public void onDrawerOpened(View drawerView) { }

            @Override
            public void onDrawerClosed(View drawerView) { }
        };

        mDrawerToggle.syncState();
        drawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            GeekNotesFragment mainFragment = new GeekNotesFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, mainFragment)
                    .commit();
        }

        // GeekNotesSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    public void handleDrawerItems(RecyclerView rv, MotionEvent e, View child) {
        Fragment fragment = null;
        switch (rv.getChildPosition(child)) {
            case 0:
                return;
            case 1:
                break;
            case 2:
                //fragment = new NewNoteFragment();
                break;
            case 3:
                break;
            case 4:
                finish();
                System.exit(0);
                break;
            default:
                break;
        }
        drawerLayout.closeDrawers();
    }
}
