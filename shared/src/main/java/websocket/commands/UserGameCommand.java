package websocket.commands;

import java.util.Objects;

import chess.ChessMove;

/**
 * Represents a command a user can send the server over a websocket
 *
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    private CommandType commandType;

    private String authToken;

    private int gameID;

    private String teamColor;

    private ChessMove move;

    public UserGameCommand(CommandType commandType, String authToken, int gameID) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public UserGameCommand(CommandType commandType, String authToken, int gameID, String teamColor) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.teamColor = teamColor;
    }

    public UserGameCommand(CommandType commandType, String authToken, int gameID, ChessMove move) {
        this.commandType = commandType;
        this.gameID = gameID;
        this.move = move;
        this.authToken = authToken;

    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public int getGameID() {
        return gameID;
    }

    public String getColor() {
        return teamColor;
    }

    public ChessMove getMove() {
        return move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand)) {
            return false;
        }
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID());
    }
}
