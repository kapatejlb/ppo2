package com.example.rssreader.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.rssreader.model.NewsItem;

@Database(entities = NewsItem.class, version = 1, exportSchema = false)
public abstract class NewsItemsDB extends RoomDatabase {

    public abstract NewsItemDao newsItemDao();

    private static NewsItemsDB instance;

    private static final String DATABASE_NAME = "newsItemsDb";

    public static NewsItemsDB getInstance(Context context) {
        if(instance == null)
            instance = Room.databaseBuilder(context, NewsItemsDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        return instance;
    }


}
