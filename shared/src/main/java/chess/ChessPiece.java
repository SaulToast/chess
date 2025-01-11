package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor teamColor;
    private final PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        teamColor = pieceColor;
        pieceType = type;
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
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // pawn moves
        return switch (pieceType) {
            case PAWN -> getPawnMoves(board, myPosition);
            case ROOK -> getRookMoves(board, myPosition);
            case KNIGHT -> getKnightMoves(board, myPosition);
            case BISHOP -> getBishopMoves(board, myPosition);
            case QUEEN -> getQueenMoves(board, myPosition);
//            case KING -> getKingMoves(board, myPosition);
            default -> Collections.emptyList();
        };
    }

    private Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        moves.addAll(getRookMoves(board, myPosition));
        moves.addAll(getBishopMoves(board, myPosition));

        return moves;
    }

    private Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition) {

        int[] rows = {1, 1, -1, -1};
        int[] columns = {1, -1, 1, -1};

        return new ArrayList<>(bishopRookHelper(board, myPosition, rows, columns));
    }

    private Collection<ChessMove> getKnightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        int row = myPosition.getRow();
        int column = myPosition.getColumn();

        for (int i = -2; i <= 2; i++){
            for (int j = -2; j <= 2; j++){
                if (Math.abs(i) == Math.abs(j)) continue;
                if (i == 0 || j == 0) continue;

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
    
    private Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition myPosition) {

        int[] rows = {0, 0, 1, -1};
        int[] columns = {1, -1, 0, 0};

        return new ArrayList<>(bishopRookHelper(board, myPosition, rows, columns));
    }

    private Collection<ChessMove> bishopRookHelper(ChessBoard board, ChessPosition myPosition, int[] rows, int[] columns) {
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

                openRow = addRookMoves(board, myPosition, row, column, moves);
            }
        }
        return moves;
    }

    private boolean addRookMoves(ChessBoard board, ChessPosition myPosition, int row, int column, Collection<ChessMove> moves) {
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

    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        // moves one square forward
        // initial move can move 2 squares forward
        // captures one diagonally forward
        // en passant
        // can't move backwards
        // can't move if blocked
        Collection<ChessMove> moves = new ArrayList<>();
        boolean initial = teamColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7;
        if (teamColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) {
            initial = true;
        }

        int direction = teamColor == ChessGame.TeamColor.BLACK? -1 : 1;

        int row = myPosition.getRow();
        int column = myPosition.getColumn();

        boolean promotion = (row == 7 && teamColor == ChessGame.TeamColor.WHITE) || (row == 2 && teamColor == ChessGame.TeamColor.BLACK);

        var endPosition = new ChessPosition(row + direction, column);
        ChessMove move;
        boolean blocked = true;
        // normal move
        if (board.getPiece(endPosition) == null) {
            if (promotion) {
                addPawnPromotionMoves(moves, myPosition, endPosition);
            } else {
                moves.add(new ChessMove(myPosition, endPosition));
                blocked = false;
            }
        }

        // initial move
        endPosition = new ChessPosition(row + (2*direction), column);
        if (initial && board.getPiece(endPosition) == null && !blocked) {
            moves.add(new ChessMove(myPosition, endPosition));
        }

        // diagonal captures
        if (column - 1 > 0) {
            endPosition = new ChessPosition(row + direction, column - 1);
            var occupyingPiece = board.getPiece(endPosition);
            if (occupyingPiece != null && occupyingPiece.getTeamColor() != teamColor) {
                if (promotion){
                    addPawnPromotionMoves(moves, myPosition, endPosition);
                } else {
                    moves.add(new ChessMove(myPosition, endPosition));
                }
            }
        }
        if (column + 1 < 9) {
            endPosition = new ChessPosition(row + direction, column + 1);
            var occupyingPiece = board.getPiece(endPosition);
            if (occupyingPiece != null && occupyingPiece.getTeamColor() != teamColor) {
                if (promotion){
                    addPawnPromotionMoves(moves, myPosition, endPosition);
                } else {
                    moves.add(new ChessMove(myPosition, endPosition));
                }
            }
        }
        return moves;
    }

    private void addPawnPromotionMoves(Collection<ChessMove> moves, ChessPosition myPosition, ChessPosition endPosition){
        moves.add(new ChessMove(myPosition, endPosition, PieceType.KNIGHT));
        moves.add(new ChessMove(myPosition, endPosition, PieceType.ROOK));
        moves.add(new ChessMove(myPosition, endPosition, PieceType.BISHOP));
        moves.add(new ChessMove(myPosition, endPosition, PieceType.QUEEN));
    }
}
