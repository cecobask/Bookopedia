package ie.bask;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ie.bask.models.User;

public class UserTest {

    private static final String EXPECTED_ID = "ixcjYB9l4WRZMHwGor9yinf5ZBB2";
    private static final String EXPECTED_EMAIL = "testemail@gmail.com";
    private static final String EXPECTED_USERNAME = "testuser";
    private static final String EXPECTED_PASSWORD = "123456";
    private static final String EXPECTED_COUNTY = "Dublin";
    private static final String EXPECTED_TO_STRING = "User{id='ixcjYB9l4WRZMHwGor9yinf5ZBB2', email='testemail@gmail.com', username='testuser', password='123456', county='Dublin'}";
    private User user;

    @Before
    public void setUp() {
        user = new User("ixcjYB9l4WRZMHwGor9yinf5ZBB2","testemail@gmail.com", "testuser", "123456", "Dublin");
    }

    @After
    public void tearDown() {
        System.out.println("Test Completed.");
    }

    @Test
    public void testUserDetails() {
        Assert.assertEquals(EXPECTED_ID, user.getId());
        Assert.assertEquals(EXPECTED_EMAIL, user.getEmail());
        Assert.assertEquals(EXPECTED_USERNAME, user.getUsername());
        Assert.assertEquals(EXPECTED_PASSWORD, user.getPassword());
        Assert.assertEquals(EXPECTED_COUNTY, user.getCounty());
    }

    @Test
    public void testUserToString() {
        Assert.assertEquals(EXPECTED_TO_STRING, user.toString());
    }
}