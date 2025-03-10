package service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import dataaccess.memorydaos.MemoryGameDao;
import model.GameData;
import model.JoinGameRequest;
import server.ResponseException;

public class GameServiceTest {

    private GameService gameService;
    private GameDAO gameDAO;

    @BeforeEach
    void setup() {
        gameDAO = new MemoryGameDao();
        gameService = new GameService(gameDAO);
    }

    @Test
    public void createGameWhenGivenGoodDataSuccessfullyCreates() {
        GameData input = new GameData(0, 
        null, 
        null, 
        "gameOne", 
        null);

        GameData expected = new GameData(1001, 
        null, 
        null, 
        "gameOne", 
        new ChessGame());

        GameData actual = assertDoesNotThrow(() -> {
            return gameService.createGame(input);
        });

        assertEquals(expected, actual);
    }

    @Test
    public void createGameWhenGivenDuplicateNameRaisesError() {
        GameData inital = new GameData(1001, 
        null, 
        null, 
        "gameOne", 
        null);

        try {
            gameDAO.createGame(inital);
        } catch (Exception e) {
            fail("Unexpected error when creating a game");
        }

        GameData input = new GameData(0, 
        null, 
        null, 
        "gameOne", 
        null);

        assertThrows(ResponseException.class, () -> {gameService.createGame(input);});
    }

    @Test
    public void addPlayerWhenGivenValidDataCorrectlyAdds() {
        GameData inital = new GameData(1001, 
            null, 
            null, 
            "gameOne", 
            null);

        GameData expected = new GameData(1001, 
            "saul", 
            null, 
            "gameOne", 
            null);

        try {
            gameDAO.createGame(inital);
        } catch (Exception e) {
            fail("Unexpected error when creating a game");
        }

        var req = new JoinGameRequest("WHITE", 1001);

        var result = assertDoesNotThrow(() -> { return gameService.addPlayer(req, "saul");});

        assertEquals(expected, result);
    }

    @Test
    public void addPlayerSpaceAlreadyTakenRaisesException() {
        GameData inital = new GameData(1001, 
        "alreadyOccupied", 
        null, 
        "gameOne", 
        null);

        try {
            gameDAO.createGame(inital);
        } catch (Exception e) {
            fail("Unexpected error when creating a game");
        }

        var req = new JoinGameRequest("WHITE", 1001);

        assertThrows(ResponseException.class, () -> {
            gameService.addPlayer(req, "saul");
        });
    }

    @Test
    public void getAllGamesWhenFullOfGamesPasses() {

        Collection<GameData> expectedGames = Arrays.asList(
            new GameData(1001, null, null, "1001", null),
            new GameData(1002, null, null, "1002", null),
            new GameData(1003, null, null, "1003", null),
            new GameData(1004, null, null, "1004", null)
        );

        for (GameData game : expectedGames) {
            try {
                gameDAO.createGame(game);
            } catch (DataAccessException e) {
                fail("unexpected database error");
            }    
        }

        Collection<GameData> actualGames = assertDoesNotThrow(() -> { return gameService.getAllGames();});

        assertEquals(new HashSet<>(expectedGames), new HashSet<>(actualGames));

    }

    @Test 
    public void getAllGamesWhenEmptyPasses() {
        assertDoesNotThrow(() -> {gameService.getAllGames();});
    }

    @Test
    public void clearTest() {
        GameData inital = new GameData(1001, 
        null, 
        null, 
        "gameOne", 
        null);

        try {
            gameDAO.createGame(inital);
        } catch (Exception e) {
            fail("Unexpected error when creating a game");
        }

        var gamesBeforeClear = ((MemoryGameDao) gameDAO).getAllGameData();
        assertFalse(gamesBeforeClear.isEmpty());
        try {
            gameService.clearGameData();
        } catch (ResponseException e) {
            fail("unexpected database error");
        }
        var gamesAfterClear = ((MemoryGameDao) gameDAO).getAllGameData();
        assertTrue(gamesAfterClear.isEmpty());


    }

}
