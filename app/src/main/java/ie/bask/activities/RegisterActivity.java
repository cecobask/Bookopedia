package ie.bask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import ie.bask.R;
import ie.bask.main.Base;
import ie.bask.main.BookopediaApp;
import ie.bask.models.User;

public class RegisterActivity extends Base implements View.OnClickListener {

    // Widgets
    private EditText editTextEmail;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private AutoCompleteTextView autoCompleteCounty;
    private Button buttonRegister;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        app = (BookopediaApp) getApplication();

        if (app.firebaseAuth.getCurrentUser() != null) {
            // That means user is already logged in
            finish();
            startActivity(new Intent(RegisterActivity.this, BookListActivity.class));
        }

        // Initialising widgets
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        autoCompleteCounty = findViewById(R.id.autoCompleteCounty);
        buttonRegister = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.pbSearch);

        // Load string-array from resources to give suggestions
        // to the user when they start typing
        ArrayAdapter<String> arrayAdapterCounties = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.counties));
        autoCompleteCounty.setAdapter(arrayAdapterCounties);
        // Show suggestions after 1 symbol is typed
        autoCompleteCounty.setThreshold(1);

        // Set max input length of autoCompleteCounty to 9 chars
        InputFilter[] filter = new InputFilter[1];
        filter[0] = new InputFilter.LengthFilter(9);
        autoCompleteCounty.setFilters(filter);

        // Set onClick listeners for the buttons
        buttonRegister.setOnClickListener(this);
    }

    private void registerUser() {

        // Get input values from widgets
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String username = editTextUsername.getText().toString().trim();
        final String county = autoCompleteCounty.getText().toString().trim();

        // Check if the user entered an existing county
        autoCompleteCounty.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence text) {
                for (int j = 0; j < getResources().getStringArray(R.array.counties).length; j++) {
                    String currentElement = getResources().getStringArray(R.array.counties)[j];
                    if (county.equals(currentElement)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public CharSequence fixText(CharSequence invalidText) {
                return null;
            }
        });
        autoCompleteCounty.performValidation();

        // Checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
        } else if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Username is required!");
            editTextUsername.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
        } else if (TextUtils.isEmpty(county) || !autoCompleteCounty.getValidator().isValid(county)) {
            autoCompleteCounty.setError("Empty or invalid county!");
            autoCompleteCounty.requestFocus();
        } else {

            // If the email and password are not empty
            // display a ProgressBar
            progressBar.setVisibility(View.VISIBLE);

            // Creating a new user
            app.firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // Checking if successful
                            if (task.isSuccessful()) {
                                // Getting the created user
                                FirebaseUser firebaseUser = app.firebaseAuth.getCurrentUser();
                                String id = firebaseUser.getUid();

                                // Create new User object to store extra data about user
                                User user = new User(id, email, username, password, county);

                                // Store in Firebase database
                                app.usersDb.child(id).setValue(user);
                                app.booksToReadDb = app.usersDb.child(task.getResult().getUser().getUid()).child("booksToRead");
                                app.bookResultsDb = app.usersDb.child(task.getResult().getUser().getUid()).child("bookResults");

                                // Close activity
                                finish();
                                startActivity(new Intent(RegisterActivity.this, BookListActivity.class));
                            } else {
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }


    /**
     * Handle onClick events
     */
    @Override
    public void onClick(View view) {

        if (view == buttonRegister) {
            registerUser();
        }

    }
}