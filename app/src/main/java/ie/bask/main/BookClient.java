package ie.bask.main;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.commons.validator.routines.ISBNValidator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class BookClient {
    private static final String API_BASE_URL = "https://www.googleapis.com/books/v1/";
    private AsyncHttpClient client;

    public BookClient() {
        this.client = new AsyncHttpClient();
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }

    // Method for accessing the search API
    public void getBooks(final String query, JsonHttpResponseHandler handler) {
        try {
            String url;
            if(ISBNValidator.getInstance().isValid(query)){
                url = getApiUrl("volumes?q=isbn:" + URLEncoder.encode(query, "utf-8"));
            } else {
                url = getApiUrl("volumes?q=" + URLEncoder.encode(query, "utf-8"));
            }
            client.get(url, handler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}