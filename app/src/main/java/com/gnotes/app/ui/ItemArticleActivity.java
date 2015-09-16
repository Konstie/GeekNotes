package com.gnotes.app.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.gnotes.app.R;

public class ItemArticleActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_article_activity);

        if (savedInstanceState == null) {
            ItemArticleFragment fragment = new ItemArticleFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_article_container, fragment)
                    .commit();
        }
    }
}
