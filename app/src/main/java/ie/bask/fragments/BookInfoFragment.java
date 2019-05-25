package ie.bask.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.annotations.NotNull;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import ie.bask.R;
import ie.bask.activities.MainActivity;
import ie.bask.adapters.PicassoTrustAll;
import ie.bask.main.BookopediaApp;
import ie.bask.models.Book;
import ie.bask.models.Coordinates;
import mumayank.com.airlocationlibrary.AirLocation;


public class BookInfoFragment extends Fragment {
    private ImageView ivBookCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvPublisher;
    private TextView tvPageCount;
    private TextView tvDescription;
    private Button btnToRead;
    private Button btnRemove;
    private TextView tvDateAdded;
    private TextView tvNotes;
    private View hrView;
    private TextView tvNotesLabel;
    private Button btnAddNotes;
    private boolean bookInList;
    private FrameLayout mapFrame;
    private BookopediaApp app = BookopediaApp.getInstance();
    private Book book;
    private Context mContext;

    public static BookInfoFragment newInstance(Book book) {
        BookInfoFragment fragment = new BookInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable("book_info_key", book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = (Book) getArguments().getSerializable("book_info_key");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.book_info, container, false);
        ivBookCover = view.findViewById(R.id.ivBookCover);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvAuthor = view.findViewById(R.id.tvAuthor);
        tvPublisher = view.findViewById(R.id.tvPublisher);
        tvPageCount = view.findViewById(R.id.tvPageCount);
        tvDescription = view.findViewById(R.id.tvDescription);
        btnToRead = view.findViewById(R.id.btnToRead);
        btnRemove = view.findViewById(R.id.btnRemove);
        tvDateAdded = view.findViewById(R.id.tvDateAdded);
        tvNotes = view.findViewById(R.id.tvNotes);
        hrView = view.findViewById(R.id.hrView2);
        tvNotesLabel = view.findViewById(R.id.tvNotesLabel);
        btnAddNotes = view.findViewById(R.id.btnAddNotes);
        mapFrame = view.findViewById(R.id.mapFrame);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("book_info_key")) {
            book = (Book) getArguments().getSerializable("book_info_key");
            loadBook(book);
        }

