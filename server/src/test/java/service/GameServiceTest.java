package service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.DataAccessException;
import dataaccess.MemoryGameDAO;
import dataaccess.interfaces.GameDAO;
import model.GameData;
import model.JoinGameRequest;
import server.ResponseException;

public class GameServiceTest {

    private GameService gameService;
    private GameDAO gameDAO;

    @BeforeEach
    void setup() {
        gameDAO = new MemoryGameDAO();
        gameService = new GameService(gameDAO);
    }

    @Test
    public void createGame_WhenGivenGoodData_SuccessfullyCreates() {
        GameData input = new GameData(0, 
        null, 
        null, 
        "gameOne", 
        null);

        GameData expected = new GameData(1001, 
        null, 
        null, 
        "gameOne", 
        null);

        GameData actual = assertDoesNotThrow(() -> {
            return gameService.createGame(input);
        });

        assertEquals(expected, actual);
    }

    @Test
    public void createGame_WhenGivenDuplicateName_RaisesError() {
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
    public void addPlayer_WhenGivenValidData_CorrectlyAdds() {
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
    public void addPlayer_SpaceAlreadyTaken_RaisesException() {
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
    public void getAllGames_WhenFullOfGames_Passes() {

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
}
