package ie.bask.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import ie.bask.R;
import ie.bask.activities.BookScannerActivity;
import ie.bask.activities.MainActivity;
import ie.bask.adapters.BookViewHolder;
import ie.bask.adapters.PicassoTrustAll;
import ie.bask.main.BookClient;
import ie.bask.main.BookopediaApp;
import ie.bask.models.Book;


public class BookSearchFragment extends Fragment {

    private FirebaseRecyclerAdapter adapter;
    private ImageView slogan;
    private FloatingActionButton fab;
    private List<String> mPermDeniedList = new ArrayList<>();
    private final BookopediaApp app = BookopediaApp.getInstance();
    private ProgressBar pbSearch;
    private TextView tvNoResults;
    private RecyclerView rvBooks;
    private Context mContext;
    private ValueEventListener valueEventListener;
    private ConstraintLayout mLayout;

    public static BookSearchFragment newInstance() {
        return new BookSearchFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        Log.v("Bookopedia", "current: " + MainActivity.currentFragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.book_search, container, false);
        pbSearch = view.findViewById(R.id.pbSearch);
        slogan = view.findViewById(R.id.slogan);
        fab = view.findViewById(R.id.fab_scan);
        tvNoResults = view.findViewById(R.id.tvNoResults);
        rvBooks = view.findViewById(R.id.rvBooks);
        mLayout = view.findViewById(R.id.root);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    adapter.startListening();
                    tvNoResults.setText(null);
                    slogan.setVisibility(View.GONE);
                    fab.hide();
                    app.booksResults.clear();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        app.booksResults.add(child.getValue(Book.class));
                    }
                    ((AppCompatActivity) mContext).invalidateOptionsMenu();
                } else {
                    tvNoResults.setVisibility(View.VISIBLE);
                    tvNoResults.setText(getString(R.string.welcome_message));
                    slogan.setVisibility(View.VISIBLE);
                    fab.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Bookopedia", "" + databaseError);
            }
        };

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        // Set listener for barcode scanner button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the user has provided permissions
                if (isPermGranted(Manifest.permission.READ_EXTERNAL_STORAGE) &&
                        isPermGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                        isPermGranted(Manifest.permission.CAMERA)) {
                    Intent scanIntent = new Intent(view.getContext(), BookScannerActivity.class);
                    // Open barcode scanner activity
                    startActivityForResult(scanIntent, 1000);
                } else {
                    // Add permission to List
                    mPermDeniedList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                    // Request user permission for READ_EXTERNAL_STORAGE
                    requestPermissions(mPermDeniedList);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseRecyclerOptions<Book> options =
                new FirebaseRecyclerOptions.Builder<Book>()
                        .setQuery(app.bookResultsDb, Book.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Book, BookViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final BookViewHolder holder, int position, @NonNull Book book) {
                book = getItem(holder.getAdapterPosition());
                // Populate the data into the template view using the Book object
                holder.tvTitle.setText(book.getTitle());
                holder.tvAuthor.setText(book.getAuthor());

                // Use custom Picasso instance to fetch book cover
                PicassoTrustAll.getInstance(mContext)
                        .load(Uri.parse(book.getImageLink()))
                        .fit().centerInside().error(R.drawable.ic_nocover).into(holder.ivCover);

                if (book.getDateAdded() != null) {
                    holder.tvDateAdded.setText(book.getDateAdded());
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Launch the BookInfoFragment passing book as an extra
                        Book bookExtra = null;
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        String clickedID = getItem(holder.getAdapterPosition()).getBookId();
                        boolean no_match = true;
                        // Loop through books in list
                        for (Book elem : app.booksToRead) {
                            if (elem.getBookId().equals(clickedID)) {
                                bookExtra = elem;
                                no_match = false;
                                break;
                            }
                        }
                        // Mean book is not in the list
                        if (no_match) {
                            bookExtra = getItem(holder.getAdapterPosition());
                        }
                        BookInfoFragment fragment = BookInfoFragment.newInstance(bookExtra);
                        transaction.replace(R.id.flContent, fragment).commit();

                        // Set current fragment
                        MainActivity.currentFragment = fragment;
                        NavigationView nvDrawer = MainActivity.nvDrawer;
                        nvDrawer.getMenu().findItem(R.id.nav_home).setChecked(false);
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

        rvBooks.setLayoutManager(new LinearLayoutManager(mContext));
        rvBooks.setAdapter(adapter);
    }

    // Executes an API call to the Google Books search endpoint, parses the results
    // Converts them into an array of book objects and adds them to the adapter
    public void getBooks(String query) {
        // Show progress bar if search query is not empty
        if (query.length() > 0) {
            mLayout.setAlpha(0.3f);
            pbSearch.setVisibility(View.VISIBLE);
        }

        BookClient client = new BookClient();
        client.getBooks(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // Hide progress bar and TextView
                    mLayout.setAlpha(1f);
                    pbSearch.setVisibility(View.GONE);
                    tvNoResults.setText(null);
                    slogan.setVisibility(View.GONE);
                    fab.hide();

                    // Clear stored results
                    app.bookResultsDb.removeValue();
                    app.booksResults.clear();

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
                        tvNoResults.setVisibility(View.VISIBLE);
                        tvNoResults.setText(getString(R.string.no_results));
                        slogan.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    mLayout.setAlpha(1f);
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
    public void onResume() {
        super.onResume();
        // Attach ValueEventListener to bookResultsDB
        app.bookResultsDb.addValueEventListener(valueEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }

        // Remove ValueEventListener from bookResultsDB
        app.bookResultsDb.removeEventListener(valueEventListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    private void requestPermissions(List<String> access) {
        // Convert List to Array for use in requesting permissions
        String[] stringArray = access.toArray(new String[0]);
        ActivityCompat.requestPermissions((Activity) mContext, stringArray, 1002);
    }

    // Method to check if a permission is granted
    private boolean isPermGranted(String perm) {
        return ContextCompat.checkSelfPermission(mContext, perm) == PackageManager.PERMISSION_GRANTED;
    }


}
