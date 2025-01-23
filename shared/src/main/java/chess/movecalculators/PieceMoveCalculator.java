package chess.movecalculators;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public abstract class PieceMoveCalculator {

    protected final ChessGame.TeamColor teamColor;

    public PieceMoveCalculator(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    protected Collection<ChessMove> bishopRookHelper(ChessBoard board, ChessPosition myPosition, int[] rows, int[] columns) {
        Collection<ChessMove> moves = new ArrayList<>();

        int initialRow = myPosition.getRow();
        int initialColumn = myPosition.getColumn();

        for (int i = 0; i < rows.length; i++) {
            int row = initialRow;
            int column = initialColumn;
            boolean openRow = true;

            while (openRow) {
                row += rows[i];
                column += columns[i];

                if (row < 1 || row > 8 || column < 1 || column > 8) {
                    break;
                }

                openRow = addMovesHelper(board, myPosition, row, column, moves);
            }
        }
        return moves;
    }

    protected boolean addMovesHelper(ChessBoard board, ChessPosition myPosition, int row, int column, Collection<ChessMove> moves) {
        var endPosition = new ChessPosition(row, column);
        var occupyingPiece = board.getPiece(endPosition);
        if (occupyingPiece == null) {
            moves.add(new ChessMove(myPosition, endPosition));
            return true;
        } else if (occupyingPiece.getTeamColor() != teamColor) {
            moves.add(new ChessMove(myPosition, endPosition));
            return false;
        }
        return false;
    }
}
