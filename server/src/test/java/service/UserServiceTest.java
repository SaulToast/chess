package service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import dataaccess.memoryDAOs.MemoryAuthDAO;
import dataaccess.memoryDAOs.MemoryUserDAO;
import model.UserData;
import server.ResponseException;

public class UserServiceTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    private UserData addUserData() {
        UserData data = new UserData("test", "1234", "email");
        var hashPass = BCrypt.hashpw("1234", BCrypt.gensalt());
        UserData dataWithHashedPassword = new UserData("test", hashPass, "email");
        try {
            userDAO.addUserData(dataWithHashedPassword);
        } catch (DataAccessException e) {
            fail("Unexpected database error");
        }

        return data;

    }

    @BeforeEach
    void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    public void createUserWhenNewUserPasses() {

        UserData data = new UserData("test", "1234", "email");
        assertDoesNotThrow(() -> {userService.createUser(data);});

    }

    @Test
    public void createUserWhenUserAlreadyExistsFails() {
        var data = addUserData();

        assertThrows(ResponseException.class, () -> {userService.createUser(data);});
    }

    @Test
    public void loginUserWhenValidUserPasses(){
        var data = addUserData();
        assertDoesNotThrow(() -> { userService.loginUser(data);});

    }

    @Test
    public void loginUserWhenInvalidUserRaisesException () {

        UserData data = new UserData("test", "1234", "email");
        ResponseException exception = assertThrows(ResponseException.class, () -> {
            userService.loginUser(data);
        });

        assertEquals("Error: The requested user doesn't exist", exception.getMessage());

    }

    @Test
    public void logoutUserWhenValidUserPasses () {
        var data = addUserData();
        try {
            authDAO.createAuth(data.username(), "validToken");
        } catch (DataAccessException e) {
            fail("unexpected database error");
        }

        assertDoesNotThrow(() -> {userService.logoutUser("validToken");});

    }

    @Test
    public void logoutUserWhenInvalidUserRaisesException () {

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            userService.logoutUser("invalidToken");
        });

        assertEquals("Error: authToken isn't valid", exception.getMessage());

    }

    @Test
    public void clearTest() {
        addUserData();
        var usersBeforeClear = ((MemoryUserDAO) userDAO).getAllUserData();
        assertFalse(usersBeforeClear.isEmpty());
        try {
            userService.clearUserData();
        } catch (ResponseException e) {
            fail("unexpected database error");
        }
        var usersAfterClear = ((MemoryUserDAO) userDAO).getAllUserData();
        assertTrue(usersAfterClear.isEmpty());

    }

    



}
 