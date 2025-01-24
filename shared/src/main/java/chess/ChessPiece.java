package chess;

import chess.movecalculators.*;
import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor teamColor;
    private final PieceType pieceType;
    private boolean pieceMoved;

    public ChessPosition getMyPosition() {
        return myPosition;
    }

    public void setMyPosition(ChessPosition myPosition) {
        this.myPosition = myPosition;
    }

    private ChessPosition myPosition;
    private static final Map<PieceType, Class<? extends PieceMoveCalculator>> calculators = new HashMap<>();

    static {
        calculators.put(PieceType.PAWN, PawnMovesCalculator.class);
        calculators.put(PieceType.KNIGHT, KnightMovesCalculator.class);
        calculators.put(PieceType.ROOK, RookMovesCalculator.class);
        calculators.put(PieceType.BISHOP, BishopMovesCalculator.class);
        calculators.put(PieceType.KING, KingMovesCalculator.class);
        calculators.put(PieceType.QUEEN, QueenMovesCalculator.class);
    }


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        teamColor = pieceColor;
        pieceType = type;
    }

    public ChessPiece(ChessPiece piece) {
        teamColor = piece.teamColor;
        pieceType = piece.pieceType;
        var position = piece.getMyPosition();
        myPosition = new ChessPosition(position);
        pieceMoved = piece.pieceMoved;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }

    @Override
    public String toString() {
        String piece = switch (pieceType) {
            case PAWN -> "p";
            case KNIGHT -> "n";
            case ROOK -> "r";
            case BISHOP -> "b";
            case KING -> "k";
            case QUEEN -> "q";
        };
        if (teamColor == ChessGame.TeamColor.WHITE) {
            piece = piece.toUpperCase();
        }
        return piece;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        // pawn moves
        try {
            var calculatorClass = calculators.get(pieceType);
            var calcConstructor = calculatorClass.getDeclaredConstructor(ChessGame.TeamColor.class);
            var moveCalc = calcConstructor.newInstance(teamColor);
            return moveCalc.pieceMoves(board, myPosition);

        } catch (Exception e) {
            return null;
        }
    }


}
