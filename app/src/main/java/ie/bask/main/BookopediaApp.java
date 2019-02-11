package ie.bask.main;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ie.bask.models.Book;

public class BookopediaApp extends Application {

    public ArrayList<Book> booksToRead = new ArrayList<>();
    public ArrayList<Book> booksList = new ArrayList<>();
    public DatabaseReference booksToReadDb;


    // Called when the application is starting, before any other application objects have been created.
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("Bookopedia", "BookopediaApp started.");
        loadBooks();
    }

    // Called by the system when the device configuration changes while your component is running.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    // Load Firebase database
    public void loadBooks() {
        // Getting the reference of booksToRead node
        booksToReadDb = FirebaseDatabase.getInstance().getReference("booksToRead");
        // Attaching value event listener
        booksToReadDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Clearing the previous Books list
                booksToRead.clear();

                // Iterating through all the nodes
                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    Book book = bookSnapshot.getValue(Book.class);
                    // Adding Book to the list
                    booksToRead.add(book);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Bookopedia", ""+databaseError);
            }
        });
    }
}