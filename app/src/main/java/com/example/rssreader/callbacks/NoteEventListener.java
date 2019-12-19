package com.example.rssreader.callbacks;

import com.example.rssreader.model.NewsItem;

public interface NoteEventListener {

    void onNoteClick(NewsItem newsItem);

    void onNoteLongClick(NewsItem newsItem);

}
