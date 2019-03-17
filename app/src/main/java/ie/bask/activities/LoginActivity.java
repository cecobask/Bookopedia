package ie.bask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ie.bask.R;
import ie.bask.main.Base;
import ie.bask.main.BookopediaApp;
import ie.bask.models.Book;

public class LoginActivity extends Base implements View.OnClickListener {

    // Widgets
    private Button buttonLogin;
    private Button buttonGoogle;
    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressBar progressBar;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        app = (BookopediaApp) getApplication();

        // Initialising widgets
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonGoogle = findViewById(R.id.buttonGoogleLogin);
        progressBar = findViewById(R.id.pbSearch);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        app.mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

//        // Check for existing Google Sign In account, if the user is already signed in
//        // the GoogleSignInAccount will be non-null.
//        GoogleSignInAccount googleUser = GoogleSignIn.getLastSignedInAccount(this);

        app.firebaseAuth = FirebaseAuth.getInstance();
        currentUser = app.firebaseAuth.getCurrentUser();

        // Check if user is logged in
        if (currentUser != null) {
            // Getting reference of nodes
            loadBooks(currentUser.getUid());
            finish();
            startActivity(new Intent(LoginActivity.this, BookListActivity.class));
        }

        // Set onClick listeners to buttons
        buttonLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
        buttonGoogle.setOnClickListener(this);
    }


    private void firebaseLogin() {

        // Get values from widgets
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        // Checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        // If the email and password are not empty
        // displaying a ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        // Logging in the user
        app.firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            loadBooks(task.getResult().getUser().getUid());
                            finish();
                            startActivity(new Intent(getApplicationContext(), BookListActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Handle onClick events
     */
    @Override
    public void onClick(View view) {
        if (view == buttonLogin) {
            firebaseLogin();
        } else if (view == buttonRegister) {
            finish();
            startActivity(new Intent(this, RegisterActivity.class));
        } else {
            Log.v("Bookopedia", "Google Sign in pressed");
            Intent signInIntent = app.mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 12345);
        }
    }

    // Load Firebase database
    public void loadBooks(String userID) {
        app.booksToReadDb = app.usersDb.child(userID).child("booksToRead");
        app.bookResultsDb = app.usersDb.child(userID).child("bookResults");

        // Attaching value event listener
        app.booksToReadDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clearing the previous Books list
                app.booksToRead.clear();

                // Iterating through all the nodes
                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    Book book = bookSnapshot.getValue(Book.class);
                    // Adding Book to the list
                    app.booksToRead.add(book);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Bookopedia", "" + databaseError);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 12345) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.v("Bookopedia", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.v("Bookopedia", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        app.firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.v("Bookopedia", "signInWithCredential:success");
                            FirebaseUser user = app.firebaseAuth.getCurrentUser();
                            loadBooks(user.getUid());
                            finish();
                            startActivity(new Intent(LoginActivity.this, BookListActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.v("Bookopedia", "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(android.R.id.content), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}