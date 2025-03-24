package ui;

import exceptions.ResponseException;
import facade.ServerFacade;

public class ChessClient {

    private final ServerFacade facade;
    private final String serverUrl;

    public ChessClient(String serverUrl) {
        this.serverUrl = serverUrl;
        facade = new ServerFacade(serverUrl);
    }

    public String registerUser(String... params) throws ResponseException {
        // TODO:
        throw new UnsupportedOperationException();
    }

    public String loginUser(String... params) throws ResponseException {
        // TODO:
        throw new UnsupportedOperationException();
    }

    public String logoutUser(String... params) throws ResponseException {
        // TODO:
        throw new UnsupportedOperationException();
    }


}
