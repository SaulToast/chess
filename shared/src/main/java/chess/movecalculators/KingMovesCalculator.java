package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMovesCalculator extends PieceMoveCalculator{

    public KingMovesCalculator(ChessGame.TeamColor teamColor) {
        super(teamColor);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        int [] rowDirections = {1, 1, 1 , 0, -1, -1, -1, 0};
        int [] colDirections = {1, 0, -1, -1, -1, 0, 1, 1};

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int i = 0; i < rowDirections.length; i++) {
            int newRow = row + rowDirections[i];
            int newCol = col + colDirections[i];
            if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8){
                continue;
            }
            addMovesHelper(board, myPosition, newRow, newCol, moves);

        }
        return moves;
    }
}
