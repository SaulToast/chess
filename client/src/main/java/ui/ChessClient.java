package ui;

import static ui.EscapeSequences.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

import chess.ChessMove;
import chess.ChessPosition;
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
    private TeamColor color = TeamColor.WHITE;
    private GameData currentGame = null;

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
                case INGAME:
                    inGame.run();
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
        currentGame = game;
        color = team.equals("WHITE") ? TeamColor.WHITE  : TeamColor.BLACK;
        ws = new WebSocketFacade(serverUrl, notificationHandler, this);
        ws.joinGame(authData.authToken(), params[1].toLowerCase(), id);
        state = State.INGAME;
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
        color = TeamColor.WHITE;
        currentGame = game;
        ws = new WebSocketFacade(serverUrl, notificationHandler, this);
        ws.joinGame(authData.authToken(), "an observer", game.gameID());
        state = State.INGAME;
        return "";
    }

    public String quit() {
        state = State.EXIT;
        return "";
    }

    public void drawGame(GameData game) {
        
        currentGame = new GameData(
            currentGame.gameID(), 
            game.whiteUsername(), 
            game.blackUsername(), 
            currentGame.gameName(), 
            game.game());
        var drawer = new ChessBoardDrawer(color, game.game());
        drawer.drawBoard();
        inGame.printPrompt();
    }

    public String redrawGame() {
        var drawer = new ChessBoardDrawer(color, currentGame.game());
        drawer.drawBoard();
        return "";
    }

    public void drawGameWithHighlights(Collection<ChessMove> moves) {
        var drawer = new ChessBoardDrawer(color, currentGame.game());
        drawer.setHighlightedMoves(moves);
        drawer.drawBoard();
    }

    public String makeMove(String... params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(500, "Invalid move format. Expected <start> <end> like e2 e4");
        }
        var start = parsePosition(params[0]);
        var end = parsePosition(params[1]);
        var move = new ChessMove(start, end);
        ws.makeMove(authData.authToken(), currentGame.gameID(), move);
        return "";
    }

    private ChessPosition parsePosition(String input) throws ResponseException {
        if (input.length() != 2) {
            throw new ResponseException(500, "Invalid position");
        }

        char colChar = input.charAt(0);
        char rowChar = input.charAt(1);
        int col = colChar - 'a' + 1;
        int row = rowChar - '1' + 1;

        if (col < 1 || col > 8 || row < 1 || row > 8) {
            throw new ResponseException(500, "Invalid coordinates" + input);
        }

        return new ChessPosition(row, col);
    }

    public String leaveGame() throws ResponseException {
        state = State.POSTLOGIN;
        var currColor = color == TeamColor.WHITE ? "white" : "black";
        ws.leaveGame(authData.authToken(), currColor, currentGame.gameID());
        System.out.print(ERASE_SCREEN);
        System.out.println(SET_TEXT_COLOR_BLUE + "\nYou left the game" + RESET_TEXT_COLOR);
        currentGame = null;
        return "";
    }

    public String resignGame() throws ResponseException {
        var name = authData.username();

        if (!name.equals(currentGame.whiteUsername()) && !name.equals(currentGame.blackUsername())) {
            throw new ResponseException(500, "Only players can resign");
        }

        if (currentGame.game().isOver()) {
            throw new ResponseException(500, "Game is already over, try leave instead");
        }

        System.out.print(SET_TEXT_COLOR_BLUE + "Are you sure you want to resign? (yes/no): " + RESET_TEXT_COLOR);
        String response = scanner.next().trim().toLowerCase();
    
        if (!response.equals("yes")) {
            System.out.println(SET_TEXT_COLOR_BLUE + "\nResignation cancelled." + RESET_TEXT_COLOR);
            return "";
        }

        var currColor = color == TeamColor.WHITE ? "white" : "black";
        ws.resignGame(authData.authToken(), currColor, currentGame.gameID());
        System.out.println(SET_TEXT_COLOR_BLUE + "\nYou resigned from the game" + RESET_TEXT_COLOR);
        return "";
    }

    public String showValidMoves(String... params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(500, "Expected <start> in the format of e2");
        }
        var start = parsePosition(params[0]);
        var game = currentGame.game();
        var moves = game.validMoves(start);
        drawGameWithHighlights(moves);
        return "";
    }


}
