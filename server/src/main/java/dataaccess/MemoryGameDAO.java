package dataaccess;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dataaccess.interfaces.GameDAO;
import model.GameData;

public class MemoryGameDAO implements GameDAO{

    private Map<Integer, GameData> games;

    public MemoryGameDAO(){
        games = new HashMap<>();
    }    

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData data = games.get(gameID);
        if (data != null){
            return data;
        }
        throw new DataAccessException("Requested gameID doesn't exist");
    }

    @Override
    public void createGame(GameData g) throws DataAccessException {
        if (!games.containsKey(g.GameID())){
            games.put(g.GameID(), g);
            return;
        }
        throw new DataAccessException("A game with the id already exists");
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void updateGame(int gameID, GameData g) throws DataAccessException {
        games.put(gameID, g);
    }

}
