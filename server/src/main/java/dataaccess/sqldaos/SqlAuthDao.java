package dataaccess.sqldaos;

import java.sql.SQLException;

import dataaccess.DataAccessException;

import model.AuthData;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.AuthDAO;


public class SqlAuthDao implements AuthDAO{

    public SqlAuthDao() throws DataAccessException {
        try {
            DatabaseManager.createAuthTable();
        } catch (Exception e) {
            throw new DataAccessException("Failed to create auth table");
        }
    }

    @Override
    public AuthData createAuth(String username, String authToken) throws DataAccessException {
        String insertStatement = "INSERT INTO authData (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement(insertStatement)) {
            stmt.setString(1, authToken);
            stmt.setString(2, username);

            stmt.executeUpdate();

            return new AuthData(authToken, username);

        } catch (Exception e) {
            throw new DataAccessException("Error: couldn't add authData");
        }
    }

    @Override
    public String getUsernameFromToken(String authToken) throws DataAccessException {
        String selectStatement = "SELECT username FROM authData WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement(selectStatement)) {
            stmt.setString(1, authToken);
            try (var rs = stmt.executeQuery()) {
                rs.next();
                var username = rs.getString("username");
                return username;
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: No user associated with given token");
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String deleteStatement = "DELETE FROM authData WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement(deleteStatement)) {
            stmt.setString(1, authToken);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("authToken doesn't exist");
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Couldn't delete requested authData");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var truncateStatement = "DELETE FROM authData";
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement(truncateStatement)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: couldn't clear auth table");
        }
    }

}
