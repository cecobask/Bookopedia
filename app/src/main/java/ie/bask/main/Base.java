package ie.bask.main;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import ie.bask.adapters.BookAdapter;
import ie.bask.models.Book;

public class Base extends AppCompatActivity {

    public BookopediaApp app;
    public ProgressBar pbSearch;
    public BookAdapter bookAdapter;
    public TextView tvNoResults;

    // Executes an API call to the Google Books search endpoint, parses the results
    // Converts them into an array of book objects and adds them to the adapter
    public void getBooks(String query) {
        // Show progress bar if search query is not empty
        if (query.length() > 0) pbSearch.setVisibility(View.VISIBLE);

        BookClient client = new BookClient();
        client.getBooks(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // Hide progress bar and TextView
                    pbSearch.setVisibility(View.GONE);
                    tvNoResults.setText(null);
                    JSONArray items;
                    if (response.getInt("totalItems") != 0) {
                        // Get the items json array
                        items = response.getJSONArray("items");
                        // Parse json array into array of Book objects
                        app.booksList = Book.fromJson(items);
                        bookAdapter.clear();
                        // Load Book objects into the adapter
                        for (Book book : app.booksList) {
                            bookAdapter.add(book);
                        }
                        bookAdapter.notifyDataSetChanged();
                    } else {
                        bookAdapter.clear();
                        tvNoResults.setText("No results found.\nSorry for the inconvenience.");
                    }
                } catch (JSONException e) {
                    pbSearch.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("Failed: ", "" + statusCode);
                Log.d("Error: ", "" + throwable);
                Log.d("JsonObject: ", "" + errorResponse);
            }
        });
    }

}
