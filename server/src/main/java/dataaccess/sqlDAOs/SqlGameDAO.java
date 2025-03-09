package dataaccess.sqlDAOs;

import java.sql.SQLException;
import java.util.Collection;

import com.google.gson.Gson;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.GameDAO;
import model.GameData;

public class SqlGameDAO implements GameDAO {

    public SqlGameDAO() throws DataAccessException {
        try {
            DatabaseManager.createGameTable();
        } catch(Exception e) {
            throw new DataAccessException("Failed to create Game Table");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String selectStatement = "SELECT * FROM gameData WHERE gameID=?";
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement(selectStatement)) {
            stmt.setInt(1, gameID);
            try (var rs = stmt.executeQuery()) {
                rs.next();
                var name = rs.getString("gameName");
                var whiteUsername = rs.getString("whiteUsername");
                var blackUsername = rs.getString("blackUsername");
                var json = rs.getString("gameJson");
                var game = new Gson().fromJson(json, ChessGame.class);
                return new GameData(gameID, name, whiteUsername, blackUsername, game);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void createGame(GameData g) throws DataAccessException {
        String insertStatement = "INSERT INTO gameData (gameID, gameName, whiteUsername, blackUsername, gameJson) VALUES (?, ?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement(insertStatement)) {
            stmt.setInt(1, g.gameID());
            stmt.setString(2, g.gameName());
            stmt.setString(3, g.whiteUsername());
            stmt.setString(4, g.blackUsername());
            stmt.setString(5, new Gson().toJson(g.game()));

            stmt.executeUpdate();

        } catch (Exception e) {
            throw new DataAccessException("Error: couldn't add gameData - " + e.getMessage());
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listGames'");
    }

    @Override
    public void updateGame(int gameID, GameData g) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateGame'");
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clear'");
    }

}
