package com.gnotes.app;

import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;
import com.melnykov.fab.FloatingActionButton;
import com.gnotes.app.data.GeekNotesDbHelper;

public class NewNoteActivity extends AppCompatActivity {
    private Spinner mSpinner;
    private EditText mTitle;
    private TextView mDescriptionTitle;
    private EditText mExtraNotes;

    private GeekNotesDbHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note_activity);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Новая заметка");
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
            if (mSpinner.getSelectedItem().equals("Книга")) {
                mDescriptionTitle.setText("Автор");
            } else if (mSpinner.getSelectedItem().equals("Фильм")) {
                mDescriptionTitle.setText("Режиссер");
            } else if (mSpinner.getSelectedItem().equals("Сериал") || mSpinner.getSelectedItem().equals("Мультсериал")) {
                mDescriptionTitle.setText("Студия / озвучка");
            } else if (mSpinner.getSelectedItem().equals("Мультфильм")) {
                mDescriptionTitle.setText("Студия / режиссёр");
            } else if (mSpinner.getSelectedItem().equals("Муз. исполнитель")) {
                mDescriptionTitle.setText("Жанр");
            } else if (mSpinner.getSelectedItem().equals("Игра")) {
                mDescriptionTitle.setText("Платформа");
            } else if (mSpinner.getSelectedItem().equals("Комикс")) {
                mDescriptionTitle.setText("Автор");
            } else if (mSpinner.getSelectedItem().equals("Аниме")) {
                mDescriptionTitle.setText("Режиссер / студия");
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}
