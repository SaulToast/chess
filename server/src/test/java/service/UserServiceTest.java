package service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        UserData data = new UserData("test", "1234", "email");
        try {
            userDAO.addUserData(data);
        } catch (DataAccessException e) {
            fail("Unexpected database error");
        }
        assertThrows(ResponseException.class, () -> {userService.createUser(data);});
    }

    



}
 