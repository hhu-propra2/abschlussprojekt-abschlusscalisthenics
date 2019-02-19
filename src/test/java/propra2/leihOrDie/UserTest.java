package propra2.leihOrDie;

import org.junit.Test;
import org.junit.Assert;
import propra2.leihOrDie.model.Address;
import propra2.leihOrDie.model.User;

import java.security.MessageDigest;

public class UserTest {
    @Test
    public void testCreateUser() throws Exception {
        Address address = new Address(12345, "Teststr", 1, "Kuhlstadt");

        CharSequence password = new StringBuffer("qwerty");

        User user = new User("testuser", "test@email.de", password, address);

        Assert.assertEquals("testuser", user.getUsername());
        Assert.assertEquals(address, user.getAddress());
        Assert.assertNotEquals("qwerty", user.getPassword());
        Assert.assertEquals("test@email.de", user.getEmail());

    }
}
