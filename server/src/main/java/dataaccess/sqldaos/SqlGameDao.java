package dataaccess.sqldaos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.GameDAO;
import model.GameData;

public class SqlGameDao implements GameDAO {

    public SqlGameDao() throws DataAccessException {
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
                return new GameData(gameID, whiteUsername, blackUsername, name, game);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void deleteGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("DELETE FROM gameData WHERE gameID = ?");
            stmt.setInt(1, gameID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting game from DB");
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
        var gameList = new ArrayList<GameData>();
        String selectStatement = "SELECT * FROM gameData";
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement(selectStatement)) {
            try (var rs = stmt.executeQuery()) {
                while(rs.next()) {
                    var gameID = rs.getInt("gameID");
                    var name = rs.getString("gameName");
                    var whiteUsername = rs.getString("whiteUsername");
                    var blackUsername = rs.getString("blackUsername");
                    var json = rs.getString("gameJson");
                    var game = new Gson().fromJson(json, ChessGame.class);
                    var gameData = new GameData(gameID, whiteUsername, blackUsername, name, game);
                    gameList.add(gameData);
                }

                return gameList;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void updateGame(int gameID, GameData g) throws DataAccessException {
        String updateStatement = """
        UPDATE gameData 
        SET gameName = ?, whiteUsername = ?, blackUsername = ?, gameJson = ? 
        WHERE gameID = ?;
        """;
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement(updateStatement)) {
            stmt.setString(1, g.gameName());
            stmt.setString(2, g.whiteUsername());
            stmt.setString(3, g.blackUsername());
            stmt.setString(4, new Gson().toJson(g.game()));
            stmt.setInt(5, gameID);
            
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("A game with that ID doesn't exist");
            }

        } catch (Exception e) {
            throw new DataAccessException("Error: couldn't add gameData - " + e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var truncateStatement = "DELETE FROM gameData";
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement(truncateStatement)) {
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException("Error: couldn't clear gameData table");
        }
    }

}
