package dataaccess.sqlDAOs;

import java.sql.SQLException;

import dataaccess.DataAccessException;
import dataaccess.interfaces.UserDAO;
import model.UserData;
import dataaccess.DatabaseManager;

public class SqlUserDAO implements UserDAO{

    public SqlUserDAO() throws DataAccessException {
        try {
            DatabaseManager.createUserTable();
        } catch (Exception e) {
            throw new DataAccessException("Failed to create User Table");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String selectStatement = "SELECT username, password, email FROM userData WHERE username=?";
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement(selectStatement)) {
            stmt.setString(1, username);
            try (var rs = stmt.executeQuery()) {
                rs.next();
                var name = rs.getString("username");
                var password = rs.getString("password");
                var email = rs.getString("email");
                return new UserData(name, password, email);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: couldn't get userdata");
        }
    }

    @Override
    public void addUserData(UserData data) throws DataAccessException {
        String insertStatement = "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement(insertStatement)) {
            stmt.setString(1, data.username());
            stmt.setString(2, data.password());
            stmt.setString(3, data.email());

            stmt.executeUpdate();

        } catch (Exception e) {
            throw new DataAccessException("Error: couldn't add userData");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var truncateStatement = "TRUNCATE userData";
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement(truncateStatement)) {
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException("Error: couldn't clear database");
        }
    }

}
