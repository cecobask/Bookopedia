package ie.bask.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import ie.bask.PicassoTrustAll;
import ie.bask.R;
import ie.bask.adapters.BookViewHolder;
import ie.bask.main.Base;
import ie.bask.main.BookClient;
import ie.bask.main.BookopediaApp;
import ie.bask.models.Book;


public class BookListActivity extends Base {

    private Handler handler = new Handler();
    private Runnable runnable;
    private RecyclerView rvBooks;
    private FirebaseRecyclerAdapter adapter;
    private Menu menu;

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

        FirebaseRecyclerOptions<Book> options =
                new FirebaseRecyclerOptions.Builder<Book>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("bookResults"), Book.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Book, BookViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final BookViewHolder holder, int position, @NonNull Book book) {
                book = getItem(holder.getAdapterPosition());
                // Populate the data into the template view using the Book object
                holder.tvTitle.setText(book.getTitle());
                holder.tvAuthor.setText(book.getAuthor());

                // Use custom Picasso instance to fetch book cover
                PicassoTrustAll.getInstance(getApplicationContext())
                        .load(Uri.parse(book.getImageLink()))
                        .fit().centerInside().error(R.drawable.ic_nocover).into(holder.ivCover);

                if (book.getDateAdded() != null) {
                    holder.tvDateAdded.setText(book.getDateAdded());
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Launch the BookInfoActivity activity passing book as an extra
                        Intent intent = new Intent(getApplicationContext(), BookInfoActivity.class);
                        intent.putExtra("book_info_key", getItem(holder.getAdapterPosition()));
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.book_item, parent, false);

                return new BookViewHolder(view);
            }
        };

        rvBooks.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter.startListening();
        rvBooks.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final MenuItem homeItem = menu.findItem(R.id.action_home);
        final MenuItem toReadItem = menu.findItem(R.id.action_to_read);
        final MenuItem clearBooksItem = menu.findItem(R.id.action_clear);
        final MenuItem deleteBookItem = menu.findItem(R.id.action_delete);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        // Trick to give enough time for response from Firebase
        handler.removeCallbacks(runnable);
        runnable = new Runnable() {
            @Override
            public void run() {
                clearBooksItem.setVisible(false);
                deleteBookItem.setVisible(false);
                if (app.booksToRead.isEmpty()) {
                    toReadItem.setVisible(false);
                } else {
                    toReadItem.setVisible(true);
                }
            }
        };
        // Delay MenuItems population
        handler.postDelayed(runnable, 500);

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

        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case (R.id.action_home):
                // Clear ListView and hide ProgressBar
                BookListActivity.this.setTitle(R.string.app_name);
                app.booksResults.clear();
                pbSearch.setVisibility(View.GONE);
                tvNoResults.setText(null);
                break;
            case (R.id.action_to_read):
                Intent toReadIntent = new Intent(getApplicationContext(), ToReadBooksActivity.class);
                startActivity(toReadIntent);
        }

        return super.onOptionsItemSelected(item);
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

                    // Clear stored results
                    app.bookResultsDb.removeValue();

                    if (response.getInt("totalItems") != 0) {
                        // Get the items json array
                        JSONArray items = response.getJSONArray("items");

                        // Parse json array into array of Book objects
                        app.booksResults = Book.fromJson(items);

                        // Store search results on Firebase
                        for (Book bookResult : app.booksResults) {
                            app.bookResultsDb.child(bookResult.getBookId()).setValue(bookResult);
                        }
                    } else {
                        // Clear results
                        app.booksResults.clear();
                        app.bookResultsDb.removeValue();
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

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }

        // Show 'To Read' MenuItem if there are books in the list
        if(!app.booksToRead.isEmpty()){
            MenuItem toReadItem = menu.findItem(R.id.action_to_read);
            toReadItem.setVisible(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
