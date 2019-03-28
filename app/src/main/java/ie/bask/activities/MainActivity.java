package ie.bask.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import ie.bask.R;
import ie.bask.fragments.BookInfoFragment;
import ie.bask.fragments.BookSearchFragment;
import ie.bask.fragments.WishlistFragment;
import ie.bask.main.BookopediaApp;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    public static NavigationView nvDrawer;
    private Handler handler = new Handler();
    private Runnable runnable;
    public static MainActivity mInstance;

    // Make sure to be using android.support.v7.app.ActionBarDrawerToggle version.
    // The android.support.v4.app.ActionBarDrawerToggle has been deprecated.
    private ActionBarDrawerToggle drawerToggle;
    public static Fragment currentFragment;
    private BookopediaApp app;

    public static MainActivity getInstance() {
        return mInstance;
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_layout);
        nvDrawer = findViewById(R.id.nvView);

        // Initialise application object
        app = (BookopediaApp) getApplication();

        // Tie DrawerLayout events to the ActionBarToggle
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);

        // Setup drawer view
        setupDrawerContent(nvDrawer);

        // Initialise Firebase authentication object
        app.firebaseAuth = FirebaseAuth.getInstance();

        // Get current user
        FirebaseUser user = app.firebaseAuth.getCurrentUser();

        // If the user is not logged in that means current user will return null
        if (user == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Load BookSearchFragment as a default on start of app
        currentFragment = BookSearchFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.flContent, currentFragment)
                .commit();

        mInstance = this;

        // Setup nav drawer's header
        View headerLayout = nvDrawer.getHeaderView(0);
        CircleImageView ivUserPhoto = headerLayout.findViewById(R.id.userPhoto);
        TextView tvUserEmail = headerLayout.findViewById(R.id.userEmail);
        tvUserEmail.setText(user.getEmail());
        if(user.getPhotoUrl()!= null) {
            Picasso.with(mInstance).load(user.getPhotoUrl()).into(ivUserPhoto);
        } else {
            Picasso.with(mInstance).load(R.drawable.ic_default_photo).into(ivUserPhoto);
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Specify the fragment class to show based on nav item clicked
        Class fragmentClass = null;
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                fragmentClass = BookSearchFragment.class;
                break;
            case R.id.nav_wishlist:
                fragmentClass = WishlistFragment.class;
                break;
            case R.id.nav_logout:
                signOut();
                break;
        }

        try {
            if (currentFragment.getClass() != fragmentClass) {
                if (fragmentClass == WishlistFragment.class && app.booksToRead.isEmpty()) {
                    Snackbar.make(findViewById(R.id.drawer_layout), "No books in your Wishlist!", Snackbar.LENGTH_SHORT).show();
                    menuItem.setChecked(false);
                } else {
                    // Set new current fragment
                    currentFragment = (Fragment) fragmentClass.newInstance();
                    // Insert the fragment by replacing any existing fragment
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, currentFragment).commit();

                    // Highlight the selected item has been done by NavigationView
                    menuItem.setChecked(true);
                    // Set action bar title
                    setTitle(menuItem.getTitle());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Close the navigation drawer
        mDrawer.closeDrawers();
        invalidateOptionsMenu();
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE 1: Make sure to override the method with only a single `Bundle` argument
    // Note 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.
    // There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final MenuItem clearBooksItem = menu.findItem(R.id.action_clear);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        if (currentFragment instanceof BookInfoFragment) {
            searchItem.setVisible(false);
            clearBooksItem.setVisible(false);
        } else if (currentFragment instanceof BookSearchFragment) {
            clearBooksItem.setVisible(!app.booksResults.isEmpty());
            searchItem.setVisible(true);
        } else if(currentFragment instanceof WishlistFragment) {
            searchItem.setVisible(true);
            clearBooksItem.setVisible(true);
        }

        searchView.setMaxWidth(android.R.attr.width);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (currentFragment instanceof BookSearchFragment) {
                    // Get books based on user input
                    ((BookSearchFragment) currentFragment).getBooks(query);
                    searchView.clearFocus();
                    searchView.setQuery("", false);
                    searchView.setIconified(true);
                    searchItem.collapseActionView();
                    // Set activity title to search query
                    MainActivity.this.setTitle(query);
                } else if (currentFragment instanceof WishlistFragment) {
                    ((WishlistFragment) currentFragment).bookFilter.filter(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String query) {
                if (currentFragment instanceof BookSearchFragment) {
                    // Remove all previous callbacks
                    handler.removeCallbacks(runnable);
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (query.length() > 0) {
                                // Get books based on user input
                                ((BookSearchFragment) currentFragment).getBooks(query);
                            }
                        }
                    };
                    // Delay API call with 1 second resulting in less API calls
                    handler.postDelayed(runnable, 1000);
                } else if (currentFragment instanceof WishlistFragment) {
                    ((WishlistFragment) currentFragment).bookFilter.filter(query);

                }
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case (R.id.action_clear):
                if(currentFragment instanceof BookSearchFragment) {
                    showDialog("searchFragment", "Clear results?", "deleteAllBooks");
                } else {
                    showDialog("", "Delete all books from Wishlist?", "deleteAllBooks");
                    ((WishlistFragment)currentFragment).bookAdapter.notifyDataSetChanged();
                }
                break;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem wishlistItem = nvDrawer.getMenu().findItem(R.id.nav_wishlist);
        // Trick to give enough time for response from Firebase
        handler.removeCallbacks(runnable);
        runnable = new Runnable() {
            @Override
            public void run() {
                if (app.booksToRead.isEmpty()) {
                    wishlistItem.setEnabled(false);
                } else {
                    wishlistItem.setEnabled(true);
                }
            }
        };
        // Delay wishlist check
        handler.postDelayed(runnable, 500);


        return super.onPrepareOptionsMenu(menu);
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1002) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Open barcode scanner activity if permission was granted
                Intent scanIntent = new Intent(MainActivity.this, BookScannerActivity.class);
                startActivityForResult(scanIntent, 1000);
            }
        }
    }

    // Handle results from barcode scanner activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == 1000) {
            // Search for books using the query from result
            ((BookSearchFragment) currentFragment).getBooks(data.getStringExtra("bar_code"));
        }
    }

    @Override
    protected void onResume() {
        Log.v("Bookopedia", "+");
        super.onResume();
    }

    public void signOut() {
        app.firebaseAuth.signOut();
        app.mGoogleSignInClient.signOut();
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        MainActivity.this.finish();
    }

    public void deleteAllBooks(String fragment) {
        if (fragment.equals("searchFragment")) {
            app.booksResults.clear();
            app.bookResultsDb.removeValue();
            invalidateOptionsMenu();
        } else if (fragment.equals("")) {
            app.booksToReadDb.removeValue();
            app.booksToRead.clear();
            Snackbar.make(findViewById(R.id.drawer_layout), "All books deleted!", Snackbar.LENGTH_SHORT).show();
            // Simulate selection of drawer item
            selectDrawerItem(nvDrawer.getMenu().findItem(R.id.nav_home));
        }
    }

    public void showDialog(final String fragment, String message, final String action) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
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
                        switch (action) {
                            case ("signOut"):
                                signOut();
                                break;
                            case ("deleteAllBooks"):
                                deleteAllBooks(fragment);
                                break;
                        }

                    }
                });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (currentFragment instanceof BookSearchFragment) {
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
            selectDrawerItem(nvDrawer.getMenu().findItem(R.id.nav_home));
        }
    }
}