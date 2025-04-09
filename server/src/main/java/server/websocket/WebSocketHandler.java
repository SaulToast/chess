package server.websocket;

import exceptions.ResponseException;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.ServerMessageType;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import chess.ChessGame.TeamColor;
import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public WebSocketHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }
    
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(), command.getGameID(), command.getColor(), session);
            case MAKE_MOVE -> make_move(command.getAuthToken(), command.getGameID(), command.getMove(), session);
            case LEAVE -> leave(command.getAuthToken(), command.getGameID(), command.getColor(), session);
            case RESIGN -> resign(command.getGameID());
        }
    }

    private void connect(String authToken, int gameID, String color, Session session) throws IOException {
        try {
            var name = authDAO.getUsernameFromToken(authToken);
            connections.add(name, session, gameID);
            var game = gameDAO.getGame(gameID);
            var response = new ServerMessage(ServerMessageType.LOAD_GAME, game.game());
            String jsonResponse = new Gson().toJson(response);
            session.getRemote().sendString(jsonResponse);

            var message = String.format("%s joined the game as %s", name, color);
            var notification = new ServerMessage(ServerMessageType.NOTIFICATION, message);
            connections.broadcast(name, gameID, notification);

        } catch (DataAccessException e) {
            var errResponse = new ServerMessage(ServerMessageType.ERROR, e.getMessage());
            session.getRemote().sendString(new Gson().toJson(errResponse));
        }
    }

    private void make_move(String authToken, int gameID, ChessMove move, Session session) throws IOException {
        try {
            var name = authDAO.getUsernameFromToken(authToken);
            var data = gameDAO.getGame(gameID);

            if (data.whiteUsername() == null || data.blackUsername() == null) {
                throw new InvalidMoveException("Wait for a second player");
            }

            var game = data.game();

            if (game.isOver()) {
                throw new InvalidMoveException("Game is already over");
            }

            var color = name.equals(data.whiteUsername()) ? TeamColor.WHITE : TeamColor.BLACK;


            if (!color.equals(game.getTeamTurn())) {
                throw new InvalidMoveException("Not your turn");
            }

            game.makeMove(move);

            var response = new ServerMessage(ServerMessageType.LOAD_GAME, game);
            connections.broadcast("", gameID, response);

            var message = String.format("%s made move %s", name, move);
            var notification = new ServerMessage(ServerMessageType.NOTIFICATION, message);
            connections.broadcast(name, gameID, notification);

            handleSpecialGameStates(gameID, data, game);

            var newData = new GameData(gameID, data.whiteUsername(), data.blackUsername(), data.gameName(), game);
            gameDAO.updateGame(gameID, newData);
            
        } catch (DataAccessException e) {
            var errResponse = new ServerMessage(ServerMessageType.ERROR, "Error: couldn't make move");
            session.getRemote().sendString(new Gson().toJson(errResponse));
        } catch (InvalidMoveException e) {
            var errResponse = new ServerMessage(ServerMessageType.ERROR, e.getMessage());
            session.getRemote().sendString(new Gson().toJson(errResponse));
        }
        
    }

    private void handleSpecialGameStates(int gameID, GameData data, ChessGame game) throws IOException {
        var opponentColor = game.getTeamTurn();
        var otherPlayer = opponentColor == TeamColor.BLACK ? data.blackUsername() : data.whiteUsername();

        if (game.isInCheckmate(opponentColor)) {
            game.setOver(true);
            connections.broadcast("", gameID, 
                new ServerMessage(ServerMessageType.NOTIFICATION, otherPlayer + " is in checkmate"));
        } else if (game.isInStalemate(opponentColor)) {
            game.setOver(true);
            connections.broadcast("", gameID, 
                new ServerMessage(ServerMessageType.NOTIFICATION, otherPlayer + " is in stalemate"));
        } else if (game.isInCheck(opponentColor)) {
            connections.broadcast("", gameID, 
                new ServerMessage(ServerMessageType.NOTIFICATION, otherPlayer + " is in check"));
        }
    }

    private void leave(String name, int gameID, String color, Session session) throws IOException {
        try {
            removePlayer(gameID, color);
            var message = String.format("%s left the game", name);
            var notification = new ServerMessage(ServerMessageType.NOTIFICATION, message);
            connections.broadcast(name, gameID, notification);
        } catch (Exception e) {
            var errResponse = new ServerMessage(ServerMessageType.ERROR, "Error: couldn't leave game");
            session.getRemote().sendString(new Gson().toJson(errResponse));
        }

    }

    private void removePlayer(int gameID, String color) throws DataAccessException {
        var game = gameDAO.getGame(gameID);
        String blackUsername;
        String whiteUsername;
        if (color.equals("white")) {
            blackUsername = game.blackUsername();
            whiteUsername = null;
        } else {
            blackUsername = null;
            whiteUsername = game.whiteUsername();
        }
        var newGame = new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName(), game.game());
        gameDAO.updateGame(gameID, newGame);
    }

    private void resign(int gameID) {}
}
