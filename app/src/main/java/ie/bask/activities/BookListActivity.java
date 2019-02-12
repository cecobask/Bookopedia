package ie.bask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import ie.bask.R;
import ie.bask.adapters.BookAdapter;
import ie.bask.main.Base;
import ie.bask.main.BookClient;
import ie.bask.main.BookopediaApp;
import ie.bask.models.Book;


public class BookListActivity extends Base {

    private Handler handler = new Handler();
    private Runnable runnable;
    private RecyclerView rvBooks;
    private BookAdapter bookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        app = (BookopediaApp) getApplication();
        pbSearch = findViewById(R.id.pbSearch);
        tvNoResults = findViewById(R.id.tvNoResults);
        rvBooks = findViewById(R.id.rvBooks);
        rvBooks.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        Log.v("Bookopedia", ""+app.booksList.size());
        // Trick to give enough time for response from Firebase
        handler.removeCallbacks(runnable);
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.v("Bookopedia", ""+app.booksList.size());
            }
        };
        // Delay MenuItems population
        handler.postDelayed(runnable, 3000);

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

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("Bookopedia", "" + app.booksList.size());
    }

    // Executes an API call to the Google Books search endpoint, parses the results
    // Converts them into an array of book objects and adds them to the adapter
    public void getBooks(String query) {
        // Show progress bar if search query is not empty
        if (query.length() > 0) pbSearch.setVisibility(View.VISIBLE);

        BookClient client = new BookClient();
        client.getBooks(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // Hide progress bar and TextView
                    pbSearch.setVisibility(View.GONE);
                    tvNoResults.setText(null);
                    JSONArray items;
                    if (response.getInt("totalItems") != 0) {
                        // Get the items json array
                        items = response.getJSONArray("items");
                        // Parse json array into array of Book objects
                        app.booksList = Book.fromJson(items);
                        bookAdapter = new BookAdapter(getApplicationContext(), app.booksList);
                        rvBooks.setAdapter(bookAdapter);

                    } else {
                        app.booksList.clear();
                        tvNoResults.setText("No results found.\nSorry for the inconvenience.");
                    }
                } catch (JSONException e) {
                    pbSearch.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("Failed: ", "" + statusCode);
                Log.d("Error: ", "" + throwable);
                Log.d("JsonObject: ", "" + errorResponse);
            }
        });
    }
}
