package com.gnotes.app.ui;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.*;
import com.gnotes.app.R;
import com.gnotes.app.adapters.DrawerAdapter;

public class GeekNotesActivity extends AppCompatActivity {

    private int icons[] = {R.mipmap.ic_menu_add, R.mipmap.ic_action_tutorial, R.mipmap.ic_action_quit};

    private DrawerLayout drawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geek_notes);

        String menuTitles[] = {getString(R.string.menu_addnote),
                getString(R.string.menu_archive),
                getString(R.string.menu_exit)};

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        String name = "GeekNotes";
        String slogan = "don't forget to widen your outlook";
        RecyclerView.Adapter mAdapter = new DrawerAdapter(menuTitles, icons, name, slogan, this);

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

            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    public void handleDrawerItems(RecyclerView rv, MotionEvent e, View child) {

        switch (rv.getChildPosition(child)) {
            case 1:
                Intent newNoteIntent = new Intent(this, NewNoteActivity.class); // call new note activity
                startActivity(newNoteIntent);
                break;
            case 2:
                Intent archiveIntent = new Intent(this, ArchiveActivity.class);
                startActivity(archiveIntent);
                break;
            case 3:
                finish();
                System.exit(0);
                break;
            default:
                break;
        }
        drawerLayout.closeDrawers();
    }
}
