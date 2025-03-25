package ui;

import java.util.Scanner;

import exceptions.ResponseException;
import facade.ServerFacade;
import model.AuthData;
import model.UserData;

public class ChessClient {

    private final ServerFacade facade;
    private final Prelogin prelogin;
    private final Postlogin postlogin;
    private Scanner scanner;
    private final String serverUrl;
    private AuthData authData;
    private State state = State.PRELOGIN;

    public ChessClient(String serverUrl) {
        this.serverUrl = serverUrl;
        facade = new ServerFacade(this.serverUrl);
        scanner = new Scanner(System.in);
        prelogin = new Prelogin(this, scanner);
        postlogin = new Postlogin(this, scanner);
    }

    public void run() {
        while (state != State.EXIT){
            switch (state) {
                case PRELOGIN:
                    prelogin.run();
                    break;
                case POSTLOGIN:
                    postlogin.run();
                    break;
                default:
            }
        }
    }

    public String registerUser(String... params) throws ResponseException {
        if (params.length != 3) {
            throw new ResponseException(400, "Expected username, password, and email");
        }
        var data = new UserData(params[0], params[1], params[2]);
        authData = facade.register(data);
        state = State.POSTLOGIN;
        System.out.println(String.format("Successfully Registered User: %s", params[0]));
        return "";
    }

    public String loginUser(String... params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, "Expected <username> <password>");
        }
        authData = facade.login(params[0], params[1]);
        postlogin.run();
        return "";
    }

    public String logoutUser(String... params) throws ResponseException {
        facade.logout(authData);
        state = State.PRELOGIN;
        System.out.println(String.format("Successfully Logged Out User"));
        return "";
    }

    public String createGame(String... params) throws ResponseException {
        facade.createGame(params[0], authData);
        return "Successfully Created Game";
    }

    public String quit() {
        state = State.EXIT;
        return "";
    }


}
