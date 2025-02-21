package server;

import com.google.gson.Gson;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import model.AuthData;
import model.UserData;
import service.AuthService;
import service.UserService;
import spark.*;

public class Server {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserService userService;
    private final AuthService authService;

    private final Gson gson = new Gson();

    public Server() {

        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        userService = new UserService(userDAO, authDAO);
        authService = new AuthService(authDAO);
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
        Spark.post("/game", this::CreateGame);
        Spark.put("/game", this::JoinGame);
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
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }

    private Object register(Request req, Response res) throws ResponseException {
        UserData data = new Gson().fromJson(req.body(), UserData.class);
        if (data == null || data.username() == null || data.password() == null || data.email() == null){
            throw new ResponseException(400, "Error: bad request");
        }
        AuthData authData = userService.createUser(data);
        return new Gson().toJson(authData);
    }

    private Object login(Request req, Response res) throws ResponseException {
        UserData data = new Gson().fromJson(req.body(), UserData.class);
        if (data == null || data.username() == null || data.password() == null){
            throw new ResponseException(400, "Error: bad request");
        }
        var authData = userService.loginUser(data);
        return new Gson().toJson(authData);

    }

    private Object logout(Request req, Response res) throws ResponseException {
        String authToken = req.headers("authorization");

        if (authToken == null){
            throw new ResponseException(400, "Error: bad request");
        }

        userService.logoutUser(authToken);
        res.status(200);
        return "";
        
    }

    private Object listGames(Request req, Response res) throws ResponseException {
        // TODO
        throw new UnsupportedOperationException();
    }

    private Object CreateGame(Request req, Response res) throws ResponseException {
        // TODO
        throw new UnsupportedOperationException(); 
    }

    private Object JoinGame(Request req, Response res) throws ResponseException {
        // TODO
        throw new UnsupportedOperationException();
    }

    private Object clear(Request req, Response res) throws ResponseException {
        userService.clearUserData();
        authService.clearAuthData();
        res.status(200);
        return "";
    }

    //#endregion
}
