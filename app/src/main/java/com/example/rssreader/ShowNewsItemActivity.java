package com.example.rssreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rssreader.db.NewsItemsDB;
import com.example.rssreader.db.NewsItemDao;
import com.example.rssreader.model.NewsItem;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;


public class ShowNewsItemActivity extends AppCompatActivity {

    private TextView newsItemTitle;
    private TextView newsItemDescription;
    private TextView newsItemDate;
    private TextView newItemAuthor;
    private TextView newItemGuid;
    private ImageView newsItemPreviewImage;
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

        newsItemTitle = findViewById(R.id.news_item_title);
        newsItemDescription = findViewById(R.id.news_item_description);
        newsItemDate = findViewById(R.id.news_item_date);
        newItemAuthor = findViewById(R.id.news_item_author);
        newsItemPreviewImage = findViewById(R.id.preview_image);
        newItemGuid = findViewById(R.id.news_item_guid);

        dao = NewsItemsDB.getInstance(this).newsItemDao();
        int id = Objects.requireNonNull(getIntent().getExtras()).getInt(NOTE_EXTRA_Key, 0);
        temp = dao.getNewsItemById(id);
        newsItemTitle.setText(temp.getNewsItemTitle());
        newsItemDescription.setText(temp.getNewsItemDescription());
        newsItemDate.setText(temp.getNewsItemPubDate());
        newItemAuthor.setText("by " + temp.getNewsItemAuthor());
        newItemGuid.setText("Link");
        newItemGuid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(temp.getNewsItemGuid()));
                startActivity(browserIntent);
            }
        });
        Picasso.with(this)
                .load(temp.getNewsItemImage())
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .into(newsItemPreviewImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.show_news_item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
