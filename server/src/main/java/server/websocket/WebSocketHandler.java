package server.websocket;

import exceptions.ResponseException;
import websocket.commands.UserGameCommand;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(), session);
            case MAKE_MOVE -> make_move(command.getGameID());
            case LEAVE -> leave(command.getAuthToken());
            case RESIGN -> resign(command.getGameID());
        }
    }

    private void connect(String authToken, Session session) {
        connections.add(authToken, session);
    }

    private void make_move(int gameID) {

    }

    private void leave(String authToken) {}

    private void resign(int gameID) {}
}
