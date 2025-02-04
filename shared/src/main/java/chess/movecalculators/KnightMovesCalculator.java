package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightMovesCalculator extends PieceMoveCalculator{

    public KnightMovesCalculator(ChessGame.TeamColor teamColor) {
        super(teamColor);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        int row = myPosition.getRow();
        int column = myPosition.getColumn();

        for (int i = -2; i <= 2; i++){
            for (int j = -2; j <= 2; j++){
                if (Math.abs(i) == Math.abs(j)) { continue; }
                if (i == 0 || j == 0) { continue; }

                // must be in bounds
                if (row + i < 1 || row + i > 8 || column + j < 1 || column + j > 8) {
                    continue;
                }
                var endPosition = new ChessPosition(row + i, column + j);
                // don't add move if the piece is on the same team
                var occupyingPiece = board.getPiece(endPosition);
                if (occupyingPiece != null && occupyingPiece.getTeamColor() == teamColor){
                    continue;
                }
                moves.add(new ChessMove(myPosition, endPosition));
            }
        }
        return moves;
    }
}
