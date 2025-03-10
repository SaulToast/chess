package dataaccess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.sqldaos.SqlUserDao;
import model.UserData;

public class SqlUserDAOTest {

    private static SqlUserDao userDAO;

    @BeforeEach
    void resetDatabase() {
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS authData");
            stmt.execute("DROP TABLE IF EXISTS gameData");
            stmt.execute("DROP TABLE IF EXISTS userData");
            DatabaseManager.createDatabase();
            userDAO = new SqlUserDao();
        } catch (Exception e) {
            fail("Couldn't reset database: " + e.getMessage());
        }
    }

    @Test
    void testAddUserPositive() {
        try {
            var expected = new UserData("testUser", "password", "email");
            userDAO.addUserData(expected);
            var result = userDAO.getUser("testUser");
            assertNotNull(result);
            assertEquals(expected, result);

        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    void testAddUserNegative() {
        assertThrows(Exception.class, () -> {
            userDAO.addUserData(new UserData(null, null, null));
        });
    }

    @Test
    void testGetUserPositive() {
        testAddUserPositive();
    }

    @Test
    void testGetUserNegative() {
        try {
            var expected = new UserData("testUser", "password", "email");
            userDAO.addUserData(expected);
            assertThrows(Exception.class, () -> {
                userDAO.getUser("invalidUser");
            });

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testClear() {
        try {
            userDAO.addUserData(new UserData("testUser", "password", "email"));
            userDAO.clear();
            assertThrows(Exception.class, () -> {
                userDAO.getUser("testUser");
            });
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
