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
import dataaccess.interfaces.AuthDAO;
import dataaccess.memoryDs.MemoryAuthDao;
import server.ResponseException;

public class AuthServiceTest {

    private AuthService authService;
    private AuthDAO authDAO;

    @BeforeEach
    void setup() { 
        authDAO = new MemoryAuthDao();
        authService = new AuthService(authDAO);
    }

    @Test
    public void isValidAuthTokenWhenValidReturnsTrue() {
        String validToken = "validToken";
        try {
            authDAO.createAuth("testUser", validToken);
            
        } catch (DataAccessException e) {
            fail("Unexpected database failure");
        }

        assertDoesNotThrow(() -> {
            authService.isValidAuthToken(validToken);
        });
    
    }

    @Test
    public void isValidAuthTokenWhenInvalidThrowsException() {
        String invalidToken = "invalidToken";

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            authService.isValidAuthToken(invalidToken);
        });

        assertEquals("Error: unauthorized", exception.getMessage());

    }

    @Test
    public void clearTest() {
        try {
            authDAO.createAuth("testUser", "validToken");
        } catch (DataAccessException e) {
            fail("unexpected database error");
        }

        var authTokensBeforeClear = ((MemoryAuthDao) authDAO).getAllAuthData();
        assertFalse(authTokensBeforeClear.isEmpty());
        try {
            authService.clearAuthData();
        } catch (ResponseException e) {
            fail("Unexpected database error");
        }
        var authTokensAfterClear = ((MemoryAuthDao) authDAO).getAllAuthData();
        assertTrue(authTokensAfterClear.isEmpty());
        

    }

}
