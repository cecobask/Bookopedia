package ie.bask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import ie.bask.R;
import ie.bask.adapters.BookAdapter;
import ie.bask.main.Base;
import ie.bask.main.BookopediaApp;


public class BookListActivity extends Base {

    private Handler handler = new Handler();
    private Runnable runnable;
    private ListView lvBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        app = (BookopediaApp) getApplication();
        lvBooks = findViewById(R.id.lvBooks);
        pbSearch = findViewById(R.id.pbSearch);
        tvNoResults = findViewById(R.id.tvNoResults);
        bookAdapter = new BookAdapter(this, app.booksList);
        lvBooks.setAdapter(bookAdapter);

        setOnBookClickListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final MenuItem homeItem = menu.findItem(R.id.action_home);
        final MenuItem toReadItem = menu.findItem(R.id.action_to_read);
        final MenuItem clearBooksItem = menu.findItem(R.id.action_clear);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        // Trick to give enough time for response from Firebase
        handler.removeCallbacks(runnable);
        runnable = new Runnable() {
            @Override
            public void run() {
                clearBooksItem.setVisible(false);
                if (app.booksToRead.isEmpty()) {
                    toReadItem.setVisible(false);
                } else {
                    toReadItem.setVisible(true);
                }
            }
        };
        // Delay MenuItems population
        handler.postDelayed(runnable, 350);

        searchView.setMaxWidth(android.R.attr.width);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Get books based on user input
                getBooks(query);
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchItem.collapseActionView();

                // Set activity title to search query
                BookListActivity.this.setTitle(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(final String query) {
                // Remove all previous callbacks
                handler.removeCallbacks(runnable);
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (query.length() > 0) {
                            // Get books based on user input
                            getBooks(query);
                        }
                    }
                };
                // Delay API call with 1 second resulting in less API calls
                handler.postDelayed(runnable, 1000);
                return true;
            }
        });

        // Hide other menu items if SearchView is clicked
        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewDetachedFromWindow(View arg0) {
                // search was detached/closed
                homeItem.setVisible(true);
                if (app.booksToRead.isEmpty()) {
                    toReadItem.setVisible(false);
                } else {
                    toReadItem.setVisible(true);
                }
            }
            @Override
            public void onViewAttachedToWindow(View arg0) {
                // search was opened
                homeItem.setVisible(false);
                toReadItem.setVisible(false);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case (R.id.action_home):
                // Clear ListView and hide ProgressBar
                BookListActivity.this.setTitle(R.string.app_name);
                app.booksList.clear();
                pbSearch.setVisibility(View.GONE);
                tvNoResults.setText(null);
                break;
            case (R.id.action_to_read):
                Intent toReadIntent = new Intent(getApplicationContext(), ToReadBooksActivity.class);
                startActivity(toReadIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void setOnBookClickListener() {
        lvBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Launch the BookInfoActivity activity passing book as an extra
                Intent intent = new Intent(getApplicationContext(), BookInfoActivity.class);
                intent.putExtra("book_info_key", bookAdapter.getItem(position));
                startActivity(intent);
            }
        });
    }

}
