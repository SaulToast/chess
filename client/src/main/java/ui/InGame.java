package ui;

import java.util.Arrays;
import java.util.Scanner;

import exceptions.ResponseException;
import static ui.EscapeSequences.*;

public class InGame extends Repl {

    
    public InGame(ChessClient client, Scanner scanner) {
        super(client, scanner);
    }

    @Override
    protected String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            if (tokens.length == 0) { return help(); }
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "move" -> client.makeMove(params);
                case "leave" -> client.leaveGame();
                case "resign" -> client.resignGame();
                case "redraw" -> client.redrawGame();
                case "show" -> client.showValidMoves(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    
    @Override
    protected void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[IN_GAME] >>> " + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public String help() {
        return """
        
                - leave: leaves the current game, doesn't lose the game
                - resign: resigns the current game, acts as a loss
                - redraw: redraws the chess board
                - move <start> <end>: in the format of e2 e4
                - show <position>: shows all the valid moves for a position
                - help: gives list of commands
                """;
    }

}
