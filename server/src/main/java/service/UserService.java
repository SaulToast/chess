package service;

import dataaccess.interfaces.UserDAO;
import model.UserData;
import server.ResponseException;

public class UserService {

    private final UserDAO dataAccess;

    public UserService (UserDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    public UserData createUser(UserData data) throws ResponseException {
        dataAccess.addUserData(data);
        return data;
    }

}
