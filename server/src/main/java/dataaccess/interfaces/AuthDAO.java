package dataaccess.interfaces;

import dataaccess.DataAccessException;
import model.AuthData;

public interface AuthDAO {

    AuthData createAuth(String username, String authToken) throws DataAccessException;
    String getUsernameFromToken(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;

}
