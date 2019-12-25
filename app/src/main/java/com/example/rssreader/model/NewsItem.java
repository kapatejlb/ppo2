package com.example.rssreader.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "newsItems")
public class NewsItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "title")
    private String newsItemTitle;
    @ColumnInfo(name = "author")
    private String newsItemAuthor;
    @ColumnInfo(name = "description")
    private String newsItemDescription;
    @ColumnInfo(name = "date")
    private String newsItemPubDate;
    @ColumnInfo(name = "image")
    private String newsItemImage;
    @ColumnInfo(name = "guid")
    private String newsItemGuid;
    @ColumnInfo(name = "content")
    private String newsItemContent;

    public NewsItem(String newsItemTitle, String newsItemAuthor, String newsItemDescription,
                    String newsItemPubDate, String newsItemImage, String newsItemGuid,
                    String newsItemContent) {
        this.newsItemTitle = newsItemTitle;
        this.newsItemAuthor = newsItemAuthor;
        this.newsItemDescription = newsItemDescription;
        this.newsItemPubDate = newsItemPubDate;
        this.newsItemGuid = newsItemGuid;
        this.newsItemImage = newsItemImage;
        this.newsItemContent = newsItemContent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", newsItemPubDate=" + newsItemPubDate +
                '}';
    }

    public String getNewsItemTitle() {
        return newsItemTitle;
    }

    public void setNewsItemTitle(String newsItemTitle) {
        this.newsItemTitle = newsItemTitle;
    }

    public String getNewsItemAuthor() {
        return newsItemAuthor;
    }

    public void setNewsItemAuthor(String newsItemAuthor) {
        this.newsItemAuthor = newsItemAuthor;
    }

    public String getNewsItemDescription() {
        return newsItemDescription;
    }

    public void setNewsItemDescription(String newsItemDescription) {
        this.newsItemDescription = newsItemDescription;
    }

    public String getNewsItemPubDate() {
        return newsItemPubDate;
    }

    public void setNewsItemPubDate(String newsItemPubDate) {
        this.newsItemPubDate = newsItemPubDate;
    }

    public String getNewsItemImage() {
        return newsItemImage;
    }

    public void setNewsItemImage(String newsItemImage) {
        this.newsItemImage = newsItemImage;
    }

    public String getNewsItemContent() {
        return newsItemImage;
    }

    public void setNewsItemContent(String newsItemContent) {
        this.newsItemContent = newsItemContent;
    }

    public String getNewsItemGuid() {
        return newsItemGuid;
    }

    public void setNewsGuid(String newsItemGuid) {
        this.newsItemGuid = newsItemGuid;
    }
}
