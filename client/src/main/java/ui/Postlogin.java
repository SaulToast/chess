package ui;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

import java.util.Arrays;
import java.util.Scanner;

import exceptions.ResponseException;
import ui.EscapeSequences.*;

public class Postlogin {

    private final ChessClient client;
    private final Scanner scanner;

    public Postlogin(ChessClient client, Scanner scanner) {
        this.client = client;
        this.scanner = scanner;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> client.createGame(params);
                case "logout" -> client.logoutUser();
                case "list" -> client.listGames();
                case "join" -> client.joinGame(params);
                case "watch" -> client.observe(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public void run() {
        System.out.println("Successfully Logged In");
        System.out.print(help());

        var result = "start";
        while (!result.equals("")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result + RESET_TEXT_COLOR);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN);
    }

    public String help() {
        return """
                - logout
                - create <game name>
                - list
                - join <game name>
                - watch <game name>
                - help
                """;
    }
}
