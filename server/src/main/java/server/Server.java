package server;

import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.*;
import dataaccess.memoryDAOs.*;
import dataaccess.sqlDAOs.*;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;
import model.UserData;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    private final UserService userService;
    private final AuthService authService;
    private final GameService gameService;
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    private final Gson gson = new Gson();

    public Server() {

        try {
            DatabaseManager.createDatabase();
            userDAO = new SqlUserDao();
            authDAO = new SqlAuthDao();
            gameDAO = new SqlGameDao();
        } catch (DataAccessException e) {
            System.out.println("couldn't initialize database");
            userDAO = new MemoryUserDao();
            authDAO = new MemoryAuthDao();
            gameDAO = new MemoryGameDao();
        }

        userService = new UserService(userDAO, authDAO);
        authService = new AuthService(authDAO);
        gameService = new GameService(gameDAO);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                authDAO.clear();
                System.out.println("Auth data cleared on shutdown.");
            } catch (Exception e) {
                System.err.println("Error clearing auth data: " + e.getMessage());
            }
        }));
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.exception(ResponseException.class, this::exceptionHandler);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
    

    //#region Handlers

    private void exceptionHandler(ResponseException ex, Request req, Response res){
        res.status(ex.statusCode());
        res.body(ex.toJson());
    }

    private String verifyAuthToken(Request req) throws ResponseException {
        String authToken = req.headers("authorization");

        if (authToken == null) {
            throw new ResponseException(400, "Error: Bad Request");
        }

        // will raise ResponseException if invalid authToken
        authService.isValidAuthToken(authToken);
        return authToken;
    }

    private Object register(Request req, Response res) throws ResponseException {
        UserData data = new Gson().fromJson(req.body(), UserData.class);
        if (data == null || data.username() == null || data.password() == null || data.email() == null){
            throw new ResponseException(400, "Error: bad request");
        }
        AuthData authData = userService.createUser(data);
        return gson.toJson(authData);
    }

    private Object login(Request req, Response res) throws ResponseException {
        UserData data = gson.fromJson(req.body(), UserData.class);
        if (data == null || data.username() == null || data.password() == null){
            throw new ResponseException(400, "Error: bad request");
        }
        var authData = userService.loginUser(data);
        return gson.toJson(authData);

    }

    private Object logout(Request req, Response res) throws ResponseException {
        String authToken = verifyAuthToken(req);
        userService.logoutUser(authToken);
        res.status(200);
        return "";
        
    }

    private Object listGames(Request req, Response res) throws ResponseException {
        verifyAuthToken(req);

        Collection<GameData> games = gameService.getAllGames();

        var jsonObject = new JsonObject();
        jsonObject.add("games", gson.toJsonTree(games));

        res.status(200);
        res.type("application/json");

        return jsonObject.toString();
    }

    private Object createGame(Request req, Response res) throws ResponseException {

        verifyAuthToken(req);
        GameData data = gson.fromJson(req.body(), GameData.class);

        if (data == null) {
            throw new ResponseException(400, "Error: Bad Request");
        }

        GameData newData = gameService.createGame(data);
        res.status(200);
        return gson.toJson(newData);
    }

    private Object joinGame(Request req, Response res) throws ResponseException {
        String authToken = verifyAuthToken(req);
        JoinGameRequest joinReq = gson.fromJson(req.body(), JoinGameRequest.class);

        if (joinReq == null) {
            throw new ResponseException(400, "Error: Bad Request");
        }

        var username = authService.getUser(authToken);
        gameService.addPlayer(joinReq, username);
        res.status(200);
        return "";
    }

    private Object clear(Request req, Response res) throws ResponseException {
        try {
            userService.clearUserData();
            authService.clearAuthData();
            gameService.clearGameData();
            res.status(200);
            return "";
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    //#endregion
}
