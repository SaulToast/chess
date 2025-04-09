package ui;

import static ui.EscapeSequences.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPiece.PieceType;

public class ChessBoardDrawer {

    private static final int BOARD_SIZE = 10;

    private final PrintStream out;
    private final TeamColor perspective;
    private final String[] yLables = {"1", "2", "3", "4", "5", "6", "7", "8"};
    private final String[] xLabels = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private ChessBoard board;
    private Collection<ChessMove> highlightedMoves = Set.of();

    private static final Map<TeamColor, Map<PieceType, String>> SYMBOL_MAP = new HashMap<>();

    static {
        SYMBOL_MAP.put(TeamColor.WHITE, Map.of(
            PieceType.PAWN, WHITE_PAWN,
            PieceType.BISHOP, WHITE_BISHOP,
            PieceType.KNIGHT, WHITE_KNIGHT,
            PieceType.ROOK, WHITE_ROOK,
            PieceType.QUEEN, WHITE_QUEEN,
            PieceType.KING, WHITE_KING
        ));

        SYMBOL_MAP.put(TeamColor.BLACK, Map.of(
            PieceType.PAWN, BLACK_PAWN,
            PieceType.BISHOP, BLACK_BISHOP,
            PieceType.KNIGHT, BLACK_KNIGHT,
            PieceType.ROOK, BLACK_ROOK,
            PieceType.QUEEN, BLACK_QUEEN,
            PieceType.KING, BLACK_KING
        ));
    }
    
    public ChessBoardDrawer(TeamColor color, ChessGame game) {
        out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        perspective = color;
        this.board = game.getBoard();
    }

    public void setHighlightedMoves(Collection<ChessMove> moves) {
        this.highlightedMoves = moves;
    }

    public void drawBoard() {
        out.print(ERASE_SCREEN);

        drawRowBorder();
        drawSquares();
        drawRowBorder();

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private void drawRowBorder() {

        int direction = 1;
        int index = 0;

        if (perspective != TeamColor.WHITE) {
            direction = -1;
            index = 7;
        }

        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);

        for (int col = 0; col != BOARD_SIZE; col++) {
            if (col > 0 && col < 9) {
                out.print(" " + xLabels[index] + " ");
                index += direction;
            } else {
                out.print(EMPTY);
            }
        }
        
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
        out.println();
    }

    private void drawSquares() {
        for (int i = 0; i < 8; i++) {

            int boardRow = (perspective == TeamColor.WHITE) ? 8 - i : i + 1;

            for (int j = 0; j < 10; j++) {
                if (j == 0 || j == 9) {
                    drawColBorderSquare(i);
                    continue;
                }

                int boardCol = (perspective == TeamColor.WHITE) ? j : 9 - j;
                var pos = new ChessPosition(boardRow, boardCol);
                var piece = board.getPiece(pos);

                boolean isHighlighted = highlightedMoves.stream()
                    .anyMatch(move -> move.getEndPosition().equals(pos));
                
                if (isHighlighted) {
                    out.print(SET_BG_COLOR_GREEN);
                } else if ((i % 2 == 0 && j % 2 == 1) || (i % 2 == 1 && j % 2 == 0)) {
                    out.print(SET_BG_COLOR_WHITE);
                    out.print(SET_TEXT_COLOR_WHITE);
                } else {
                    out.print(SET_BG_COLOR_BLACK);
                    out.print(SET_TEXT_COLOR_BLACK);
                }

                if (piece != null){
                    var color = piece.getTeamColor() == TeamColor.WHITE 
                    ? SET_TEXT_COLOR_RED 
                    : SET_TEXT_COLOR_BLUE;
                    out.print(color);
                    out.print(getChar(piece));
                } else {
                    out.print(EMPTY);
                }
            }
            out.println();
        }
    }

    private String getChar(ChessPiece piece) {
        return SYMBOL_MAP.get(piece.getTeamColor()).get(piece.getPieceType());
    }

    private void drawColBorderSquare(int row) {
        int index = row;
        if (perspective != TeamColor.BLACK) {
            index = 7-row;
        }
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
        out.print(" " + yLables[index] + " ");
    }
}
