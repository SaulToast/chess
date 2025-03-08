package dataaccess.sqlDAOs;

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }

    @Override
    public void addUserData(UserData data) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addUserData'");
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clear'");
    }

}
