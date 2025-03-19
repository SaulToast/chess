package service;

import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import exceptions.ResponseException;
import model.AuthData;
import model.UserData;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService (UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData createUser(UserData data) throws ResponseException {
        AuthData authData;
        String authToken = UUID.randomUUID().toString();
        String passwordHash = BCrypt.hashpw(data.password(), BCrypt.gensalt());
        data = new UserData(data.username(), passwordHash, data.email());
        try {
            userDAO.addUserData(data);
            authData = authDAO.createAuth(data.username(), authToken);
        } catch (DataAccessException e) {
            throw new ResponseException(403, e.getMessage());
        }
        return authData;
    }

    public AuthData loginUser(UserData data) throws ResponseException {
        UserData storedData;
        AuthData authData;
        String authToken = UUID.randomUUID().toString();

        try {
            storedData = userDAO.getUser(data.username());
        } catch (DataAccessException e) {
            throw new ResponseException(401, e.getMessage());
        }

        if (!BCrypt.checkpw(data.password(), storedData.password())){
            throw new ResponseException(401, "Error: passwords don't match");
        }

        try {
            authData = authDAO.createAuth(data.username(), authToken);
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

    public void clearUserData() throws ResponseException{
        try {
            userDAO.clear();
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

}
