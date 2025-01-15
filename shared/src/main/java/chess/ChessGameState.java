package chess;

public class ChessGameState {

    private ChessGame.TeamColor teamColor;
    private boolean whiteCheck;
    private boolean blackCheck;
    private boolean whiteCheckmate;
    private boolean blackCheckmate;
    private boolean whiteStalemate;
    private boolean blackStalemate;

    public ChessGameState() {
        teamColor = ChessGame.TeamColor.WHITE;
        whiteCheck = false;
        blackCheck = false;
        whiteCheckmate = false;
        blackCheckmate = false;
        whiteStalemate = false;
        blackStalemate = false;
    }

    // Getter and Setter for teamColor
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    // Combined Getter and Setter for Check
    public boolean isCheck() {
        return teamColor == ChessGame.TeamColor.WHITE ? whiteCheck : blackCheck;
    }

    public void setCheck(boolean check) {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            whiteCheck = check;
        } else {
            blackCheck = check;
        }
    }

    // Combined Getter and Setter for Checkmate
    public boolean isCheckmate() {
        return teamColor == ChessGame.TeamColor.WHITE ? whiteCheckmate : blackCheckmate;
    }

    public void setCheckmate(boolean checkmate) {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            whiteCheckmate = checkmate;
        } else {
            blackCheckmate = checkmate;
        }
    }

    // Combined Getter and Setter for Stalemate
    public boolean isStalemate() {
        return teamColor == ChessGame.TeamColor.WHITE ? whiteStalemate : blackStalemate;
    }

    public void setStalemate(boolean stalemate) {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            whiteStalemate = stalemate;
        } else {
            blackStalemate = stalemate;
        }
    }
}


