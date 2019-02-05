package ie.bask;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Book implements Serializable {
    private String bookId;
    private String author;
    private String title;
    private String imageLink;
    private String description;
    private String publisher;
    private String numPages;

    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getDescription() {
        return description;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getNumPages() {
        return numPages;
    }


    // Returns a Book given the expected JSON
    public static Book fromJson(JSONObject jsonObject) {
        Book book = new Book();
        try {
            // Deserialize json into object fields
            book.bookId = jsonObject.has("id") ? jsonObject.getString("id") : "";
            JSONObject volumeInfo = jsonObject.getJSONObject("volumeInfo");
            book.title = volumeInfo.has("title") ? volumeInfo.getString("title") : "";
            book.author = getAuthor(volumeInfo);
            book.imageLink = volumeInfo.has("imageLinks") ? volumeInfo.getJSONObject("imageLinks").getString("smallThumbnail") : "";
            book.publisher = volumeInfo.has("publisher") ? volumeInfo.getString("publisher") : "";
            book.numPages = volumeInfo.has("pageCount") ? volumeInfo.getString("pageCount") : "";
            book.description = volumeInfo.has("description") ? volumeInfo.getString("description") : "";
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return book;
    }

    // Return comma separated author list when there is more than one author
    private static String getAuthor(final JSONObject volumeInfo) {
        try {
            final JSONArray authors = volumeInfo.getJSONArray("authors");
            int numAuthors = authors.length();
            final String[] authorStrings = new String[numAuthors];
            for (int i = 0; i < numAuthors; ++i) {
                authorStrings[i] = authors.getString(i);
            }
            return TextUtils.join(", ", authorStrings);
        } catch (JSONException e) {
            return "";
        }
    }

    // Decodes array of book json results into Book objects
    public static ArrayList<Book> fromJson(JSONArray jsonArray) {
        ArrayList<Book> books = new ArrayList<Book>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject bookJson = null;
            try {
                bookJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            Book book = Book.fromJson(bookJson);
            if (book != null) {
                books.add(book);
            }
        }
        return books;
    }

}