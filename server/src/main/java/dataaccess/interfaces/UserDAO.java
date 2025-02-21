package dataaccess.interfaces;

import dataaccess.DataAccessException;
import model.UserData;

public interface UserDAO {

    UserData getUser(String username) throws DataAccessException;
    void addUserData(UserData data) throws DataAccessException;
    void clear();

}
