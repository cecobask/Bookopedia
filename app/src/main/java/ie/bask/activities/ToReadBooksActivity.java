package ie.bask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import ie.bask.R;
import ie.bask.adapters.BookAdapter;
import ie.bask.main.Base;
import ie.bask.main.BookopediaApp;
import ie.bask.models.Book;

public class ToReadBooksActivity extends Base {

    private RecyclerView rvBooksToRead;
    private BookAdapter bookAdapter;
    private ArrayList<Book> selectedBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_read_books);
        app = (BookopediaApp) getApplication();
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rvBooksToRead = findViewById(R.id.rvBooksToRead);
        pbSearch = findViewById(R.id.pbSearch);
        selectedBooks = new ArrayList<>();

        // Display books in a RecyclerView
        rvBooksToRead.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        bookAdapter = new BookAdapter(ToReadBooksActivity.this, app.booksToRead);

        rvBooksToRead.setAdapter(bookAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final MenuItem homeItem = menu.findItem(R.id.action_home);
        final MenuItem toReadItem = menu.findItem(R.id.action_to_read);
        final MenuItem clearBooksItem = menu.findItem(R.id.action_clear);
        final MenuItem deleteBookItem = menu.findItem(R.id.action_delete);
        toReadItem.setVisible(false);
        deleteBookItem.setVisible(false);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(android.R.attr.width);

        // Hide home button icon if SearchView is opened
        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewDetachedFromWindow(View arg0) {
                // search was detached/closed
                homeItem.setVisible(true);
                clearBooksItem.setVisible(true);
            }

            @Override
            public void onViewAttachedToWindow(View arg0) {
                // search was opened
                homeItem.setVisible(false);
                clearBooksItem.setVisible(false);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent homeIntent = new Intent(getApplicationContext(), BookListActivity.class);

        switch (id) {
            case (R.id.action_home):
                app.booksResults.clear();
                startActivity(homeIntent);
                break;
            case (R.id.action_clear):
                app.booksToReadDb.removeValue();
                app.booksToRead.clear();
                Toast.makeText(this, "All books deleted", Toast.LENGTH_SHORT).show();
                startActivity(homeIntent);
                finishAffinity();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(app.booksToRead.isEmpty()){
            Intent goHome = new Intent(ToReadBooksActivity.this, BookListActivity.class);
            startActivity(goHome);
            finishAffinity();
        }
    }
}
