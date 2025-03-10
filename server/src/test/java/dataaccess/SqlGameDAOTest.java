package dataaccess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import dataaccess.sqlDs.SqlGameDao;
import dataaccess.sqlDs.SqlUserDao;
import model.GameData;
import model.UserData;

public class SqlGameDAOTest {

    private static SqlGameDao gameDAO;
    private static SqlUserDao userDAO;
    private static GameData testGameData;

    @BeforeEach
    void resetDatabase() {
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS authData");
            stmt.execute("DROP TABLE IF EXISTS gameData");
            stmt.execute("DROP TABLE IF EXISTS userData");
            DatabaseManager.createDatabase();
            userDAO = new SqlUserDao();
            gameDAO = new SqlGameDao();
            testGameData = new GameData(
                1001, 
                null, 
                null, 
                "testGame", 
                new ChessGame());
        } catch (Exception e) {
            fail("Couldn't initialize database - " + e.getMessage());
        }
    }

    @Test
    void testCreateGamePositive() {
        try {
            gameDAO.createGame(testGameData);

            var result = gameDAO.getGame(1001);
            
            assertNotNull(result);
            assertEquals(testGameData, result);

        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testCreateGameNegative() {

        assertThrows(Exception.class, () -> {
            gameDAO.createGame(new GameData(0, null, null, null, null));
        });

    }

    @Test 
    void testGetGamePositive() {
        testCreateGamePositive();
    }

    @Test
    void testGetGameNegative() {
        try {
            gameDAO.createGame(testGameData);

            assertThrows(Exception.class, () -> {
                gameDAO.getGame(1002);
            });

        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testListAllGamesPositive() {
        var expected = new ArrayList<GameData>();
        var testGameData2 = new GameData(
            1002, 
            null, 
            null, 
            "gameTwo", 
            new ChessGame()
            );
        expected.add(testGameData);
        expected.add(testGameData2);

        try {
            gameDAO.createGame(testGameData);
            gameDAO.createGame(testGameData2);
            var result = gameDAO.listGames();
            assertEquals(expected, result);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testListAllGamesNegative() {
        try {
            var result = gameDAO.listGames();
            assertTrue(result.isEmpty(), "Expected an empty list when no games exist.");
        } catch (DataAccessException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void updateGamePositive() {
        try {
            userDAO.addUserData(new UserData("testUser", "password", "email"));
            gameDAO.createGame(testGameData);

            var gameData = gameDAO.getGame(1001);
            var game = gameData.game();
            game.makeMove(new ChessMove(new ChessPosition(2, 4), new ChessPosition(3, 4)));

            var newData = new GameData(gameData.gameID(), 
            "testUser", 
            null, 
            gameData.gameName(), 
            game);

            gameDAO.updateGame(gameData.gameID(), newData);
            var result = gameDAO.getGame(1001);
            
            assertNotNull(result);
            assertEquals(newData, result);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void updateGameNegative() {
        try {
            gameDAO.createGame(testGameData);

            assertThrows(Exception.class, () -> {
                gameDAO.updateGame(1002, testGameData);
            });
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
