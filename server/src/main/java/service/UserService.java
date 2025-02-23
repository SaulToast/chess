package service;

import dataaccess.DataAccessException;
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
        } catch (DataAccessException e) {
            throw new ResponseException(403, e.getMessage());
        }
        return authData;
    }

    public AuthData loginUser(UserData data) throws ResponseException {
        UserData storedData;
        AuthData authData;
        try {
            storedData = userDAO.getUser(data.username());
        } catch (DataAccessException e) {
            throw new ResponseException(401, e.getMessage());
        }

        if (!storedData.password().equals(data.password())){
            System.out.println(storedData.password());
            System.out.println(data.password());
            throw new ResponseException(401, "Error: passwords don't match");
        }

        try {
            authData = authDAO.createAuth(data.username());
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }

        return authData;

    }

    public void logoutUser(String authToken) throws ResponseException{
        try {
            authDAO.deleteAuth(authToken);
        } catch (DataAccessException e) {
            throw new ResponseException(401, e.getMessage());
        }
    }

    public void clearUserData(){
        userDAO.clear();
    }

}
