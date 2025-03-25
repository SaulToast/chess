package ui;

import static ui.EscapeSequences.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import exceptions.ResponseException;
import facade.ServerFacade;
import model.AuthData;
import model.GameData;
import model.UserData;

public class ChessClient {

    private final ServerFacade facade;
    private final Prelogin prelogin;
    private final Postlogin postlogin;
    private Scanner scanner;
    private final String serverUrl;
    private AuthData authData;
    private State state = State.PRELOGIN;
    private HashMap<Integer, GameData> idToGameData = new HashMap<>();

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
        try {
            authData = facade.register(data);
            state = State.POSTLOGIN;
            System.out.println(ERASE_SCREEN + String.format("Successfully Registered User: %s", params[0]));
            return "";  
        } catch (ResponseException e) {
            if (e.statusCode() == 403) {
                throw new ResponseException(403, "User Already Exists");
            }
            throw e;
        }
    }

    public String loginUser(String... params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, "Expected <username> <password>");
        }
        authData = facade.login(params[0], params[1]);
        state = State.POSTLOGIN;
        System.out.println(ERASE_SCREEN);
        return "";
    }

    public String logoutUser(String... params) throws ResponseException {
        facade.logout(authData);
        state = State.PRELOGIN;
        System.out.println(String.format("Successfully Logged Out User"));
        return "";
    }

    public String createGame(String... params) throws ResponseException {
        var str = String.join(" ", params);
        facade.createGame(str, authData);
        return "Successfully Created Game";
    }

    public String listGames() throws ResponseException {
        System.out.println();
        var games = (ArrayList<GameData>) facade.listGames(authData);

        if (games.size() == 0) {
            return """
                    There are no current games
                    """;
        }

        var allGames = new ArrayList<String>();
        for (int i = 1; i <= games.size(); i++) {
            GameData game = (GameData) games.get(i-1);
            var result = String.format("""
                Game %d: %s
                White: %s
                Black: %s
                """, i, game.gameName(), game.whiteUsername(), game.blackUsername());
            allGames.add(result);
            idToGameData.put(i, game);
        }
        return String.join("\n", allGames);
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, "Expected <ID> [WHITE|BLACK]");
        }
        int id = idToGameData.get(Integer.parseInt(params[0])).gameID();
        facade.joinGame(params[1].toUpperCase(), id, authData);
        return "joining game...";
    }

    public String observe(String... params) throws ResponseException {
        // TODO:
        throw new UnsupportedOperationException();
    }

    public String quit() {
        state = State.EXIT;
        return "";
    }


}
