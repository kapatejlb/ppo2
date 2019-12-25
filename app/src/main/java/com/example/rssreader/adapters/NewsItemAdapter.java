package com.example.rssreader.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rssreader.R;
import com.example.rssreader.callbacks.NewsItemEventListener;
import com.example.rssreader.model.NewsItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsItemAdapter extends RecyclerView.Adapter<NewsItemAdapter.NoteHolder> {

    private Context context;
    private ArrayList<NewsItem> newsItems;
    private NewsItemEventListener listener;

    public NewsItemAdapter(Context context, ArrayList<NewsItem> newsItems) {
        this.context = context;
        this.newsItems = newsItems;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.news_item_layout, parent, false);
        return new NoteHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        final NewsItem newsItem = getNewsItem(position);
        if (newsItem != null) {
            holder.newsItemTitle.setText(newsItem.getNewsItemTitle());
            holder.newsItemDate.setText(newsItem.getNewsItemPubDate());
            holder.newItemAuthor.setText("by " + newsItem.getNewsItemAuthor());
            Picasso.with(context)
                    .load(newsItem.getNewsItemImage())
                    .placeholder(R.drawable.image_coming)
                    .error(R.drawable.no_image)
                    .into(holder.newsItemPreviewImage);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onNewsItemClick(newsItem);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    private NewsItem getNewsItem(int position) {
        return newsItems.get(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        TextView newsItemTitle, newsItemDate, newItemAuthor;
        CheckBox checkBox;
        ImageView newsItemPreviewImage;

        NoteHolder(View itemView) {
            super(itemView);
            newsItemDate = itemView.findViewById(R.id.news_item_date);
            newsItemTitle = itemView.findViewById(R.id.news_item_title);
            newsItemPreviewImage = itemView.findViewById(R.id.preview_image);
            newItemAuthor = itemView.findViewById(R.id.news_item_author);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    public void setListener(NewsItemEventListener listener) {
        this.listener = listener;
    }
}
