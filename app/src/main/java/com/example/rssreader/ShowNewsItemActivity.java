package com.example.rssreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rssreader.db.NewsItemsDB;
import com.example.rssreader.db.NewsItemDao;
import com.example.rssreader.model.NewsItem;

import java.util.Date;
import java.util.Objects;


public class ShowNewsItemActivity extends AppCompatActivity {

    private TextView newsItemTitle;
    private TextView newsItemDescription;
    private EditText inputNoteTags;

    private NewsItemDao dao;
    private NewsItem temp;
    private int theme;


    public static final String NOTE_EXTRA_Key = "note_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                theme = R.style.AppTheme_Dark;
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                theme = R.style.AppTheme;
                break;
        }
        setTheme(theme);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_news_item);

        Toolbar toolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        newsItemTitle = findViewById(R.id.news_item_title);
        newsItemDescription = findViewById(R.id.news_item_description);

        dao = NewsItemsDB.getInstance(this).newsItemDao();
        int id = Objects.requireNonNull(getIntent().getExtras()).getInt(NOTE_EXTRA_Key, 0);
        temp = dao.getNewsItemsById(id);
        newsItemTitle.setText(temp.getNewItemTitle());
        newsItemDescription.setText(temp.getNewsItemDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
