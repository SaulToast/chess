package dataaccess.interfaces;

import java.util.Collection;

import dataaccess.DataAccessException;
import model.GameData;

public interface GameDAO {

    GameData getGame(int gameID) throws DataAccessException;
    void createGame(GameData g) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(int gameID, GameData g) throws DataAccessException;
    void clear();
    

}
