package service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import model.UserData;
import server.ResponseException;

public class UserServiceTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    private UserData addUserData() {
        UserData data = new UserData("test", "1234", "email");
        try {
            userDAO.addUserData(data);
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
    public void createUser_WhenNewUser_Passes() {

        UserData data = new UserData("test", "1234", "email");
        assertDoesNotThrow(() -> {userService.createUser(data);});

    }

    @Test
    public void createUser_WhenUserAlreadyExists_Fails() {
        var data = addUserData();

        assertThrows(ResponseException.class, () -> {userService.createUser(data);});
    }

    @Test
    public void loginUser_WhenValidUser_Passes(){
        var data = addUserData();
        assertDoesNotThrow(() -> { userService.loginUser(data);});

    }

    @Test
    public void loginUser_WhenInvalidUser_RaisesException () {

        UserData data = new UserData("test", "1234", "email");
        ResponseException exception = assertThrows(ResponseException.class, () -> {
            userService.loginUser(data);
        });

        assertEquals("Error: The requested user doesn't exist", exception.getMessage());

    }

    @Test
    public void logoutUser_WhenValidUser_Passes () {
        var data = addUserData();
        try {
            authDAO.createAuth(data.username(), "validToken");
        } catch (DataAccessException e) {
            fail("unexpected database error");
        }

        assertDoesNotThrow(() -> {userService.logoutUser("validToken");});

    }

    @Test
    public void logoutUser_WhenInvalidUser_RaisesException () {

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
        userService.clearUserData();
        var usersAfterClear = ((MemoryUserDAO) userDAO).getAllUserData();
        assertTrue(usersAfterClear.isEmpty());

    }

    



}
 