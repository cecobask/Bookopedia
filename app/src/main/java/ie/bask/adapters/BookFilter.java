package ie.bask.adapters;

import android.widget.Filter;

import java.util.ArrayList;

import ie.bask.models.Book;

public class BookFilter extends Filter {
    private final ArrayList<Book> originalBookList;
    private final BookAdapter adapter;

    public BookFilter(ArrayList<Book> originalBookList, BookAdapter adapter) {
        super();
        this.originalBookList = originalBookList;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence prefix) {
        FilterResults results = new FilterResults();

        if (prefix == null || prefix.length() == 0) {
            results.values = originalBookList;
            results.count = originalBookList.size();
        } else {
            ArrayList<Book> newBooks = new ArrayList<>();
            String bookTitle;
            String prefixString = prefix.toString().toLowerCase();
            for (Book b : originalBookList) {
                bookTitle = b.getTitle().toLowerCase();
                if (bookTitle.contains(prefixString)) {
                    newBooks.add(b);
                }
            }
            results.values = newBooks;
            results.count = newBooks.size();
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence prefix, FilterResults results) {

        adapter.booksArray = (ArrayList<Book>) results.values;

        if (results.count >= 0)
            adapter.notifyDataSetChanged();
        else {
            adapter.notifyDataSetChanged();
            adapter.booksArray = originalBookList;
        }
    }
}