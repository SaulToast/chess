package ui;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

import java.util.Arrays;
import java.util.Scanner;

import exceptions.ResponseException;
import ui.EscapeSequences.*;

public class PreIn {

    private final ChessClient client;
    private final Scanner scanner;

    public PreIn(ChessClient client, Scanner scanner) {
        this.client = client;
        this.scanner = scanner;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> client.registerUser(params);
                case "login" -> client.loginUser(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public void run() {
        System.out.println("Welcome to chess. Sign in to start.");
        System.out.print(help());

        var result = "";
        while (!result.equals("quit")) {
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
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    public String help() {
        return """
                - register <username> <password> <email>
                - login <username> <password>
                - quit
                - help
                """;
    }
}
