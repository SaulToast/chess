package ui;

import static ui.EscapeSequences.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import chess.ChessGame.TeamColor;
import exceptions.ResponseException;
import facade.NotificationHandler;
import facade.ServerFacade;
import facade.WebSocketFacade;
import model.AuthData;
import model.GameData;
import model.UserData;

public class ChessClient {

    private final ServerFacade facade;
    private final Prelogin prelogin;
    private final Postlogin postlogin;
    private final InGame inGame;
    private Scanner scanner;
    private final String serverUrl;
    private AuthData authData;
    private State state = State.PRELOGIN;
    private HashMap<Integer, GameData> idToGameData = new HashMap<>();
    private WebSocketFacade ws;
    private final NotificationHandler notificationHandler;

    public ChessClient(String serverUrl) {
        this.serverUrl = serverUrl;
        facade = new ServerFacade(this.serverUrl);
        scanner = new Scanner(System.in);
        prelogin = new Prelogin(this, scanner);
        postlogin = new Postlogin(this, scanner);
        inGame = new InGame(this, scanner);
        notificationHandler = inGame;
    }

    public void run() {
        System.out.println(ERASE_SCREEN + 
            SET_TEXT_COLOR_BLUE + 
            "Welcome to Chess. Please sign in or type help to start");
        while (state != State.EXIT){
            System.out.print(SET_TEXT_COLOR_BLUE);
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
        System.out.println(SET_TEXT_COLOR_BLUE + "Successfully logged in");
        return "";
    }

    public String logoutUser(String... params) throws ResponseException {
        facade.logout(authData);
        state = State.PRELOGIN;
        System.out.println(SET_TEXT_COLOR_BLUE + String.format("Successfully Logged Out User"));
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
        GameData game = getGame(params);
        int id = game.gameID();
        var team = params[1].toUpperCase();
        if (!team.equals("WHITE") && !team.equals("BLACK")) {
            throw new ResponseException(400, "Invalid team color");
        }
        facade.joinGame(team, id, authData);
        TeamColor color = team.equals("WHITE") ? TeamColor.WHITE  : TeamColor.BLACK;
        ws = new WebSocketFacade(serverUrl, notificationHandler);
        return "";
    }

    private GameData getGame(String... params) throws ResponseException {
        int localId;
        GameData game = null;
        try {
            localId = Integer.parseInt(params[0]);
            game = idToGameData.get(localId);
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Game ID should be a number");
        }
        if (game == null) {
            throw new ResponseException(400, "Incorrect Game ID");
        }
        return game;
    }

    public String observe(String... params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Expected <game number>");
        }
        GameData game = getGame(params);
        var drawer = new ChessBoardDrawer(TeamColor.WHITE, game.game());
        drawer.drawBoard();
        return "";
    }

    public String quit() {
        state = State.EXIT;
        return "";
    }

    public String makeMove(String... params) throws ResponseException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public String leaveGame() throws ResponseException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public String resignGame() throws ResponseException {
        // TODO
        throw new UnsupportedOperationException();
    }


}
