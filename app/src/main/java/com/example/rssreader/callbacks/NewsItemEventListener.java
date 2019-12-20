package com.example.rssreader.callbacks;

import com.example.rssreader.model.NewsItem;

public interface NewsItemEventListener {

    void onNewsItemClick(NewsItem newsItem);

    void onNewsItemLongClick(NewsItem newsItem);

}
