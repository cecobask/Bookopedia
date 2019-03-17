package ie.bask.main;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import ie.bask.models.Book;

public class BookopediaApp extends Application {

    public ArrayList<Book> booksToRead = new ArrayList<>();
    public ArrayList<Book> booksResults = new ArrayList<>();
    public DatabaseReference booksToReadDb;
    public DatabaseReference bookResultsDb;
    public DatabaseReference usersDb;
    public FirebaseAuth firebaseAuth;
    public GoogleSignInClient mGoogleSignInClient;
    private static BookopediaApp mInstance;

    // Called when the application is starting, before any other application objects have been created.
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        mInstance = this;
        usersDb = FirebaseDatabase.getInstance().getReference("users");
        Log.v("Bookopedia", "BookopediaApp started.");
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

    public static BookopediaApp getInstance() { return mInstance; }

}