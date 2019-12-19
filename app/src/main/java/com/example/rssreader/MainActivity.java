package com.example.rssreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rssreader.adapters.NotesAdapter;
import com.example.rssreader.callbacks.MainActionModeCallback;
import com.example.rssreader.callbacks.NoteEventListener;
import com.example.rssreader.model.Note;
import com.example.rssreader.utils.NoteUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.prof.rssparser.Article;
import com.prof.rssparser.OnTaskCompleted;
import com.prof.rssparser.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.example.rssreader.ShowNewsItemActivity.NOTE_EXTRA_Key;

public class MainActivity extends AppCompatActivity implements NoteEventListener{

    private FloatingActionButton fab;
    private ArrayList<Article> articles;
    private RecyclerView recyclerView;
    private ArticlesAdapter adapter;
    private ArticlesDao dao;
    private int chackedCount = 0;
    private MainActionModeCallback actionModeCallback;
    private int theme;


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
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        recyclerView = findViewById(R.id.articles_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        }

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddNewNote();
            }
        });

        dao = ArticlesDB.getInstance(this).articlesDao();

        showNetWorkState();

    }

    private void parseArticles() {
        String rssLink = "https://news.tut.by/rss/index.rss";

        Parser parser = new Parser();
        parser.onFinish(new OnTaskCompleted() {

            @Override
            public void onTaskCompleted(@NonNull List<Article> list) {
                loadArticles(list);
            }

            @Override
            public void onError(Exception e) {
            }
        });
        parser.execute(rssLink);
    }

    private void loadArticles(List<Article> articles) {
//        List<Note> list = dao.getNotes();
        this.adapter = new NotesAdapter(this, this.articles);
        this.adapter.setListener(this);
        this.recyclerView.setAdapter(adapter);
        showEmptyView();

    }

    private void showEmptyView() {
        if (articles.size() == 0) {
            this.recyclerView.setVisibility(View.GONE);
            findViewById(R.id.empty_notes_view).setVisibility(View.VISIBLE);

        } else {
            this.recyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.empty_notes_view).setVisibility(View.GONE);
        }
    }

    private void onAddNewNote() {
        startActivity(new Intent(this, ShowNewsItemActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        parseArticles();
    }

    @Override
    public void onNoteClick(Note note) {
        Intent edit = new Intent(this, ShowNewsItemActivity.class);
        edit.putExtra(NOTE_EXTRA_Key, note.getId());
        startActivity(edit);

    }

    @Override
    public void onNoteLongClick(Note note) {
        note.setChecked(true);
        chackedCount = 1;
        adapter.setMultiCheckMode(true);

        adapter.setListener(new ArticleEventListener() {
            @Override
            public void onArticleClick(Article article) {
                article.setChecked(!note.isChecked());
                if (note.isChecked())
                    chackedCount++;
                else chackedCount--;

                if (chackedCount > 1) {
                    actionModeCallback.changeShareItemVisible(false);
                } else actionModeCallback.changeShareItemVisible(true);

                if (chackedCount == 0) {
                    actionModeCallback.getAction().finish();
                }

                actionModeCallback.setCount(chackedCount + "/" + articles.size());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNoteLongClick(Note note) {

            }

        });

        actionModeCallback = new MainActionModeCallback() {
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_delete_notes)
                    onDeleteMultiNotes();
                else if (menuItem.getItemId() == R.id.action_share_note)
                    onShareNote();

                actionMode.finish();
                return false;
            }

        };

        startActionMode(actionModeCallback);

        fab.hide();
        actionModeCallback.setCount(chackedCount + "/" + articles.size());
    }

    private void onShareNote() {

        Note note = adapter.getCheckedNotes().get(0);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        String notetext = note.getNoteText() + "\n\n Create on : " +
                NoteUtils.dateFromLong(note.getNoteDate()) + "\n  By :" +
                getString(R.string.app_name);
        share.putExtra(Intent.EXTRA_TEXT, notetext);
        startActivity(share);

    }

    private void onDeleteMultiNotes() {

        List<Note> chackedNotes = adapter.getCheckedNotes();
        if (chackedNotes.size() != 0) {
            for (Note note : chackedNotes) {
                dao.deleteNote(note);
            }
            loadNotes();
            Toast.makeText(this, chackedNotes.size() + " Note(s) Delete successfully !", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "No Note(s) selected", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);

        adapter.setMultiCheckMode(false);
        adapter.setListener(this);
        fab.show();
    }

    private ItemTouchHelper swipeToDeleteHelper = new ItemTouchHelper(
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.ViewHolder viewHolder,
                                      @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                    if (notes != null) {
                        Note swipedNote = notes.get(viewHolder.getAdapterPosition());
                        if (swipedNote != null) {
                            swipeToDelete(swipedNote, viewHolder);

                        }

                    }
                }
            });

    private void swipeToDelete(final Note swipedNote, final RecyclerView.ViewHolder viewHolder) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("Delete Note?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dao.deleteNote(swipedNote);
                        notes.remove(swipedNote);
                        adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        showEmptyView();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Objects.requireNonNull(recyclerView.getAdapter()).notifyItemChanged(viewHolder.getAdapterPosition());

                    }
                })
                .setCancelable(false)
                .create().show();

    }

    public void sortNotes(int order, String attr) {
        Collections.sort(notes, new Sorter(order, attr));
        adapter.notifyItemRangeChanged(0, notes.size());
    }

    class Sorter implements Comparator<Note> {
        int order;
        String attr;
        Sorter(int order, String attr) {
            this.order = order;
            this.attr = attr;
        }

        public int compare(Note note1, Note note2) {
            if(attr.equals("date")) {
                if(note1.getNoteDate() == note2.getNoteDate()) return 0;
                else if (note1.getNoteDate() > note2.getNoteDate()) return order;
                else return -1*order;
            }
            else {
                if(note1.getNoteTitle().compareTo(note2.getNoteTitle()) == 0) return 0;
                else if(note1.getNoteTitle().compareTo(note2.getNoteTitle()) > 0) return order;
                else return -1*order;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                SearchByTag(newText);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedNotes = new ArrayList<>();
                savedNotes.addAll(notes);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                notes.clear();
                notes.addAll(savedNotes);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.sort_title_in)
            sortNotes(1, "title");
        else if (id==R.id.sort_title_dec)
            sortNotes(-1, "title");
        else if (id==R.id.sort_date_in)
            sortNotes(1, "date");
        else if (id==R.id.sort_date_dec)
            sortNotes(-1, "date");
        return super.onOptionsItemSelected(item);
    }
    private void SearchByTag(String str) {
        ArrayList<Note> searchedNotes = new ArrayList<>();
        for (Note n : savedNotes) {
            if (n.getNoteTags().contains(str))
                searchedNotes.add(n);
        }
        this.notes.clear();
        this.notes.addAll(searchedNotes);
        adapter.notifyDataSetChanged();
    }

    private void showNetWorkState() {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork.isConnected())
            Snackbar.make(findViewById(R.id.layout_root), getString(R.string.net_connected),
                    Snackbar.LENGTH_LONG).show();
        else
            Snackbar.make(findViewById(R.id.layout_root), getString(R.string.net_disconnected),
                    Snackbar.LENGTH_LONG).show();
    }
}
