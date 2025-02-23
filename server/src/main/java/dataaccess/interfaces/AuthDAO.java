package dataaccess.interfaces;

import dataaccess.DataAccessException;
import model.AuthData;

public interface AuthDAO {

    AuthData createAuth(String username) throws DataAccessException;
    String getAuthToken(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear();

}
