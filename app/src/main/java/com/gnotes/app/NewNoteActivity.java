package com.gnotes.app;

import android.content.res.Resources;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;
import com.melnykov.fab.FloatingActionButton;
import com.gnotes.app.data.GeekNotesDbHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class NewNoteActivity extends AppCompatActivity {
    private Resources resources;
    private ArrayList<String> categories;

    private Spinner mSpinner;
    private EditText mTitle;
    private TextView mDescriptionTitle;
    private EditText mExtraNotes;

    private GeekNotesDbHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note_activity);

        resources = getResources();
        categories = new ArrayList<>(Arrays.asList(resources.getStringArray(R.array.categories)));

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(getResources().getString(R.string.toolbar_new_note));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mTitle = (EditText) findViewById(R.id.item_title);
        mDescriptionTitle = (TextView) findViewById(R.id.text_extra_type);

        mSpinner = (Spinner) findViewById(R.id.category_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories,
                R.layout.categories_item);
        adapter.setDropDownViewResource(R.layout.categories_dropdown_item);
        mSpinner.setOnItemSelectedListener(onCatSpinnerListener);
        mSpinner.setAdapter(adapter);

        mExtraNotes = (EditText) findViewById(R.id.extra_info);

        dbHelper = new GeekNotesDbHelper(this);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBaselineAlignBottom(true);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item item = new Item(mTitle.getText().toString(), mSpinner.getSelectedItem().toString(),
                        mDescriptionTitle.getText().toString(), mExtraNotes.getText().toString());

                if (!item.getName().equals("")) {
                    dbHelper.insertData(item.getName(), item.getCategory(), item.getExtraField(), item.getExtraInfo());
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.note_added), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });
    }

    private AdapterView.OnItemSelectedListener onCatSpinnerListener = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                   long id) {
            if (mSpinner.getSelectedItem().equals(categories.get(0))) {
                mDescriptionTitle.setText(resources.getString(R.string.cat_author));
            } else if (mSpinner.getSelectedItem().equals(categories.get(1)) ||
                    mSpinner.getSelectedItem().equals(categories.get(7))) {
                mDescriptionTitle.setText(resources.getString(R.string.cat_director));
            } else if (mSpinner.getSelectedItem().equals(categories.get(3)) ||
                    mSpinner.getSelectedItem().equals(categories.get(4)) ||
                    mSpinner.getSelectedItem().equals(categories.get(2))) {
                mDescriptionTitle.setText(resources.getString(R.string.cat_studio));
            } else if (mSpinner.getSelectedItem().equals(categories.get(5))) {
                mDescriptionTitle.setText(resources.getString(R.string.cat_genre));
            } else if (mSpinner.getSelectedItem().equals(categories.get(6))) {
                mDescriptionTitle.setText(resources.getString(R.string.cat_platform));
            } else if (mSpinner.getSelectedItem().equals(categories.get(8))) {
                mDescriptionTitle.setText(resources.getString(R.string.cat_studirector));
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}
