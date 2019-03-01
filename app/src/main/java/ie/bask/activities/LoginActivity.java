package ie.bask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        app = (BookopediaApp) getApplication();

        // Initialising widgets
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonRegister = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.pbSearch);

        app.firebaseAuth = FirebaseAuth.getInstance();
        // Check if user is logged in
        if (app.firebaseAuth.getCurrentUser() != null) {
            // Getting reference of nodes
            loadBooks();
            finish();
            startActivity(new Intent(LoginActivity.this, BookListActivity.class));
        }

        // Set onClick listeners to buttons
        buttonLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
    }


    private void userLogin() {

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
                            loadBooks();
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
            userLogin();
        } else {
            finish();
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    // Load Firebase database
    public void loadBooks() {
        app.booksToReadDb = app.usersDb.child(app.firebaseAuth.getCurrentUser().getUid()).child("booksToRead");
        app.bookResultsDb = app.usersDb.child(app.firebaseAuth.getCurrentUser().getUid()).child("bookResults");

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
}