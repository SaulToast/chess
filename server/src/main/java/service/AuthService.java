package service;

import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import server.ResponseException;

public class AuthService {

    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public boolean isValidAuthToken(String authToken) throws ResponseException {
        try {
            authDAO.getAuthToken(authToken);
            return true;
        } catch (DataAccessException e) {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    public String getUser(String authToken) throws ResponseException {
        try {
            return authDAO.getAuthToken(authToken);
        } catch (Exception e) {
            throw new ResponseException(500, "Error: User doesn't exist");
        }
    }

    public void clearAuthData(){
        authDAO.clear();
    }

}
