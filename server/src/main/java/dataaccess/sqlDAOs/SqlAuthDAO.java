package dataaccess.sqlDAOs;

import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import model.AuthData;
import dataaccess.DatabaseManager;


public class SqlAuthDAO implements AuthDAO {

    public SqlAuthDAO() throws DataAccessException {
        try {
            DatabaseManager.createAuthTable();
        } catch (Exception e) {
            throw new DataAccessException("Failed to create auth table");
        }
    }

    @Override
    public AuthData createAuth(String username, String authToken) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createAuth'");
    }

    @Override
    public String getAuthToken(String authToken) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAuthToken'");
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAuth'");
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clear'");
    }




}
