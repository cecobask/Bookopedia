package ie.bask.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davidecirillo.multichoicerecyclerview.MultiChoiceAdapter;

import java.util.ArrayList;

import ie.bask.PicassoTrustAll;
import ie.bask.R;
import ie.bask.activities.BookInfoActivity;
import ie.bask.models.Book;

public class BookAdapter extends MultiChoiceAdapter<BookViewHolder> {

    private ArrayList<Book> booksArray;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public BookAdapter(Context context, ArrayList<Book> booksArray) {
        this.mInflater = LayoutInflater.from(context);
        this.booksArray = booksArray;
    }


    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.book_item, parent, false);

        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BookViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        Book book = booksArray.get(position);
        // Populate the data into the template view using the Book object
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());

        // Use custom Picasso instance to fetch book cover
        PicassoTrustAll.getInstance(holder.ivCover.getContext())
                .load(Uri.parse(book.getImageLink()))
                .fit().centerInside().error(R.drawable.ic_nocover).into(holder.ivCover);

        if (book.getDateAdded() != null) {
            holder.tvDateAdded.setText(book.getDateAdded());
        }
    }

    @Override
    public int getItemCount() {
        return booksArray.size();
    }

    @Override
    protected View.OnClickListener defaultItemViewClickListener(final BookViewHolder holder, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the BookInfoActivity activity passing book as an extra
                Intent intent = new Intent(holder.tvTitle.getContext(), BookInfoActivity.class);
                intent.putExtra("book_info_key", booksArray.get(position));
                intent.putExtra("ToReadContext", "ToReadContext");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.tvTitle.getContext().startActivity(intent);
            }
        };


    }

}