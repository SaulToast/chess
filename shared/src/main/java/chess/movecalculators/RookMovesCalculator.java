package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RookMovesCalculator extends PieceMoveCalculator{

    public RookMovesCalculator(ChessGame.TeamColor teamColor) {
        super(teamColor);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        int[] rows = {0, 0, 1, -1};
        int[] columns = {1, -1, 0, 0};

        return new ArrayList<>(bishopRookHelper(board, myPosition, rows, columns));
    }
}
