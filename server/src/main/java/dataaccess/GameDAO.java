package dataaccess;

import java.util.Collection;

import model.GameData;

public interface GameDAO {

    GameData getGame(int gameID) throws DataAccessException;
    void createGame(GameData g) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame() throws DataAccessException;
    

}
