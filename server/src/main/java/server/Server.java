package server;

import com.google.gson.Gson;

import model.UserData;
import spark.*;

public class Server {

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

    private Object register(Request req, Response res) throws ResponseException {
        UserData data = new Gson().fromJson(req.body(), UserData.class);
        
    }

    private Object login(Request req, Response res) throws ResponseException {

    }

    private Object logout(Request req, Response res) throws ResponseException {
        
    }

    private Object listGames(Request req, Response res) throws ResponseException {
        
    }

    private Object CreateGame(Request req, Response res) throws ResponseException {
        
    }

    private Object JoinGame(Request req, Response res) throws ResponseException {
        
    }

    private Object clear(Request req, Response res) throws ResponseException {
        
    }

    //#endregion
}
