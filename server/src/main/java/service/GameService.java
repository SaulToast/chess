package service;

import model.GameData;
import model.JoinGameRequest;
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

    public GameData addPlayer(JoinGameRequest data, String username) throws ResponseException {
        GameData storedData;
        try {
            storedData = gameDAO.getGame(data.gameID());
        } catch (DataAccessException e) {
            throw new ResponseException(400, "Error: Bad Request");
        }

        String white = storedData.whiteUsername();
        String black = storedData.blackUsername();
        
        if ("WHITE".equalsIgnoreCase(data.playerColor())) {
            if (white != null) throw new ResponseException(403, "Error: already taken");
            white = username;
        } else if ("BLACK".equalsIgnoreCase(data.playerColor())) {
            if (black != null) throw new ResponseException(403, "Error: already taken");
            black = username;
        } else {
            throw new ResponseException(400, "Error: Bad Request");
        }

        var newData = new GameData(
            storedData.gameID(),
            white,
            black, 
            storedData.gameName(), 
            storedData.game());

        try {
            gameDAO.updateGame(data.gameID(), newData);
            return newData;

        } catch (Exception e) {
            throw new ResponseException(500, "Error: game doesn't exist");
        }
    }

    public Collection<GameData> getAllGames() throws ResponseException {
        try {
            return gameDAO.listGames();

        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void clearGameData() {
            gameDAO.clear();
            
    }
}
