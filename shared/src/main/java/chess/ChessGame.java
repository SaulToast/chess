package chess;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    // game data
    private TeamColor currentTeamColor;
    private ChessBoard currentBoard;
    // data for en passant
    private ChessPosition enPassantTarget;
    private ChessPiece enPassantWouldCapture;
    private int enPassantResetCounter = 0;
    
    private boolean isOver = false;
    
    public boolean isOver() {
        return isOver;
    }

    public void setOver(boolean over) {
        this.isOver = over;
    }


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
        addCastling(moves, currPiece);

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
        if (enPassantWouldCapture.getMyPosition().getRow() != currPosition.getRow()) { return; }
        moves.add(new ChessMove(currPosition, enPassantTarget));
    }

    private void addCastling(Collection<ChessMove> moves, ChessPiece piece) {
        if (piece.getPieceType() != ChessPiece.PieceType.KING) { return; }
        if (piece.isPieceMoved() || piece.getMyPosition().getColumn() != 5) { return; }
        int row = piece.getTeamColor() == TeamColor.WHITE ? 1 : 8;
        var kingPosition = piece.getMyPosition();
        var rookPositionKingside = new ChessPosition(row, 8);
        var rookPositionQueenside = new ChessPosition(row, 1);
        var kingDestinationKingside = new ChessPosition(row, 7);
        var kingDestinationQueenside = new ChessPosition(row, 3);
        addCastlingHelper(moves, kingPosition, rookPositionKingside, kingDestinationKingside);
        addCastlingHelper(moves, kingPosition, rookPositionQueenside, kingDestinationQueenside);
    }

    private void addCastlingHelper(Collection<ChessMove> moves,
                                   ChessPosition kingPosition,
                                   ChessPosition rookPosition,
                                   ChessPosition destinationPosition) {
        var rook = currentBoard.getPiece(rookPosition);
        if (rook == null || rook.isPieceMoved()) { return; }
        if (checkCastlePathDanger(kingPosition, rookPosition)) { return; }
        var castle = new ChessMove(kingPosition, destinationPosition);
        castle.updateCastle();
        moves.add(castle);

    }

    private boolean checkCastlePathDanger(ChessPosition kingPosition, ChessPosition rookPosition) {
        // returns true if path isn't safe
        int kingCol = kingPosition.getColumn();
        int rookCol = rookPosition.getColumn();
        int direction = kingCol > rookCol ? -1 : 1;
        var boardCopy = new ChessBoard(currentBoard);
        for (int i = kingCol+direction; i != rookCol; i+=direction) {
            var king = boardCopy.getPiece(kingPosition);
            var testPosition = new ChessPosition(kingPosition.getRow(), i);
            if (boardCopy.getPiece(testPosition) != null) {return true;}
            boardCopy.addPiece(testPosition, king);
            boardCopy.removePiece(kingPosition);
            if (checkForCheck(king.getTeamColor(), boardCopy)) {return true;}
            kingPosition = testPosition;
        }
        return false;
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
            var currPiece = board.getPiece(move.getEndPosition());

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
        enPassantResetCounter++;
        var currPiece = currentBoard.getPiece(move.getStartPosition());
        if (currPiece == null) { throw new InvalidMoveException(); }
        if (!validMoves(move.getStartPosition()).contains(move)) { throw new InvalidMoveException("Invalid move"); }

        if (currPiece.getTeamColor() != getTeamTurn()) { throw new InvalidMoveException("Not your turn"); }

        if (currPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (move.getEndPosition().equals(enPassantTarget)) {
                currentBoard.removePiece(enPassantWouldCapture.getMyPosition());
                enPassantWouldCapture = null;
            }
            enPassantLogic(move, currPiece);
        }

        if (currPiece.getPieceType() == ChessPiece.PieceType.KING) {
            var start = move.getStartPosition();
            var end = move.getEndPosition();
            if (start.getColumn() - end.getColumn() != 1
                    && start.getColumn() - end.getColumn() != -1
                    && start.getColumn() != end.getColumn()) {
                makeMove(getRookMove(move));
            }
        }

        var promotionPiece = move.getPromotionPiece();
        if (promotionPiece != null) {
            currPiece = new ChessPiece(currPiece.getTeamColor(), promotionPiece);
        }
        currentBoard.addPiece(move.getEndPosition(), currPiece);
        currentBoard.removePiece(move.getStartPosition());

        currPiece.updatePieceMoved();

        if (currentTeamColor.equals(TeamColor.WHITE)){
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
        if (enPassantResetCounter > 1){
            enPassantResetCounter = 0;
            enPassantTarget = null;
            enPassantWouldCapture = null;
        }

    }

    private ChessMove getRookMove(ChessMove move) {
        var kingPosition = move.getEndPosition();
        var col = kingPosition.getColumn();
        ChessPosition rookPosition;
        ChessPosition rookDestination;
        if (col == 3){
            rookPosition = new ChessPosition(move.getEndPosition().getRow(), 1);
            rookDestination = new ChessPosition(move.getEndPosition().getRow(), 4);
        } else {
            rookPosition = new ChessPosition(move.getEndPosition().getRow(), 8);
            rookDestination = new ChessPosition(move.getEndPosition().getRow(), 6);
        }
        return new ChessMove(rookPosition, rookDestination);
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null || getClass() != obj.getClass()) { return false; }
        
        ChessGame other = (ChessGame) obj;
        
        return currentTeamColor == other.currentTeamColor &&
            Objects.equals(currentBoard, other.currentBoard) &&
            Objects.equals(enPassantTarget, other.enPassantTarget) &&
            Objects.equals(enPassantWouldCapture, other.enPassantWouldCapture) &&
            enPassantResetCounter == other.enPassantResetCounter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeamColor, currentBoard, enPassantTarget, enPassantWouldCapture, enPassantResetCounter);
    }
}
