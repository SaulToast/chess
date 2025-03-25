package facade;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;

import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + String.valueOf(port);
    }

    public void clear() throws ResponseException {
        var path = "/db";
        makeRequest("DELETE", path, null, null, null);
    }

    public AuthData register(UserData data) throws ResponseException {
        var path = "/user";
        return makeRequest("POST", path, null, data, AuthData.class);

    }

    public AuthData login(String username, String password) throws ResponseException {
        var path = "/session";
        record loginRequest(String username, String password) {}
        return makeRequest("POST", path, null, new loginRequest(username, password), AuthData.class);
    }

    public void logout(AuthData data) throws ResponseException {
        var path = "/session";
        makeRequest("DELETE", path, data.authToken(), null, null);
    }

    public Collection<GameData> listGames(AuthData data) throws ResponseException {
        var path = "/game";
        record listGamesResponse(ArrayList<GameData> games) {}
        var response = makeRequest("GET", path, data.authToken(), null, listGamesResponse.class);
        return response.games();
    }

    public GameData createGame(String gameName, AuthData authData) throws ResponseException {
        var path = "/game";
        record createGameRequest(String gameName) {}
        var req = new createGameRequest(gameName);
        return makeRequest("POST", path, authData.authToken(), req, GameData.class);
    }

    public void joinGame(String color, int id, AuthData data) throws ResponseException {
        var path = "/game";
        record joinGameRequest(String playerColor, int gameID) {}
        var req = new joinGameRequest(color, id);
        makeRequest("PUT", path, data.authToken(), req, null);
    }

    
    private <T> T makeRequest(
        String method, 
        String path, 
        String token, 
        Object request, 
        Class<T> responseClass
        ) throws ResponseException {

        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            
            http.setRequestMethod(method);
            if (token != null) {
                http.setRequestProperty("Authorization", token);
            }
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }


}
