package ie.bask.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ie.bask.PicassoTrustAll;
import ie.bask.R;
import ie.bask.activities.BookInfoActivity;
import ie.bask.main.BookopediaApp;
import ie.bask.models.Book;

public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {

    ArrayList<Book> booksArray;
    private LayoutInflater mInflater;
    private Context context;
    private boolean multiSelect = false;
    private ArrayList<Book> selectedItems = new ArrayList<>();

    // data is passed into the constructor
    public BookAdapter(Context context, ArrayList<Book> booksArray) {
        this.mInflater = LayoutInflater.from(context);
        this.booksArray = booksArray;
        this.context = context;
    }


    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.book_item, parent, false);

        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BookViewHolder holder, final int position) {
        final Book book = booksArray.get(holder.getAdapterPosition());
        holder.itemView.setTag(book.getBookId());
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

        // Deselect selection when Action bar closes
        if(!multiSelect){
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        // Long click listener for starting ActionMode
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((AppCompatActivity) context).startSupportActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        multiSelect = true;
                        MenuInflater inflater = actionMode.getMenuInflater();
                        // Replace default menu
                        inflater.inflate(R.menu.menu_multi, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem) {
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setMessage("Delete selected books?");
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        for (Book book : selectedItems) {
                                            // Delete selected books
                                            booksArray.remove(book);
                                            BookopediaApp.getInstance().booksToReadDb.child(book.getBookId()).removeValue();
                                            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                                            multiSelect = false;
                                        }
                                        notifyDataSetChanged();
                                        actionMode.finish();
                                    }
                                });
                        alertDialog.show();
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode actionMode) {
                        multiSelect = false;
                        notifyDataSetChanged();
                        selectedItems.clear();
                    }
                });
                selectItem(book, holder.itemView);
                return true;
            }
        });

        // Click listener actions depending if multiSelect is active
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(multiSelect) {
                    selectItem(book, holder.itemView);
                } else {
                    // Launch the BookInfoActivity activity passing book as an extra
                    Intent intent = new Intent(context, BookInfoActivity.class);
                    intent.putExtra("book_info_key", booksArray.get(holder.getAdapterPosition()));
                    intent.putExtra("ToReadContext", "ToReadContext");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    holder.tvTitle.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return booksArray.size();
    }

    private void selectItem(Book book, View itemView) {
        if (multiSelect) {
            if (selectedItems.contains(book)) {
                selectedItems.remove(book);
                itemView.setBackgroundColor(Color.TRANSPARENT);
            } else {
                selectedItems.add(book);
                itemView.setBackgroundColor(context.getColor(R.color.md_blue_200));
            }
        }
    }


}