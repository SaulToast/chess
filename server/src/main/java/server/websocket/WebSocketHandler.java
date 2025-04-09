package server.websocket;

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

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }
    
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(), command.getGameID(), command.getColor(), session);
            case MAKE_MOVE -> makeMove(command.getAuthToken(), command.getGameID(), command.getMove(), session);
            case LEAVE -> leave(command.getAuthToken(), command.getGameID(), command.getColor(), session);
            case RESIGN -> resign(command.getAuthToken(), command.getGameID(), command.getColor(), session);
        }
    }

    private void connect(String authToken, int gameID, String color, Session session) throws IOException {
        try {
            var name = authDAO.getUsernameFromToken(authToken);
            connections.add(name, session, gameID);
            connections.playerJoined(gameID, name);
            var game = gameDAO.getGame(gameID);
            var response = new ServerMessage(ServerMessageType.LOAD_GAME, game);
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

    private void makeMove(String authToken, int gameID, ChessMove move, Session session) throws IOException {
        try {
            var name = authDAO.getUsernameFromToken(authToken);
            var data = gameDAO.getGame(gameID);
            var game = data.game();

            if (game.isOver()) {
                throw new InvalidMoveException("Game is over");
            }

            if (!name.equals(data.whiteUsername()) && !name.equals(data.blackUsername())) {
                throw new InvalidMoveException("An observer can't move");
            }
            
            if (data.whiteUsername() == null || data.blackUsername() == null) {
                throw new InvalidMoveException("Wait for a second player");
            }


            var color = name.equals(data.whiteUsername()) ? TeamColor.WHITE : TeamColor.BLACK;


            if (!color.equals(game.getTeamTurn())) {
                throw new InvalidMoveException("Not your turn");
            }

            game.makeMove(move);
            var updatedData = new GameData(
                data.gameID(), 
                data.whiteUsername(),
                data.blackUsername(), 
                data.gameName(), 
                game);

            var stateMessage = handleSpecialGameStates(gameID, data, game);

            var response = new ServerMessage(ServerMessageType.LOAD_GAME, updatedData);
            connections.broadcast("", gameID, response);

            var message = String.format("%s made move %s", name, move);
            var notification = new ServerMessage(ServerMessageType.NOTIFICATION, message);
            connections.broadcast(name, gameID, notification);

            if (!stateMessage.equals("")) {
                connections.broadcast("", gameID, 
                new ServerMessage(ServerMessageType.NOTIFICATION, stateMessage));
            }


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

    private String handleSpecialGameStates(int gameID, GameData data, ChessGame game) throws IOException {
        var opponentColor = game.getTeamTurn();
        var otherPlayer = opponentColor == TeamColor.BLACK ? data.blackUsername() : data.whiteUsername();
        String message = "";
        if (game.isInCheckmate(opponentColor)) {
            game.setOver(true);
            message = otherPlayer + " is in checkmate";
        } else if (game.isInStalemate(opponentColor)) {
            game.setOver(true);
            message = otherPlayer + " is in stalemate";
        } else if (game.isInCheck(opponentColor)) {
            message = otherPlayer + " is in check";
        }
        return message;
    }

    private void leave(String authToken, int gameID, String color, Session session) throws IOException {
        try {
            var name = authDAO.getUsernameFromToken(authToken);
            var message = String.format("%s left the game", name);
            var notification = new ServerMessage(ServerMessageType.NOTIFICATION, message);
            connections.broadcast(name, gameID, notification);
            connections.playerLeft(gameID, name);
            connections.remove(name);
            removePlayer(gameID, name);
        } catch (Exception e) {
            var errResponse = new ServerMessage(ServerMessageType.ERROR, "Error: couldn't leave game");
            session.getRemote().sendString(new Gson().toJson(errResponse));
        }
    }


    private void resign(String authToken, int gameID, String color, Session session) throws IOException {
        try {
            var name = authDAO.getUsernameFromToken(authToken);
            var data = gameDAO.getGame(gameID);
            var game = data.game();
            var whiteName = data.whiteUsername();
            var blackName = data.blackUsername();

            if (!name.equals(whiteName) && !name.equals(blackName)) {
                var errResponse = new ServerMessage(ServerMessageType.ERROR, "Observer can't resign");
                session.getRemote().sendString(new Gson().toJson(errResponse));
                return;
            }

            if (game.isOver()) {
                var errResponse = new ServerMessage(ServerMessageType.ERROR, "Game is already over. Can't resign");
                session.getRemote().sendString(new Gson().toJson(errResponse));
                return;
            }

            game.setOver(true);
            var updatedData = new GameData(
                data.gameID(), 
                data.whiteUsername(), 
                data.blackUsername(), 
                data.gameName(), 
                game);
                gameDAO.updateGame(gameID, updatedData);
                
            var message = String.format("%s resigned from the game", name);
            var notification = new ServerMessage(ServerMessageType.NOTIFICATION, message);
            connections.broadcast("", gameID, notification);

        } catch (DataAccessException e) {
            var errResponse = new ServerMessage(ServerMessageType.ERROR, "Error: couldn't resign");
            session.getRemote().sendString(new Gson().toJson(errResponse));
        }
    }

    private void removePlayer(int gameID, String name) throws DataAccessException {
        var data = gameDAO.getGame(gameID);
        var game = data.game();
        String blackUsername = data.blackUsername();
        String whiteUsername = data.whiteUsername();

        if (name.equals(whiteUsername)) {
            whiteUsername = null;
        } else if (name.equals(blackUsername)){
            blackUsername = null;
        }


        if (game.isOver() && !connections.hasActivePlayers(gameID)) {
            gameDAO.deleteGame(gameID);

        } else {
            var newGame = new GameData(data.gameID(), whiteUsername, blackUsername, data.gameName(), data.game());
            gameDAO.updateGame(gameID, newGame);
        }
    }
}
