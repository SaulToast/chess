package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator extends PieceMoveCalculator{

    public BishopMovesCalculator(ChessGame.TeamColor teamColor) {
        super(teamColor);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        int[] rows = {1, 1, -1, -1};
        int[] columns = {1, -1, 1, -1};

        return new ArrayList<>(bishopRookHelper(board, myPosition, rows, columns));
    }
}
