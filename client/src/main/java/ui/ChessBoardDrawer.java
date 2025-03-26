package ui;

import static ui.EscapeSequences.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import chess.ChessGame.TeamColor;

public class ChessBoardDrawer {

    private final int BOARD_SIZE = 10;

    private final PrintStream out;
    private final TeamColor perspective;
    private final String[] yLables = {"1", "2", "3", "4", "5", "6", "7", "8"};
    private final String[] xLabels = {"a", "b", "c", "d", "e", "f", "g", "h"};
    
    public ChessBoardDrawer(TeamColor color) {
        out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        perspective = color;
    }

    public void drawBoard() {
        out.print(ERASE_SCREEN);

        drawRowBorder();
        drawSquares();
        drawRowBorder();

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
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

        out.println();
    }

    private void drawSquares() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 10; j++) {
                if (j == 0 || j == 9) {
                    drawColBorderSquare(i);
                    continue;
                }
                if ((i % 2 == 0 && j % 2 == 1) || (i % 2 == 1 && j % 2 == 0)) {
                    out.print(SET_BG_COLOR_WHITE);
                    out.print(SET_TEXT_COLOR_WHITE);
                    out.print(EMPTY);
                } else {
                    out.print(SET_BG_COLOR_BLACK);
                    out.print(SET_TEXT_COLOR_BLACK);
                    out.print(EMPTY);
                }
            }
            out.println();
        }
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
