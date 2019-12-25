package com.example.rssreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rssreader.db.NewsItemsDB;
import com.example.rssreader.db.NewsItemDao;
import com.example.rssreader.model.NewsItem;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class ShowNewsItemActivity extends AppCompatActivity {

    private NewsItem temp;

    public static final String NOTE_EXTRA_Key = "note_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_news_item);

        TextView newsItemTitle = findViewById(R.id.news_item_title);
        TextView newsItemDescription = findViewById(R.id.news_item_description);
        TextView newsItemDate = findViewById(R.id.news_item_date);
        TextView newItemAuthor = findViewById(R.id.news_item_author);
        ImageView newsItemPreviewImage = findViewById(R.id.preview_image);
        TextView newItemGuid = findViewById(R.id.news_item_guid);

        NewsItemDao dao = NewsItemsDB.getInstance(this).newsItemDao();
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
                .placeholder(R.drawable.image_coming)
                .error(R.drawable.no_image)
                .into(newsItemPreviewImage);
    }

}
