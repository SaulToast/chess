package facade;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.management.Notification;
import javax.swing.RowFilter.ComparisonType;
import javax.websocket.*;

import com.google.gson.Gson;

import chess.ChessMove;
import exceptions.ResponseException;
import ui.ChessClient;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.ServerMessageType;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    ChessClient client;

    public WebSocketFacade(String url, NotificationHandler notificationHandler, ChessClient client) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage response = new Gson().fromJson(message, ServerMessage.class);
                    ServerMessageType type = response.getServerMessageType();
            
                    switch (type) {
                        case NOTIFICATION:
                            notificationHandler.notify(response);
                            break;
                        case LOAD_GAME:
                            client.drawGame(response.getGame());
                            break;
                        case ERROR:
                            notificationHandler.notify(response);
                            break;
                    }
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
    }

    public void joinGame(String authToken, String color, int gameID) throws ResponseException {
        try {
            var command = new UserGameCommand(CommandType.CONNECT, authToken, gameID, color);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(int gameID, ChessMove move) {}

    public void leaveGame(String authToken, String color, int gameID) throws ResponseException {
        try {
            var command = new UserGameCommand(CommandType.LEAVE, authToken, gameID, color);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
            this.session.close();
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

}
