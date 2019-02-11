package ie.bask.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import ie.bask.PicassoTrustAll;
import ie.bask.R;
import ie.bask.main.Base;
import ie.bask.main.BookopediaApp;
import ie.bask.models.Book;


public class BookInfoActivity extends Base {
    private ImageView ivBookCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvPublisher;
    private TextView tvPageCount;
    private TextView tvDescription;
    private Button btnToRead;
    private TextView tvDateAdded;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        app = (BookopediaApp) getApplication();
        ivBookCover = findViewById(R.id.ivBookCover);
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvPublisher = findViewById(R.id.tvPublisher);
        tvPageCount = findViewById(R.id.tvPageCount);
        tvDescription = findViewById(R.id.tvDescription);
        btnToRead = findViewById(R.id.btnToRead);
        tvDateAdded = findViewById(R.id.tvDateAdded);

        // Use the book to populate the data into our views
        Book book = (Book) getIntent().getSerializableExtra("book_info_key");
        loadBook(book);

//        loadBooks();

        // Listen for clicks on the Add to Read List button
        setBookToReadListener();
    }

    private void setBookToReadListener() {
        btnToRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use the book to populate the data into our views
                Book book = (Book) getIntent().getSerializableExtra("book_info_key");
                String currentDate = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
                Book bookToRead = new Book(book.getBookId(),book.getAuthor(),book.getTitle(),book.getImageLink(),
                        book.getDescription(),book.getPublisher(),book.getNumPages(),currentDate);
                addBookToRead(bookToRead);
                MenuItem toReadItem = menu.findItem(R.id.action_to_read);
                toReadItem.setVisible(true);
            }
        });
    }

    // Populate data for the book
    private void loadBook(Book book) {
        if(book.getDateAdded()==null) {
            //change activity title
            this.setTitle(book.getTitle());
            btnToRead.setVisibility(View.VISIBLE);
            tvDateAdded.setVisibility(View.GONE);
            // Populate data
            PicassoTrustAll.getInstance(getApplicationContext())
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
        } else {
            //change activity title
            this.setTitle(book.getTitle());
            btnToRead.setVisibility(View.GONE);
            tvDateAdded.setVisibility(View.VISIBLE);
            // Populate data
            tvDateAdded.setText(String.format(getResources().getString(R.string.added_on), book.getDateAdded()));

            PicassoTrustAll.getInstance(getApplicationContext())
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
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final MenuItem toReadItem = menu.findItem(R.id.action_to_read);
        final MenuItem clearBooksItem = menu.findItem(R.id.action_clear);

        // Hide menu items
        searchItem.setVisible(false);
        clearBooksItem.setVisible(false);
        if(app.booksToRead.isEmpty()){
            toReadItem.setVisible(false);
        }

        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case(R.id.action_home):
                app.booksList.clear();
                Intent goHome = new Intent(getApplicationContext(),BookListActivity.class);
                startActivity(goHome);
                break;
            case (R.id.action_to_read):
                Intent toReadIntent = new Intent(getApplicationContext(), ToReadBooksActivity.class);
                startActivity(toReadIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void addBookToRead(Book book) {
        // Adding the book to Firebase Database
        app.booksToReadDb.child(book.getBookId()).setValue(book);
        app.booksToRead.add(book);
        Log.e("Bookopedia", book.toString());
        Toast.makeText(getApplicationContext(),"Book added to TO READ list", Toast.LENGTH_SHORT).show();
    }
}