package chess;

import jdk.jshell.spi.ExecutionControl;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessGameState gameState;
    private ChessBoard currentBoard;

    public ChessGame() {
        gameState = new ChessGameState();
        gameState.setTeamColor(TeamColor.WHITE);
        currentBoard = new ChessBoard();

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return gameState.getTeamColor();
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        gameState.setTeamColor(team);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currPiece = currentBoard.getPiece(startPosition);
        if (currPiece == null) { return null; }

        var moves = currPiece.pieceMoves(currentBoard, startPosition);

        var boardCopy = new ChessBoard(currentBoard);

        moves.removeIf(move -> simulateMoveAndTestCheck(move, boardCopy));

        return moves;
    }

    private boolean simulateMoveAndTestCheck(ChessMove move, ChessBoard board) {
        var currPiece = board.getPiece(move.startPosition);
        board.addPiece(move.endPosition, currPiece);
        board.removePiece(move.startPosition);
        return checkForCheck(board, currPiece);
    }

    private boolean checkForCheck(ChessBoard board, ChessPiece piece){
        var king = new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.KING);
        var pos = board.getPosition(king);
        var moves = board.getAllMoves();
        for (var move : moves) {
            if (move.getEndPosition().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!validMoves(move.startPosition).contains(move)) { throw new InvalidMoveException(); }

        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return gameState.getTeamColor() == teamColor;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return gameState.isCheckmate();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return gameState.isStalemate();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }
}
