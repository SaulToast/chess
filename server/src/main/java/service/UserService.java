package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import model.AuthData;
import model.UserData;
import server.ResponseException;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService (UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData createUser(UserData data) throws ResponseException {
        var authData = new AuthData(null, null);
        try {
            userDAO.addUserData(data);
            authData = authDAO.createAuth(data.username());
        } catch (Exception e) {
            throw new ResponseException(403, e.getMessage());
        }
        return authData;
    }

    public void clearUserData(){
        userDAO.clear();
    }

}
