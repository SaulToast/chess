package dataaccess.memorydaos;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import model.GameData;

public class MemoryGameDao implements GameDAO{

    private Map<Integer, GameData> games;

    public MemoryGameDao(){
        games = new HashMap<>();
    }    

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData data = games.get(gameID);
        if (data != null){
            return data;
        }
        throw new DataAccessException("Error: Requested gameID doesn't exist");
    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {
        if (games.containsKey(gameID)) {
            games.remove(gameID);
            return;
        }
        throw new DataAccessException("No game with the given ID");
    }

    @Override
    public void createGame(GameData g) throws DataAccessException {
        if (!games.containsKey(g.gameID())){
            games.put(g.gameID(), g);
            return;
        }
        throw new DataAccessException("Error: A game with the id already exists");
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void updateGame(int gameID, GameData g) throws DataAccessException {
        games.put(gameID, g);
    }

    @Override
    public void clear() {
        games = new HashMap<>();
    }

    public Map<Integer, GameData> getAllGameData() {
        return games;
    }

}
