package com.gnotes.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class ItemArticleActivity extends AppCompatActivity {

    public static final String AUTHORITY = "com.gnotes.app.notessync.provider";
    public static final String ACCOUNT_TYPE = "gnotes.com";
    public static final String ACCOUNT = "geeknotes_account";
    private Account mAccount;

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
