package com.example.rssreader.utils;

import com.prof.rssparser.Article;

import com.example.rssreader.model.NewsItem;

public class RssUtils {

    public static NewsItem articleToNewsItem(Article article) {
        NewsItem newsItem = new NewsItem(
                article.getTitle(),
                article.getAuthor(),
                article.getDescription(),
                article.getPubDate(),
                article.getImage(),
                article.getGuid(),
                article.getContent());
        return newsItem;
    }

}
