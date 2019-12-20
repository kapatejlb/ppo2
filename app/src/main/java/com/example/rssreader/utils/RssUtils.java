package com.example.rssreader.utils;

import com.prof.rssparser.Article;

import com.example.rssreader.model.NewsItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class RssUtils {

    public static NewsItem articleToNewsItem(Article article) {
        String newsDescription = article.getDescription();
        Document html = Jsoup.parse(newsDescription);
        newsDescription = html.body().text();
        NewsItem newsItem = new NewsItem(
                article.getTitle(),
                article.getAuthor(),
                newsDescription,
                article.getPubDate(),
                article.getImage(),
                article.getGuid(),
                article.getContent());
        return newsItem;
    }

}
