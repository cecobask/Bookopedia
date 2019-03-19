package ie.bask;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ie.bask.models.Book;
import ie.bask.models.Coordinates;

public class BookTest {

    private static final String EXPECTED_ID = "4KwCoQEACAAJ";
    private static final String EXPECTED_AUTHOR = "J. R. R. Tolkien";
    private static final String EXPECTED_TITLE = "The Fellowship of the Ring";
    private static final String EXPECTED_LINK = "http://books.google.com/books/content?id=4KwCoQEACAAJ&printsec=frontcover&img=1&zoom=5&source=gbs_api";
    private static final String EXPECTED_DESCRIPTION = "Large print hardback edition of the first volume of J.R.R. Tolkien's epic adventure, The Lord of the Rings, complete with fold-out map. Sauron, the Dark Lord, has gathered to him all the Rings of Power -- the means by which he intends to rule Middle-earth. All he lacks in his plans for dominion is the One Ring -- the ring that rules them all -- which has fallen into the hands of the hobbit, Bilbo Baggins. In a sleepy village in the Shire, young Frodo Baggins finds himself faced with an immense task, as his elderly cousin Bilbo entrusts the Ring to his care. Frodo must leave his home and make a perilous journey across Middle-earth to the Cracks of Doom, there to destroy the Ring and foil the Dark Lord in his evil purpose. Now available in large print and impossible to describe in a few words, JRR Tolkien's great work of imaginative fiction has been labelled both a heroic romance and a classic fantasy fiction. By turns comic and homely, epic and diabolic, the narrative moves through countless changes of scene and character in an imaginary world which is totally convincing in its detail.";
    private static final String EXPECTED_PUBLISHER = "HarperCollins";
    private static final int EXPECTED_PAGES = 560;
    private static final String EXPECTED_DATE_ADDED = null;
    private static final String EXPECTED_NOTES = null;
    private static final boolean EXPECTED_TO_READ = false;
    private static final String EXPECTED_TO_STRING = "Book{bookId='4KwCoQEACAAJ', author='J. R. R. Tolkien', title='The Fellowship of the Ring', imageLink='http://books.google.com/books/content?id=4KwCoQEACAAJ&printsec=frontcover&img=1&zoom=5&source=gbs_api', description='Large print hardback edition of the first volume of J.R.R. Tolkien's epic adventure, The Lord of the Rings, complete with fold-out map. Sauron, the Dark Lord, has gathered to him all the Rings of Power -- the means by which he intends to rule Middle-earth. All he lacks in his plans for dominion is the One Ring -- the ring that rules them all -- which has fallen into the hands of the hobbit, Bilbo Baggins. In a sleepy village in the Shire, young Frodo Baggins finds himself faced with an immense task, as his elderly cousin Bilbo entrusts the Ring to his care. Frodo must leave his home and make a perilous journey across Middle-earth to the Cracks of Doom, there to destroy the Ring and foil the Dark Lord in his evil purpose. Now available in large print and impossible to describe in a few words, JRR Tolkien's great work of imaginative fiction has been labelled both a heroic romance and a classic fantasy fiction. By turns comic and homely, epic and diabolic, the narrative moves through countless changes of scene and character in an imaginary world which is totally convincing in its detail.', publisher='HarperCollins', numPages=560, dateAdded='null', notes='null', toRead=false, coordinates=Coordinates{latitude=37.4220347, longitude=-122.0840085}}";
    private Book book;

    @Before
    public void setUp() {
        book = new Book("4KwCoQEACAAJ",
                "J. R. R. Tolkien",
                "The Fellowship of the Ring",
                "http://books.google.com/books/content?id=4KwCoQEACAAJ&printsec=frontcover&img=1&zoom=5&source=gbs_api",
                "Large print hardback edition of the first volume of J.R.R. Tolkien's epic adventure, The Lord of the Rings, complete with fold-out map. Sauron, the Dark Lord, has gathered to him all the Rings of Power -- the means by which he intends to rule Middle-earth. All he lacks in his plans for dominion is the One Ring -- the ring that rules them all -- which has fallen into the hands of the hobbit, Bilbo Baggins. In a sleepy village in the Shire, young Frodo Baggins finds himself faced with an immense task, as his elderly cousin Bilbo entrusts the Ring to his care. Frodo must leave his home and make a perilous journey across Middle-earth to the Cracks of Doom, there to destroy the Ring and foil the Dark Lord in his evil purpose. Now available in large print and impossible to describe in a few words, JRR Tolkien's great work of imaginative fiction has been labelled both a heroic romance and a classic fantasy fiction. By turns comic and homely, epic and diabolic, the narrative moves through countless changes of scene and character in an imaginary world which is totally convincing in its detail.",
                "HarperCollins",
                560,
                null,
                null,
                false,
                new Coordinates(37.4220347,-122.0840085));
    }

    @After
    public void tearDown() {
        System.out.println("Test Completed.");
    }

    @Test
    public void testUserDetails() {
        Assert.assertEquals(EXPECTED_ID, book.getBookId());
        Assert.assertEquals(EXPECTED_AUTHOR, book.getAuthor());
        Assert.assertEquals(EXPECTED_TITLE, book.getTitle());
        Assert.assertEquals(EXPECTED_LINK, book.getImageLink());
        Assert.assertEquals(EXPECTED_DESCRIPTION, book.getDescription());
        Assert.assertEquals(EXPECTED_PUBLISHER, book.getPublisher());
        Assert.assertEquals(EXPECTED_PAGES, book.getNumPages());
        Assert.assertEquals(EXPECTED_DATE_ADDED, book.getDateAdded());
        Assert.assertEquals(EXPECTED_NOTES, book.getNotes());
        Assert.assertEquals(EXPECTED_TO_READ, book.toReadStatus());
    }

    @Test
    public void testUserToString() {
        Assert.assertEquals(EXPECTED_TO_STRING, book.toString());
    }
}