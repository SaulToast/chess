package dataaccess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.sqlDAOs.SqlAuthDAO;
import dataaccess.sqlDAOs.SqlUserDAO;
import model.UserData;


public class SqlAuthDAOTest {

    private static SqlAuthDAO authDAO;
    private static SqlUserDAO userDAO;

    @BeforeAll
    static void setup() {
        try {
            DatabaseManager.createDatabase();
            // userdao needs to exist for authdao
            userDAO = new SqlUserDAO();
            authDAO = new SqlAuthDAO();
        } catch (DataAccessException e) {
            fail("couldn't start database - " + e.getMessage());
        }
    }

    @BeforeEach
    void resetDatabse() throws SQLException {
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS authData;");
            stmt.execute("DROP TABLE IF EXISTS userData;");
            userDAO = new SqlUserDAO();
            authDAO = new SqlAuthDAO();
            userDAO.addUserData(new UserData("testUser", "password", "email"));
        } catch (Exception e) {
            fail("Couldn't reset database: " + e.getMessage());
        }

    }

    @Test
    void testCreateAuthPositive() {
        try {
            authDAO.createAuth("testUser", "validAuth");

            var username = authDAO.getUsernameFromToken("validAuth");
            
            assertNotNull(username, "auth token wasn't found in the database");
            assertEquals("testUser", username, "found username doesn't match expected");

        } catch (DataAccessException e) {
            fail("Failure creating Auth - " + e.getMessage());
        }
    }

    @Test
    void testCreateAuthNegative() {
        assertThrows(Exception.class, () -> {
            authDAO.createAuth("invalidUser", "validAuth");
        });
    }

    @Test
    void testGetUsernameFromTokenPositive() {
        testCreateAuthPositive();
    }

    @Test
    void testGetUsernameFromTokenNegative() {
        try {
            authDAO.createAuth("testUser", "validAuth");

            assertThrows(Exception.class, () -> {
                authDAO.getUsernameFromToken("invalidAuth");
            });

        } catch (DataAccessException e) {
            fail("Failure creating Auth - " + e.getMessage());
        }
    }

    @Test
    void testDeleteAuthPositive() {
        try {

            authDAO.createAuth("testUser", "validAuth");
            authDAO.deleteAuth("validToken");

        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testDeleteAuthNegative() {
        try {
            
            authDAO.createAuth("testUser", "validAuth");
            assertThrows(Exception.class, () -> {
                authDAO.deleteAuth("invalidToken");
            });

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testClear() {
        try {
            authDAO.createAuth("testUser", "validToken");
            authDAO.clear();
            assertThrows(Exception.class, () -> {
                authDAO.getUsernameFromToken("validToken");
            });
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
