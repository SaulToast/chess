package service;

import model.GameData;
import server.ResponseException;

import java.util.Collection;

import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;;

public class GameService {

    private final GameDAO gameDAO;
    private int gameIDCounter = 1000;

    public GameService(GameDAO gameDAO){

        this.gameDAO = gameDAO;
    }

    private int generateGameID(){
        gameIDCounter += 1;
        return gameIDCounter;
    }

    public GameData createGame(GameData data) throws ResponseException {
        var newData = new GameData(
            generateGameID(),
            null,
            null, 
            data.gameName(), 
            null);

        try {
            gameDAO.createGame(newData);
            return newData;
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public Collection<GameData> getAllGames() throws ResponseException {
        try {
            return gameDAO.listGames();

        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void clearGameData() throws ResponseException {
        try {
            gameDAO.clear();
            
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }



}
