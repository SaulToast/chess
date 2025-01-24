package chess.movecalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator extends PieceMoveCalculator{

    public PawnMovesCalculator(ChessGame.TeamColor teamColor) {
        super(teamColor);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        boolean initial = isInitialMove(myPosition);
        int direction = teamColor == ChessGame.TeamColor.BLACK ? -1 : 1;

        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        boolean promotion = isPromotionRow(row);

        // Normal move
        var endPosition = new ChessPosition(row + direction, column);
        if (canMoveTo(board, endPosition)) {
            addMoveOrPromotion(moves, myPosition, endPosition, promotion);
            // Initial move
            var doubleStepPosition = new ChessPosition(row + (2 * direction), column);
            if (initial && canMoveTo(board, doubleStepPosition)) {
                moves.add(new ChessMove(myPosition, doubleStepPosition));
            }
        }

        // Diagonal captures
        addDiagonalCapture(moves, board, myPosition, row + direction, column - 1, promotion);
        addDiagonalCapture(moves, board, myPosition, row + direction, column + 1, promotion);

        return moves;
    }

    // Pawn Helper methods
    private boolean isInitialMove(ChessPosition myPosition) {
        return (teamColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7)
                || (teamColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2);
    }

    private boolean isPromotionRow(int row) {
        return (row == 7 && teamColor == ChessGame.TeamColor.WHITE)
                || (row == 2 && teamColor == ChessGame.TeamColor.BLACK);
    }

    private boolean canMoveTo(ChessBoard board, ChessPosition position) {
        return board.getPiece(position) == null;
    }

    private void addMoveOrPromotion(Collection<ChessMove> moves, ChessPosition start, ChessPosition end, boolean promotion) {
        if (promotion) {
            addPawnPromotionMoves(moves, start, end);
        } else {
            moves.add(new ChessMove(start, end));
        }
    }

    private void addDiagonalCapture(Collection<ChessMove> moves, ChessBoard board, ChessPosition start, int endRow, int endColumn, boolean promotion) {
        if (endColumn >= 1 && endColumn <= 8) {
            var endPosition = new ChessPosition(endRow, endColumn);
            var occupyingPiece = board.getPiece(endPosition);
            if (occupyingPiece != null && occupyingPiece.getTeamColor() != teamColor) {
                addMoveOrPromotion(moves, start, endPosition, promotion);
            }
        }
    }

    private void addPawnPromotionMoves(Collection<ChessMove> moves, ChessPosition myPosition, ChessPosition endPosition){
        moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.QUEEN));
    }
}
