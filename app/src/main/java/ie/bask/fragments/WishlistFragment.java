package ie.bask.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
    public BookAdapter bookAdapter;
    private BookopediaApp app = BookopediaApp.getInstance();
    private RecyclerView rvBooksToRead;

    public static WishlistFragment newInstance() {
        return new WishlistFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    public void onResume() {
        super.onResume();

        // Used when user deletes a book and returns to this fragment
        bookAdapter.notifyDataSetChanged();

        // Close fragment if there are no more books in the wishlist
        if (app.booksToRead.isEmpty()) {
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager != null) {
                fragmentManager.beginTransaction().replace(R.id.flContent, BookSearchFragment.newInstance()).commit();
            }
        }
    }
}
