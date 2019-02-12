package ie.bask.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ie.bask.PicassoTrustAll;
import ie.bask.R;
import ie.bask.activities.BookInfoActivity;
import ie.bask.models.Book;

public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the BookInfoActivity activity passing book as an extra
                Intent intent = new Intent(holder.tvTitle.getContext(), BookInfoActivity.class);
                intent.putExtra("book_info_key", booksArray.get(position));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.tvTitle.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return booksArray.size();
    }
}