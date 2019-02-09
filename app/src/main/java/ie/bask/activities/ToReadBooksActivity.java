package ie.bask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import ie.bask.R;
import ie.bask.adapters.BookAdapter;

public class ToReadBooksActivity extends Base {

    private ListView lvBooksToRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_read_books);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lvBooksToRead = findViewById(R.id.lvToReadBooks);
        pbSearch = findViewById(R.id.pbSearch);
        bookAdapter = new BookAdapter(this, booksToRead);
        lvBooksToRead.setAdapter(bookAdapter);

        setOnBookClickListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final MenuItem homeItem = menu.findItem(R.id.action_home);
        final MenuItem toReadItem = menu.findItem(R.id.action_to_read);
        toReadItem.setVisible(false);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(android.R.attr.width);

        // Hide home button icon if SearchView is opened
        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewDetachedFromWindow(View arg0) {
                // search was detached/closed
                homeItem.setVisible(true);
            }

            @Override
            public void onViewAttachedToWindow(View arg0) {
                // search was opened
                homeItem.setVisible(false);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case(R.id.action_home):
                Intent homeIntent = new Intent(getApplicationContext(), BookListActivity.class);
                startActivity(homeIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void setOnBookClickListener() {
        lvBooksToRead.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Launch the BookInfoActivity activity passing book as an extra
                Intent intent = new Intent(getApplicationContext(), BookInfoActivity.class);
                intent.putExtra("book_info_key", bookAdapter.getItem(position));
                startActivity(intent);
            }
        });
    }

}
