package ie.bask.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ie.bask.R;
import ie.bask.adapters.BookAdapter;
import ie.bask.adapters.BookFilter;
import ie.bask.main.BookopediaApp;

public class WishlistFragment extends Fragment {

    public BookFilter bookFilter;
    private BookAdapter bookAdapter;
    private BookopediaApp app = BookopediaApp.getInstance();
    private RecyclerView rvBooksToRead;

    public static WishlistFragment newInstance() {
//        Bundle args = new Bundle();
//        args.putString(KEY_NAME, var_name);
//        fragment.setArguments(args);
        return new WishlistFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            name_name = getArguments().getString(KEY_NAME);
//        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_to_read_books, container, false);
        rvBooksToRead = view.findViewById(R.id.rvBooksToRead);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Display books in a RecyclerView

        bookAdapter = new BookAdapter(getContext(), app.booksToRead);
        bookFilter = new BookFilter(app.booksToRead, bookAdapter);
        rvBooksToRead.setAdapter(bookAdapter);
        rvBooksToRead.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        final MenuItem searchItem = menu.findItem(R.id.action_search);
//        final MenuItem homeItem = menu.findItem(R.id.action_home);
//        final MenuItem toReadItem = menu.findItem(R.id.action_to_read);
//        final MenuItem clearBooksItem = menu.findItem(R.id.action_clear);
//        final MenuItem deleteBookItem = menu.findItem(R.id.action_delete);
//        final MenuItem logoutItem = menu.findItem(R.id.action_logout);
//        toReadItem.setVisible(false);
//        deleteBookItem.setVisible(false);
//        final SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.setMaxWidth(android.R.attr.width);
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                bookFilter.filter(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//                bookFilter.filter(query);
//                return false;
//            }
//        });
//
//        // Hide home button icon if SearchView is opened
//        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
//            @Override
//            public void onViewDetachedFromWindow(View arg0) {
//                // search was detached/closed
//                homeItem.setVisible(true);
//                clearBooksItem.setVisible(true);
//                logoutItem.setVisible(true);
//            }
//
//            @Override
//            public void onViewAttachedToWindow(View arg0) {
//                // search was opened
//                homeItem.setVisible(false);
//                clearBooksItem.setVisible(false);
//                logoutItem.setVisible(false);
//            }
//        });
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        final Intent homeIntent = new Intent(WishlistFragment.this, BookSearchFragment.class);
//
//        switch (id) {
//            case (R.id.action_home):
//                startActivity(homeIntent);
//                break;
//            case (R.id.action_clear):
//                showDialog(WishlistFragment.this, "Delete all books?", "deleteAllBooks");
//                break;
//            case (R.id.action_logout):
//                showDialog(WishlistFragment.this, "You are about to log out. Proceed?", "signOut");
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onResume() {
        super.onResume();
        if (app.booksToRead.isEmpty()) {
//            FragmentManager fragmentManager = getFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.flContent,BookSearchFragment.newInstance()).commit();
        }

        // Used when user deletes a book and returns to this activity
        bookAdapter.notifyDataSetChanged();
    }
}
