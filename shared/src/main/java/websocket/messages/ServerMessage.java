package websocket.messages;

import java.util.Objects;

import chess.ChessGame;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    String message;
    ChessGame game;
    String errorMessage;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessage(ServerMessageType type, String message) {
        this.serverMessageType = type;
        if (type.equals(ServerMessageType.ERROR)) {
            this.errorMessage = message;
        } else {
            this.message = message;
        }
    }

    public ServerMessage(ServerMessageType type, ChessGame game) {
        this.serverMessageType = type;
        this.game = game;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public ChessGame getGame() {
        return game;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
