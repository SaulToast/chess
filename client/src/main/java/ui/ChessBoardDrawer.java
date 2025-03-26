package ui;

import static ui.EscapeSequences.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import chess.ChessGame.TeamColor;

public class ChessBoardDrawer {

    private final int BOARD_SIZE = 10;

    private final PrintStream out;
    private final TeamColor perspective;
    
    public ChessBoardDrawer(TeamColor color) {
        out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        perspective = color;
    }

    public void drawBoard() {
        out.print(ERASE_SCREEN);

        drawRowBorder();
        drawSquares();

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private void drawRowBorder() {
        String[] xLabels = {"a", "b", "c", "d", "e", "f", "g", "h"};

        int direction = 1;
        int index = 0;

        if (perspective != TeamColor.WHITE) {
            direction = -1;
            index = 7;
        }

        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);

        for (int col = 0; col != BOARD_SIZE; ++col) {
            if (col > 0 && col < 9) {
                out.print("  " + xLabels[index] + "  ");
                index += direction;
            } else {
                out.print(EMPTY);
            }
        }
    }

    private void drawSquares() {

    }
}