        // Listen for clicks
        setButtonListeners(book);
        setAddNoteListener(book);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    // Populate data for the book
    private void loadBook(Book book) {
        if (!book.toReadStatus()) {
            // Change activity title
            ((AppCompatActivity) mContext).setTitle(book.getTitle());
            btnToRead.setVisibility(View.VISIBLE);
            btnRemove.setVisibility(View.GONE);
            hrView.setVisibility(View.VISIBLE);
            tvDateAdded.setVisibility(View.GONE);
            tvNotesLabel.setVisibility(View.GONE);
            btnAddNotes.setVisibility(View.GONE);
            tvNotes.setVisibility(View.GONE);

            // Populate data
            PicassoTrustAll.getInstance(mContext)
                    .load(Uri.parse(book.getImageLink()))
                    .fit()
                    .centerInside()
                    .error(R.drawable.ic_nocover)
                    .into(ivBookCover);
            tvTitle.setText(book.getTitle());
            tvAuthor.setText(book.getAuthor());
            tvPublisher.setText(String.format(getResources().getString(R.string.publisher), book.getPublisher()));
            tvPageCount.setText(String.format(getResources().getString(R.string.page_count), book.getNumPages()));
            tvDescription.setText(book.getDescription());

            for(Book elem: app.booksToRead){
                if(elem.getBookId().equals(book.getBookId())){
                    btnToRead.setText(getString(R.string.book_added));
                    btnToRead.getBackground().setColorFilter(mContext.getColor(R.color.md_green_200), PorterDuff.Mode.MULTIPLY);
                    bookInList = true;
                } else {
                    btnToRead.setText(getString(R.string.to_read_button));
                    btnToRead.getBackground().setColorFilter(Color.parseColor("#ffd6d7d7"), PorterDuff.Mode.MULTIPLY);
                    bookInList = false;
                }
            }

            // Hide map layout
            mapFrame.setVisibility(View.GONE);
        } else {
            // Change activity title
            ((AppCompatActivity) mContext).setTitle(book.getTitle());
            // Hide/show widgets
            btnToRead.setVisibility(View.GONE);
            btnRemove.setVisibility(View.VISIBLE);
            hrView.setVisibility(View.VISIBLE);
            tvDateAdded.setVisibility(View.VISIBLE);
            btnAddNotes.setVisibility(View.VISIBLE);
            tvNotes.setVisibility(View.VISIBLE);

            // Populate data
            PicassoTrustAll.getInstance(mContext)
                    .load(Uri.parse(book.getImageLink()))
                    .fit()
                    .centerInside()
                    .error(R.drawable.ic_nocover)
                    .into(ivBookCover);
            tvTitle.setText(book.getTitle());
            tvAuthor.setText(book.getAuthor());
            tvPublisher.setText(String.format(getResources().getString(R.string.publisher), book.getPublisher()));
            tvPageCount.setText(String.format(getResources().getString(R.string.page_count), book.getNumPages()));
            tvDescription.setText(book.getDescription());
            tvDateAdded.setText(String.format(getResources().getString(R.string.added_on), book.getDateAdded()));
            if (!book.getNotes().equals("0")) {
                tvNotes.setText(book.getNotes());
            }

            // Show map with book location
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.mapFrame, MapsFragment.newInstance(book));
            ft.commit();
        }
    }

    private void setButtonListeners(final Book book) {
        btnToRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!bookInList) {
                    // Fetch location
                    new AirLocation(((AppCompatActivity) mContext), true, true, new AirLocation.Callbacks() {
                        @Override
                        public void onSuccess(@NotNull Location location) {
                            // Store latitude and longitude
                            Coordinates coordinates = new Coordinates(location.getLatitude(), location.getLongitude());
                            // Get current date and time
                            String currentDate = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
                            // Create a book object
                            Book bookToRead = new Book(book.getBookId(), book.getAuthor(), book.getTitle(), book.getImageLink(),
                                    book.getDescription(), book.getPublisher(), book.getNumPages(), currentDate, "0", true, coordinates);
                            addBookToRead(bookToRead);

                            bookInList = true;
                            btnToRead.setText(getString(R.string.book_added));
                            btnToRead.getBackground().setColorFilter(mContext.getColor(R.color.md_green_200), PorterDuff.Mode.MULTIPLY);
                        }

                        @Override
                        public void onFailed(@NotNull AirLocation.LocationFailedEnum locationFailedEnum) {
                            Log.v("Bookopedia", locationFailedEnum.toString());
                        }
                    });
                } else {
                    removeBookToRead(book);
                    bookInList = false;
                    btnToRead.setText(getString(R.string.to_read_button));
                    btnToRead.getBackground().setColorFilter(Color.parseColor("#ffd6d7d7"), PorterDuff.Mode.MULTIPLY);
                }
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeBookToRead(book);
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    MainActivity mainActivity = MainActivity.getInstance();
                    mainActivity.selectDrawerItem(MainActivity.nvDrawer.getMenu().findItem(R.id.nav_home));
                }
            }
        });
    }

    private void addBookToRead(Book book) {
        // Adding the book to Firebase Database
        app.booksToReadDb.child(book.getBookId()).setValue(book);
        app.booksToRead.add(book);
        Snackbar.make(((AppCompatActivity) mContext).findViewById(R.id.drawer_layout), "Book added to your Wishlist!", Snackbar.LENGTH_SHORT).show();
        MainActivity.nvDrawer.getMenu().findItem(R.id.nav_wishlist).setEnabled(true);
    }

    private void removeBookToRead(Book book) {
        // Removing the book from Firebase Database and local ArrayList
        for (Iterator<Book> iterator = app.booksToRead.iterator(); iterator.hasNext();){
            Book value = iterator.next();
            if (value.getBookId().equals(book.getBookId())) {
                iterator.remove();
            }
        }
        app.booksToReadDb.child(book.getBookId()).removeValue();
        Snackbar.make(((AppCompatActivity) mContext).findViewById(R.id.drawer_layout), "Book removed from your Wishlist!", Snackbar.LENGTH_SHORT).show();
        MainActivity.nvDrawer.getMenu().findItem(R.id.nav_wishlist).setEnabled(false);
    }

    public void setAddNoteListener(final Book book) {
        tvNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(mContext);
                final ViewGroup nullParent = null;
                final View promptsView = li.inflate(R.layout.alert_dialog, nullParent);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                // Set layout alert_dialog.xml to AlertDialog
                alertDialogBuilder.setView(promptsView);

                EditText userInput = promptsView.findViewById(R.id.etUserInput);
                userInput.setText(book.getNotes().equals("0") ? "" : tvNotes.getText().toString());
                userInput.requestFocus();

                // Set dialog messages
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Get user input and set it to tvNotes
                                EditText userInput = promptsView.findViewById(R.id.etUserInput);
                                tvNotes.setText(userInput.getText().toString().trim());
                                app.booksToReadDb.child(book.getBookId()).child("notes").setValue(userInput.getText().toString().trim());
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // Create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        btnAddNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(mContext);
                final ViewGroup nullParent = null;
                View promptsView = li.inflate(R.layout.alert_dialog, nullParent);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                // Set layout alert_dialog.xml to AlertDialog
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = promptsView.findViewById(R.id.etUserInput);
                userInput.setText(book.getNotes().equals("0") ? "" : tvNotes.getText().toString());
                userInput.requestFocus();

                // Set dialog messages
                alertDialogBuilder
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Get user input and set it to tvNotes
                                tvNotes.setText(userInput.getText().toString());
                                app.booksToReadDb.child(book.getBookId()).child("notes").setValue(userInput.getText().toString().trim());
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // Create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }


}