package chess;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currentTeamColor;
    private ChessBoard currentBoard;
    private ChessPosition enPassantTarget;
    private ChessPiece enPassantWouldCapture;

    public ChessGame() {
        currentTeamColor = TeamColor.WHITE;
        currentBoard = new ChessBoard();
        currentBoard.resetBoard();

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeamColor = team;
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

        // get initial moves
        var moves = currPiece.pieceMoves(currentBoard, startPosition);

        // copy the board and simulate moves prevents moving into check
        moves.removeIf(this::simulateMoveAndTestCheck);
        addEnPassant(moves, currPiece);

        return moves;
    }

    public Collection<ChessMove> allValidMoves(TeamColor teamColor) {
        Collection<ChessMove> moves = switch (teamColor) {
            case WHITE -> currentBoard.getWhiteMoves();
            case BLACK -> currentBoard.getBlackMoves();
        };
        moves.removeIf(this::simulateMoveAndTestCheck);
        return moves;
    }

    private void addEnPassant(Collection<ChessMove> moves, ChessPiece piece) {
        if (piece.getPieceType() != ChessPiece.PieceType.PAWN) { return;}
        if (enPassantTarget == null) { return; }
        var currPosition = piece.getMyPosition();
        if (enPassantTarget.getRow() != currPosition.getRow()) { return; }
        moves.add(new ChessMove(currPosition, enPassantTarget));
    }

    private boolean simulateMoveAndTestCheck(ChessMove move) {
        var boardCopy = new ChessBoard(currentBoard);
        var piece = boardCopy.getPiece(move.getStartPosition());
        boardCopy.addPiece(move.getEndPosition(), piece);
        boardCopy.removePiece(move.getStartPosition());
        return checkForCheck(piece.getTeamColor(), boardCopy);
    }

    private boolean checkForCheck(TeamColor teamColor, ChessBoard board){
        // true if found check, false otherwise
        var moves = teamColor == TeamColor.BLACK? board.getWhiteMoves() : board.getBlackMoves();
        boolean foundCheck = false;
        for (var move : moves) {
            var currPiece = board.getPiece(move.endPosition);

            if (currPiece == null) { continue; }

            if (currPiece.equals(board.getKing(teamColor))) {
                foundCheck = true;
            }
        }
        return foundCheck;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        enPassantTarget = null;
        var currPiece = currentBoard.getPiece(move.getStartPosition());
        if (currPiece == null) { throw new InvalidMoveException(); }
        if (!validMoves(move.getStartPosition()).contains(move)) { throw new InvalidMoveException(); }

        if (currPiece.getTeamColor() != getTeamTurn()) { throw new InvalidMoveException(); }

        if (currPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (move.getEndPosition().equals(enPassantTarget)) {
                currentBoard.removePiece(enPassantWouldCapture.getMyPosition());
                enPassantWouldCapture = null;
            }
            enPassantLogic(move, currPiece);
        }

        var promotionPiece = move.getPromotionPiece();
        if (promotionPiece != null) {
            currPiece = new ChessPiece(currPiece.getTeamColor(), promotionPiece);
        }
        currentBoard.addPiece(move.getEndPosition(), currPiece);
        currentBoard.removePiece(move.getStartPosition());

        if (currentTeamColor.equals(TeamColor.WHITE)){
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }

    }

    private void enPassantLogic(ChessMove move, ChessPiece piece) {
        int direction = piece.getTeamColor() == TeamColor.WHITE ? 1 : -1;
        var start = move.getStartPosition();
        var end = move.getEndPosition();
        if (end.getRow() != start.getRow() + 2*direction) {
            return;
        }
        enPassantTarget = new ChessPosition(end.getRow() - direction, end.getColumn());
        enPassantWouldCapture = piece;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return checkForCheck(teamColor, currentBoard);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // Checkmate is when you are in check and the king has no valid moves
        // can't protect king with any other piece
        var inCheck = isInCheck(teamColor);
        var king = currentBoard.getKing(teamColor);
        var kingPos = king.getMyPosition();
        var kingMoves = validMoves(kingPos);
        var allMoves = allValidMoves(teamColor);
        for (var move : allMoves) {
            if (!simulateMoveAndTestCheck(move)) { return false; }
        }
        return inCheck && kingMoves.isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {

        var inCheck = isInCheck(teamColor);
        var allMoves = allValidMoves(teamColor);
        return !inCheck && allMoves.isEmpty();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        currentBoard = new ChessBoard();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                var pos = new ChessPosition(i, j);
                var piece = board.getPiece(pos);
                if (piece == null) { continue; }
                currentBoard.addPiece(pos, piece);
            }
        }
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
