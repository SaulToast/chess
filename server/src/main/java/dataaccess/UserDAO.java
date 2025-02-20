package dataaccess;

import model.UserData;

public interface UserDAO {

    UserData getUser(String username) throws DataAccessException;
    void addUserData(UserData data) throws DataAccessException;

}
