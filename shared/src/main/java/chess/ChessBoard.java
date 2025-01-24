package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board;
    private ChessPiece whiteKing = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
    private ChessPiece blackKing = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);

    public ChessBoard() {
        board = new ChessPiece[8][8];

    }

    public ChessBoard(ChessBoard oldBoard) {
        board = new ChessPiece[8][];

        for (int i = 0; i < 8; i++) {
            board[i] = Arrays.copyOf(oldBoard.board[i], oldBoard.board[i].length);
        }

        whiteKing = oldBoard.whiteKing;
        blackKing = oldBoard.blackKing;

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[8-position.getRow()][position.getColumn()-1] = piece;
        piece.setMyPosition(position);
        if (piece.equals(whiteKing)) {whiteKing = piece;}
        if (piece.equals(blackKing)) {blackKing = piece;}
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[8-position.getRow()][position.getColumn()-1];
    }

    public void removePiece(ChessPosition position) {
        board[8-position.getRow()][position.getColumn()-1] = null;
    }

    public ChessPiece getKing(ChessGame.TeamColor teamColor) {
        return teamColor == ChessGame.TeamColor.BLACK ? blackKing : whiteKing;
    }


    private Collection<ChessMove> getAllMoves(boolean white, boolean black){

        Collection<ChessMove> moves = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                var pos = new ChessPosition(i, j);
                var currPiece = getPiece(pos);
                if (currPiece == null) { continue; }
                if (!white || !black) {
                    if (white && !currPiece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                        continue;
                    }
                    if (black && !currPiece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                        continue;
                    }
                }
                moves.addAll(currPiece.pieceMoves(this, pos));
            }
        }

        return moves;
    }

    public Collection<ChessMove> getAllMoves() {
        return getAllMoves(true, true);
    }

    public Collection<ChessMove> getWhiteMoves(){
        return getAllMoves(true, false);
    }

    public Collection<ChessMove> getBlackMoves(){
        return getAllMoves(false, true);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // needs to have pieces, not just be an empty board
        board = new ChessPiece[8][8];

        // all pawns
        resetPawns(ChessGame.TeamColor.BLACK);
        resetPawns(ChessGame.TeamColor.WHITE);

        // rooks
        resetPairs(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK, 1, 8);
        resetPairs(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK, 1, 8);

        // knights
        resetPairs(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT, 2, 7);
        resetPairs(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT, 2, 7);

        // bishops
        resetPairs(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP, 3, 6);
        resetPairs(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP, 3, 6);

        // queens
        var blackQueen = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        addPiece(new ChessPosition(8, 4), blackQueen);
        var whiteQueen = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        addPiece(new ChessPosition(1, 4), whiteQueen);

        // kings
        blackKing = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        addPiece(new ChessPosition(8, 5), blackKing);
        whiteKing = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        addPiece(new ChessPosition(1, 5), whiteKing);


    }

    private int getDefaultRowFromColor(ChessGame.TeamColor color) {
        int row;
        if (color == ChessGame.TeamColor.BLACK){
            row = 8;
        } else { row = 1; }
        return row;
    }

    private void resetPairs(ChessGame.TeamColor color, ChessPiece.PieceType pieceType, int start, int end) {
        int row = getDefaultRowFromColor(color);
        for (int i = start; i <= end; i+=(end-start)) {
            var piece = new ChessPiece(color, pieceType);
            addPiece(new ChessPosition(row, i), piece);
        }
    }

    private void resetPawns(ChessGame.TeamColor color){
        int row;
        if (color == ChessGame.TeamColor.BLACK){
            row = 7;
        } else { row = 2; }

        for (int i = 1; i < 9; i++) {
            var pawn = new ChessPiece(color, ChessPiece.PieceType.PAWN);
            addPiece(new ChessPosition(row, i), pawn);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                var pos = new ChessPosition(i, j);
                if (!Objects.equals(getPiece(pos), that.getPiece(pos))){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < board.length; i++) {
            ChessPiece[] row = board[i];
            sb.append("|");
            for (ChessPiece chessPiece : row) {
                if (chessPiece != null) {
                    sb.append(chessPiece.toString());
                } else {
                    sb.append(" ");
                }

                sb.append("|");

            }
            if (i < board.length - 1) {
                sb.append("\n");
            }

        }
        return sb.toString();
    }
}


