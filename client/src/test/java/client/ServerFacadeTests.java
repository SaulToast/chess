package client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;

import org.junit.jupiter.api.*;

import com.mysql.cj.exceptions.ExceptionFactory;

import dataaccess.DatabaseManager;
import dataaccess.sqldaos.SqlAuthDao;
import dataaccess.sqldaos.SqlGameDao;
import dataaccess.sqldaos.SqlUserDao;
import exceptions.ResponseException;
import server.Server;
import facade.ServerFacade;
import model.AuthData;
import model.UserData;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    UserData userData;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void setUp() throws ResponseException {
        facade.clear();
        userData = new UserData("Test User", "password", "email");
    }

    @AfterEach
    void resetDatabase() throws ResponseException{
        facade.clear();
    }



    @Test
    void registerPositive() throws Exception {
        var authData = facade.register(userData);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerNegative() throws Exception {
        userData = new UserData(null, null, null);
        assertThrows(ResponseException.class, () -> {
            facade.register(userData);
        });
    }

    @Test
    void loginUserPositive() throws Exception {
        facade.register(userData);
        var authData = facade.login(userData);
        assertTrue(authData.authToken().length() > 10);

    }

    @Test 
    void loginUserNegative() throws Exception {
        assertThrows(ResponseException.class, () -> {
            facade.login(userData);
        });
    }

    @Test
    void logoutUserPositive() throws Exception {
        var authData = facade.register(userData);
        assertDoesNotThrow(() -> {
            facade.logout(authData);
        });
    }

    @Test
    void logoutUserNegative() throws Exception {
        var authData = new AuthData("invalid token", "invalid user");
        assertThrows(ResponseException.class, () -> {
            facade.logout(authData);
        });
    }

}
