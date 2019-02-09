package ie.bask.models;

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
    private int numPages;
    private String dateAdded;

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

    public int getNumPages() {
        return numPages;
    }

    public String getDateAdded() {
        return dateAdded;
    }


    public Book(String bookId, String author, String title, String imageLink, String description, String publisher, int numPages, String dateAdded) {
        this.bookId = bookId;
        this.author = author;
        this.title = title;
        this.imageLink = imageLink;
        this.description = description;
        this.publisher = publisher;
        this.numPages = numPages;
        this.dateAdded = dateAdded;
    }

    private Book(String bookId, String author, String title, String imageLink, String description, String publisher, int numPages) {
        this.bookId = bookId;
        this.author = author;
        this.title = title;
        this.imageLink = imageLink;
        this.description = description;
        this.publisher = publisher;
        this.numPages = numPages;
    }

    // Returns a Book given the expected JSON
    private static Book fromJson(JSONObject jsonObject) {
        Book book;
        String bookId, author, title, imageLink, description, publisher;
        int numPages;
        try {
            // Deserialize json into object fields
            bookId = jsonObject.has("id") ? jsonObject.getString("id") : "";
            JSONObject volumeInfo = jsonObject.getJSONObject("volumeInfo");
            title = volumeInfo.has("title") ? volumeInfo.getString("title") : "";
            author = getAuthor(volumeInfo);
            imageLink = volumeInfo.has("imageLinks") ? volumeInfo.getJSONObject("imageLinks").getString("smallThumbnail") : "";
            publisher = volumeInfo.has("publisher") ? volumeInfo.getString("publisher") : "Unknown";
            numPages = volumeInfo.has("pageCount") ? volumeInfo.getInt("pageCount") : 0;
            description = volumeInfo.has("description") ? volumeInfo.getString("description") : "No description available.";
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        book = new Book(bookId, author, title, imageLink, description, publisher, numPages);
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
        ArrayList<Book> books = new ArrayList<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject bookJson;
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