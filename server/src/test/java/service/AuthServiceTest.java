package service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.interfaces.AuthDAO;
import server.ResponseException;

public class AuthServiceTest {

    private AuthService authService;
    private AuthDAO authDAO;

    @BeforeEach
    void setup() { 
        authDAO = new MemoryAuthDAO();
        authService = new AuthService(authDAO);
    }

    @Test
    public void isValidAuthToken_WhenValid_ReturnsTrue() {
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
    public void isValidAuthToken_WhenInvalid_ThrowsException() {
        String invalidToken = "invalidToken";

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            authService.isValidAuthToken(invalidToken);
        });

        assertEquals("Error: unauthorized", exception.getMessage());

    }

}
