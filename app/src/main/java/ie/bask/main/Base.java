package ie.bask.main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ie.bask.R;
import ie.bask.activities.LoginActivity;
import ie.bask.activities.MainActivity;
import ie.bask.fragments.BookSearchFragment;
import ie.bask.fragments.WishlistFragment;
import ie.bask.models.Book;

public class Base extends AppCompatActivity {

    public BookopediaApp app;

    @Override
    public void onBackPressed() {
        if(isTaskRoot()){
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setMessage("Exit application?");
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
                            finish();
                        }
                    });
            alertDialog.show();
        } else {
            super.onBackPressed();
        }

    }

    public void signOut(Context context) {
        app.firebaseAuth.signOut();
        app.mGoogleSignInClient.signOut();
        Intent loginIntent = new Intent(context, LoginActivity.class);
        startActivity(loginIntent);
        finishAffinity();
    }

    public void deleteBook(final Context context) {
        // Remove book from Firebase Database and local ArrayList
        Book book = (Book) getIntent().getSerializableExtra("book_info_key");
        app.booksToRead.remove(book);
        app.booksToReadDb.child(book.getBookId()).removeValue();
        app.booksToReadDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // If the user deletes the last book in his list
                if (!dataSnapshot.exists()) {
                    finishAffinity();
                    startActivity(new Intent(getApplicationContext(), BookSearchFragment.class));
                } else {
                    ((Activity) context).finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Bookopedia", "" + databaseError);
            }
        });
    }

    public void deleteAllBooks(Context context, String fragment) {
        if(fragment.equals("searchFragment")){
            app.booksResults.clear();
            app.bookResultsDb.removeValue();
        } else if (fragment.equals("")) {
            app.booksToReadDb.removeValue();
            app.booksToRead.clear();
            Toast.makeText(context, "All books deleted", Toast.LENGTH_SHORT).show();
            // Start the search fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            BookSearchFragment bookSearchFragment = BookSearchFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.flContent, bookSearchFragment).commit();
            MainActivity mainActivity = MainActivity.getInstance();
            mainActivity.setTitle("Bookopedia");
            NavigationView nvDrawer = mainActivity.nvDrawer;
            nvDrawer.getMenu().findItem(R.id.nav_wishlist).setChecked(false);
            nvDrawer.getMenu().findItem(R.id.nav_home).setChecked(true);
        }
    }

    public void showDialog(final Context context, final String fragment, String message, final String action) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setMessage(message);
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
                        switch(action) {
                            case("signOut"):
                                signOut(context);
                                break;
                            case("deleteBook"):
                                deleteBook(context);
                                break;
                            case("deleteAllBooks"):
                                deleteAllBooks(context, fragment);
                                break;
                        }

                    }
                });
        alertDialog.show();
    }


}
