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

import ie.bask.R;
import ie.bask.adapters.BookAdapter;
import ie.bask.adapters.BookFilter;
import ie.bask.main.Base;
import ie.bask.main.BookopediaApp;

public class ToReadBooksActivity extends Base {

    private BookFilter bookFilter;
    private BookAdapter bookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_read_books);
        app = (BookopediaApp) getApplication();
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView rvBooksToRead = findViewById(R.id.rvBooksToRead);
        pbSearch = findViewById(R.id.pbSearch);

        // Display books in a RecyclerView
        rvBooksToRead.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        bookAdapter = new BookAdapter(ToReadBooksActivity.this, app.booksToRead);
        bookFilter = new BookFilter(app.booksToRead, bookAdapter);

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
        final MenuItem logoutItem = menu.findItem(R.id.action_logout);
        toReadItem.setVisible(false);
        deleteBookItem.setVisible(false);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(android.R.attr.width);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                bookFilter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                bookFilter.filter(query);
                return false;
            }
        });

        // Hide home button icon if SearchView is opened
        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewDetachedFromWindow(View arg0) {
                // search was detached/closed
                homeItem.setVisible(true);
                clearBooksItem.setVisible(true);
                logoutItem.setVisible(true);
            }

            @Override
            public void onViewAttachedToWindow(View arg0) {
                // search was opened
                homeItem.setVisible(false);
                clearBooksItem.setVisible(false);
                logoutItem.setVisible(false);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        final Intent homeIntent = new Intent(ToReadBooksActivity.this, BookListActivity.class);

        switch (id) {
            case (R.id.action_home):
                startActivity(homeIntent);
                break;
            case (R.id.action_clear):
                showDialog(ToReadBooksActivity.this, "Delete all books?", "deleteAllBooks");
                break;
            case (R.id.action_logout):
                showDialog(ToReadBooksActivity.this, "You are about to log out. Proceed?", "signOut");
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

        // Used when user deletes a book and returns to this activity
        bookAdapter.notifyDataSetChanged();
    }
}
