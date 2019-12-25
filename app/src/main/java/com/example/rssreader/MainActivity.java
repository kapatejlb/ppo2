package com.example.rssreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.rssreader.adapters.NewsItemAdapter;
import com.example.rssreader.callbacks.NewsItemEventListener;
import com.example.rssreader.db.NewsItemDao;
import com.example.rssreader.db.NewsItemsDB;
import com.example.rssreader.model.NewsItem;
import com.example.rssreader.utils.RssUtils;
import com.prof.rssparser.Article;
import com.prof.rssparser.OnTaskCompleted;
import com.prof.rssparser.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.rssreader.ShowNewsItemActivity.NOTE_EXTRA_Key;

public class MainActivity extends AppCompatActivity implements NewsItemEventListener{

    private ArrayList<NewsItem> newsItems;
    private RecyclerView recyclerView;
    private NewsItemDao dao;
    private String savedLink = "";
    private MutableLiveData<List<Article>> articleListLive = new MutableLiveData<>();
    private SwipeRefreshLayout refreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        recyclerView = findViewById(R.id.news_items_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        }

        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        parseArticles(savedLink);
                    }
                }
        );

        dao = NewsItemsDB.getInstance(this).newsItemDao();

        checkNetWorkState();

        articleListLive.observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                for (NewsItem newsItem: dao.getNewsItems()) {
                    dao.deleteNewsItem(newsItem);
                }
                for (Article article : articles) {
                    dao.insertNewsItem(RssUtils.articleToNewsItem(article));
                }
                loadNewsItems();
            }
        });

    }

    private void parseArticles(String rssLink) {
        if(checkNetWorkState()) {
            Parser parser = new Parser();
            parser.onFinish(new OnTaskCompleted() {

                @Override
                public void onTaskCompleted(@NonNull List<Article> list) {
                    articleListLive.postValue(list);
                    refreshLayout.setRefreshing(false);
                }

                @Override
                public void onError(Exception e) {
                    refreshLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this, getString(R.string.link_trouble),
                            Toast.LENGTH_LONG).show();
                }
            });
            parser.execute(rssLink);
        }
    }

    public void loadNewsItems() {
        this.newsItems = new ArrayList<>();
        List<NewsItem> list = dao.getNewsItems();
        this.newsItems.addAll(list);
        NewsItemAdapter adapter = new NewsItemAdapter(this, newsItems);
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);
        showEmptyView();
    }

    private void showEmptyView() {
        if (newsItems.size() == 0) {
            this.recyclerView.setVisibility(View.GONE);
            findViewById(R.id.empty_news_items_view).setVisibility(View.VISIBLE);

        } else {
            this.recyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.empty_news_items_view).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNewsItems();
    }

    @Override
    public void onNewsItemClick(NewsItem newsItem) {
        Intent show = new Intent(this, ShowNewsItemActivity.class);
        show.putExtra(NOTE_EXTRA_Key, newsItem.getId());
        startActivity(show);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                savedLink = newText;
                parseArticles(newText);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchView sv = (SearchView) v;
                sv.setQuery(savedLink, false);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private boolean checkNetWorkState() {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork == null){
            Toast.makeText(this, getString(R.string.net_disconnected),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if(activeNetwork.isConnected()) {
            Toast.makeText(this, getString(R.string.net_connected),
                    Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
}
