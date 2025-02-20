package dataaccess;

import model.AuthData;

public interface AuthDAO {

    void createAuth() throws DataAccessException;
    AuthData getAuth() throws DataAccessException;
    void deleteAuth() throws DataAccessException;

}
