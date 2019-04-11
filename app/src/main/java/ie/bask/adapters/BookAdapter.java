package ie.bask.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ie.bask.R;
import ie.bask.activities.MainActivity;
import ie.bask.fragments.BookInfoFragment;
import ie.bask.main.BookopediaApp;
import ie.bask.models.Book;

public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {

    ArrayList<Book> booksArray;
    private LayoutInflater mInflater;
    private final Context context;
    private boolean multiSelect = false;
    private ArrayList<Book> selectedItems = new ArrayList<>();
    private Handler handler = new Handler();
    private Runnable runnable;
    private Book mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;
    private MainActivity mainActivity = MainActivity.getInstance();
    private BookopediaApp app = BookopediaApp.getInstance();

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
            holder.itemView.findViewById(R.id.rel_layout).setBackgroundColor(Color.TRANSPARENT);
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
                                            app.booksToReadDb.child(book.getBookId()).removeValue();
                                            multiSelect = false;
                                            // Delay the notifyDataSetChanged command. This gives
                                            // the RecyclerView enough time to respond to changes.
                                            handler.removeCallbacks(runnable);
                                            runnable = new Runnable() {
                                                @Override
                                                public void run() {
                                                    notifyItemRemoved(holder.getAdapterPosition());
                                                }
                                            };
                                            handler.postDelayed(runnable, 500);
                                        }
                                        actionMode.finish();

                                        if (booksArray.isEmpty()){
                                            mainActivity.selectDrawerItem(MainActivity.nvDrawer.getMenu().findItem(R.id.nav_home));
                                        }
                                    }
                                });
                        alertDialog.show();
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode actionMode) {
                        multiSelect = false;
                        selectedItems.clear();
                        holder.itemView.findViewById(R.id.rel_layout).setBackgroundColor(Color.TRANSPARENT);
                        notifyDataSetChanged();
                        Log.v("Bookopedia", "booksArray: "+booksArray.size());
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
                    // Launch the BookInfoFragment passing book as an extra
                    BookInfoFragment fragment = BookInfoFragment.newInstance(booksArray.get(holder.getAdapterPosition()));
                    ((AppCompatActivity) context).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flContent, fragment)
                            .commit();

                    // Set current fragment
                    MainActivity.currentFragment = fragment;
                    NavigationView nvDrawer = MainActivity.nvDrawer;
                    nvDrawer.getMenu().findItem(R.id.nav_wishlist).setChecked(false);
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
                itemView.findViewById(R.id.rel_layout).setBackgroundColor(Color.TRANSPARENT);
            } else {
                selectedItems.add(book);
                itemView.findViewById(R.id.rel_layout).setBackgroundColor(context.getColor(R.color.selected_item));
            }
        }
    }

    void deleteItem(int position) {
        mRecentlyDeletedItem = booksArray.get(position);
        mRecentlyDeletedItemPosition = position;
        app.booksToReadDb.child(mRecentlyDeletedItem.getBookId()).removeValue();
        booksArray.remove(position);
        notifyItemRemoved(position);

        if (booksArray.isEmpty()){
            mainActivity.selectDrawerItem(MainActivity.nvDrawer.getMenu().findItem(R.id.nav_home));
        } else {
            showUndoSnackbar();
        }
    }

    public Context getContext() {
        return context;
    }

    private void showUndoSnackbar() {
        View view = ((AppCompatActivity)context).findViewById(R.id.drawer_layout);
        Snackbar snackbar = Snackbar.make(view, "Book deleted.", 3000);
        snackbar.setAction("Undo deletion?", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undoDelete();
            }
        });
        snackbar.show();
    }

    private void undoDelete() {
        booksArray.add(mRecentlyDeletedItemPosition, mRecentlyDeletedItem);
        app.booksToReadDb.child(mRecentlyDeletedItem.getBookId()).setValue(mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
    }

}