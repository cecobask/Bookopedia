package ie.bask.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import ie.bask.PicassoTrustAll;
import ie.bask.R;
import ie.bask.adapters.BookViewHolder;
import ie.bask.main.Base;
import ie.bask.main.BookopediaApp;
import ie.bask.models.Book;

public class ToReadBooksActivity extends Base {

    private RecyclerView rvBooksToRead;
    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_read_books);
        app = (BookopediaApp) getApplication();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rvBooksToRead = findViewById(R.id.rvBooksToRead);
        pbSearch = findViewById(R.id.pbSearch);

        FirebaseRecyclerOptions<Book> options =
                new FirebaseRecyclerOptions.Builder<Book>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("booksToRead"), Book.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Book,BookViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BookViewHolder holder, final int position, @NonNull Book book) {
                book = getItem(position);
                // Populate the data into the template view using the Book object
                holder.tvTitle.setText(book.getTitle());
                holder.tvAuthor.setText(book.getAuthor());

                // Use custom Picasso instance to fetch book cover
                PicassoTrustAll.getInstance(getApplicationContext())
                        .load(Uri.parse(book.getImageLink()))
                        .fit().centerInside().error(R.drawable.ic_nocover).into(holder.ivCover);

                if(book.getDateAdded()!=null){
                    holder.tvDateAdded.setText(book.getDateAdded());
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Launch the BookInfoActivity activity passing book as an extra
                Intent intent = new Intent(getApplicationContext(), BookInfoActivity.class);
                intent.putExtra("book_info_key", getItem(position));
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

        rvBooksToRead.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter.startListening();
        rvBooksToRead.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final MenuItem homeItem = menu.findItem(R.id.action_home);
        final MenuItem toReadItem = menu.findItem(R.id.action_to_read);
        final MenuItem clearBooksItem = menu.findItem(R.id.action_clear);
        toReadItem.setVisible(false);
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

        switch(id){
            case(R.id.action_home):
                app.booksList.clear();
                startActivity(homeIntent);
                break;
            case(R.id.action_clear):
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
    protected void onStart() {
        super.onStart();
        if(adapter!=null){
            adapter.startListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null) {
            adapter.stopListening();
        }
    }
}
