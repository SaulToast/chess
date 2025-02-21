package service;

import dataaccess.interfaces.AuthDAO;

public class AuthService {

    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public void clearAuthData(){
        authDAO.clear();
    }

}
