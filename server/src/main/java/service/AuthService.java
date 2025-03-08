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
            authDAO.getUsernameFromToken(authToken);
            return true;
        } catch (DataAccessException e) {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    public String getUser(String authToken) throws ResponseException {
        try {
            return authDAO.getUsernameFromToken(authToken);
        } catch (Exception e) {
            throw new ResponseException(500, "Error: User doesn't exist");
        }
    }

    public void clearAuthData() throws ResponseException{
        try {
            authDAO.clear();
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

}
